package com.icsseseguridad.locationsecurity.view.ui.visit;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.gson.Gson;
import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.service.dao.AppDatabase;
import com.icsseseguridad.locationsecurity.service.entity.Incidence;
import com.icsseseguridad.locationsecurity.service.entity.Photo;
import com.icsseseguridad.locationsecurity.service.entity.VehicleType;
import com.icsseseguridad.locationsecurity.service.repository.PhotoController;
import com.icsseseguridad.locationsecurity.service.repository.VisitController;
import com.icsseseguridad.locationsecurity.service.event.OnAddVehicleFailure;
import com.icsseseguridad.locationsecurity.service.event.OnAddVehicleSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnUploadPhotoFailure;
import com.icsseseguridad.locationsecurity.service.event.OnUploadPhotoSuccess;
import com.icsseseguridad.locationsecurity.service.entity.VisitorVehicle;
import com.icsseseguridad.locationsecurity.util.UTILITY;
import com.icsseseguridad.locationsecurity.view.adapter.IncidenceSpinnerAdapter;
import com.icsseseguridad.locationsecurity.view.adapter.VehicleTypeSpinnerAdapter;
import com.icsseseguridad.locationsecurity.view.ui.PhotoActivity;
import com.icsseseguridad.locationsecurity.view.ui.binnacle.AddReportActivity;
import com.icsseseguridad.locationsecurity.viewmodel.IncidenceListViewModel;
import com.icsseseguridad.locationsecurity.viewmodel.VehicleTypeListViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class AddVehicleActivity extends PhotoActivity {

    private static final Integer INTENT_PHOTO = 99;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.photo) ImageView photoImage;
    @BindView(R.id.plate) TextView plateText;
    @BindView(R.id.spinner_vehicle) Spinner spinner;
    @BindView(R.id.model) TextView modelText;
    @BindView(R.id.type) TextView typeText;

    private Uri photoUri;
    private VisitorVehicle vehicle;

    private VehicleTypeListViewModel typeListViewModel;

    private List<VehicleType> vehicleTypes = new ArrayList<>();

    private VehicleTypeSpinnerAdapter adapterSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);
        ButterKnife.bind(this);
        setSupportActionBarBack(toolbar);

        typeText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN) {
                    save();
                    return true;
                }
                return false;
            }
        });
        getVehiclesTypes();
    }

    private void getVehiclesTypes() {
        typeListViewModel = ViewModelProviders
                .of(this).get(VehicleTypeListViewModel.class);
        typeListViewModel.getVehicles().observe(this, new Observer<List<VehicleType>>() {
            @Override
            public void onChanged(@Nullable final List<VehicleType> vehicleTypes) {
                vehicleTypes.add(0, new VehicleType("Selecciona un vehiculo..."));
                AddVehicleActivity.this.vehicleTypes = new ArrayList<>(vehicleTypes);
                loadSpinner();
            }
        });
    }

    private void loadSpinner() {
        adapterSpinner = new VehicleTypeSpinnerAdapter(this,
                R.layout.spinner_item, vehicleTypes);
        spinner.setAdapter(adapterSpinner);
    }

    @OnClick(R.id.icon_photo)
    public void onSelectPhoto() {
        Pix.start(this, INTENT_PHOTO, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onSelectPhoto();
                } else {
                    Toast.makeText(this, "Debes aprobar permisos para tomar la foto",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == INTENT_PHOTO) {
            ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            if (returnValue.size() > 0) {
                String uriCompress = compressImage(returnValue.get(0));
                Uri uri = Uri.fromFile(new File(uriCompress));
                photoUri = uri;
                photoImage.setImageURI(uri);
            }
        }
    }

    @OnClick(R.id.add_button)
    public void save() {
        if (plateText.getText().toString().isEmpty()) {
            plateText.setError(getString(R.string.error_empty_label));
            plateText.requestFocus();
            return;
        }
        if (plateText.getText().toString().length() < 4) {
            plateText.setError(getString(R.string.error_size_4_label));
            plateText.requestFocus();
            return;
        }
        if (spinner.getSelectedItemPosition() < 1) {
            Toast.makeText(this, "Selecciona un tipo de vehiculo", Toast.LENGTH_SHORT).show();
            spinner.requestFocus();
            return;
        }
        if (modelText.getText().toString().isEmpty()) {
            modelText.setError(getString(R.string.error_empty_label));
            modelText.requestFocus();
            return;
        }
        if (modelText.getText().toString().length() < 3) {
            modelText.setError(getString(R.string.error_size_3_label));
            modelText.requestFocus();
            return;
        }
        if (typeText.getText().toString().isEmpty()) {
            typeText.setError(getString(R.string.error_empty_label));
            typeText.requestFocus();
            return;
        }
        if (typeText.getText().toString().length() < 4) {
            typeText.setError(getString(R.string.error_size_4_label));
            typeText.requestFocus();
            return;
        }
        if (photoUri == null) {
            Snackbar.make(toolbar, R.string.error_photo, Snackbar.LENGTH_LONG).show();
            hideKeyboard();
            return;
        }
        hideKeyboard();
        final VisitorVehicle vehicle = new VisitorVehicle();
        vehicle.plate = plateText.getText().toString();
        VehicleType vehicleType = (VehicleType) spinner.getSelectedItem();
        vehicle.vehicle = vehicleType.name;
        vehicle.model = modelText.getText().toString();
        vehicle.type = typeText.getText().toString();
        vehicle.createDate = UTILITY.longToString(new Date().getTime());
        vehicle.updateDate = UTILITY.longToString(new Date().getTime());
        vehicle.sync = false;
        vehicle.active = 1;

        builderDialog.text("Guardando...");
        dialog.show();

        Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter e) throws Exception {
                saveVehicle(vehicle);
                e.onComplete();
            }})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        onSuccess(vehicle);
                    }
                }).isDisposed();
    }

    private void saveVehicle(VisitorVehicle vehicle) {
        VisitorVehicle saved = AppDatabase.getInstance(getApplicationContext())
                .getVisitorVehicleDao().findByPlate(vehicle.plate);
        if (saved == null) {
            vehicle.id = AppDatabase.getInstance(getApplicationContext())
                    .getVisitorVehicleDao().insert(vehicle);
            Log.e("this", gson().toJson(vehicle));
            if (photoUri != null) {
                Photo photo = new Photo(photoUri.toString(), Photo.LINKED.VEHICLE, vehicle.id);
                AppDatabase.getInstance(getApplicationContext())
                        .getPhotoDao().insert(photo);
            }
        }
    }

    private void onSuccess(VisitorVehicle vehicle) {
        dialog.dismiss();
        if (vehicle.id == null) {
            Snackbar.make(toolbar, "Esta placa se encuentra en uso",
                    Snackbar.LENGTH_LONG).show();
            return;
        }
        app.vehicle = vehicle;
        setResult(RESULT_OK, getIntent());
        finish();
    }

//    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
//    public void uploadPhotoSuccess(OnUploadPhotoSuccess event) {
//        EventBus.getDefault().removeStickyEvent(OnUploadPhotoSuccess.class);
//        vehicle.photo = event.url;
//        VisitController visitController = new VisitController();
//        visitController.saveVehicle(vehicle);
//    }
//
//    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
//    public void uploadPhotoFailure(OnUploadPhotoFailure event) {
//        EventBus.getDefault().removeStickyEvent(OnUploadPhotoFailure.class);
//        dialog.dismiss();
//        Snackbar.make(toolbar, event.message, Snackbar.LENGTH_LONG).show();
//    }
//
//    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
//    public void onAddVehicleSuccess(OnAddVehicleSuccess event) {
//        EventBus.getDefault().removeStickyEvent(OnAddVehicleSuccess.class);
//        dialog.dismiss();
//        app.vehicle = event.vehicle;
//        setResult(RESULT_OK, getIntent());
//        finish();
//    }
//
//    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
//    public void onAddVehicleFailure(OnAddVehicleFailure event) throws JSONException {
//        EventBus.getDefault().removeStickyEvent(OnAddVehicleFailure.class);
//        dialog.dismiss();
//
//        if (event.response.errors != null) {
//            JSONObject jsonErrors = new JSONObject(new Gson().toJson(event.response.errors));
//            if (jsonErrors.has("plate")) {
//                String errorPlate = jsonErrors.getJSONArray("plate").get(0).toString();
//                plateText.setError(errorPlate);
//                plateText.requestFocus();
//            }
//            if (jsonErrors.has("vehicle")) {
//                String errorPlate = jsonErrors.getJSONArray("vehicle").get(0).toString();
//                vehicleText.setError(errorPlate);
//                vehicleText.requestFocus();
//            }
//            if (jsonErrors.has("model")) {
//                String errorPlate = jsonErrors.getJSONArray("model").get(0).toString();
//                modelText.setError(errorPlate);
//                modelText.requestFocus();
//            }
//            if (jsonErrors.has("type")) {
//                String errorPlate = jsonErrors.getJSONArray("type").get(0).toString();
//                typeText.setError(errorPlate);
//                typeText.requestFocus();
//            }
//        } else {
//            Snackbar.make(toolbar, event.response.message, Snackbar.LENGTH_LONG).show();
//        }
//
//    }

    @OnClick(R.id.sos_alarm)
    public void SOS() {
        dialogSOS();
    }
}

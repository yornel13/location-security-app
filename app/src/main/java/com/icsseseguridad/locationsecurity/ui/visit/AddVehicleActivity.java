package com.icsseseguridad.locationsecurity.ui.visit;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.gson.Gson;
import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.controller.PhotoController;
import com.icsseseguridad.locationsecurity.controller.VisitController;
import com.icsseseguridad.locationsecurity.events.OnAddVehicleFailure;
import com.icsseseguridad.locationsecurity.events.OnAddVehicleSuccess;
import com.icsseseguridad.locationsecurity.events.OnUploadPhotoFailure;
import com.icsseseguridad.locationsecurity.events.OnUploadPhotoSuccess;
import com.icsseseguridad.locationsecurity.model.VisitorVehicle;
import com.icsseseguridad.locationsecurity.ui.PhotoActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddVehicleActivity extends PhotoActivity {

    private static final Integer INTENT_PHOTO = 99;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.photo) ImageView photoImage;
    @BindView(R.id.plate) TextView plateText;
    @BindView(R.id.vehicle) TextView vehicleText;
    @BindView(R.id.model) TextView modelText;
    @BindView(R.id.type) TextView typeText;

    private Uri photoUri;
    private VisitorVehicle vehicle;

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
        VisitorVehicle vehicle = new VisitorVehicle();
        vehicle.plate = plateText.getText().toString();
        vehicle.vehicle = vehicleText.getText().toString();
        vehicle.model = modelText.getText().toString();
        vehicle.type = typeText.getText().toString();

        if (photoUri != null) {
            this.vehicle = vehicle;
            new PhotoController().save(photoUri.toString());
        } else {
            VisitController visitController = new VisitController();
            visitController.saveVehicle(vehicle);
        }
        builderDialog.text("Guardando...");
        dialog.show();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void uploadPhotoSuccess(OnUploadPhotoSuccess event) {
        EventBus.getDefault().removeStickyEvent(OnUploadPhotoSuccess.class);
        vehicle.photo = event.url;
        VisitController visitController = new VisitController();
        visitController.saveVehicle(vehicle);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void uploadPhotoFailure(OnUploadPhotoFailure event) {
        EventBus.getDefault().removeStickyEvent(OnUploadPhotoFailure.class);
        dialog.dismiss();
        Snackbar.make(toolbar, event.message, Snackbar.LENGTH_LONG).show();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onAddVehicleSuccess(OnAddVehicleSuccess event) {
        EventBus.getDefault().removeStickyEvent(OnAddVehicleSuccess.class);
        dialog.dismiss();
        app.vehicle = event.vehicle;
        setResult(RESULT_OK, getIntent());
        finish();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onAddVehicleFailure(OnAddVehicleFailure event) throws JSONException {
        EventBus.getDefault().removeStickyEvent(OnAddVehicleFailure.class);
        dialog.dismiss();

        if (event.response.errors != null) {
            JSONObject jsonErrors = new JSONObject(new Gson().toJson(event.response.errors));
            if (jsonErrors.has("plate")) {
                String errorPlate = jsonErrors.getJSONArray("plate").get(0).toString();
                plateText.setError(errorPlate);
                plateText.requestFocus();
            }
            if (jsonErrors.has("vehicle")) {
                String errorPlate = jsonErrors.getJSONArray("vehicle").get(0).toString();
                vehicleText.setError(errorPlate);
                vehicleText.requestFocus();
            }
            if (jsonErrors.has("model")) {
                String errorPlate = jsonErrors.getJSONArray("model").get(0).toString();
                modelText.setError(errorPlate);
                modelText.requestFocus();
            }
            if (jsonErrors.has("type")) {
                String errorPlate = jsonErrors.getJSONArray("type").get(0).toString();
                typeText.setError(errorPlate);
                typeText.requestFocus();
            }
        } else {
            Snackbar.make(toolbar, event.response.message, Snackbar.LENGTH_LONG).show();
        }

    }

    @OnClick(R.id.sos_alarm)
    public void SOS() {
        dialogSOS();
    }
}

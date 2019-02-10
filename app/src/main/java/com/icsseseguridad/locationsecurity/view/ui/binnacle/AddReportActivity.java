package com.icsseseguridad.locationsecurity.view.ui.binnacle;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.gson.Gson;
import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.service.dao.AppDatabase;
import com.icsseseguridad.locationsecurity.service.entity.Photo;
import com.icsseseguridad.locationsecurity.service.entity.TabletPosition;
import com.icsseseguridad.locationsecurity.service.entity.VisitorVehicle;
import com.icsseseguridad.locationsecurity.util.CurrentLocation;
import com.icsseseguridad.locationsecurity.view.adapter.IncidenceSpinnerAdapter;
import com.icsseseguridad.locationsecurity.service.repository.BinnacleController;
import com.icsseseguridad.locationsecurity.service.repository.PhotoController;
import com.icsseseguridad.locationsecurity.service.event.OnRegisterReportFailure;
import com.icsseseguridad.locationsecurity.service.event.OnRegisterReportSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnSyncIncidences;
import com.icsseseguridad.locationsecurity.service.event.OnUploadPhotoFailure;
import com.icsseseguridad.locationsecurity.service.event.OnUploadPhotoSuccess;
import com.icsseseguridad.locationsecurity.service.entity.Incidence;
import com.icsseseguridad.locationsecurity.service.entity.SpecialReport;
import com.icsseseguridad.locationsecurity.view.ui.PhotoActivity;
import com.icsseseguridad.locationsecurity.util.UTILITY;
import com.icsseseguridad.locationsecurity.viewmodel.IncidenceListViewModel;
import com.icsseseguridad.locationsecurity.viewmodel.VehicleListViewModel;
import com.stfalcon.frescoimageviewer.ImageViewer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class AddReportActivity extends PhotoActivity {

    private static final Integer INTENT_PHOTO = 99;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.spinner) Spinner spinner;
    @BindView(R.id.scroll_photos) HorizontalScrollView scrollPhotos;

    @BindView(R.id.container_photo_1) View containerPhoto1;
    @BindView(R.id.container_photo_2) View containerPhoto2;
    @BindView(R.id.container_photo_3) View containerPhoto3;
    @BindView(R.id.container_photo_4) View containerPhoto4;
    @BindView(R.id.container_photo_5) View containerPhoto5;
    @BindView(R.id.photo_1) ImageView photo1;
    @BindView(R.id.photo_2) ImageView photo2;
    @BindView(R.id.photo_3) ImageView photo3;
    @BindView(R.id.photo_4) ImageView photo4;
    @BindView(R.id.photo_5) ImageView photo5;
    @BindView(R.id.delete_1) ImageView delete1;
    @BindView(R.id.delete_2) ImageView delete2;
    @BindView(R.id.delete_3) ImageView delete3;
    @BindView(R.id.delete_4) ImageView delete4;
    @BindView(R.id.delete_5) ImageView delete5;
    @BindView(R.id.add_photo) View addPhoto;
    @BindView(R.id.save) Button saveButton;

    @BindView(R.id.observation) EditText observationText;

    private List<String> photos = new ArrayList<>();

    private IncidenceSpinnerAdapter adapterSpinner;

    private SpecialReport report;

    private List<Incidence> incidences = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_report);
        ButterKnife.bind(this);
        setSupportActionBarBack(toolbar);
        saveButton.setTypeface(UTILITY.typefaceButton(this));
        setupPhotos();
        getIncidences();
    }

    private void getIncidences() {
        Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter e) throws Exception {
                incidences = AppDatabase.getInstance(getApplicationContext())
                        .getIncidenceDao().getAllSync();
                e.onComplete();
            }})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        loadSpinner();
                    }
                }).isDisposed();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void loadSpinner() {
        adapterSpinner = new IncidenceSpinnerAdapter(this,
                R.layout.spinner_item, incidences);
        spinner.setAdapter(adapterSpinner);
        int defaultIndex = 0;
        if (incidences != null) {
            for (Incidence incidence: incidences) {
                if (incidence.name.toLowerCase().equals("general".toLowerCase())) {
                    defaultIndex = incidences.indexOf(incidence);
                }
            }
            spinner.setSelection(defaultIndex);
        }
    }

    @OnClick(R.id.add_photo)
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
                photos.add(0, uri.toString());
                setupPhotos();
            }
        }
    }

    @OnClick(R.id.delete_1)
    public void delete1() {
        photos.remove(0);
        setupPhotos();
    }

    @OnClick(R.id.delete_2)
    public void delete2() {
        photos.remove(1);
        setupPhotos();
    }

    @OnClick(R.id.delete_3)
    public void delete3() {
        photos.remove(2);
        setupPhotos();
    }

    @OnClick(R.id.delete_4)
    public void delete4() {
        photos.remove(3);
        setupPhotos();
    }

    @OnClick(R.id.delete_5)
    public void delete5() {
        photos.remove(4);
        setupPhotos();
    }

    @OnClick(R.id.photo_1)
    public void clickPhoto1() {
        new ImageViewer.Builder(this, photos)
                .setStartPosition(0)
                .show();
    }

    @OnClick(R.id.photo_2)
    public void clickPhoto2() {
        new ImageViewer.Builder(this, photos)
                .setStartPosition(1)
                .show();
    }

    @OnClick(R.id.photo_3)
    public void clickPhoto3() {
        new ImageViewer.Builder(this, photos)
                .setStartPosition(2)
                .show();
    }

    @OnClick(R.id.photo_4)
    public void clickPhoto4() {
        new ImageViewer.Builder(this, photos)
                .setStartPosition(3)
                .show();
    }

    @OnClick(R.id.photo_5)
    public void clickPhoto5() {
        new ImageViewer.Builder(this, photos)
                .setStartPosition(4)
                .show();
    }

    public void setupPhotos() {
        addPhoto.setVisibility(View.VISIBLE);
        if (photos.size() == 0) {
            photo1.setImageDrawable(getResources().getDrawable(R.drawable.empty_image));
            photo2.setImageDrawable(getResources().getDrawable(R.drawable.empty_image));
            photo3.setImageDrawable(getResources().getDrawable(R.drawable.empty_image));
            photo4.setImageDrawable(getResources().getDrawable(R.drawable.empty_image));
            photo5.setImageDrawable(getResources().getDrawable(R.drawable.empty_image));
            containerPhoto1.setVisibility(View.GONE);
            containerPhoto2.setVisibility(View.GONE);
            containerPhoto3.setVisibility(View.GONE);
            containerPhoto4.setVisibility(View.GONE);
            containerPhoto5.setVisibility(View.GONE);
            delete1.setVisibility(View.GONE);
            delete2.setVisibility(View.GONE);
            delete3.setVisibility(View.GONE);
            delete4.setVisibility(View.GONE);
            delete5.setVisibility(View.GONE);
        } else if (photos.size() == 1) {
            photo1.setImageURI(Uri.parse(photos.get(0)));
            photo2.setImageDrawable(getResources().getDrawable(R.drawable.empty_image));
            photo3.setImageDrawable(getResources().getDrawable(R.drawable.empty_image));
            photo4.setImageDrawable(getResources().getDrawable(R.drawable.empty_image));
            photo5.setImageDrawable(getResources().getDrawable(R.drawable.empty_image));
            containerPhoto1.setVisibility(View.VISIBLE);
            containerPhoto2.setVisibility(View.GONE);
            containerPhoto3.setVisibility(View.GONE);
            containerPhoto4.setVisibility(View.GONE);
            containerPhoto5.setVisibility(View.GONE);
            delete1.setVisibility(View.VISIBLE);
            delete2.setVisibility(View.GONE);
            delete3.setVisibility(View.GONE);
            delete4.setVisibility(View.GONE);
            delete5.setVisibility(View.GONE);
        } else if (photos.size() == 2) {
            photo1.setImageURI(Uri.parse(photos.get(0)));
            photo2.setImageURI(Uri.parse(photos.get(1)));
            photo3.setImageDrawable(getResources().getDrawable(R.drawable.empty_image));
            photo4.setImageDrawable(getResources().getDrawable(R.drawable.empty_image));
            photo5.setImageDrawable(getResources().getDrawable(R.drawable.empty_image));
            containerPhoto1.setVisibility(View.VISIBLE);
            containerPhoto2.setVisibility(View.VISIBLE);
            containerPhoto3.setVisibility(View.GONE);
            containerPhoto4.setVisibility(View.GONE);
            containerPhoto5.setVisibility(View.GONE);
            delete1.setVisibility(View.VISIBLE);
            delete2.setVisibility(View.VISIBLE);
            delete3.setVisibility(View.GONE);
            delete4.setVisibility(View.GONE);
            delete5.setVisibility(View.GONE);
        } else if (photos.size() == 3) {
            photo1.setImageURI(Uri.parse(photos.get(0)));
            photo2.setImageURI(Uri.parse(photos.get(1)));
            photo3.setImageURI(Uri.parse(photos.get(2)));
            photo4.setImageDrawable(getResources().getDrawable(R.drawable.empty_image));
            photo5.setImageDrawable(getResources().getDrawable(R.drawable.empty_image));
            containerPhoto1.setVisibility(View.VISIBLE);
            containerPhoto2.setVisibility(View.VISIBLE);
            containerPhoto3.setVisibility(View.VISIBLE);
            containerPhoto4.setVisibility(View.GONE);
            containerPhoto5.setVisibility(View.GONE);
            delete1.setVisibility(View.VISIBLE);
            delete2.setVisibility(View.VISIBLE);
            delete3.setVisibility(View.VISIBLE);
            delete4.setVisibility(View.GONE);
            delete5.setVisibility(View.GONE);
        } else if (photos.size() == 4) {
            photo1.setImageURI(Uri.parse(photos.get(0)));
            photo2.setImageURI(Uri.parse(photos.get(1)));
            photo3.setImageURI(Uri.parse(photos.get(2)));
            photo4.setImageURI(Uri.parse(photos.get(3)));
            photo5.setImageDrawable(getResources().getDrawable(R.drawable.empty_image));
            containerPhoto1.setVisibility(View.VISIBLE);
            containerPhoto2.setVisibility(View.VISIBLE);
            containerPhoto3.setVisibility(View.VISIBLE);
            containerPhoto4.setVisibility(View.VISIBLE);
            containerPhoto5.setVisibility(View.GONE);
            delete1.setVisibility(View.VISIBLE);
            delete2.setVisibility(View.VISIBLE);
            delete3.setVisibility(View.VISIBLE);
            delete4.setVisibility(View.VISIBLE);
            delete5.setVisibility(View.GONE);
        } else if (photos.size() == 5) {
            photo1.setImageURI(Uri.parse(photos.get(0)));
            photo2.setImageURI(Uri.parse(photos.get(1)));
            photo3.setImageURI(Uri.parse(photos.get(2)));
            photo4.setImageURI(Uri.parse(photos.get(3)));
            photo5.setImageURI(Uri.parse(photos.get(4)));
            containerPhoto1.setVisibility(View.VISIBLE);
            containerPhoto2.setVisibility(View.VISIBLE);
            containerPhoto3.setVisibility(View.VISIBLE);
            containerPhoto4.setVisibility(View.VISIBLE);
            containerPhoto5.setVisibility(View.VISIBLE);
            delete1.setVisibility(View.VISIBLE);
            delete2.setVisibility(View.VISIBLE);
            delete3.setVisibility(View.VISIBLE);
            delete4.setVisibility(View.VISIBLE);
            delete5.setVisibility(View.VISIBLE);
            addPhoto.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.save)
    public void save() {
        if (observationText.getText().toString().isEmpty()) {
            observationText.setError(getString(R.string.error_empty_label));
            observationText.requestFocus();
            return;
        }
        if (observationText.getText().length() < 6) {
            observationText.setError(getString(R.string.error_size_10_label));
            observationText.requestFocus();
            return;
        }
        report = new SpecialReport();
        Incidence incidence = (Incidence) spinner.getSelectedItem();
        report.incidenceId = incidence.id;
        report.title = incidence.name;
        report.watchId = getPreferences().getWatch().id;
        report.watch = getPreferences().getWatch();
        report.observation = observationText.getText().toString();
        report.createDate = UTILITY.getCurrentTimestamp();
        report.updateDate = UTILITY.getCurrentTimestamp();
        report.guardId = getPreferences().getGuard().id;
        report.guardDni = getPreferences().getGuard().dni;
        report.guardName = getPreferences().getGuard().name;
        report.guardLastname = getPreferences().getGuard().lastname;
        report.sync = false;
        report.status = 1;
        report.resolved = 1;
        report.level = incidence.level;

        builderDialog.text("Enviando...");
        dialog.show();

        Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter e) throws Exception {
                Location location = CurrentLocation.get(AddReportActivity.this);
                saveReport(report, location);
                if (report.id != null)
                    savePosition(report, location);
                e.onComplete();
            }})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        onSuccess(report);
                    }
                }).isDisposed();
    }

    private void saveReport(SpecialReport report, Location location) {
        report.latitude = String.valueOf(location.getLatitude());
        report.longitude = String.valueOf(location.getLongitude());
        report.id = AppDatabase.getInstance(getApplicationContext())
                .getSpecialReportDao().insert(report);
        if (photos != null && photos.size() > 0) {
            for (String uri: photos) {
                Photo photo = new Photo(uri, Photo.LINKED.REPORT, report.id);
                AppDatabase.getInstance(getApplicationContext())
                        .getPhotoDao().insert(photo);
            }
        }
    }

    private void savePosition(SpecialReport report, Location location) {
        TabletPosition position = new TabletPosition(location, getImei());
        position.generatedTime = report.createDate;
        position.watchId = report.watchId;
        if (report.level > 1) {
            position.message = TabletPosition.MESSAGE.INCIDENCE_LEVEL_2.name();
        } else {
            position.message = TabletPosition.MESSAGE.INCIDENCE_LEVEL_1.name();
        }
        position.isException = true;
        position.alertMessage = report.observation;
        AppDatabase.getInstance(getApplicationContext())
                .getPositionDao().insert(position);
    }

    private void onSuccess(SpecialReport report) {
        if (report.id == null) {
            Snackbar.make(toolbar, "Error de guardado",
                    Snackbar.LENGTH_LONG).show();
            return;
        }
        dialog.dismiss();
        app.report = report;
        setResult(RESULT_OK, getIntent());
        finish();
    }

    @OnClick(R.id.sos_alarm)
    public void SOS() {
        dialogSOS();
    }
}

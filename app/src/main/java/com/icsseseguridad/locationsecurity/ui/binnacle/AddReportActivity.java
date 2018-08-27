package com.icsseseguridad.locationsecurity.ui.binnacle;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.gson.Gson;
import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.adapter.IncidenceSpinnerAdapter;
import com.icsseseguridad.locationsecurity.controller.BinnacleController;
import com.icsseseguridad.locationsecurity.controller.PhotoController;
import com.icsseseguridad.locationsecurity.events.OnRegisterReportFailure;
import com.icsseseguridad.locationsecurity.events.OnRegisterReportSuccess;
import com.icsseseguridad.locationsecurity.events.OnRegisterVisitFailure;
import com.icsseseguridad.locationsecurity.events.OnRegisterVisitSuccess;
import com.icsseseguridad.locationsecurity.events.OnSyncIncidences;
import com.icsseseguridad.locationsecurity.events.OnUploadPhotoFailure;
import com.icsseseguridad.locationsecurity.events.OnUploadPhotoSuccess;
import com.icsseseguridad.locationsecurity.model.Incidence;
import com.icsseseguridad.locationsecurity.model.SpecialReport;
import com.icsseseguridad.locationsecurity.ui.BaseActivity;
import com.icsseseguridad.locationsecurity.ui.PhotoActivity;
import com.icsseseguridad.locationsecurity.util.UTILITY;
import com.stfalcon.frescoimageviewer.ImageViewer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_report);
        ButterKnife.bind(this);
        setSupportActionBarBack(toolbar);
        saveButton.setTypeface(UTILITY.typefaceButton(this));
        setupPhotos();
        if (app.incidences != null)
            loadSpinner();
        else {
            new BinnacleController().getIncidences();
            builderDialog.text("Cargando...");
            dialog.show();
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void loadSpinner() {
        adapterSpinner = new IncidenceSpinnerAdapter(this,
                R.layout.spinner_item, app.incidences.incidences);
        spinner.setAdapter(adapterSpinner);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onSync(OnSyncIncidences event) {
        EventBus.getDefault().removeStickyEvent(OnSyncIncidences.class);
        if (dialog.isShowing()) {
            dialog.dismiss();
            if (!event.result) {
                Toast.makeText(this, R.string.no_connection,
                        Toast.LENGTH_LONG).show();
                finish();
            } else {
                Snackbar.make(toolbar, "Se han cargado las incidencias con exito.",
                        Snackbar.LENGTH_SHORT).show();
                loadSpinner();
            }
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
        report.observation = observationText.getText().toString();
        report.latitude = String.valueOf(getPreferences().getLastKnownLoc().getLatitude());
        report.longitude = String.valueOf(getPreferences().getLastKnownLoc().getLongitude());

        builderDialog.text("Enviando...");
        dialog.show();

        if (photos != null && photos.size() > 0) {
            saving = 0;
            photosSaved = new ArrayList<>();
            savePhotos();
        } else {
            new BinnacleController().register(report);
        }
    }

    public static int saving;
    public static List<String> photosSaved;
    public void savePhotos() {
        if (photos.size() > saving)
            new PhotoController().save(photos.get(saving));
        else {
            if (photosSaved.size() >= 1) {
                report.image1 = photosSaved.get(0);
            }
            if (photosSaved.size() >= 2) {
                report.image2 = photosSaved.get(1);
            }
            if (photosSaved.size() >= 3) {
                report.image3 = photosSaved.get(2);
            }
            if (photosSaved.size() >= 4) {
                report.image4 = photosSaved.get(3);
            }
            if (photosSaved.size() == 5) {
                report.image5 = photosSaved.get(4);
            }
            new BinnacleController().register(report);
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void uploadPhotoSuccess(OnUploadPhotoSuccess event) {
        EventBus.getDefault().removeStickyEvent(OnUploadPhotoSuccess.class);
        Log.i("OnUploadPhotoSuccess", event.url);
        saving++;
        photosSaved.add(event.url);
        savePhotos();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void uploadPhotoFailure(OnUploadPhotoFailure event) {
        EventBus.getDefault().removeStickyEvent(OnUploadPhotoFailure.class);
        dialog.dismiss();
        Snackbar.make(toolbar, event.message, Snackbar.LENGTH_LONG).show();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void registerReportSuccess(OnRegisterReportSuccess event) {
        EventBus.getDefault().removeStickyEvent(OnRegisterReportSuccess.class);
        dialog.dismiss();
        app.report = event.report;
        setResult(RESULT_OK, getIntent());
        finish();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void registerReportFailure(OnRegisterReportFailure event) {
        EventBus.getDefault().removeStickyEvent(OnRegisterReportFailure.class);
        dialog.dismiss();
        if (event.response.errors != null) {
            try {
                JSONObject jsonErrors = new JSONObject(new Gson().toJson(event.response.errors));
                Iterator<String> keys = jsonErrors.keys();
                if (keys.hasNext())
                    while(keys.hasNext()) {
                        String key = keys.next();
                        String value = (String) jsonErrors.getJSONArray(key).get(0);
                        Toast.makeText(this, key+": "+value, Toast.LENGTH_LONG).show();
                    }
                else
                    Snackbar.make(toolbar, event.response.message, Snackbar.LENGTH_LONG).show();

            } catch (Exception ex) {
                Snackbar.make(toolbar, event.response.message, Snackbar.LENGTH_LONG).show();
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

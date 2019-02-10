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
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.service.dao.AppDatabase;
import com.icsseseguridad.locationsecurity.service.entity.Photo;
import com.icsseseguridad.locationsecurity.service.entity.Company;
import com.icsseseguridad.locationsecurity.service.entity.ListCompany;
import com.icsseseguridad.locationsecurity.service.entity.Visitor;
import com.icsseseguridad.locationsecurity.util.UTILITY;
import com.icsseseguridad.locationsecurity.view.ui.PhotoActivity;
import com.icsseseguridad.locationsecurity.util.TextInputAutoCompleteTextView;
import com.icsseseguridad.locationsecurity.viewmodel.CompanyListViewModel;

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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class AddVisitorActivity extends PhotoActivity {

    private static final Integer INTENT_PHOTO = 99;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.photo) ImageView photoImage;
    @BindView(R.id.dni) EditText dniText;
    @BindView(R.id.name) EditText nameText;
    @BindView(R.id.lastname) EditText lastnameText;
    @BindView(R.id.company) TextInputAutoCompleteTextView companyText;

    private Uri photoUri;

    private CompanyListViewModel companyListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_visitor);
        ButterKnife.bind(this);
        setSupportActionBarBack(toolbar);

        companyText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
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

        companyListViewModel = ViewModelProviders
                .of(this).get(CompanyListViewModel.class);
        companyListViewModel.getCompanies().observe(this, new Observer<List<Company>>() {
            @Override
            public void onChanged(@Nullable List<Company> companies) {
                setSuggestion(companies);
            }
        });
    }

    private void setSuggestion(List<Company> companies) {
        List<String> stringList = new ArrayList<>();
        for (Company company : companies) {
            stringList.add(company.name);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, stringList);
        companyText.setThreshold(0);
        companyText.setAdapter(adapter);
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
        if (dniText.getText().toString().isEmpty()) {
            dniText.setError(getString(R.string.error_empty_label));
            dniText.requestFocus();
            return;
        }
        if (dniText.getText().toString().length() < 6) {
            dniText.setError(getString(R.string.error_size_6_label));
            dniText.requestFocus();
            return;
        }
        if (nameText.getText().toString().isEmpty()) {
            nameText.setError(getString(R.string.error_empty_label));
            nameText.requestFocus();
            return;
        }
        if (nameText.getText().toString().length() < 4) {
            nameText.setError(getString(R.string.error_size_4_label));
            nameText.requestFocus();
            return;
        }
        if (lastnameText.getText().toString().isEmpty()) {
            lastnameText.setError(getString(R.string.error_empty_label));
            lastnameText.requestFocus();
            return;
        }
        if (lastnameText.getText().toString().length() < 4) {
            lastnameText.setError(getString(R.string.error_size_4_label));
            lastnameText.requestFocus();
            return;
        }
        if (companyText.getText().toString().isEmpty()) {
            companyText.setError(getString(R.string.error_empty_label));
            companyText.requestFocus();
            return;
        }
        if (companyText.getText().toString().length() < 4) {
            companyText.setError(getString(R.string.error_size_4_label));
            companyText.requestFocus();
            return;
        }
        if (photoUri == null) {
            Snackbar.make(toolbar, R.string.error_photo, Snackbar.LENGTH_LONG).show();
            hideKeyboard();
            return;
        }
        hideKeyboard();
        final Visitor visitor = new Visitor();
        visitor.dni = dniText.getText().toString();
        visitor.name = nameText.getText().toString();
        visitor.lastname = lastnameText.getText().toString();
        visitor.company = companyText.getText().toString();
        visitor.createDate = UTILITY.getCurrentTimestamp();
        visitor.updateDate = UTILITY.getCurrentTimestamp();
        visitor.sync = false;
        visitor.active = 1;

//        if (photoUri != null) {
//            this.visitor = visitor;
//            new PhotoController().save(photoUri.toString());
//        } else {
//            VisitController visitController = new VisitController();
//            visitController.saveVisitor(visitor);
//        }
        builderDialog.text("Guardando...");
        dialog.show();

        Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter e) throws Exception {
                saveVisitor(visitor);
                e.onComplete();
            }})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        onSuccess(visitor);
                    }
                }).isDisposed();
    }

    private void saveVisitor(Visitor visitor) {
        Visitor saved = AppDatabase.getInstance(getApplicationContext())
                .getVisitorDao().findByDni(visitor.dni);
        if (saved == null) {
            visitor.id = AppDatabase.getInstance(getApplicationContext())
                    .getVisitorDao().insert(visitor);
            if (photoUri != null) {
                Photo photo = new Photo(photoUri.toString(), Photo.LINKED.VISITOR, visitor.id);
                AppDatabase.getInstance(getApplicationContext())
                        .getPhotoDao().insert(photo);
            }
        }
    }

    private void onSuccess(Visitor visitor) {
        dialog.dismiss();
        if (visitor.id == null) {
            Snackbar.make(toolbar, "Esta cedula se encuentra en uso",
                    Snackbar.LENGTH_LONG).show();
            return;
        }
        app.visitor = visitor;
        setResult(RESULT_OK, getIntent());
        finish();
    }

//    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
//    public void uploadPhotoSuccess(OnUploadPhotoSuccess event) {
//        EventBus.getDefault().removeStickyEvent(OnUploadPhotoSuccess.class);
//        visitor.photo = event.url;
//        VisitController visitController = new VisitController();
//        visitController.saveVisitor(visitor);
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
//    public void onAddVisitorSuccess(OnAddVisitorSuccess event) {
//        EventBus.getDefault().removeStickyEvent(OnAddVisitorSuccess.class);
//        dialog.dismiss();
//        app.visitor = event.visitor;
//        if (event.visitor.company != null) {
//            app.companies.companies.add(new Company(event.visitor.company));
//        }
//        setResult(RESULT_OK, getIntent());
//        finish();
//    }
//
//    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
//    public void onAddVisitorFailure(OnAddVisitorFailure event) throws JSONException {
//        EventBus.getDefault().removeStickyEvent(OnAddVisitorFailure.class);
//        dialog.dismiss();
//
//        if (event.response.errors != null) {
//            JSONObject jsonErrors = new JSONObject(new Gson().toJson(event.response.errors));
//            if (jsonErrors.has("dni")) {
//                String errorPlate = jsonErrors.getJSONArray("dni").get(0).toString();
//                dniText.setError(errorPlate);
//                dniText.requestFocus();
//            }
//            if (jsonErrors.has("name")) {
//                String errorPlate = jsonErrors.getJSONArray("name").get(0).toString();
//                nameText.setError(errorPlate);
//                nameText.requestFocus();
//            }
//            if (jsonErrors.has("lastname")) {
//                String errorPlate = jsonErrors.getJSONArray("lastname").get(0).toString();
//                lastnameText.setError(errorPlate);
//                lastnameText.requestFocus();
//            }
//            if (jsonErrors.has("company")) {
//                String errorPlate = jsonErrors.getJSONArray("company").get(0).toString();
//                companyText.setError(errorPlate);
//                companyText.requestFocus();
//            }
//        } else {
//            Snackbar.make(toolbar, event.response.message, Snackbar.LENGTH_LONG).show();
//        }
//    }

    @OnClick(R.id.sos_alarm)
    public void SOS() {
        dialogSOS();
    }
}

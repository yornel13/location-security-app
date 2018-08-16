package com.icsseseguridad.locationsecurity.ui.visit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.fxn.pix.Pix;
import com.google.gson.Gson;
import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.controller.PhotoController;
import com.icsseseguridad.locationsecurity.controller.VisitController;
import com.icsseseguridad.locationsecurity.events.OnAddClerkFailure;
import com.icsseseguridad.locationsecurity.events.OnAddClerkSuccess;
import com.icsseseguridad.locationsecurity.events.OnAddVisitorFailure;
import com.icsseseguridad.locationsecurity.events.OnAddVisitorSuccess;
import com.icsseseguridad.locationsecurity.events.OnUploadPhotoFailure;
import com.icsseseguridad.locationsecurity.events.OnUploadPhotoSuccess;
import com.icsseseguridad.locationsecurity.model.Clerk;
import com.icsseseguridad.locationsecurity.model.Visitor;
import com.icsseseguridad.locationsecurity.ui.BaseActivity;

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

public class AddClerkActivity extends BaseActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.dni) TextView dniText;
    @BindView(R.id.name) TextView nameText;
    @BindView(R.id.lastname) TextView lastnameText;
    @BindView(R.id.address) TextView addressText;

    private Clerk clerk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_clerk);
        ButterKnife.bind(this);
        setSupportActionBarBack(toolbar);

        addressText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
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
        if (addressText.getText().toString().isEmpty()) {
            addressText.setError(getString(R.string.error_empty_label));
            addressText.requestFocus();
            return;
        }
        if (addressText.getText().toString().length() < 4) {
            addressText.setError(getString(R.string.error_size_4_label));
            addressText.requestFocus();
            return;
        }
        hideKeyboard();
        Clerk clerk = new Clerk();
        clerk.dni = dniText.getText().toString();
        clerk.name = nameText.getText().toString();
        clerk.lastname = lastnameText.getText().toString();
        clerk.address = addressText.getText().toString();

        VisitController visitController = new VisitController();
        visitController.saveClerk(clerk);

        builderDialog.text("Guardando...");
        dialog.show();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onAddClerkSuccess(OnAddClerkSuccess event) {
        EventBus.getDefault().removeStickyEvent(OnAddClerkSuccess.class);
        dialog.dismiss();
        app.clerk = event.clerk;
        setResult(RESULT_OK, getIntent());
        finish();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onAddClerkFailure(OnAddClerkFailure event) throws JSONException {
        EventBus.getDefault().removeStickyEvent(OnAddClerkFailure.class);
        dialog.dismiss();

        if (event.response.errors != null) {
            JSONObject jsonErrors = new JSONObject(new Gson().toJson(event.response.errors));
            if (jsonErrors.has("dni")) {
                String errorPlate = jsonErrors.getJSONArray("dni").get(0).toString();
                dniText.setError(errorPlate);
                dniText.requestFocus();
            }
            if (jsonErrors.has("name")) {
                String errorPlate = jsonErrors.getJSONArray("name").get(0).toString();
                nameText.setError(errorPlate);
                nameText.requestFocus();
            }
            if (jsonErrors.has("lastname")) {
                String errorPlate = jsonErrors.getJSONArray("lastname").get(0).toString();
                lastnameText.setError(errorPlate);
                lastnameText.requestFocus();
            }
            if (jsonErrors.has("address")) {
                String errorPlate = jsonErrors.getJSONArray("address").get(0).toString();
                addressText.setError(errorPlate);
                addressText.requestFocus();
            }
        } else {
            Snackbar.make(toolbar, event.response.message, Snackbar.LENGTH_LONG).show();
        }
    }
}

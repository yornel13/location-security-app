package com.icsseseguridad.locationsecurity.view.ui.visit;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.service.entity.ControlVisit;
import com.icsseseguridad.locationsecurity.view.ui.PhotoActivity;
import com.icsseseguridad.locationsecurity.util.UTILITY;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddInfoVisitActivity extends PhotoActivity {

    private static final Integer INTENT_PHOTO = 99;
    private List<View> views = new ArrayList<>();
    private List<String> photos = new ArrayList<>();
    private List<String> materials = new ArrayList<>();

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.linear_materials) LinearLayout linearMaterials;
    @BindView(R.id.scroll_photos) View scrollPhotos;
    @BindView(R.id.persons) TextView personsText;
    @BindView(R.id.scroll_view) ScrollView scrollView;

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

    @BindView(R.id.add_photo) Button addPhoto;
    @BindView(R.id.add_material) Button addMaterial;

    private ControlVisit visit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_info_visit);
        ButterKnife.bind(this);
        setSupportActionBarBack(toolbar);

        addPhoto.setTypeface(UTILITY.typefaceButton(this));
        addMaterial.setTypeface(UTILITY.typefaceButton(this));

        visit = app.getNewVisit();
        setupInfo();
        setupPhotos();
    }

    public void setupInfo() {
        if (visit.getUris() != null && !visit.getUris().isEmpty()) {
            photos = visit.getUris();
        }
        if (visit.getMaterialList() != null && !visit.getMaterialList().isEmpty()) {
            for (String material : visit.getMaterialList()) {
                addMaterial(material);
            }
        }
        if (visit.persons != null) {
            personsText.setText(visit.persons.toString());
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
                photos.add(uri.toString());
                setupPhotos();
            }
        }
    }

    @OnClick(R.id.add_material)
    public void addMaterial() {
        LayoutInflater li = LayoutInflater.from(this);
        final View contentMaterial = li.inflate(R.layout.item_material, null);
        View remove = contentMaterial.findViewById(R.id.remove);
        TextView text = contentMaterial.findViewById(R.id.text);
        text.requestFocus();
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearMaterials.removeView(contentMaterial);
                views.remove(contentMaterial);
            }
        });
        linearMaterials.addView(contentMaterial);
        views.add(contentMaterial);
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }

    public void addMaterial(String material) {
        LayoutInflater li = LayoutInflater.from(this);
        final View contentMaterial = li.inflate(R.layout.item_material, null);
        View remove = contentMaterial.findViewById(R.id.remove);
        TextView text = contentMaterial.findViewById(R.id.text);
        text.requestFocus();
        text.setText(material);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearMaterials.removeView(contentMaterial);
                views.remove(contentMaterial);
            }
        });
        linearMaterials.addView(contentMaterial);
        views.add(contentMaterial);
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
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

    public void setupPhotos() {
        scrollPhotos.setVisibility(View.VISIBLE);
        addPhoto.setVisibility(View.VISIBLE);
        if (photos.size() == 0) {
            scrollPhotos.setVisibility(View.GONE);
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

    @OnClick(R.id.add_button)
    public void save() {
        materials = new ArrayList<>();
        if (personsText.getText().toString().isEmpty()) {
            personsText.setError(getString(R.string.error_empty_label));
            personsText.requestFocus();
            return;
        }
        int persons = Integer.valueOf(personsText.getText().toString());
        if (persons < 1) {
            personsText.setError("Debe ser mayor a 0");
            personsText.requestFocus();
            return;
        }

        for (View view : views) {
            TextView text = view.findViewById(R.id.text);
            if (text.getText().toString().isEmpty()) {
                text.setError(getString(R.string.error_empty_label));
                text.requestFocus();
                return;
            }
            materials.add(text.getText().toString());
        }
        JSONArray jsonArray = new JSONArray(materials);
        visit.persons = persons;
        visit.materials = jsonArray.toString();
        visit.setUris(photos);
        app.setNewVisit(visit);
        setResult(RESULT_OK, getIntent());
        finish();
    }

    @OnClick(R.id.sos_alarm)
    public void SOS() {
        dialogSOS();
    }
}

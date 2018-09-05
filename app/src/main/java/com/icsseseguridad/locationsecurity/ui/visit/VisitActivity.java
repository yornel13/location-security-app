package com.icsseseguridad.locationsecurity.ui.visit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.controller.VisitController;
import com.icsseseguridad.locationsecurity.events.OnFinishVisitFailure;
import com.icsseseguridad.locationsecurity.events.OnFinishVisitSuccess;
import com.icsseseguridad.locationsecurity.model.Clerk;
import com.icsseseguridad.locationsecurity.model.ControlVisit;
import com.icsseseguridad.locationsecurity.model.Visitor;
import com.icsseseguridad.locationsecurity.model.VisitorVehicle;
import com.icsseseguridad.locationsecurity.ui.BaseActivity;
import com.icsseseguridad.locationsecurity.util.AppDatetime;
import com.stfalcon.frescoimageviewer.ImageViewer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bumptech.glide.request.RequestOptions.centerCropTransform;

public class VisitActivity extends BaseActivity {

    public static final Integer INTENT_FINISH = 55;


    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.title) TextView titleBar;
    @BindView(R.id.scroll_view) ScrollView scrollView;
    @BindView(R.id.time) TextView timeText;

    @BindView(R.id.card_vehicle) CardView cardVehicle;
    @BindView(R.id.vehicle_photo) ImageView vehiclePhoto;
    @BindView(R.id.vehicle_plate) TextView vehiclePlate;
    @BindView(R.id.vehicle_vehicle) TextView vehicleVehicle;
    @BindView(R.id.vehicle_model) TextView vehicleModel;
    @BindView(R.id.vehicle_type) TextView vehicleType;

    @BindView(R.id.card_visitor) CardView cardVisitor;
    @BindView(R.id.visitor_photo) ImageView visitorPhoto;
    @BindView(R.id.visitor_dni) TextView visitorDni;
    @BindView(R.id.visitor_name) TextView visitorName;
    @BindView(R.id.visitor_company) TextView visitorCompany;

    @BindView(R.id.card_clerk) CardView cardClerk;
    @BindView(R.id.clerk_photo) ImageView clerkPhoto;
    @BindView(R.id.clerk_dni) TextView clerkDni;
    @BindView(R.id.clerk_name) TextView clerkName;
    @BindView(R.id.clerk_address) TextView clerkAddress;

    @BindView(R.id.card_info) CardView cardInfo;
    @BindView(R.id.persons) TextView personsText;
    @BindView(R.id.date) TextView dateText;
    @BindView(R.id.materials) TextView materialsText;
    @BindView(R.id.photo_1) ImageView photo1;
    @BindView(R.id.photo_2) ImageView photo2;
    @BindView(R.id.photo_3) ImageView photo3;
    @BindView(R.id.photo_4) ImageView photo4;
    @BindView(R.id.photo_5) ImageView photo5;
    @BindView(R.id.photos_container) View photosContainer;

    @BindView(R.id.add_button) FloatingActionButton finishButton;

    private ControlVisit visit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_visit);
        ButterKnife.bind(this);
        setSupportActionBarBack(toolbar);
        titleBar.setText("Visita");

        this.visit = app.visit;
        setupVehicle(app.visit.vehicle);
        setupVisitor(app.visit.visitor);
        setupClerk(app.visit.clerk);
        setupMaterials(app.visit);
        finishButton.setImageDrawable(getResources().getDrawable(R.drawable.icon_exit));
    }

    public void setupVehicle(VisitorVehicle vehicle) {
        cardVehicle.setCardBackgroundColor(getResources().getColor(R.color.white));
        if (vehicle != null) {
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.vehicle_empty)
                    .error(R.drawable.vehicle_empty);
            if (vehicle.photo != null)
                Glide.with(this)
                        .setDefaultRequestOptions(options)
                        .load(vehicle.photo)
                        .into(vehiclePhoto);
            vehiclePlate.setText(vehicle.plate);
            vehicleVehicle.setText(vehicle.vehicle);
            vehicleModel.setText(vehicle.model);
            vehicleType.setText(vehicle.type);
        } else {
            cardVehicle.setVisibility(View.GONE);
        }
    }

    public void setupVisitor(Visitor visitor) {
        cardVisitor.setCardBackgroundColor(getResources().getColor(R.color.white));
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.user_empty)
                .error(R.drawable.user_empty);
        if (visitor.photo != null)
            Glide.with(this)
                    .setDefaultRequestOptions(options)
                    .load(visitor.photo)
                    .into(visitorPhoto);
        visitorDni.setText(visitor.dni);
        visitorName.setText(visitor.getFullname());
        visitorCompany.setText(visitor.company);
    }

    public void setupClerk(Clerk clerk) {
        cardClerk.setCardBackgroundColor(getResources().getColor(R.color.white));
        if (clerk != null) {
            clerkPhoto.setImageDrawable(getDrawable(R.drawable.admin_icon));
            clerkPhoto.setImageTintList(null);
            clerkDni.setText(clerk.dni);
            clerkName.setText(clerk.getFullname());
            clerkAddress.setText(clerk.address);
        } else {
            cardClerk.setVisibility(View.GONE);
        }
    }

    public void setupMaterials(ControlVisit visit) {
        timeText.setText("Entrada el "+AppDatetime.longDatetime(visit.createDate.getTime()));
        cardInfo.setCardBackgroundColor(getResources().getColor(R.color.white));
        personsText.setText(visit.persons.toString()+(visit.persons==1?" persona":" personas"));
        dateText.setText(DateUtils.getRelativeTimeSpanString(visit.createDate.getTime()));
        List<String> listMaterials = visit.getMaterialList();
        if (listMaterials == null || listMaterials.isEmpty()) {
            materialsText.setVisibility(View.GONE);
        } else {
            String materialString = "\nMateriales:";
            for (String material : listMaterials) {
                materialString += "\n   * "+material;
            }
            materialsText.setText(materialString);
        }
        if (visit.image1 != null && !visit.image1.isEmpty()) {
            setImage(photo1, visit.image1);
            photo1.setVisibility(View.VISIBLE);
        } else {
            photo1.setImageDrawable(getDrawable(R.drawable.empty_image));
            photo1.setVisibility(View.GONE);
        }
        if (visit.image2 != null && !visit.image2.isEmpty()) {
            setImage(photo2, visit.image2);
            photo2.setVisibility(View.VISIBLE);
        } else {
            photo1.setImageDrawable(getDrawable(R.drawable.empty_image));
            photo2.setVisibility(View.GONE);
        }
        if (visit.image3 != null && !visit.image3.isEmpty()) {
            setImage(photo3, visit.image3);
            photo3.setVisibility(View.VISIBLE);
        } else {
            photo1.setImageDrawable(getDrawable(R.drawable.empty_image));
            photo3.setVisibility(View.GONE);
        }
        if (visit.image4 != null  && !visit.image4.isEmpty()) {
            setImage(photo4, visit.image4);
            photo4.setVisibility(View.VISIBLE);
        } else {
            photo1.setImageDrawable(getDrawable(R.drawable.empty_image));
            photo4.setVisibility(View.GONE);
        }
        if (visit.image5 != null  && !visit.image5.isEmpty()) {
            setImage(photo5, visit.image5);
            photo5.setVisibility(View.VISIBLE);
        } else {
            photo1.setImageDrawable(getDrawable(R.drawable.empty_image));
            photo5.setVisibility(View.GONE);
        }
        if ((visit.image1 == null || visit.image1.isEmpty())
                && (visit.image2 == null || visit.image2.isEmpty())
                && (visit.image3 == null || visit.image3.isEmpty())
                && (visit.image4 == null || visit.image4.isEmpty())
                && (visit.image5 == null || visit.image5.isEmpty())) {
            photosContainer.setVisibility(View.GONE);
        } else {
            photosContainer.setVisibility(View.VISIBLE);
        }
    }

    private void setImage(ImageView imageView, String url) {
        Glide.with(this)
                .load(url)
                .apply(centerCropTransform()
                        .placeholder(R.drawable.empty_image)
                        .error(R.drawable.empty_image))
                .into(imageView);
    }

    @OnClick(R.id.visitor_photo)
    public void clickVisitorPhoto() {
        if (visit.visitor != null && visit.visitor.photo != null && !visit.visitor.photo.isEmpty())
            new ImageViewer.Builder(this, new ArrayList(Collections
                    .singleton(visit.visitor.photo)))
                    .setStartPosition(0)
                    .show();
    }

    @OnClick(R.id.vehicle_photo)
    public void clickVehiclePhoto() {
        if (visit.vehicle != null && visit.vehicle.photo != null && !visit.vehicle.photo.isEmpty())
            new ImageViewer.Builder(this, new ArrayList(Collections
                    .singleton(visit.vehicle.photo)))
                    .setStartPosition(0)
                    .show();
    }

    @OnClick(R.id.photo_1)
    public void clickPhoto1() {
        new ImageViewer.Builder(this, getPhotos())
                .setStartPosition(0)
                .show();
    }

    @OnClick(R.id.photo_2)
    public void clickPhoto2() {
        new ImageViewer.Builder(this, getPhotos())
                .setStartPosition(1)
                .show();
    }

    @OnClick(R.id.photo_3)
    public void clickPhoto3() {
        new ImageViewer.Builder(this, getPhotos())
                .setStartPosition(2)
                .show();
    }

    @OnClick(R.id.photo_4)
    public void clickPhoto4() {
        new ImageViewer.Builder(this, getPhotos())
                .setStartPosition(3)
                .show();
    }

    @OnClick(R.id.photo_5)
    public void clickPhoto5() {
        new ImageViewer.Builder(this, getPhotos())
                .setStartPosition(4)
                .show();
    }

    public List<String> getPhotos() {
        List<String> photos = new ArrayList<>();
        if (visit.image1 != null && !visit.image1.isEmpty()) {
            photos.add(visit.image1);
        }
        if (visit.image2 != null && !visit.image2.isEmpty()) {
            photos.add(visit.image2);
        }
        if (visit.image3 != null && !visit.image3.isEmpty()) {
            photos.add(visit.image3);
        }
        if (visit.image4 != null && !visit.image4.isEmpty()) {
            photos.add(visit.image4);
        }
        if (visit.image5 != null && !visit.image5.isEmpty()) {
            photos.add(visit.image5);
        }
        return photos;
    }

    @OnClick(R.id.add_button)
    public void onFinish() {
        Intent intent = new Intent(this, FinishVisitActivity.class);
        intent.putExtra(ControlVisit.class.getName(), gson().toJson(visit));
        startActivityForResult(intent, INTENT_FINISH);
    }

    @OnClick(R.id.sos_alarm)
    public void SOS() {
        dialogSOS();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_FINISH && resultCode == RESULT_OK) {
            setResult(RESULT_OK, getIntent());
            finish();
        }
    }
}

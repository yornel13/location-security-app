package com.icsseseguridad.locationsecurity.view.ui.visit;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.service.dao.AppDatabase;
import com.icsseseguridad.locationsecurity.service.entity.Photo;
import com.icsseseguridad.locationsecurity.service.repository.PhotoController;
import com.icsseseguridad.locationsecurity.service.repository.VisitController;
import com.icsseseguridad.locationsecurity.view.dialog.ClerkSearchDialog;
import com.icsseseguridad.locationsecurity.view.dialog.VehicleSearchDialog;
import com.icsseseguridad.locationsecurity.view.dialog.VisitorSearchDialog;
import com.icsseseguridad.locationsecurity.service.event.OnRegisterVisitFailure;
import com.icsseseguridad.locationsecurity.service.event.OnRegisterVisitSuccess;
import com.icsseseguridad.locationsecurity.service.event.OnSyncClerks;
import com.icsseseguridad.locationsecurity.service.event.OnSyncVehicles;
import com.icsseseguridad.locationsecurity.service.event.OnSyncVisitors;
import com.icsseseguridad.locationsecurity.service.event.OnUploadPhotoFailure;
import com.icsseseguridad.locationsecurity.service.event.OnUploadPhotoSuccess;
import com.icsseseguridad.locationsecurity.service.entity.Clerk;
import com.icsseseguridad.locationsecurity.service.entity.ControlVisit;
import com.icsseseguridad.locationsecurity.service.entity.Visitor;
import com.icsseseguridad.locationsecurity.service.entity.VisitorVehicle;
import com.icsseseguridad.locationsecurity.view.ui.BaseActivity;
import com.icsseseguridad.locationsecurity.util.AppDatetime;
import com.icsseseguridad.locationsecurity.viewmodel.ClerkListViewModel;
import com.icsseseguridad.locationsecurity.viewmodel.VehicleListViewModel;
import com.icsseseguridad.locationsecurity.viewmodel.VisitListViewModel;
import com.icsseseguridad.locationsecurity.viewmodel.VisitorListViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

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
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;

public class AddVisitActivity extends BaseActivity {

    public static final int INTENT_ADD_VEHICLE = 1;
    public static final int INTENT_ADD_VISITOR = 2;
    public static final int INTENT_ADD_CLERK = 3;
    public static final int INTENT_ADD_MATERIAL = 4;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.scroll_view) ScrollView scrollView;
    @BindView(R.id.time) TextView timeText;

    @BindView(R.id.card_vehicle) CardView cardVehicle;
    @BindView(R.id.vehicle_photo) ImageView vehiclePhoto;
    @BindView(R.id.vehicle_plate) TextView vehiclePlate;
    @BindView(R.id.vehicle_vehicle) TextView vehicleVehicle;
    @BindView(R.id.vehicle_model) TextView vehicleModel;
    @BindView(R.id.vehicle_type) TextView vehicleType;
    @BindView(R.id.remove_vehicle) View removeVehicle;

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
    @BindView(R.id.photos_container) View photosContainer;
    @BindView(R.id.photo_1) ImageView photo1;
    @BindView(R.id.photo_2) ImageView photo2;
    @BindView(R.id.photo_3) ImageView photo3;
    @BindView(R.id.photo_4) ImageView photo4;
    @BindView(R.id.photo_5) ImageView photo5;

    private ControlVisit visit;
    private Visitor visitor;
    private VisitorVehicle vehicle;
    private Clerk clerk;

    private VehicleListViewModel vehicleListViewModel;
    private VisitorListViewModel visitorListViewModel;
    private ClerkListViewModel clerkListViewModel;

    private ArrayList<VisitorVehicle> visitorVehicles = new ArrayList<>();
    private ArrayList<Visitor> visitors = new ArrayList<>();
    private ArrayList<Clerk> clerks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_visit);
        ButterKnife.bind(this);
        setSupportActionBarBack(toolbar);
        if (savedInstanceState == null) {
            visit = new ControlVisit();
            app.setNewVisit(visit);
        } else {
            visit = app.getNewVisit();
            if (visit == null) {
                visit = new ControlVisit();
            }
        }
        setTime();
        getLists();
    }

    void getLists() {
        vehicleListViewModel = ViewModelProviders
                .of(this).get(VehicleListViewModel.class);
        vehicleListViewModel.getVehicles().observe(this, new Observer<List<VisitorVehicle>>() {
            @Override
            public void onChanged(@Nullable final List<VisitorVehicle> vehicles) {
                VisitorVehicle addVehicle = new VisitorVehicle();
                addVehicle.plate = "Crear Vehiculo...";
                vehicles.add(0, addVehicle);
                AddVisitActivity.this.visitorVehicles.clear();
                AddVisitActivity.this.visitorVehicles.addAll(vehicles);
            }
        });

        visitorListViewModel = ViewModelProviders
                .of(this).get(VisitorListViewModel.class);
        visitorListViewModel.getVisitors().observe(this, new Observer<List<Visitor>>() {
            @Override
            public void onChanged(@Nullable final List<Visitor> visitors) {
                Visitor addVisitor = new Visitor();
                addVisitor.dni = "Crear Visitante...";
                visitors.add(0, addVisitor);
                AddVisitActivity.this.visitors.clear();
                AddVisitActivity.this.visitors.addAll(visitors);
            }
        });

        clerkListViewModel = ViewModelProviders
                .of(this).get(ClerkListViewModel.class);
        clerkListViewModel.getClerks().observe(this, new Observer<List<Clerk>>() {
            @Override
            public void onChanged(@Nullable final List<Clerk> clerks) {
                AddVisitActivity.this.clerks.clear();
                AddVisitActivity.this.clerks.addAll(clerks);
            }
        });
    }

    @OnClick(R.id.card_vehicle)
    public void selectVehicle() {
        new VehicleSearchDialog<>(this, getString(R.string.select_vehicle),
                getString(R.string.search_vehicle), null, visitorVehicles,
                new SearchResultListener<VisitorVehicle>() {
                    @Override
                    public void onSelected(BaseSearchDialogCompat dialog,
                                           VisitorVehicle item, int position) {
                        if (item.id != null)
                            setupVehicle(item);
                        else
                            startActivityForResult(new Intent(AddVisitActivity.this,
                                    AddVehicleActivity.class), INTENT_ADD_VEHICLE);
                        dialog.dismiss();
                    }
                }).show();
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onSyncVehicles(OnSyncVehicles event) {
//        dialog.dismiss();
//        if (!event.result) {
//            Toast.makeText(this, R.string.no_connection,
//                    Toast.LENGTH_LONG).show();
//            finish();
//        } else {
//            Snackbar.make(toolbar, "Se han cargado los vehiculos con exito.",
//                    Snackbar.LENGTH_SHORT).show();
//        }
//    }

    @OnClick(R.id.card_visitor)
    public void selectVisitor() {
        new VisitorSearchDialog<>(this, getString(R.string.select_visitor),
                getString(R.string.search_visitor), null, visitors,
                new SearchResultListener<Visitor>() {
                    @Override
                    public void onSelected(BaseSearchDialogCompat dialog,
                                           Visitor item, int position) {
                        if (item.id != null)
                            setupVisitor(item);
                        else
                            startActivityForResult(new Intent(AddVisitActivity.this,
                                    AddVisitorActivity.class), INTENT_ADD_VISITOR);
                        dialog.dismiss();
                    }
                }).show();
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onSyncVisitors(OnSyncVisitors event) {
//        dialog.dismiss();
//        if (!event.result) {
//            Toast.makeText(this, R.string.no_connection,
//                    Toast.LENGTH_LONG).show();
//            finish();
//        } else {
//            Snackbar.make(toolbar, "Se han cargado los visitante con exito.",
//                    Snackbar.LENGTH_SHORT).show();
//        }
//    }

    @OnClick(R.id.card_clerk)
    public void selectClerk() {
        new ClerkSearchDialog<>(this, getString(R.string.select_clerk),
                getString(R.string.search_clerk), null, clerks,
                new SearchResultListener<Clerk>() {
                    @Override
                    public void onSelected(BaseSearchDialogCompat dialog,
                                           Clerk item, int position) {
                        if (item.id != null)
                            setupClerk(item);
                        dialog.dismiss();
                    }
                }).show();
    }

    @OnClick(R.id.card_info)
    public void selectInfo() {
        startActivityForResult(new Intent(AddVisitActivity.this,
                AddInfoVisitActivity.class), INTENT_ADD_MATERIAL);

    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onSyncClerks(OnSyncClerks event) {
//        dialog.dismiss();
//        if (!event.result) {
//            Toast.makeText(this, R.string.no_connection,
//                    Toast.LENGTH_LONG).show();
//            finish();
//        } else {
//            Snackbar.make(toolbar, "Se han cargado los funcionarios con exito.",
//                    Snackbar.LENGTH_SHORT).show();
//        }
//    }

    public void setupVehicle(final VisitorVehicle vehicle) {
        cardVehicle.setCardBackgroundColor(getResources().getColor(R.color.white));
        if (vehicle.sync) {
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.vehicle_empty)
                    .error(R.drawable.vehicle_empty);
            Glide.with(this)
                    .setDefaultRequestOptions(options)
                    .load(vehicle.photo)
                    .into(vehiclePhoto);

        } else {
            Single.create(new SingleOnSubscribe<Photo>() {
                @Override
                public void subscribe(SingleEmitter<Photo> e) throws Exception {
                    Photo photo = AppDatabase.getInstance(getApplicationContext())
                            .getPhotoDao()
                            .getBy(vehicle.id, Photo.LINKED.VEHICLE.name());
                    e.onSuccess(photo);
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new SingleObserver<Photo>() {
                @Override
                public void onSubscribe(Disposable d) { }

                @Override
                public void onError(Throwable e) { }

                @Override
                public void onSuccess(Photo photo) {
                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .placeholder(R.drawable.vehicle_empty)
                            .error(R.drawable.vehicle_empty);
                    Glide.with(AddVisitActivity.this)
                            .setDefaultRequestOptions(options)
                            .load(photo.uri)
                            .into(vehiclePhoto);
                }
            });
        }
        vehiclePlate.setText(vehicle.plate);
        vehicleVehicle.setText(vehicle.vehicle);
        vehicleModel.setText(vehicle.model);
        vehicleType.setText(vehicle.type);
        setTime();
        this.vehicle = vehicle;

        if (vehicle.lastVisit != null) {
            System.out.println(gson().toJson(vehicle.lastVisit));
            for (Visitor visitor : app.getVisitors().visitors) {
                if (visitor.id.longValue() == vehicle.lastVisit.visitorId.longValue()) {
                    setupVisitor(visitor);
                }
            }
            for (Clerk clerk : app.getClerks().clerks) {
                if (clerk.id.longValue() == vehicle.lastVisit.clerkId.longValue()) {
                    setupClerk(clerk);
                }
            }
            Toast.makeText(this, "Autocompletado", Toast.LENGTH_SHORT).show();
            scrollView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            }, 500);
        }
        removeVehicle.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.remove_vehicle)
    public void removeVehicle() {
        visit.vehicle = null;
        this.vehicle = null;
        vehiclePlate.setText("Place");
        vehicleVehicle.setText("Vehiculo");
        vehicleModel.setText("Modelo");
        vehicleType.setText("Color");
        cardVehicle.setCardBackgroundColor(getResources().getColor(R.color.colorGrayAgate));
        vehiclePhoto.setImageDrawable(getResources().getDrawable(R.drawable.vehicle_empty));
        removeVehicle.setVisibility(View.GONE);
    }

    public void setupVisitor(final Visitor visitor) {
        cardVisitor.setCardBackgroundColor(getResources().getColor(R.color.white));
        if (visitor.sync) {
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.user_empty)
                    .error(R.drawable.user_empty);
            Glide.with(this)
                    .setDefaultRequestOptions(options)
                    .load(visitor.photo)
                    .into(visitorPhoto);
        } else {
            Single.create(new SingleOnSubscribe<Photo>() {
                @Override
                public void subscribe(SingleEmitter<Photo> e) throws Exception {
                    Photo photo = AppDatabase.getInstance(getApplicationContext())
                            .getPhotoDao()
                            .getBy(visitor.id, Photo.LINKED.VISITOR.name());
                    e.onSuccess(photo);
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new SingleObserver<Photo>() {
                @Override
                public void onSubscribe(Disposable d) { }

                @Override
                public void onError(Throwable e) { }

                @Override
                public void onSuccess(Photo photo) {
                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .placeholder(R.drawable.user_empty)
                            .error(R.drawable.user_empty);
                    Glide.with(AddVisitActivity.this)
                            .setDefaultRequestOptions(options)
                            .load(photo.uri)
                            .into(visitorPhoto);
                }
            });
        }
        visitorDni.setText(visitor.dni);
        visitorName.setText(visitor.getFullname());
        visitorCompany.setText(visitor.company);
        setTime();
        this.visitor = visitor;
    }

    public void setupClerk(Clerk clerk) {
        cardClerk.setCardBackgroundColor(getResources().getColor(R.color.white));
        clerkPhoto.setImageDrawable(getDrawable(R.drawable.admin_icon));
        clerkPhoto.setImageTintList(null);
        clerkDni.setText(clerk.dni);
        clerkName.setText(clerk.getFullname());
        clerkAddress.setText(clerk.address);
        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 500);
        setTime();
        this.clerk = clerk;
    }

    public void setupMaterials(ControlVisit visit) {
        cardInfo.setCardBackgroundColor(getResources().getColor(R.color.white));
        personsText.setText(visit.persons.toString()+(visit.persons==1?" persona":" personas"));
        List<String> listMaterials = visit.getMaterialList();
        if (listMaterials.isEmpty()) {
            materialsText.setVisibility(View.GONE);
        } else {
            materialsText.setVisibility(View.VISIBLE);
            String materialString = "Materiales:";
            for (String material : listMaterials) {
                materialString += "\n   * "+material;
            }
            materialsText.setText(materialString);
        }
        if (visit.getUris().size() > 0) {
            photo1.setImageURI(Uri.parse(visit.getUris().get(0)));
            photo1.setVisibility(View.VISIBLE);
            photosContainer.setVisibility(View.VISIBLE);
        } else {
            photo1.setImageURI(null);
            photo1.setVisibility(View.GONE);
            photosContainer.setVisibility(View.GONE);
        }
        if (visit.getUris().size() > 1) {
            photo2.setImageURI(Uri.parse(visit.getUris().get(1)));
            photo2.setVisibility(View.VISIBLE);
        } else {
            photo2.setImageURI(null);
            photo2.setVisibility(View.GONE);
        }
        if (visit.getUris().size() > 2) {
            photo3.setImageURI(Uri.parse(visit.getUris().get(2)));
            photo3.setVisibility(View.VISIBLE);
        } else {
            photo3.setImageURI(null);
            photo3.setVisibility(View.GONE);
        }
        if (visit.getUris().size() > 3) {
            photo4.setImageURI(Uri.parse(visit.getUris().get(3)));
            photo4.setVisibility(View.VISIBLE);
        } else {
            photo4.setImageURI(null);
            photo4.setVisibility(View.GONE);
        }
        if (visit.getUris().size() > 4) {
            photo5.setImageURI(Uri.parse(visit.getUris().get(4)));
            photo5.setVisibility(View.VISIBLE);
        } else {
            photo5.setImageURI(null);
            photo5.setVisibility(View.GONE);
        }
        setTime();
        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 500);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case INTENT_ADD_VEHICLE:
                    VisitorVehicle vehicle = app.vehicle;
                    visitorVehicles.add(vehicle);
                    setupVehicle(vehicle);
                    break;
                case INTENT_ADD_VISITOR:
                    Visitor visitor = app.visitor;
                    visitors.add(visitor);
                    setupVisitor(visitor);
                    break;
                case INTENT_ADD_MATERIAL:
                    setupMaterials(app.getNewVisit());
                    break;
            }
        }
    }

    @OnClick(R.id.add_button)
    public void save() {
        if (clerk == null) {
            cardClerk.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryVeryLight));
            Snackbar.make(toolbar, "Debes seleccionar a quien se visita", Snackbar.LENGTH_LONG).show();
            cardClerk.requestFocus();
            return;
        }
        if (visitor == null) {
            cardVisitor.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryVeryLight));
            Snackbar.make(toolbar, "Debes seleccionar un visitante", Snackbar.LENGTH_LONG).show();
            cardVisitor.requestFocus();
            return;
        }
        if (visit.persons <= 0) {
            cardInfo.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryVeryLight));
            Snackbar.make(toolbar, "Debes ingresar la informacion de la visita", Snackbar.LENGTH_LONG).show();
            cardInfo.requestFocus();
            return;
        }
        hideKeyboard();
        if (vehicle != null)
            visit.vehicleId = vehicle.id;
        visit.visitorId = visitor.id;
        visit.latitude = String.valueOf(getPreferences().getLastKnownLoc().getLatitude());
        visit.longitude = String.valueOf(getPreferences().getLastKnownLoc().getLongitude());
        visit.clerkId = clerk.id;
        visit.guardId = getPreferences().getGuard().id;
        visit.createDate = new Timestamp(new Date().getTime());
        visit.finishDate = null;
        visit.sync = false;
        visit.status = 1;
        visit.vehicle = vehicle;
        visit.visitor = visitor;
        visit.clerk = clerk;

        builderDialog.text("Guardando...");
        dialog.show();

        Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter e) throws Exception {
                saveVisit(visit);
                e.onComplete();
            }})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        onSuccess(visit);
                    }
                }).isDisposed();
//        if (visit.getUris() != null && visit.getUris().size() > 0) {
//            saving = 0;
//            photosSaved = new ArrayList<>();
//            savePhotos();
//        } else {
//            VisitController visitController = new VisitController();
//            visitController.register(visit);
//        }
    }

    private void saveVisit(ControlVisit visit) {
        visit.id = AppDatabase.getInstance(getApplicationContext())
                .getControlVisitDao().insert(visit);
        if (visit.getUris() != null && visit.getUris().size() > 0) {
            for (String uri: visit.getUris()) {
                Photo photo = new Photo(uri, Photo.LINKED.MATERIAL, visit.id);
                AppDatabase.getInstance(getApplicationContext())
                        .getPhotoDao().insert(photo);
            }
        }
    }

    private void onSuccess(ControlVisit visit) {
        if (visit.id == null) {
            Snackbar.make(toolbar, "Error de guardado",
                    Snackbar.LENGTH_LONG).show();
            return;
        }
        dialog.dismiss();
        app.visit = visit;
        app.setNewVisit(null);
        setResult(RESULT_OK, getIntent());
        finish();
    }

//    public static int saving;
//    public static List<String> photosSaved;
//    public void savePhotos() {
//        if (visit.getUris().size() > saving)
//            new PhotoController().save(visit.getUris().get(saving));
//        else {
//            if (photosSaved.size() >= 1) {
//                visit.image1 = photosSaved.get(0);
//            }
//            if (photosSaved.size() >= 2) {
//                visit.image2 = photosSaved.get(1);
//            }
//            if (photosSaved.size() >= 3) {
//                visit.image3 = photosSaved.get(2);
//            }
//            if (photosSaved.size() >= 4) {
//                visit.image4 = photosSaved.get(3);
//            }
//            if (photosSaved.size() == 5) {
//                visit.image5 = photosSaved.get(4);
//            }
//            VisitController visitController = new VisitController();
//            visitController.register(visit);
//        }
//
//    }
//
//    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
//    public void uploadPhotoSuccess(OnUploadPhotoSuccess event) {
//        EventBus.getDefault().removeStickyEvent(OnUploadPhotoSuccess.class);
//        Log.i("OnUploadPhotoSuccess", event.url);
//        saving++;
//        photosSaved.add(event.url);
//        savePhotos();
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
//    public void registerVisitSuccess(OnRegisterVisitSuccess event) {
//        EventBus.getDefault().removeStickyEvent(OnRegisterVisitSuccess.class);
//        dialog.dismiss();
//        app.visit = event.controlVisit;
//        app.setNewVisit(null);
//        setResult(RESULT_OK, getIntent());
//        finish();
//    }
//
//    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
//    public void registerVisitFailure(OnRegisterVisitFailure event) {
//        EventBus.getDefault().removeStickyEvent(OnRegisterVisitFailure.class);
//        dialog.dismiss();
//        if (event.response.errors != null) {
//            try {
//                JSONObject jsonErrors = new JSONObject(new Gson().toJson(event.response.errors));
//                if (jsonErrors.has("visitor_id")) {
//                    Toast.makeText(this, "Visitante no encontrado", Toast.LENGTH_LONG).show();
//                    cardVisitor.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryVeryLight));
//                    cardInfo.requestFocus();
//                }
//                if (jsonErrors.has("vehicle_id")) {
//                    Toast.makeText(this, "Vehiculo no encontrado", Toast.LENGTH_LONG).show();
//                    cardVisitor.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryVeryLight));
//                    cardInfo.requestFocus();
//                }
//                if (jsonErrors.has("visited_id")) {
//                    Toast.makeText(this, "Funcionario no encontrado", Toast.LENGTH_LONG).show();
//                    cardVisitor.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryVeryLight));
//                    cardInfo.requestFocus();
//                }
//                if (jsonErrors.has("guard_id")) {
//                    Toast.makeText(this, "Al parecer no estas autorizado para este registro.",
//                            Toast.LENGTH_LONG).show();
//                    finish();
//                }
//            } catch (Exception ex) {
//                Snackbar.make(toolbar, event.response.message, Snackbar.LENGTH_LONG).show();
//            }
//        } else {
//            Snackbar.make(toolbar, event.response.message, Snackbar.LENGTH_LONG).show();
//        }
//    }

    public void setTime() {
        timeText.setText("Entrada el "+AppDatetime.longDatetime(System.currentTimeMillis()));
        if (visit != null && visit.persons != null)
            dateText.setText(AppDatetime.longTime(System.currentTimeMillis()));
    }

    @OnClick(R.id.sos_alarm)
    public void SOS() {
        dialogSOS();
    }
}

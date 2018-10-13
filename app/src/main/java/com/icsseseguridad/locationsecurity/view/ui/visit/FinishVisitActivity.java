package com.icsseseguridad.locationsecurity.view.ui.visit;

import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.service.dao.AppDatabase;
import com.icsseseguridad.locationsecurity.service.event.OnFinishVisitFailure;
import com.icsseseguridad.locationsecurity.service.event.OnFinishVisitSuccess;
import com.icsseseguridad.locationsecurity.service.entity.ControlVisit;
import com.icsseseguridad.locationsecurity.util.CurrentLocation;
import com.icsseseguridad.locationsecurity.util.UTILITY;
import com.icsseseguridad.locationsecurity.view.ui.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Observable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.subscribers.SinglePostCompleteSubscriber;
import io.reactivex.schedulers.Schedulers;

public class FinishVisitActivity extends BaseActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.comment) TextView commentText;

    private ControlVisit visit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_visit);
        ButterKnife.bind(this);
        setSupportActionBarBack(toolbar);
        visit = gson().fromJson(getIntent()
                .getStringExtra(ControlVisit.class.getName()), ControlVisit.class);

        commentText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
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
        String comment = null;
        if (!commentText.getText().toString().trim().equals("")) {
            comment = commentText.getText().toString();
        }
        builderDialog.text("Guardando...");
        dialog.show();
        // new VisitController().finish(visit.id, comment);
        visit.status = 0;
        visit.sync = false;
        visit.comment = comment;
        visit.guardOutId = getPreferences().getWatch().guardId;
        visit.finishDate = UTILITY.longToString(new Date().getTime());

        Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter e) throws Exception {
                Location location = CurrentLocation.get(FinishVisitActivity.this);
                saveFinishedVisit(visit, location);
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        dialog.dismiss();
                        setResult(RESULT_OK, getIntent());
                        finish();
                    }
                }).isDisposed();
    }

    public void saveFinishedVisit(ControlVisit visit, Location location) {
        visit.fLatitude = String.valueOf(location.getLatitude());
        visit.fLongitude = String.valueOf(location.getLongitude());

        try {
            AppDatabase.getInstance(getApplicationContext())
                    .getControlVisitDao().update(visit);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onFinishVisitSuccess(OnFinishVisitSuccess event) {
        EventBus.getDefault().removeStickyEvent(OnFinishVisitSuccess.class);
        dialog.dismiss();
        setResult(RESULT_OK, getIntent());
        finish();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onFinishVisitFailure(OnFinishVisitFailure event) {
        EventBus.getDefault().removeStickyEvent(OnFinishVisitFailure.class);
        dialog.dismiss();
        final Snackbar snackbar = Snackbar.make(toolbar, event.response.message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    @OnClick(R.id.sos_alarm)
    public void SOS() {
        dialogSOS();
    }
}

package com.icsseseguridad.locationsecurity.ui.visit;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.controller.VisitController;
import com.icsseseguridad.locationsecurity.events.OnFinishVisitFailure;
import com.icsseseguridad.locationsecurity.events.OnFinishVisitSuccess;
import com.icsseseguridad.locationsecurity.model.ControlVisit;
import com.icsseseguridad.locationsecurity.ui.BaseActivity;
import com.icsseseguridad.locationsecurity.ui.PhotoActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
        new VisitController().finish(visit.id, comment);
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

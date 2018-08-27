package com.icsseseguridad.locationsecurity.ui.binnacle;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.adapter.ReplyAdapter;
import com.icsseseguridad.locationsecurity.controller.BinnacleController;
import com.icsseseguridad.locationsecurity.events.OnGetRepliesFailure;
import com.icsseseguridad.locationsecurity.events.OnGetRepliesSuccess;
import com.icsseseguridad.locationsecurity.events.OnPostReplyFailure;
import com.icsseseguridad.locationsecurity.events.OnPostReplySuccess;
import com.icsseseguridad.locationsecurity.model.Reply;
import com.icsseseguridad.locationsecurity.model.SpecialReport;
import com.icsseseguridad.locationsecurity.ui.BaseActivity;
import com.icsseseguridad.locationsecurity.util.AppDatetime;
import com.stfalcon.frescoimageviewer.ImageViewer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bumptech.glide.request.RequestOptions.centerCropTransform;

public class ReportActivity extends BaseActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
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

    @BindView(R.id.incidence) EditText incidenceText;
    @BindView(R.id.observation) EditText observationText;
    @BindView(R.id.time) TextView timeText;
    @BindView(R.id.recycler_list) RecyclerView recyclerView;
    @BindView(R.id.comment_size) TextView commentSizeText;
    @BindView(R.id.comment_layout) View commentLayout;
    @BindView(R.id.comment_loading) View commentLoading;
    @BindView(R.id.messing_area) View messingArea;
    @BindView(R.id.new_message) EditText messageText;
    @BindView(R.id.send) View messageSend;
    @BindView(R.id.sending) View messageSending;

    private SpecialReport report;
    private List<Reply> replies;
    private ReplyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ButterKnife.bind(this);
        setSupportActionBarBack(toolbar);
        setupData(app.report);
    }

    public void setupData(SpecialReport report) {
        this.report = report;
        timeText.setText("Creado el "+AppDatetime.longDatetime(report.createDate.getTime()));
        incidenceText.setText(report.title);
        observationText.setText(report.observation);

        if (report.image1 != null && !report.image1.isEmpty()) {
            setImage(photo1, report.image1);
            photo1.setVisibility(View.VISIBLE);
        } else {
            photo1.setImageDrawable(getDrawable(R.drawable.empty_image));
            photo1.setVisibility(View.GONE);
        }
        if (report.image2 != null && !report.image2.isEmpty()) {
            setImage(photo2, report.image2);
            photo2.setVisibility(View.VISIBLE);
        } else {
            photo1.setImageDrawable(getDrawable(R.drawable.empty_image));
            photo2.setVisibility(View.GONE);
        }
        if (report.image3 != null && !report.image3.isEmpty()) {
            setImage(photo3, report.image3);
            photo3.setVisibility(View.VISIBLE);
        } else {
            photo1.setImageDrawable(getDrawable(R.drawable.empty_image));
            photo3.setVisibility(View.GONE);
        }
        if (report.image4 != null  && !report.image4.isEmpty()) {
            setImage(photo4, report.image4);
            photo4.setVisibility(View.VISIBLE);
        } else {
            photo1.setImageDrawable(getDrawable(R.drawable.empty_image));
            photo4.setVisibility(View.GONE);
        }
        if (report.image5 != null  && !report.image5.isEmpty()) {
            setImage(photo5, report.image5);
            photo5.setVisibility(View.VISIBLE);
        } else {
            photo1.setImageDrawable(getDrawable(R.drawable.empty_image));
            photo5.setVisibility(View.GONE);
        }
        if ((report.image1 == null || report.image1.isEmpty())
                && (report.image2 == null || report.image2.isEmpty())
                && (report.image3 == null || report.image3.isEmpty())
                && (report.image4 == null || report.image4.isEmpty())
                && (report.image5 == null || report.image5.isEmpty())) {
            scrollPhotos.setVisibility(View.GONE);
        } else {
            scrollPhotos.setVisibility(View.VISIBLE);
        }
        getReplies();
    }

    public void getReplies()  {
        commentLayout.setVisibility(View.GONE);
        commentLoading.setVisibility(View.VISIBLE);
        new BinnacleController().getReplies(report.id);
        messingArea.setVisibility(View.GONE);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getRepliesFailure(OnGetRepliesFailure event) {
        EventBus.getDefault().removeStickyEvent(OnGetRepliesFailure.class);
        commentLoading.setVisibility(View.GONE);
        Snackbar.make(toolbar, event.message, Snackbar.LENGTH_LONG).show();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getRepliesSuccess(OnGetRepliesSuccess event) {
        EventBus.getDefault().removeStickyEvent(OnGetRepliesSuccess.class);
        replies = event.list.replies;
        setupReplies(replies);
    }

    private void setupReplies(List<Reply> replies) {
        this.replies = replies;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new ReplyAdapter(this, replies);
        recyclerView.setAdapter(adapter);
        commentSizeText.setText("Comentarios ("+replies.size()+")");
        commentLayout.setVisibility(View.VISIBLE);
        commentLoading.setVisibility(View.GONE);
        messingArea.setVisibility(View.VISIBLE);
    }

    private void setImage(ImageView imageView, String url) {
        Glide.with(this)
                .load(url)
                .apply(centerCropTransform()
                        .placeholder(R.drawable.empty_image)
                        .error(R.drawable.empty_image))
                .into(imageView);
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
        if (report.image1 != null && !report.image1.isEmpty()) {
            photos.add(report.image1);
        }
        if (report.image2 != null && !report.image2.isEmpty()) {
            photos.add(report.image2);
        }
        if (report.image3 != null && !report.image3.isEmpty()) {
            photos.add(report.image3);
        }
        if (report.image4 != null && !report.image4.isEmpty()) {
            photos.add(report.image4);
        }
        if (report.image5 != null && !report.image5.isEmpty()) {
            photos.add(report.image5);
        }
        return photos;
    }

    @OnClick(R.id.send)
    public void send() {
        if (messageText.getText().toString().trim().isEmpty()) {
            return;
        }
        if (messageText.getText().toString().length() < 10) {
            Toast.makeText(this, "El comentario debe contener al menos 10 caracteres",
                    Toast.LENGTH_LONG).show();
            return;
        }
        hideKeyboard();
        Reply reply = new Reply();
        reply.reportId = report.id;
        reply.guardId = getPreferences().getGuard().id;
        reply.userName = getPreferences().getGuard().getFullname();
        reply.text = messageText.getText().toString();
        new BinnacleController().postReply(reply);

        messageText.setEnabled(false);
        messageSend.setVisibility(View.GONE);
        messageSending.setVisibility(View.VISIBLE);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void postReplySuccess(OnPostReplySuccess event) {
        EventBus.getDefault().removeStickyEvent(OnPostReplySuccess.class);
        messageText.setEnabled(true);
        messageSend.setVisibility(View.VISIBLE);
        messageSending.setVisibility(View.GONE);
        messageText.setText("");
        replies.add(0, event.reply);
        adapter.replaceAll(replies);
        commentSizeText.setText("Comentarios ("+replies.size()+")");
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void postReplyFailure(OnPostReplyFailure event) {
        EventBus.getDefault().removeStickyEvent(OnPostReplyFailure.class);
        messageText.setEnabled(true);
        messageSend.setVisibility(View.VISIBLE);
        messageSending.setVisibility(View.GONE);
        System.out.println(new Gson().toJson(event.response));
        Snackbar.make(toolbar, event.response.message, Snackbar.LENGTH_LONG).show();
    }

    @OnClick(R.id.sos_alarm)
    public void SOS() {
        dialogSOS();
    }
}

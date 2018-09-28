package com.icsseseguridad.locationsecurity.service.repository;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.icsseseguridad.locationsecurity.service.event.OnUploadPhotoFailure;
import com.icsseseguridad.locationsecurity.service.event.OnUploadPhotoSuccess;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PhotoController extends BaseController {

    public void save(String uploadUri) {
        final StorageReference ref = FirebaseStorage.getInstance().getReference()
                .child(ICSSE_IMAGES).child(createToken()+".jpg");
        UploadTask uploadTask = ref.putFile(Uri.parse(uploadUri));
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    EventBus.getDefault().postSticky(new OnUploadPhotoSuccess(downloadUri.toString()));
                } else {
                    EventBus.getDefault().postSticky(new OnUploadPhotoFailure("Error de conexion"));
                }
            }
        });
    }

    public String saveSynchronously(String uploadUri) {
        final TaskCompletionSource<Uri> tcs = new TaskCompletionSource<>();
        final StorageReference ref = FirebaseStorage.getInstance().getReference()
                .child(ICSSE_IMAGES+"test2").child(createToken()+".jpg");
        UploadTask uploadTask = ref.putFile(Uri.parse(uploadUri));
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    tcs.setResult(task.getResult());
                    // Uri downloadUri = task.getResult();
                    // System.out.println(downloadUri.toString());
                } else {
                    tcs.setException(new Exception());
                }
            }
        });
        Task<Uri> t = tcs.getTask();
        try {
            Tasks.await(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (t.isSuccessful()) {
            return t.getResult().toString();
        } else {
            return null;
        }
    }
}

package com.icsseseguridad.locationsecurity.controller;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.icsseseguridad.locationsecurity.events.OnUploadPhotoFailure;
import com.icsseseguridad.locationsecurity.events.OnUploadPhotoSuccess;

import org.greenrobot.eventbus.EventBus;

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
}

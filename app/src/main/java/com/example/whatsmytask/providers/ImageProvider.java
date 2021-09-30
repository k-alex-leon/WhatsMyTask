package com.example.whatsmytask.providers;

import android.content.Context;

import com.example.whatsmytask.utils.CompressorBitmapImage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Date;

public class ImageProvider {

    StorageReference mStorage;

    public ImageProvider(){
        // hacemos referencia a donde se va a guardar la img
        mStorage = FirebaseStorage.getInstance().getReference();
    }

    // almacenar las img
    public UploadTask save(Context context, File file){
        byte[] imageByte = CompressorBitmapImage.getImage(context, file.getPath(), 500, 500);
        StorageReference storage = mStorage.child(new Date()+ ".jpg");
        mStorage = storage;
        UploadTask task = storage.putBytes(imageByte);
        return task;

    }

    public StorageReference  getStorage(){
        return mStorage;
    }
}

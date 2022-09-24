package com.example.whatsmytask.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.whatsmytask.R;
import com.example.whatsmytask.models.User;
import com.example.whatsmytask.providers.AuthProvider;
import com.example.whatsmytask.providers.ImageProvider;
import com.example.whatsmytask.providers.UsersProvider;
import com.example.whatsmytask.utils.FileUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    CircleImageView mImageViewCircleBack;
    CircleImageView mCircleImageViewProfile;
    ImageView mImageViewCover;
    TextInputEditText mTextInputUserName;
    TextInputEditText mTextInputPhone;

    ImageProvider mImageProvider;
    UsersProvider mUserProvider;
    AuthProvider mAuth;

    //le muestra al usuario que debe esperar mientras termina un proceso
    // AlertDialog mDialog;


    Button mBtnEditProfile;

    String mUserName;
    String mPhone;
    String mImageProfile;

    File mImageFile;
    private final int GALERY_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mImageViewCircleBack = findViewById(R.id.circleImageBack);
        mCircleImageViewProfile = findViewById(R.id.circleImageProfile);
        mImageViewCover = findViewById(R.id.imageViewCover);
        mTextInputUserName = findViewById(R.id.textInputEditUserName);
        mBtnEditProfile = findViewById(R.id.btnEditProfile);

        mImageProvider = new ImageProvider();
        mUserProvider = new UsersProvider();
        mAuth = new AuthProvider();


        // cuadro de carga
        /** mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Loading")
                .setCancelable(false).build();**/
        
        mBtnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickEditProfile();
            }
        });

        mCircleImageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });


        mImageViewCircleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // obtiene los datos del user para mostrar
        getUser();
    }

    private void getUser(){
        mUserProvider.getUser(mAuth.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // validamos si la info existe para mostrar en la activity
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("userName")){
                        mUserName = documentSnapshot.getString("userName");
                    }
                    if(documentSnapshot.contains("imageProfile")){
                        mImageProfile = documentSnapshot.getString("imageProfile");
                        if(mImageProfile != null){
                            if(!mImageProfile.isEmpty()){
                                //Picasso = permite mostrar las img desde internet
                                Picasso.with(EditProfileActivity.this).load(mImageProfile).into(mCircleImageViewProfile);
                            }
                        }

                    }
                    mTextInputUserName.setText(mUserName);


                }
            }
        });
    }

    // Guardar los datos cambiados del perfil
    private void clickEditProfile() {

        mUserName = mTextInputUserName.getText().toString();

        if (!mUserName.isEmpty()){
            if (mImageFile != null){
                saveImageProfile();
            }else{
                User user = new User();
                user.setUserName(mUserName);
                user.setImageProfile(mImageProfile);
                user.setId(mAuth.getUid());
                updateInfo(user);

            }
        }else{
            Toast.makeText(this, "Oops it seems that something is missing", Toast.LENGTH_LONG).show();
        }


    }

    private void saveImageProfile(){
        // mDialog.show();
        mImageProvider.save(EditProfileActivity.this, mImageFile)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String urlProfile = uri.toString();
                                    User user = new User();
                                    user.setUserName(mUserName);
                                    user.setImageProfile(urlProfile);
                                    user.setId(mAuth.getUid());

                                    updateInfo(user);
                                }
                            });

                        }else{
                            // mDialog.dismiss();
                            Toast.makeText(EditProfileActivity.this, "Error saving image", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    private void updateInfo(User user){

        // if (mDialog.isShowing()){ mDialog.show(); }
        mUserProvider.update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // mDialog.dismiss();
                if(task.isSuccessful()){
                    Toast.makeText(EditProfileActivity.this, "Data updated.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else{
                    Toast.makeText(EditProfileActivity.this, "Error saving image", Toast.LENGTH_LONG).show();
                }
            }
        });

    }



    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALERY_REQUEST_CODE);

    }

    // GALERY_REQUEST_CODE = requere sobreescribir el metodo onActivityResult

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALERY_REQUEST_CODE && resultCode == RESULT_OK){
            try{
                mImageFile = FileUtil.from(this,data.getData());
                mCircleImageViewProfile.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
            }catch (Exception e){
                Log.d("ERROR", "Error with onActivityResult" + e.getMessage());
                Toast.makeText(this, "Error with onActivityResult", Toast.LENGTH_LONG).show();
            }

        }
    }
}
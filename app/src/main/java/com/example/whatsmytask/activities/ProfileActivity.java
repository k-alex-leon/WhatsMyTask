package com.example.whatsmytask.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.whatsmytask.R;
import com.example.whatsmytask.adapters.UserFriendsAdapter;
import com.example.whatsmytask.models.User;
import com.example.whatsmytask.providers.AuthProvider;
import com.example.whatsmytask.providers.FriendsProvider;
import com.example.whatsmytask.providers.TaskProvider;
import com.example.whatsmytask.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;


import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    FriendsProvider mFriendsProvider;
    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;
    TaskProvider mTaskProvider;

    ImageView mImageViewEditProfile;
    CircleImageView mImageViewCircleBack, mCircleImageViewProfile;
    TextView mEmailTextView, mNameTextView;
    FloatingActionButton mFabAddFriend;
    UserFriendsAdapter mUserFriendAdapter;
    RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mImageViewEditProfile = findViewById(R.id.imageViewEditProfile);
        mImageViewCircleBack = findViewById(R.id.circleImageBack);

        mCircleImageViewProfile = findViewById(R.id.circleImageViewProfile);
        mNameTextView = findViewById(R.id.textViewName);
        mEmailTextView = findViewById(R.id.textViewEmail);
        mFabAddFriend = findViewById(R.id.fabAddFriend);
        mRecyclerView = findViewById(R.id.recyclerViewUserFriends);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mFriendsProvider = new FriendsProvider();
        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();
        mTaskProvider = new TaskProvider();

        // agregar amigo
        mFabAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this,AddFriendActivity.class);
                startActivity(intent);
            }
        });

        mImageViewCircleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        mImageViewEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        getUser();

    }

    // se hace consulta a la db
    @Override
    public void onStart() {
        super.onStart();
        Query query = mFriendsProvider.getAll(mAuthProvider.getUid());
        FirestoreRecyclerOptions<User> options =
                new FirestoreRecyclerOptions.Builder<User>()
                        .setQuery(query, User.class)
                        .build();

        mUserFriendAdapter = new UserFriendsAdapter(options,this);
        mRecyclerView.setAdapter(mUserFriendAdapter);
        // esto escucha los cambios que se hagan en la db
        mUserFriendAdapter.startListening();
    }

    // cuando la app esta en segundo plano deja de escuchar la db
    @Override
    public void onStop() {
        super.onStop();
        mUserFriendAdapter.stopListening();
    }

    // obtener data del user
    private void getUser(){
        mUsersProvider.getUser(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){

                    if(documentSnapshot.contains("email")){
                        String email = documentSnapshot.getString("email");
                        mEmailTextView.setText(email);
                    }
                    if(documentSnapshot.contains("userName")){
                        String userName = documentSnapshot.getString("userName");
                        mNameTextView.setText(userName);
                    }

                    // imagen de perfil
                    if(documentSnapshot.contains("imageProfile")){
                        String imageProfile = documentSnapshot.getString("imageProfile");

                        if(imageProfile != null){
                            if (!imageProfile.isEmpty()){
                                Picasso.with(ProfileActivity.this).load(imageProfile).into(mCircleImageViewProfile);
                            }
                        }
                    }
                }
            }
        });

    }
}
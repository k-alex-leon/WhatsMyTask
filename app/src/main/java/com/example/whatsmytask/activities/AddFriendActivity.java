package com.example.whatsmytask.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.whatsmytask.R;
import com.example.whatsmytask.adapters.FriendAdapter;
import com.example.whatsmytask.models.User;
import com.example.whatsmytask.providers.AuthProvider;
import com.example.whatsmytask.providers.UsersProvider;
import com.example.whatsmytask.utils.ItemChecked;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;


public class AddFriendActivity extends AppCompatActivity implements MaterialSearchBar.OnSearchActionListener {


    FriendAdapter mFriendAdapterSearch;
    UsersProvider mUserProvider;
    AuthProvider mAuthProvider;
    RecyclerView mRecyclerView;
    MaterialSearchBar mSearchBar;


    String userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        mUserProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewFriend);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AddFriendActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        // Search bar
        mSearchBar = (MaterialSearchBar) findViewById(R.id.searchBarFriends);
        mSearchBar.setOnSearchActionListener(this);

        passDataUser();
    }

    public void passDataUser(){
        mUserProvider.getUser(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = new User();
                userName = user.getUserName();
            }
        });


    }

    // se hace consulta a la db a partir de la busqueda
    private void searchByEmail(String emailSearch){

        Query query = mUserProvider.getUserByEmail(emailSearch);
        FirestoreRecyclerOptions<User> options =
                new FirestoreRecyclerOptions.Builder<User>()
                        .setQuery(query, User.class)
                        .build();

        mFriendAdapterSearch = new FriendAdapter(options,this);
        mFriendAdapterSearch.notifyDataSetChanged();
        mRecyclerView.setAdapter(mFriendAdapterSearch);
        // esto escucha los cambios que se hagan en la db
        mFriendAdapterSearch.startListening();


    }


    // cuando la app esta en segundo plano deja de escuchar la db
    @Override
    public void onStop() {
        super.onStop();
        if(mFriendAdapterSearch != null){
            mFriendAdapterSearch.stopListening();
        }
    }


    @Override
    public void onSearchStateChanged(boolean enabled) {
        
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        //transforma el CharSequience text a string para evitar el error
        searchByEmail(text.toString().toLowerCase());
    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }
}
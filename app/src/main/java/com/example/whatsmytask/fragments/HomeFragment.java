package com.example.whatsmytask.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.whatsmytask.R;
import com.example.whatsmytask.activities.MainActivity;
import com.example.whatsmytask.activities.NewTaskActivity;
import com.example.whatsmytask.activities.ProfileActivity;
import com.example.whatsmytask.adapters.TasksAdapter;
import com.example.whatsmytask.models.TaskU;
import com.example.whatsmytask.providers.AuthProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;
import com.example.whatsmytask.providers.TaskProvider;

public class HomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    View mView;
    FloatingActionButton mFab;
    Toolbar mToolbar;
    AuthProvider mAuthProvider;
    RecyclerView mRecyclerView;
    TaskProvider mTaskProvider;
    TasksAdapter mTaskAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }


    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        mFab = mView.findViewById(R.id.fab);
        mToolbar = mView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("My tasks");
        setHasOptionsMenu(true);
        mAuthProvider = new AuthProvider();

        mTaskProvider = new TaskProvider();

        mRecyclerView = mView.findViewById(R.id.recyclerViewHome);
        //LinearLayoutManager = muestra las cardview una debajo de la otra
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNewTask();
            }
        });



        return mView;

    }

    // se hace consulta a la db

    @Override
    public void onStart() {
        super.onStart();
        Query query = mTaskProvider.getTaskByUser(mAuthProvider.getUid());
        FirestoreRecyclerOptions<TaskU> taskU =
                new FirestoreRecyclerOptions.Builder<TaskU>()
                        .setQuery(query, TaskU.class)
                        .build();


        mTaskAdapter = new TasksAdapter(taskU,getContext());
        mRecyclerView.setAdapter(mTaskAdapter);
        // esto escucha los cambios que se hagan en la db
        mTaskAdapter.startListening();
    }


/**
    // cuando la app esta en segundo plano deja de escuchar la db
    @Override
    public void onStop() {
        super.onStop();
        mTaskAdapter.stopListening();
    }
    **/

    private void goToNewTask() {
        Intent intent = new Intent(getContext(), NewTaskActivity.class);
        startActivity(intent);
    }

    // PERMITE INSTANCIAR EL MENU

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itemLogout){
            logout();
        }if (item.getItemId()== R.id.itmeSession){
            Intent intent = new Intent(getContext(), ProfileActivity.class);
            startActivity(intent);

        }

        return true;
    }

    // cerrar la sesion

    private void logout() {
        mAuthProvider.logout();
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
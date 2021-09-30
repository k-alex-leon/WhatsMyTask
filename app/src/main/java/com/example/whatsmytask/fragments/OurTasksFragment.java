package com.example.whatsmytask.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.Toolbar;

import com.example.whatsmytask.R;
import com.example.whatsmytask.adapters.TasksAdapter;
import com.example.whatsmytask.models.TaskU;
import com.example.whatsmytask.providers.AuthProvider;
import com.example.whatsmytask.providers.TaskProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;


public class OurTasksFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    View mView;
    Toolbar mToolbar;
    AuthProvider mAuthProvider;
    RecyclerView mRecyclerView;
    TaskProvider mTaskProvider;
    TasksAdapter mTaskAdapter;

    public OurTasksFragment() {
        // Required empty public constructor
    }


    public static OurTasksFragment newInstance(String param1, String param2) {
        OurTasksFragment fragment = new OurTasksFragment();
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
        mView = inflater.inflate(R.layout.fragment_our_tasks, container, false);
        mToolbar = mView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Our tasks");
        mAuthProvider = new AuthProvider();
        mTaskProvider = new TaskProvider();
        mRecyclerView = mView.findViewById(R.id.recyclerOurTask);

        //LinearLayoutManager = muestra las cardview una debajo de la otra
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mTaskProvider.getTaskFriends(mAuthProvider.getUid());
        FirestoreRecyclerOptions<TaskU> taskU =
                new FirestoreRecyclerOptions.Builder<TaskU>()
                        .setQuery(query, TaskU.class)
                        .build();

        mTaskAdapter = new TasksAdapter(taskU,getContext());
        mRecyclerView.setAdapter(mTaskAdapter);
        // esto escucha los cambios que se hagan en la db
        mTaskAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mTaskAdapter.stopListening();
    }
}
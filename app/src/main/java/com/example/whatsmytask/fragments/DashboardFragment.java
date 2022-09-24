package com.example.whatsmytask.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.whatsmytask.R;
import com.example.whatsmytask.activities.MainActivity;
import com.example.whatsmytask.activities.ProfileActivity;
import com.example.whatsmytask.providers.AuthProvider;
import com.example.whatsmytask.providers.TaskProvider;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class DashboardFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;


    TaskProvider mTaskProvider;
    AuthProvider mAuthProvider;
    Toolbar mToolbar;
    View mView;
    TextView textViewTaskSize, textViewPendingTaskSize,
            textViewTaskDoneSize, textViewTeamTask;

    int numberTask,numberPendingTask,
            numberTaskDone,numberTeamTask;

    RadarChart graphRadarChart;

    public DashboardFragment() {}


    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
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

        mTaskProvider = new TaskProvider();
        mAuthProvider = new AuthProvider();

        mView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mToolbar = mView.findViewById(R.id.toolbar);
        //((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Dashboard");
        setHasOptionsMenu(true);

        textViewTaskSize = mView.findViewById(R.id.allTaskSize);
        textViewPendingTaskSize = mView.findViewById(R.id.pendingTaskSize);
        textViewTaskDoneSize = mView.findViewById(R.id.taskDoneSize);
        textViewTeamTask = mView.findViewById(R.id.teamTaskSize);

        //grafico de pie
        graphRadarChart = (RadarChart) mView.findViewById(R.id.graphRadarChart);

        createGraph();

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();


    // Todas las tareas
        mTaskProvider.getTaskByUser(mAuthProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                numberTask = queryDocumentSnapshots.size();
                textViewTaskSize.setText(String.valueOf(numberTask));

                // Tareas pendientes
                mTaskProvider.getTaskPending(mAuthProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        numberPendingTask = queryDocumentSnapshots.size();
                        textViewPendingTaskSize.setText(String.valueOf(numberPendingTask));

                        //Tareas terminadas
                        mTaskProvider.getTaskDone(mAuthProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                numberTaskDone = queryDocumentSnapshots.size();
                                textViewTaskDoneSize.setText(String.valueOf(numberTaskDone));

                                // Tareas de equipo
                                mTaskProvider.getTeamTask(mAuthProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        numberTeamTask = queryDocumentSnapshots.size();
                                        textViewTeamTask.setText(String.valueOf(numberTeamTask));
                                        createGraph();

                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

    }


    private void createGraph() {

        // Grafica de radar


        ArrayList<RadarEntry> allTask = new ArrayList<>();
        allTask.add(new RadarEntry(numberTask));
        allTask.add(new RadarEntry(numberPendingTask));
        allTask.add(new RadarEntry(numberTaskDone));
        allTask.add(new RadarEntry(numberTeamTask));

        RadarDataSet radarDataSet = new RadarDataSet(allTask, "Your progress.");
        radarDataSet.setColor(Color.parseColor("#1B7BCF"));
        radarDataSet.setLineWidth(2f);
        radarDataSet.setValueTextColor(Color.parseColor("#1B7BCF"));
        radarDataSet.setValueTextSize(14f);


        RadarData radarData = new RadarData();
        radarData.addDataSet(radarDataSet);

        String[] labels = { "All task", "Pending task", "Task done", "Team task"};

        XAxis xAxis = graphRadarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

        graphRadarChart.getDescription().setText("Tap to refresh.");
        graphRadarChart.setData(radarData);

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
package com.example.whatsmytask.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.whatsmytask.R;

import com.example.whatsmytask.adapters.TaskFriendAdapter;
import com.example.whatsmytask.models.TaskU;

import com.example.whatsmytask.models.User;
import com.example.whatsmytask.providers.AuthProvider;

import com.example.whatsmytask.providers.FriendsProvider;
import com.example.whatsmytask.providers.TaskProvider;
import com.example.whatsmytask.utils.ItemChecked;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.Query;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


public class NewTaskActivity extends AppCompatActivity{

    Button mBtnSaveTask;
    CircleImageView mBtnCircleButtonBackTask;
    TextInputEditText mTextInputTaskTitle,mTextInputDescriptionTask;
    ImageView mTaskDate, mTaskHour;
    TextView mEtDate, mEtHour;
    String dateT, hourT, mTitleTask, mDescriptionTask;
    LinearLayout mLlAddFriends;

    ArrayList<String> friendsId;

    TaskFriendAdapter mTaskFriendAdapter;
    FriendsProvider mFriendsProvider;
    TaskProvider mTaskProvider;
    AuthProvider mAuthProvider;

    Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        mFriendsProvider = new FriendsProvider();
        mTaskProvider = new TaskProvider();
        mAuthProvider = new AuthProvider();

        friendsId = new ArrayList<>();


        mBtnSaveTask = findViewById(R.id.btnSaveTask);
        mBtnCircleButtonBackTask = findViewById(R.id.circleImageBackTask);
        mTaskDate = findViewById(R.id.taskDate);
        mTaskHour = findViewById(R.id.taskHour);
        mTextInputTaskTitle = findViewById(R.id.textInputTaskTitle);
        mTextInputDescriptionTask = findViewById(R.id.textInputDescriptionTask);
        mLlAddFriends = findViewById(R.id.lLAddFriends);
        mEtDate = findViewById(R.id.etDate);
        mEtHour = findViewById(R.id.etHour);


        mLlAddFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFriendsList();
            }
        });

        // IMGVIEW QUE ABRE EL CALENDARIO
        mTaskDate.setOnClickListener(v -> openCalendar());

        // IMGVIEW QUE ABRE LA HORA
        mTaskHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHour();
            }
        });

        //El metodo finish devuelve a la pagina anterior
        mBtnCircleButtonBackTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBtnSaveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateTask();
            }
        });

    }

    // ABRIR HORA

    private void openHour() {
        Calendar hourPicker = Calendar.getInstance();
        int hour = hourPicker.get(Calendar.HOUR_OF_DAY);
        int minute = hourPicker.get(Calendar.MINUTE);


        TimePickerDialog tmd = new TimePickerDialog(NewTaskActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int h, int m) {

                calendar.set(Calendar.HOUR_OF_DAY,h);
                calendar.set(Calendar.MINUTE,m);
                SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
                hourT = hourFormat.format(calendar.getTime());
                mEtHour.setText(hourT);

            }

            //is24HourView para permitir o no formato 24h
        },hour,minute,false);
        tmd.show();
    }

    // ABRIR CALENDARIO

    public void openCalendar(){
        Calendar today = Calendar.getInstance();
        int y = today.get(Calendar.YEAR);
        int m = today.get(Calendar.MONTH);
        int d = today.get(Calendar.DAY_OF_MONTH);



            DatePickerDialog dpd = new DatePickerDialog(NewTaskActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int day) {

                    calendar.set(Calendar.DAY_OF_MONTH,day);
                    calendar.set(Calendar.MONTH,month);
                    calendar.set(Calendar.YEAR,year);

                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    dateT = format.format(calendar.getTimeInMillis());
                    mEtDate.setText(dateT);

                }
            },y,m,d);
            dpd.show();
        }


    public void showFriendsList(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = getLayoutInflater().inflate(R.layout.add_friend_dialog, null);
        // obteniendo ref del layout
        ImageView imgVCLose = view.findViewById(R.id.imgVCloseSelectFriendsDialog);
        Button btnAdd = view.findViewById(R.id.btnAddSelectFriensDialog);
        RecyclerView recyclerViewSelectFriends = view.findViewById(R.id.recyclerSelectFriendsDialog);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        // quitamos el background del dialog para pasarle el custom_border
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewSelectFriends.setLayoutManager(linearLayoutManager);

        // haciendo consulta de lista de amigos
        Query query = mFriendsProvider.getAll(mAuthProvider.getUid());
        FirestoreRecyclerOptions<User> options =
                new FirestoreRecyclerOptions.Builder<User>()
                        .setQuery(query, User.class)
                        .build();

        mTaskFriendAdapter = new TaskFriendAdapter(options,this, new ItemChecked() {
            @Override
            public void itemSelected(ArrayList<String> array) {
                friendsId = array;
            }
        });
        recyclerViewSelectFriends.setAdapter(mTaskFriendAdapter);
        // esto escucha los cambios que se hagan en la db
        mTaskFriendAdapter.startListening();


        imgVCLose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (friendsId.size() >= 1){
                    Toast.makeText(view.getContext(), "Finish select friends", Toast.LENGTH_SHORT).show();
                }else{
                    dialog.dismiss();
                }
            }
        });


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (friendsId.size() < 1){
                    Toast.makeText(view.getContext(), "Add a friend", Toast.LENGTH_SHORT).show();
                }else{
                    dialog.dismiss();
                }
            }
        });


    }

    // CREAR LA TAREA EN LA DB

    private void validateTask() {

        mTitleTask = mTextInputTaskTitle.getText().toString();
        mDescriptionTask = mTextInputDescriptionTask.getText().toString();
        String dateSeleted = mEtDate.getText().toString();
        String hourSelected = mEtHour.getText().toString();

        if(!mTitleTask.isEmpty() && !mDescriptionTask.isEmpty()){
            if (mTitleTask.length() < 40){

                if (mDescriptionTask.length() > 40){

                    if (!dateSeleted.isEmpty() && !hourSelected.isEmpty()){
                        // mDialog.show();
                        createTask();
                    }else{
                        Toast.makeText(this, "Select hour and date", Toast.LENGTH_LONG).show();
                    }

                }else{
                    mTextInputDescriptionTask.setError("Description too short (40 min)");
                }

            }else{
                mTextInputTaskTitle.setError("Title too long (40 max)");
            }


        }else{
            Toast.makeText(this, "Oops it seems that something is missing", Toast.LENGTH_LONG).show();
        }


    }



    // CREAR LA TAREA EN LA DB
    private void createTask() {

        // agrego el idUser al array para despues llamarlo desde el fragment

        TaskU taskU = new TaskU();
        taskU.setTitleTask(mTitleTask);
        taskU.setDescriptionTask(mDescriptionTask);
        taskU.setIdUser(mAuthProvider.getUid());
        taskU.setDateTask(dateT);
        taskU.setHourTask(hourT);
        taskU.setTaskAlarmDate(calendar.getTimeInMillis());
        if(friendsId.size() < 1){
            taskU.setFriendsTask(null);
        }else{
            friendsId.add(mAuthProvider.getUid());
            taskU.setFriendsTask(friendsId);
        }
        // new Date().getTime() = determina la fecha exacta de creacion de la tarea
        taskU.setTimestamp(new Date().getTime());
        mTaskProvider.saveTask(taskU).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> taskSave) {

                if(taskSave.isSuccessful()){
                    // mDialog.dismiss();
                    clearForm();
                    finish();
                    Toast.makeText(NewTaskActivity.this, "Task save", Toast.LENGTH_SHORT).show();

                }else{
                    // mDialog.dismiss();
                    Toast.makeText(NewTaskActivity.this, "Error saving task", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    // LIMPIAR EL FORMULARIO DESPUES DE CREAR UNA TAREA

    private void clearForm() {

        mTextInputTaskTitle.setText("");
        mTextInputDescriptionTask.setText("");
        mTitleTask = "";
        mDescriptionTask = "";
        mEtDate.setText("");
        mEtHour.setText("");
        dateT = "";
        hourT = "";

    }



}
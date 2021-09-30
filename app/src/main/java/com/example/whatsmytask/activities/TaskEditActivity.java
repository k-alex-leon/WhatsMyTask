package com.example.whatsmytask.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;


import org.jetbrains.annotations.NotNull;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class TaskEditActivity extends AppCompatActivity {

    Button mBtnUpdateTask;
    CircleImageView mBtnCircleButtonBackTask;
    TextInputEditText mEditTaskTitle, mEditDescriptionTask;
    ImageView mTaskImageDate, mTaskImageHour, mTaskImageDelete, mImageAddFriend;
    RecyclerView mRecyclerView;
    TextView mEtUDate, mEtUHour;
    LinearLayout mLinearRecyclerEditFriends;
    String dateT;
    String hourT;


    //le muestra al usuario que debe esperar mientras termina un proceso
    AlertDialog mDialog;

    TaskProvider mTaskProvider;
    AuthProvider mAuthProvider;
    FriendsProvider mFriendsProvider;
    TaskFriendAdapter mTaskFriendAdapter;
    TaskU mTaskU;

    String mExtraTaskId;

    String titleTask,descriptionTask,dateTask,hourTask;
    String mTaskTitle,mDescriptionTask,mDateTask,mHourTask;

    ArrayList<String> friendsId;


    Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);

        mFriendsProvider = new FriendsProvider();
        mTaskProvider = new TaskProvider();
        mAuthProvider = new AuthProvider();
        mTaskU = new TaskU();

        friendsId = new ArrayList<>();

        mExtraTaskId = getIntent().getStringExtra("id");

        mBtnUpdateTask = findViewById(R.id.btnUpdateTask);
        mBtnCircleButtonBackTask = findViewById(R.id.circleImageBackTask);
        mEditTaskTitle = findViewById(R.id.editTaskTitle);
        mEditDescriptionTask = findViewById(R.id.editDescriptionTask);
        mTaskImageDate = findViewById(R.id.taskDate);
        mTaskImageHour = findViewById(R.id.taskHour);
        mTaskImageDelete = findViewById(R.id.imgDeleteTaskView);
        mImageAddFriend = findViewById(R.id.imgViewAddFriendTask);


        mRecyclerView = findViewById(R.id.recyclerEditFriends);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mLinearRecyclerEditFriends = findViewById(R.id.linearRecyclerEditFriends);

        mEtUDate = findViewById(R.id.editTaskDate);
        mEtUHour = findViewById(R.id.editTaskHour);

        mBtnCircleButtonBackTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBtnUpdateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickUpdateTask(friendsId);
            }
        });

        mTaskImageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDelete(mExtraTaskId);
            }
        });

        mImageAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLinearRecyclerEditFriends.setVisibility(View.VISIBLE);
                friendsList();
            }
        });



        // cuadro de carga (ALERT DIALOG)
        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Loading")
                .setCancelable(false).build();

        // IMGVIEW QUE ABRE EL CALENDARIO
        mTaskImageDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalendar();
            }
        });

        // IMGVIEW QUE ABRE LA HORA
        mTaskImageHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHour();
            }
        });

        getTask();

    }




    // ABRIR HORA

    private void openHour() {
        Calendar hourPicker = Calendar.getInstance();
        int hour = hourPicker.get(Calendar.HOUR_OF_DAY);
        int minute = hourPicker.get(Calendar.MINUTE);


        TimePickerDialog tmd = new TimePickerDialog(TaskEditActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int h, int m) {

                calendar.set(Calendar.HOUR_OF_DAY,h);
                calendar.set(Calendar.MINUTE,m);

                mEtUHour.setText(String.format("%02d:%02d",h,m));

            }

            //is24HourView para permitir o no formato 24h
        },hour,minute,false);
        tmd.show();
    }

    // ABRIR CALENDARIO

    public void openCalendar(){
        Calendar actual = Calendar.getInstance();
        int y = actual.get(Calendar.YEAR);
        int m = actual.get(Calendar.MONTH);
        int d = actual.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog dpd = new DatePickerDialog(TaskEditActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                calendar.set(Calendar.DAY_OF_MONTH,day);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.YEAR,year);

                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                dateT = format.format(calendar.getTime());
                mEtUDate.setText(dateT);

            }
        },y,m,d);
        dpd.show();
    }

    private void friendsList() {
        Query query = mFriendsProvider.getAll(mAuthProvider.getUid());
        FirestoreRecyclerOptions<User> options =
                new FirestoreRecyclerOptions.Builder<User>()
                        .setQuery(query, User.class)
                        .build();

        mTaskFriendAdapter = new TaskFriendAdapter(options,this, new ItemChecked() {
            @Override
            public void itemSelected(ArrayList<String> array) {
                friendsId = array;
                // agrega el id del usuario a la lista de amigos de tarea
                friendsId.add(mAuthProvider.getUid());
            }
        });
        mRecyclerView.setAdapter(mTaskFriendAdapter);
        // esto escucha los cambios que se hagan en la db
        mTaskFriendAdapter.startListening();
    }

    private void getTask(){
        mTaskProvider.getTaskById(mExtraTaskId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("titleTask")){
                        titleTask = documentSnapshot.getString("titleTask");
                    }
                    if(documentSnapshot.contains("descriptionTask")){
                        descriptionTask = documentSnapshot.getString("descriptionTask");
                    }
                    if(documentSnapshot.contains("hourTask")){
                        hourTask = documentSnapshot.getString("hourTask");
                    }
                    if(documentSnapshot.contains("dateTask")){
                        dateTask = documentSnapshot.getString("dateTask");
                    }
                    if (documentSnapshot.contains("friendsTask")){
                        friendsId = (ArrayList<String>) documentSnapshot.get("friendsTask");
                    }


                    mEditTaskTitle.setText(titleTask);
                    mEditDescriptionTask.setText(descriptionTask);
                    mEtUDate.setText(dateTask);
                    mEtUHour.setText(hourTask);


                }
            }
        });

    }

    private void clickUpdateTask(ArrayList friendsId){

        mDialog.show();

         mTaskTitle = mEditTaskTitle.getText().toString();
         mDescriptionTask = mEditDescriptionTask.getText().toString();
         mDateTask = mEtUDate.getText().toString();
         mHourTask = mEtUHour.getText().toString();

        if (!mTaskTitle.isEmpty() && !mDescriptionTask.isEmpty() &&
            !mDateTask.isEmpty() && !mHourTask.isEmpty()){

            TaskU taskU = new TaskU();
            taskU.setId(mExtraTaskId);
            taskU.setTitleTask(mTaskTitle);
            taskU.setDescriptionTask(mDescriptionTask);
            taskU.setDateTask(mDateTask);
            taskU.setHourTask(mHourTask);
            if (!friendsId.isEmpty()){
                taskU.setFriendsTask(friendsId);
            }
            taskU.setTaskAlarmDate(calendar.getTimeInMillis());
            updateTask(taskU);

        }else{
            mDialog.dismiss();
            Toast.makeText(this, "Oops it seems that something is missing", Toast.LENGTH_LONG).show();
        }



    }

    private void updateTask(TaskU taskU) {

        mTaskProvider.updateTask(taskU).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> taskUpdate) {
                mDialog.dismiss();
                if (taskUpdate.isSuccessful()){
                    Toast.makeText(TaskEditActivity.this, "Task Updated", Toast.LENGTH_LONG).show();
                    finish();
                }

            }
        });
    }

    //se encarga de mostrar una alerta antes de eliminar la tarea
    private void showConfirmDelete(String mExtraTaskId) {
        new AlertDialog.Builder(TaskEditActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Delete task")
                .setMessage("Do you want to delete this task?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteTask(mExtraTaskId);
                    }
                })
                .setNegativeButton("No", null)
                .show();

    }

    //este metodo necesita el id de la tarea
    private void deleteTask(String mExtraTaskId) {
        mTaskProvider.deleteTask(mExtraTaskId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(TaskEditActivity.this, "Task deleted.", Toast.LENGTH_LONG).show();
                    finish();

                }else{
                    Toast.makeText(TaskEditActivity.this, "Error.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


}
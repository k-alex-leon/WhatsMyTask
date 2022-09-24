package com.example.whatsmytask.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.whatsmytask.R;
import com.example.whatsmytask.adapters.FriendsWorkingAdapter;
import com.example.whatsmytask.models.TaskU;
import com.example.whatsmytask.providers.AuthProvider;
import com.example.whatsmytask.providers.FriendsProvider;
import com.example.whatsmytask.providers.TaskProvider;
import com.example.whatsmytask.providers.UsersProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;


import org.jetbrains.annotations.NotNull;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class TaskEditActivity extends AppCompatActivity {

    Button mBtnUpdateTask;
    CircleImageView mBtnCircleButtonBackTask;
    TextInputEditText mEditTaskTitle, mEditDescriptionTask;
    ImageView mTaskImageDate, mTaskImageHour, mTaskImageDelete;
    TextView mEtUDate, mEtUHour;

    RecyclerView mRecyclerViewFriendsWorking;
    //le muestra al usuario que debe esperar mientras termina un proceso
    // AlertDialog mDialog;

    TaskProvider mTaskProvider;
    AuthProvider mAuthProvider;
    FriendsProvider mFriendsProvider;
    TaskU mTaskU;
    UsersProvider mUserProvider;
    FriendsWorkingAdapter mFriendsWorkingAdapter;
    String mExtraTaskId;


    String mTaskTitle,mDescriptionTask,mDateTask,mHourTask;

    ArrayList<String> friendsIdArray , newEditFriendsArray;
    ListenerRegistration mListenerRegistration = null;


    Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);

        mFriendsProvider = new FriendsProvider();
        mTaskProvider = new TaskProvider();
        mAuthProvider = new AuthProvider();
        mTaskU = new TaskU();
        mUserProvider = new UsersProvider();

        friendsIdArray = new ArrayList<>();
        newEditFriendsArray = new ArrayList<>();

        mExtraTaskId = getIntent().getStringExtra("id");

        mBtnUpdateTask = findViewById(R.id.btnUpdateTask);
        mBtnCircleButtonBackTask = findViewById(R.id.circleImageBackTask);
        mEditTaskTitle = findViewById(R.id.editTaskTitle);
        mEditDescriptionTask = findViewById(R.id.editDescriptionTask);
        mTaskImageDate = findViewById(R.id.taskDate);
        mTaskImageHour = findViewById(R.id.taskHour);
        mTaskImageDelete = findViewById(R.id.imgDeleteTaskView);

        mRecyclerViewFriendsWorking = findViewById(R.id.recyclerTeamEditTask);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerViewFriendsWorking.setLayoutManager(mLinearLayoutManager);

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
                validateUpdateTask();
            }
        });

        mTaskImageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDelete(mExtraTaskId);
            }
        });


        // cuadro de carga (ALERT DIALOG)
        /** mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Loading")
                .setCancelable(false).build(); **/

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
                String dateT = format.format(calendar.getTime());
                mEtUDate.setText(dateT);

            }
        },y,m,d);
        dpd.show();
    }

    // obtenemos info de la tarea a editar
    private void getTask(){
        mListenerRegistration = mTaskProvider.getTaskById(mExtraTaskId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()){
                    TaskU taskU = value.toObject(TaskU.class);

                    mEditTaskTitle.setText(taskU.getTitleTask());
                    mEditDescriptionTask.setText(taskU.getDescriptionTask());
                    mEtUDate.setText(taskU.getDateTask());
                    mEtUHour.setText(taskU.getHourTask());

                    if (taskU.getFriendsTask() != null){
                        friendsIdArray = taskU.getFriendsTask();
                        showFriendsWorkingList(taskU.getId());
                    }

                }
            }
        });

    }

    private void showFriendsWorkingList(String idTask) {
        if (friendsIdArray != null && friendsIdArray.size() > 1){

            // este adapter recibe un array con los id para luego hacer la consulta en la bd
                mFriendsWorkingAdapter = new FriendsWorkingAdapter(friendsIdArray, TaskEditActivity.this, idTask);
                mRecyclerViewFriendsWorking.setAdapter(mFriendsWorkingAdapter);

            }

    }


    private void validateUpdateTask(){

        // mDialog.show();

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
            // taskU.setTaskAlarmDate(calendar.getTimeInMillis());
            updateTask(taskU);

        }else{
            // mDialog.dismiss();
            Toast.makeText(this, "Oops it seems that something is missing", Toast.LENGTH_LONG).show();
        }

    }

    private void updateTask(TaskU taskU) {

        mTaskProvider.updateTask(taskU).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> taskUpdate) {
                // mDialog.dismiss();
                if (taskUpdate.isSuccessful()){
                    Toast.makeText(TaskEditActivity.this, "Task Updated", Toast.LENGTH_LONG).show();
                    finish();
                }

            }
        });
    }

    //se encarga de mostrar un alertdialog antes de eliminar la tarea
    private void showConfirmDelete(String mExtraTaskId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = getLayoutInflater().inflate(R.layout.delete_dialog, null);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Button btnCancel, btnDelete;
        ImageView imgVClose;

        imgVClose = view.findViewById(R.id.imgVCloseDelete);
        btnCancel = view.findViewById(R.id.btnCancelDelete);
        btnDelete = view.findViewById(R.id.btnDelete);


        imgVClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTask(mExtraTaskId);
                dialog.dismiss();
            }
        });

    }


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

    @Override
    protected void onStop() {
        super.onStop();
        if (mListenerRegistration != null){
            mListenerRegistration.remove();
        }
    }
}
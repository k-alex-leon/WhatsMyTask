package com.example.whatsmytask.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
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
import com.example.whatsmytask.adapters.FriendsWorkingAdapter;
import com.example.whatsmytask.adapters.TaskFriendAdapter;
import com.example.whatsmytask.models.TaskU;
import com.example.whatsmytask.models.User;
import com.example.whatsmytask.providers.AuthProvider;
import com.example.whatsmytask.providers.FriendsProvider;
import com.example.whatsmytask.providers.TaskProvider;
import com.example.whatsmytask.providers.UsersProvider;
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
    TextView mEtUDate, mEtUHour;
    String dateT, hourT;
    LinearLayout mLlAddFriendsEdit;

    RecyclerView mRecyclerViewFriendsWorking;
    //le muestra al usuario que debe esperar mientras termina un proceso
    AlertDialog mDialog;

    TaskProvider mTaskProvider;
    AuthProvider mAuthProvider;
    FriendsProvider mFriendsProvider;
    TaskFriendAdapter mTaskFriendAdapter;
    TaskU mTaskU;
    UsersProvider mUserProvider;
    FriendsWorkingAdapter mFriendsWorkingAdapter;
    String mExtraTaskId;


    String titleTask,descriptionTask,dateTask,hourTask;
    String mTaskTitle,mDescriptionTask,mDateTask,mHourTask;

    ArrayList<String> friendsIdArray , newEditFriendsArray;


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
        mLlAddFriendsEdit = findViewById(R.id.lLAddFriendsEdit);

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

        mLlAddFriendsEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFriendsList();
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

    private void getTask(){
        mTaskProvider.getTaskById(mExtraTaskId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    TaskU taskU = documentSnapshot.toObject(TaskU.class);
                    if(documentSnapshot.contains("titleTask")){
                        // titleTask = documentSnapshot.getString("titleTask");
                        titleTask = taskU.getTitleTask();
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
                        friendsIdArray = taskU.getFriendsTask();
                        showFriendsWorkingList();
                    }

                    mEditTaskTitle.setText(titleTask);
                    mEditDescriptionTask.setText(descriptionTask);
                    mEtUDate.setText(dateTask);
                    mEtUHour.setText(hourTask);



                }
            }
        });

    }

    private void showFriendsList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = getLayoutInflater().inflate(R.layout.add_friend_dialog, null);
        // obteniendo ref del layout
        ImageView imgVCLose = view.findViewById(R.id.imgVCloseSelectFriendsDialog);
        Button btnAdd = view.findViewById(R.id.btnAddSelectFriensDialog);
        RecyclerView recyclerViewSelectFriends = view.findViewById(R.id.recyclerSelectFriendsDialog);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
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
                newEditFriendsArray = array;
            }
        });
        recyclerViewSelectFriends.setAdapter(mTaskFriendAdapter);
        // esto escucha los cambios que se hagan en la db
        mTaskFriendAdapter.startListening();


        imgVCLose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTaskFriendAdapter.stopListening();
                if (newEditFriendsArray.size() >= 1){
                    // quitando los amigos seleccionados
                    for (int i = 0; i < newEditFriendsArray.size(); i++){
                        newEditFriendsArray.remove(i);
                    }
                    dialog.dismiss();
                }else{
                    dialog.dismiss();
                }
            }
        });


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTaskFriendAdapter.stopListening();
                if (newEditFriendsArray.size() < 1){
                    Toast.makeText(view.getContext(), "Add a friend", Toast.LENGTH_SHORT).show();
                }else{
                    /*
                    * if (newEditFriendsArray.size() > friendsIdArray.size()){
                        // compara los valores con el anterior array
                        for (int i = 0; i < friendsIdArray.size(); i++){
                            // si el valor del nuevo es diferente lo agrega al viejo
                            if (!newEditFriendsArray.get(i).equals(friendsIdArray.get(i))){
                                friendsIdArray.add(newEditFriendsArray.get(i));
                            }
                            Log.d("OLDARRAY", friendsIdArray.get(i));
                        }
                    }
                    * */
                    dialog.dismiss();
                }
            }
        });

    }

    private void showFriendsWorkingList() {
        if (friendsIdArray != null && friendsIdArray.size() > 1){

            // este adapter recibe un array con los id para luego hacer la consulta en la bd
                mFriendsWorkingAdapter = new FriendsWorkingAdapter(friendsIdArray, TaskEditActivity.this);
                mRecyclerViewFriendsWorking.setAdapter(mFriendsWorkingAdapter);

            }

    }


    private void validateUpdateTask(){

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

            if (newEditFriendsArray.size() >= 1){
                taskU.setFriendsTask(newEditFriendsArray);
            }else{
                taskU.setFriendsTask(friendsIdArray);
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
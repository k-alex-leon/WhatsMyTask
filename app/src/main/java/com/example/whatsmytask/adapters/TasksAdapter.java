package com.example.whatsmytask.adapters;



import static com.example.whatsmytask.R.drawable.*;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsmytask.R;
import com.example.whatsmytask.activities.TaskEditActivity;
import com.example.whatsmytask.models.FCMBody;
import com.example.whatsmytask.models.FCMResponse;
import com.example.whatsmytask.models.TaskU;

import com.example.whatsmytask.providers.AuthProvider;


import com.example.whatsmytask.providers.NotificationProvider;
import com.example.whatsmytask.providers.TaskProvider;
import com.example.whatsmytask.providers.TokenProvider;
import com.example.whatsmytask.providers.UsersProvider;
import com.example.whatsmytask.utils.AlertReceiver;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TasksAdapter extends FirestoreRecyclerAdapter<TaskU, TasksAdapter.ViewHolder> {


    Context context;
    AuthProvider mAuthProvider;
    String idUser;
    TaskProvider mTaskProvider;
    UsersProvider mUserProvider;
    FriendsTeamAdapter mFriendsTeamAdapter;


    public TasksAdapter(FirestoreRecyclerOptions<TaskU> options, Context context){
        super(options);
        this.context = context;
    }


    // ESTABLECE EL CONTENIDO QUE SE QUIERE MOSTRAR
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull TaskU taskU) {
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String taskId = document.getId();

        int odd = position % 2;

        holder.textViewTitle.setText(taskU.getTitleTask().toUpperCase(Locale.ROOT));
        holder.textViewDescription.setText(taskU.getDescriptionTask());
        holder.textViewDate.setText(taskU.getDateTask());
        holder.textViewHour.setText(taskU.getHourTask());

        if (taskU.isTaskCheck()){
            // holder.checkboxTask.setChecked(true);
            holder.checkboxTask.setVisibility(View.GONE);
            holder.linearContainerCardview.setBackgroundResource(cardviewcheck);
        }else{

            if(odd == 1){
                holder.linearContainerCardview.setBackgroundResource(cardviewblueinvert);
            }else if (odd == 0){
                holder.linearContainerCardview.setBackgroundResource(cardviewblue);
            }

            // alerta de notificacion
            long alarmTaskDate = taskU.getTaskAlarmDate();
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlertReceiver.class);
            PendingIntent pendingIntent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_IMMUTABLE);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTaskDate, pendingIntent);
            }


        }

        holder.checkboxTask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                    taskU.setId(taskId);
                    taskU.setTaskCheck(true);
                    mTaskProvider.updateTaskStatus(taskU);
                }else {

                    if(odd == 1){
                        holder.linearContainerCardview.setBackgroundResource(cardviewblueinvert);
                    }else if (odd == 0){
                        holder.linearContainerCardview.setBackgroundResource(cardviewblue);
                    }

                    // alerta de notificacion
                    long alarmTaskDate = taskU.getTaskAlarmDate();
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(context, AlertReceiver.class);

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S){

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_IMMUTABLE);
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTaskDate, pendingIntent);

                    }

                    //si la tarea no es cumplida se enviara alerta
                    taskU.setId(taskId);
                    taskU.setTaskCheck(false);
                    mTaskProvider.updateTask(taskU);
                }


            }
        });

        idUser = mAuthProvider.getUid();
        // comprobar que el que crea la tarea sea el unico que puede editar

        if(taskU.getIdUser().contentEquals(idUser)){
            holder.imageViewEditTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, TaskEditActivity.class);
                    intent.putExtra("id",taskId);
                    context.startActivity(intent);
                }
            });

        }
        // para usuarios agregados a la tarea
        else if(!taskU.getIdUser().contentEquals(idUser)){
            holder.checkboxTask.setVisibility(View.GONE);
            holder.imageViewEditTask.setImageResource(ic_eye);
            holder.imageViewEditTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTaskInfoDialog(taskU);
                }
            });
        }


    }

    private void showTaskInfoDialog(TaskU taskU) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.task_info_dialog, null);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        RecyclerView recyclerVTeamTaskInfo;
        ImageView imgVCloseTaskInfoDialog, cImgVUserInfoTask;
        TextView txtVTitleInfoTask, txtVUsernameInfoTask,
                txtVEmailInfoTask, txtVHourInfoTask, txtVDateInfoTask,
                txtVDescriptionInfoTask;

        cImgVUserInfoTask = view.findViewById(R.id.cImgVUserInfoTask);
        txtVUsernameInfoTask = view.findViewById(R.id.txtVUsernameInfoTask);
        txtVEmailInfoTask = view.findViewById(R.id.txtVEmailInfoTask);

        txtVTitleInfoTask = view.findViewById(R.id.txtVTitleInfoTask);
        txtVHourInfoTask = view.findViewById(R.id.txtVHourInfoTask);
        txtVDateInfoTask = view.findViewById(R.id.txtVDateInfoTask);
        txtVDescriptionInfoTask = view.findViewById(R.id.txtVDescriptionInfoTask);

        recyclerVTeamTaskInfo = view.findViewById(R.id.recyclerVTeamTaskInfo);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        recyclerVTeamTaskInfo.setLayoutManager(linearLayoutManager);

        mFriendsTeamAdapter = new FriendsTeamAdapter(taskU.getFriendsTask(), context, taskU.getId());
        recyclerVTeamTaskInfo.setAdapter(mFriendsTeamAdapter);

        mUserProvider.getUser(taskU.getIdUser()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.contains("imageProfile")){
                        Picasso.with(context).load(documentSnapshot.getString("imageProfile")).into(cImgVUserInfoTask);
                    }
                    txtVUsernameInfoTask.setText(documentSnapshot.getString("userName"));
                    txtVEmailInfoTask.setText(documentSnapshot.getString("email"));
                }
            }
        });

        txtVTitleInfoTask.setText(taskU.getTitleTask());
        txtVDescriptionInfoTask.setText(taskU.getDescriptionTask());
        txtVHourInfoTask.setText(taskU.getHourTask());
        txtVDateInfoTask.setText(taskU.getDateTask());

        imgVCloseTaskInfoDialog = view.findViewById(R.id.imgVCloseTaskInfoDialog);
        imgVCloseTaskInfoDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    // INSTANCIAMOS LA VISTA QUE QUEREMOS USAR
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_task,parent,false);
        return new ViewHolder(view);
    }

    // INSTANCIAMOS CADA UNA DE LAS VISTAS DEL CARDVIEW
    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView textViewTitle;
        TextView textViewDescription;
        TextView textViewDate, textViewHour;
        ImageView imageViewEditTask;
        CheckBox checkboxTask;
        LinearLayout linearContainerCardview;


        public ViewHolder(View view){
            super(view);

            textViewTitle = view.findViewById(R.id.textViewTitlePostCard);
            textViewDescription = view.findViewById(R.id.textViewDescriptionPostCard);
            textViewDate = view.findViewById(R.id.textViewDatePostCard);
            textViewHour = view.findViewById(R.id.textViewHourPostCard);
            imageViewEditTask = view.findViewById(R.id.imageViewEditTask);
            checkboxTask = view.findViewById(R.id.checkboxTask);
            linearContainerCardview = view.findViewById(R.id.linearContainerCardview);

            mAuthProvider = new AuthProvider();
            mTaskProvider = new TaskProvider();
            mUserProvider = new UsersProvider();
        }

    }
}

package com.example.whatsmytask.providers;


import com.example.whatsmytask.models.TaskU;
import com.example.whatsmytask.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TaskProvider {


    CollectionReference mCollection;

    public TaskProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("Task");
    }




    public Task<Void> saveTask(TaskU taskU){
        return mCollection.document().set(taskU);
    }

    public Query getAll(){
        return mCollection.orderBy("titleTask",Query.Direction.DESCENDING);
    }

    public Query getTaskByUser(String id){
        return mCollection.whereEqualTo("idUser",id);
    }

    public Query getTaskPending(String id){return mCollection.whereEqualTo("idUser",id).whereEqualTo("taskCheck", false);}

    public Query getTaskDone(String id){return mCollection.whereEqualTo("idUser", id).whereEqualTo("taskCheck", true);}

    public Query getTeamTask(String id){return mCollection.whereArrayContains("friendsTask", id);}

    public Task<DocumentSnapshot> getTaskById(String id){
        return mCollection.document(id).get();
    }

    public Query getTaskFriends(String id){
        return mCollection.whereArrayContains("friendsTask", id);
    }

    public Task<Void> deleteTask(String id){
        return mCollection.document(id).delete();
    }

    public Task<Void> updateTask (TaskU taskU){
        // si se quiere actualizar mas valores simplemente se agrega un nuevo map.put
        Map<String,Object> map = new HashMap<>();
        map.put("id",taskU.getId());
        map.put("titleTask", taskU.getTitleTask());
        map.put("descriptionTask", taskU.getDescriptionTask());
        map.put("dateTask", taskU.getDateTask());
        map.put("hourTask", taskU.getHourTask());
        map.put("taskAlarmDate", taskU.getTaskAlarmDate());
        map.put("friendsTask", taskU.getFriendsTask());
        map.put("taskCheck", taskU.isTaskCheck());
        return mCollection.document(taskU.getId()).update(map);
    }


    }



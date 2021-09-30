package com.example.whatsmytask.models;



import java.util.ArrayList;
import java.util.Calendar;


public class TaskU {

    private String id;
    private String idUser;
    private String titleTask;
    private String descriptionTask;
    private String dateTask;
    private String hourTask;
    ArrayList<String> friendsTask;
    private long timestamp;
    private long taskAlarmDate;
    private boolean taskCheck;


    public TaskU(){}

    // CONSTRUCTOR

    public TaskU(String id, String idUser, String titleTask, String descriptionTask,
                 String dateTask, String hourTask, ArrayList<String> friendsTask, long timestamp, long taskAlarmDate, boolean taskCheck) {
        this.id = id;
        this.idUser = idUser;
        this.titleTask = titleTask;
        this.descriptionTask = descriptionTask;
        this.dateTask = dateTask;
        this.hourTask = hourTask;
        this.friendsTask = friendsTask;
        this.timestamp = timestamp;
        this.taskAlarmDate = taskAlarmDate;
        this.taskCheck = taskCheck;

    }


    // FIN CONSTRUCTOR

    // GETTER SETTER



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getTitleTask() {
        return titleTask;
    }

    public void setTitleTask(String titleTask) {
        this.titleTask = titleTask;
    }

    public String getDescriptionTask() {
        return descriptionTask;
    }

    public void setDescriptionTask(String descriptionTask) {
        this.descriptionTask = descriptionTask;
    }

    public String getDateTask() {
        return dateTask;
    }

    public void setDateTask(String dateTask) {
        this.dateTask = dateTask;
    }

    public String getHourTask() {
        return hourTask;
    }

    public void setHourTask(String hourTask) {
        this.hourTask = hourTask;
    }

    public ArrayList<String> getFriendsTask() {
        return friendsTask;
    }

    public void setFriendsTask(ArrayList<String> friendsTask) {
        this.friendsTask = friendsTask;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTaskAlarmDate() {
        return taskAlarmDate;
    }

    public void setTaskAlarmDate(long taskAlarmDate) {
        this.taskAlarmDate = taskAlarmDate;
    }

    public boolean isTaskCheck() {
        return taskCheck;
    }

    public void setTaskCheck(boolean taskCheck) {
        this.taskCheck = taskCheck;
    }
}

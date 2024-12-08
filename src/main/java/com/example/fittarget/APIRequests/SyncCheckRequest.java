package com.example.fittarget.APIRequests;

import com.example.fittarget.FitTargetDatabaseHelper;

import java.util.List;
import java.util.Map;

public class SyncCheckRequest {
    private String userId;
    private List<Map<String, String>> workoutsPendingUpload;
    private List<Object> foodEntriesPendingUpload;
    private String lastLocalSync;

    public SyncCheckRequest(FitTargetDatabaseHelper DB) {
        this.userId = DB.getUserId();
        this.workoutsPendingUpload = DB.getWorkoutsPendingUpload();
        this.foodEntriesPendingUpload = DB.getFoodEntriesPendingUpload();
        this.lastLocalSync = DB.getLastLocalSync();
    }
}

package com.example.fittarget.APIRequests;

import com.example.fittarget.FitTargetDatabaseHelper;
import com.example.fittarget.objects.Workout;

import java.util.List;
import java.util.Map;

public class SyncRequest {
    private String userId;
    private List<Map<String, String>> workoutsPendingUpload;
    private List<Map<String, String>> exercisesPendingUpload;
    private List<Map<String, String>> setsPendingUpload;
    private String lastLocalSync;

    public SyncRequest(FitTargetDatabaseHelper DB) {
        this.userId = DB.getUserId();
        this.workoutsPendingUpload = DB.getWorkoutsForUpload();
        this.exercisesPendingUpload = DB.getExercisesForUpload(workoutsPendingUpload);
        this.setsPendingUpload = DB.getSetsForUpload(exercisesPendingUpload);
        this.lastLocalSync = DB.getLastLocalSync();
    }
}


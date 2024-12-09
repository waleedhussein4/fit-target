package com.example.fittarget.APIResponses;

import com.example.fittarget.objects.Workout;

public class SyncResponse {
    private boolean success;

    public SyncResponse(boolean success) {
        this.success = success;
    }

    public boolean getSuccess() {
        return success;
    }
}

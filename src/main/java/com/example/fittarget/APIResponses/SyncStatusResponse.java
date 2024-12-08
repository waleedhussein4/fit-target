package com.example.fittarget.APIResponses;

public class SyncStatusResponse {
    private boolean is_synced;
    private String last_sync_time;

    public boolean isSynced() {
        return is_synced;
    }
}
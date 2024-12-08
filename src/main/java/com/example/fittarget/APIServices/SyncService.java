package com.example.fittarget.APIServices;

import com.example.fittarget.APIRequests.SyncCheckRequest;
import com.example.fittarget.APIRequests.SyncRequest;
import com.example.fittarget.APIResponses.SyncStatusResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface SyncService {
    @POST("/sync/check-sync")
    Call<SyncStatusResponse> checkSyncStatus(@Body SyncCheckRequest request);

    @POST("/sync/")
    Call<SyncRequest> syncAllData();
}

package com.example.samuel.pentrufacultate.managers;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import com.example.samuel.pentrufacultate.products.clients.AllProductsCallClient;

public class SyncProductsInformationJobService extends JobService {
    private static final String TAG = SyncProductsInformationJobService.class.getSimpleName();
    boolean isWorking = false;
    boolean jobCancelled = false;

    // Called by the Android system when it's time to run the job
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Job started!");
        isWorking = true;
        // We need 'jobParameters' so we can call 'jobFinished'
        startWorkOnNewThread(jobParameters); // Services do NOT run on a separate thread

        return isWorking;
    }

    private void startWorkOnNewThread(final JobParameters jobParameters) {
        new Thread(new Runnable() {
            public void run() {
                doWork(jobParameters);
            }
        }).start();
    }

    private void doWork(JobParameters jobParameters) {
        AllProductsCallClient getProductsClient = new AllProductsCallClient();
        getProductsClient.run(SyncProductsInformationJobService.this);
    }

    // Called if the job was cancelled before being finished
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "Job cancelled before being completed.");
        jobCancelled = true;
        boolean needsReschedule = isWorking;
        jobFinished(jobParameters, needsReschedule);
        return needsReschedule;
    }
}

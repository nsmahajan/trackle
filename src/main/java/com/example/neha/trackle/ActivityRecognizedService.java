package com.example.neha.trackle;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import android.app.IntentService;
import android.content.Intent;
import android.util.Config;
import android.util.Log;

import java.util.List;

public class ActivityRecognizedService extends IntentService{
String Activity = "";
    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            String S = getFriendlyName(result.getMostProbableActivity().getType()); //+", Confidence: "+result.getMostProbableActivity().getConfidence();
            broadcastMessage(S);
        }
    }
    private static String getFriendlyName_test(int detected_activity_type){
        if(detected_activity_type == DetectedActivity.STILL)
            return "no";
        else
            return "drive";
    }
    private static String getFriendlyName(int detected_activity_type){
        if(detected_activity_type == DetectedActivity.RUNNING)
            return "drive";
        else
            return "no";
    }

    void broadcastMessage(String S) {
        Intent brIntent = new Intent();
        brIntent.addCategory(Intent.CATEGORY_DEFAULT);
        brIntent.setAction("com.example.neha.trackle.UPDATE");
        brIntent.putExtra("ACTIVITY",S);
        sendBroadcast(brIntent);
    }
}
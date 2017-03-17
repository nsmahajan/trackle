package com.example.neha.trackle;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

/**
 * Created by neha on 4/3/2016.
 */
public class SettingsFragment extends Fragment{
    private static final String MyPREFERENCES = "MyPrefs";
    private SharedPreferences sharedpreferences;
    private ToggleButton parkingHistory, parkingAlerts;
    private SharedPreferences.Editor editor;

    /**
     * The function loads the layout file for the fragment.Assigns click events to button in the layout and set defaults values.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return returns the view instance.
     *
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String getHistorySetting = sharedpreferences.getString("HistoryEnabled", "");
        String getParkingAlertSetting = sharedpreferences.getString("ParkingAlert", "");

        parkingHistory = (ToggleButton)rootView.findViewById(R.id.parkingHistory);
        parkingAlerts = (ToggleButton)rootView.findViewById(R.id.parkingAlerts);

        Log.e("settings", ""+getHistorySetting);
        Log.e("settings", ""+getParkingAlertSetting);
        editor = sharedpreferences.edit();

        if(getHistorySetting.equals("")){
            parkingHistory.setChecked(true);
            editor.putString("HistoryEnabled", "true");
            editor.commit();
        }else{
            if(getHistorySetting.equals("true"))
                parkingHistory.setChecked(true);
            else
                parkingHistory.setChecked(false);
        }

        if(getParkingAlertSetting.equals("")){
            parkingAlerts.setChecked(false);
            editor.putString("ParkingAlert", "false");
            editor.commit();
        }else{
            if(getParkingAlertSetting.equals("true"))
                parkingAlerts.setChecked(true);
            else
                parkingAlerts.setChecked(false);
        }

        parkingAlerts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    editor.putString("ParkingAlert", "true");
                    editor.commit();
                }
                else {
                    editor.putString("ParkingAlert", "false");
                    editor.commit();
                }
            }
        });

        parkingHistory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    editor.putString("HistoryEnabled", "true");
                    editor.commit();
                }
                else {
                    editor.putString("HistoryEnabled", "false");
                    editor.commit();
                }
            }
        });
        return rootView;
    }
}

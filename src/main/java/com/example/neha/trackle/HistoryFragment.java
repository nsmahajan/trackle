package com.example.neha.trackle;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by neha on 4/3/2016.
 */
public class HistoryFragment extends Fragment implements OnMapReadyCallback {
    private QueryLab queryLab = null;
    private GoogleMap googleMap;
    private Context mContext;
    private LinearLayout mScrollList;
    private TextView mDateRange;
    private RelativeLayout mMapHolder;

    private List<HistoryRecord> historyRecords = new ArrayList<>();
    private String[] mMonths = {"Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec"};
    private Date today;
    private Calendar calEnd = Calendar.getInstance();
    private Calendar calStart = Calendar.getInstance();
    private FloatingActionButton previousButton, nextButton;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
    private String installDate = "";
    private SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    private String currentUUIDUsed = "";

    /**
     * The function loads the layout file for the fragment.Assigns click events to button in the layout and set defaults values.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return returns the view instance.
     *
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);

        previousButton = (FloatingActionButton) rootView.findViewById(R.id.prevButton);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPreviousWeek();
            }
        });

        nextButton = (FloatingActionButton) rootView.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNextWeek();
            }
        });

        mContext = container.getContext();
        queryLab = new QueryLab(mContext);
        mScrollList = (LinearLayout) rootView.findViewById(R.id.scrollList);
        mDateRange = (TextView) rootView.findViewById(R.id.dateRange);
        mMapHolder = (RelativeLayout) rootView.findViewById(R.id.mapHolder);
        mMapHolder.setVisibility(View.GONE);
        mMapHolder.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideMapHolder();
            }
        });

        PackageManager pm = mContext.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo("com.example.neha.trackle", 0);
            Field field = PackageInfo.class.getField("firstInstallTime");
            long timestamp = field.getLong(info);
            Date date = new Date(timestamp);
            Calendar cal  = Calendar.getInstance();
            cal.setTime(date);
            installDate = sdf.format(cal.getTimeInMillis());
            Log.e("DATE", date + "");
        }catch(PackageManager.NameNotFoundException ne){
            ne.printStackTrace();
        }catch (IllegalAccessException e1) {
            e1.printStackTrace();
        }catch (NoSuchFieldException e1) {
            e1.printStackTrace();
        }

        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        currentUUIDUsed = sharedpreferences.getString("UUID", "");
        setInitialDateRange();
        return rootView;
    }


    /**
     * The function loads the current week by calculating the initial start and end date.
     * Enables/disables next and prev buttons accordingly.
     * Each time when the fragment starts the enddate is todays date and hence next button is disabled.
     * In case the data available is only for a week, previous button will be disabled.
     *
     * @param -
     * @return -
     *
     */
    private void setInitialDateRange(){
        today = new Date();
        calEnd.setTime(today);
        calEnd.set(Calendar.HOUR_OF_DAY, 23);
        calEnd.set(Calendar.MINUTE, 59);
        calEnd.set(Calendar.SECOND, 59);
        calEnd.set(Calendar.MILLISECOND, 999);

        String endDate = sdf.format(calEnd.getTimeInMillis());

        calStart.setTime(today);
        calStart.add(Calendar.DATE, -6);
        calStart.set(Calendar.HOUR_OF_DAY, 0);
        calStart.set(Calendar.MINUTE, 0);
        calStart.set(Calendar.SECOND, 0);
        calStart.set(Calendar.MILLISECOND, 0);
        String startDate = sdf.format(calStart.getTimeInMillis());

        showHistory(startDate, endDate);

        boolean isInstallInRange = checkDates(startDate, endDate, installDate);

        nextButton.setEnabled(false);
        nextButton.setVisibility(View.INVISIBLE);

        if(isInstallInRange == true){
            previousButton.setEnabled(false);
            previousButton.setVisibility(View.INVISIBLE);

        }
    }

    /**
     * The function calculates the new start and end date when previous button is clicked.
     * Enables/disables next and prev buttons accordingly.
     * In case the data is not available for a previous week, previous button will be disabled.
     *
     *
     * @param -
     * @return -
     *
     */
    private void setPreviousWeek(){

        calEnd.add(Calendar.DATE, -7);
        String endDate = sdf.format(calEnd.getTimeInMillis());

        calStart.add(Calendar.DATE, -7);
        String startDate = sdf.format(calStart.getTimeInMillis());

        boolean isTodayInRange = checkDates(startDate,endDate, installDate);

        if(isTodayInRange == true){
            nextButton.setEnabled(false);
            nextButton.setVisibility(View.INVISIBLE);

            previousButton.setEnabled(true);
            previousButton.setVisibility(View.VISIBLE);
        }else {
            nextButton.setEnabled(true);
            nextButton.setVisibility(View.VISIBLE);

            previousButton.setEnabled(true);
            previousButton.setVisibility(View.VISIBLE);
        }

        showHistory(startDate, endDate);
    }

    /**
     * The function calculates the new start and end date when next button is clicked.
     * Enables/disables next and prev buttons accordingly.
     * In case the current date is between the end and start date, next button will be disabled.
     *
     *
     * @param -
     * @return -
     *
     */
    private void setNextWeek(){

        calStart.add(Calendar.DATE, 7);
        String startDate = sdf.format(calStart.getTimeInMillis());

        calEnd.add(Calendar.DATE, 7);
        String endDate = sdf.format(calEnd.getTimeInMillis());

        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        boolean isTodayInRange = checkDates(startDate,endDate, sdf.format(cal.getTimeInMillis()));

        if(isTodayInRange == true){
            nextButton.setEnabled(false);
            nextButton.setVisibility(View.INVISIBLE);

            previousButton.setEnabled(true);
            previousButton.setVisibility(View.VISIBLE);
        }else {
            nextButton.setEnabled(true);
            nextButton.setVisibility(View.VISIBLE);

            previousButton.setEnabled(true);
            previousButton.setVisibility(View.VISIBLE);
        }

        showHistory(startDate, endDate);
    }

    /**
     * The function calls the database query class to fetched the data for the week defined by the start and end date.
     * Sends the fetched values to be displayed on the UI.
     *
     *
     * @param startDate String contains the start date for a week.
     * @param endDate String contains the end date for the week.
     * @return -
     *
     */
    private void showHistory(String startDate, String endDate) {
        String []endDateTime = endDate.split(" ");
        String []enddate = endDateTime[0].split("-");

        String []startDateTime = startDate.split(" ");
        String []startdate = startDateTime[0].split("-");

        mDateRange.setText(startdate[2] + " " + mMonths[Integer.parseInt(startdate[1]) - 1] + ", " + startdate[0] + " to " + enddate[2] + " " + mMonths[Integer.parseInt(enddate[1]) - 1] + ", " + enddate[0]);
        historyRecords.clear();
        historyRecords = queryLab.getHistoryRecords(startDate, endDate);
        updateUI();
    }

    /**
     * The function updates the scroll list UI by repopulating the history records fetched from the database.
     * Assigns a delete button functionality to each history record.
     *
     * @param -
     * @return -
     *
     */
    private void updateUI() {
        mScrollList.removeAllViews();
        for (int i = 0; i < historyRecords.size(); i++) {
            if(historyRecords.get(i).getHistoryEnabled().equals("true") && !historyRecords.get(i).getID().equals(UUID.fromString(currentUUIDUsed))) {
                HistoryRecordHolder historyRecordHolder = new HistoryRecordHolder(this.mContext);
                historyRecordHolder.bindCrime(historyRecords.get(i), queryLab, getActivity());
                historyRecordHolder.setEventHandler(new HistoryRecordHolder.EventHandler() {
                    @Override
                    public void deleteRecord(View view, HistoryRecord historyRecord) {
                        delete(view, historyRecord);
                    }

                    @Override
                    public void showLocation(double latitude, double longitude) {
                        showLocationMap(latitude, longitude);
                    }
                });
                mScrollList.addView(historyRecordHolder);
            }
        }
    }

    /**
     * The function is called when delete button of a individual record is clicked.
     * It query's the database to delete the record from the database.
     *
     * @param view contains the reference to the history record UI for which delete was clicked.
     * @param historyRecord Contains the reference to the record on which delete is called.
     * @return -
     *
     */
    public void delete(View view, HistoryRecord historyRecord){
        mScrollList.removeView(view);
        queryLab.deleteHistoryRecord(historyRecord.getID().toString());
    }

    /**
     * The function is called when location button of a individual record is clicked.
     * It opens a google map in a dialog box and displays a marker for the location on the map.
     *
     * @param latitude contains the latitude for a location.
     * @param longitude contains the longitude for a location.
     * @return -
     *
     */
    public void showLocationMap(double latitude, double longitude){
        LatLng parkedLL = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions()
                .position(parkedLL)
                .title(""));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(parkedLL));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        mMapHolder.setVisibility(View.VISIBLE);
    }

    /**
     * The function loads the google maps.
     *
     * @param gMap contains the instance of the google map.
     * @return -
     *
     */
    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        try {
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        }catch(SecurityException se){
            String message = "Unable to load map";
            makeToast(message);
        }
    }

    /**
     * The function hides the map holder.
     *
     * @param -
     * @return -
     *
     */
    public void hideMapHolder(){
        mMapHolder.setVisibility(View.GONE);
    }

    /**
     * The function is used to show error message to the user.
     *
     * @param -
     * @return -
     *
     */
    private void makeToast(String message){
        Context context = this.mContext;
        int duration = Toast.LENGTH_SHORT;
        Toast.makeText(context, message, duration).show();
    }

    /**
     * The function is used to decide when to show and hide next and previous button.
     *
     *
     * @param startDate contains the start date for the week.
     * @param endDate contains the end date for the week.
     * @param targetDate contains the date which is to be checked if it lies within the date range given by startdate and enddate.
     * @return boolean returns true if within the date range or false.
     *
     */
    public boolean checkDates(String startDate, String endDate, String targetDate) {
        boolean b = false;

        try {
            if(sdf.parse(targetDate).after(sdf.parse(startDate)) && sdf.parse(targetDate).before(sdf.parse(endDate))){
                b = true;
            }
            else {
                b = false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return b;
    }
}
package com.example.neha.trackle;

import android.Manifest;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.location.Address;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
* Class "ParkFragment" for detecting activities of the phone
* Parent - Fragment
* Inherited Interfaces - nMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener
*/
public class ParkFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap googleMap = null;
    private Context mContext;
    PendingIntent pendingIntent;
    private FloatingActionButton fab_dir, fab_park, fab_edit, fab_done, fab_auto, fab_manual;
    private GoogleApiClient mGoogleApiClient = null;
    private LocationRequest mLocationRequest = null;
    private Boolean isMapReady = false;
    private ArrayList<Polyline> drawPath = new ArrayList<Polyline>();
    Marker markDestination = null;
    private LatLng parkedLLDest = null, parkedLL = null, parkedLLTag = null;
    private boolean firstTimeCurrentLocation = false;
    private boolean autoMode = false;
    private boolean parkMode = false;
    //String serverKey = "AIzaSyAVY9jRg6agxTcGC1DTzv5fFqwE1IGNU0w";  // arunvodc
    //String serverKey = "AIzaSyCkVucBkgHFMs3OJTmzUVvzkACC-iUq6AY"; // arun4av
    String serverKey = "AIzaSyA3siZXVkth_phFllgDg6-vq2EV1gkn3y8"; // arun4av (2)
    private boolean directionClicked = false;
    private boolean onDirectionClickedUpdateSourceLocation = false;
    private boolean keepMarker = false;
    private HistoryRecord currentHistoryRecord = null;
    private SharedPreferences sharedpreferences;
    private MyBroadcastReceiver myBroadcastReceiver;
    public static final String MyPREFERENCES = "MyPrefs";
    private QueryLab queryLab = null;
    private String previousMode = "no", currentMode;
    private TextView distance, time;

    /*
    * Class for BroadcastReceiver to receive the activity from ActivityRecognizedService every second.
     */
    public class MyBroadcastReceiver extends BroadcastReceiver {
        /*
        * Overridden function to receive the Broadcast message sent from ActivityRecognizedService to ParkFragement
        * @param context - An instance of class Context passed by the BroadcastReceiver class
        * @param intent - An instance of class Intent provided by the BroadcastReceiver class
        * @return -
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("TAG!","onReceive: "+intent.getStringExtra("ACTIVITY"));
            currentMode = intent.getStringExtra("ACTIVITY");
            //makeToast("Mode: " + currentMode);
            if(previousMode.equals("drive") && currentMode.equals("no")) {
                parkMode = true;
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return; }
                Location myLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                parkedLLTag = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                Log.d("TAG!","onReceive: Tagging");
            }
            previousMode = currentMode;
            if(currentMode.equals("drive"))
                parkMode = false;
        }
    }
    /*
    * Overridden function that gets called when the view is created for the Fragment (ParkFragement)
    * @param inflater - An instance of class LayoutInflater passed by the Fragment class
    * @param container - An instance of class ViewGroup passed by the Fragment class
    * @param savedInstanceState - An instance of class Bundle passed by the Fragment class
    * @return - rootView of type View class
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_park, container, false);
        mContext = container.getContext();
        queryLab = new QueryLab(mContext);
        distance = (TextView) rootView.findViewById(R.id.distance);
        time = (TextView) rootView.findViewById(R.id.time);
        distance.setText("");
        time.setText("");
        distance.setVisibility(View.GONE);
        time.setVisibility(View.GONE);
        firstTimeCurrentLocation = false;
        directionClicked = false;
        myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter("com.example.neha.trackle.UPDATE");
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        mContext.registerReceiver(myBroadcastReceiver, intentFilter);

        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fab_dir = (FloatingActionButton) rootView.findViewById(R.id.fab_dir);
        fab_park = (FloatingActionButton) rootView.findViewById(R.id.fab_park);
        fab_edit = (FloatingActionButton) rootView.findViewById(R.id.fab_edit);
        fab_done = (FloatingActionButton) rootView.findViewById(R.id.fab_done);
        fab_auto = (FloatingActionButton) rootView.findViewById(R.id.fab_auto);
        fab_manual = (FloatingActionButton) rootView.findViewById(R.id.fab_manual);

        fab_done.hide();

        fab_dir.setOnClickListener(new View.OnClickListener() { // Floating Action Button for Direction
            @Override
            public void onClick(View view) {
                directionClicked = true;
                onDirectionClickedUpdateSourceLocation = true;
                fab_dir.hide();
                fab_done.show();
                distance.setVisibility(View.VISIBLE);
                time.setVisibility(View.VISIBLE);
                startLocationUpdates();
            }
        });

        fab_edit.setOnClickListener(new View.OnClickListener() { // Floating Action Button for Edit
            @Override
            public void onClick(View view) {
                MyDialogFragment newFragment = new MyDialogFragment();
                newFragment.bindRecord(currentHistoryRecord, queryLab);
                newFragment.setOnDismissListener(new MyDialogFragment.OnDismissListener() {
                    @Override
                    public void onDismiss(MyDialogFragment myDialogFragment) {
                        String data = myDialogFragment.getUserData();
                        String[] arr = data.split(";");
                        currentHistoryRecord.setDuration(arr[1]);
                        currentHistoryRecord.setCost(arr[0]);
                        currentHistoryRecord.setNote(arr[2]);
                        currentHistoryRecord.setPaidParking((arr[3].equals("true") ? "Yes" : "No"));
                        updatetoDB();
                    }
                });
                newFragment.show(getActivity().getFragmentManager(),"dialog");
            }
        });

        fab_park.setOnClickListener(new View.OnClickListener() { // Floating Action Button for Park
            @Override
            public void onClick(View view) {
                directionClicked = false;
                stopLocationUpdates();
                tagLocation();
                distance.setVisibility(View.GONE);
                time.setVisibility(View.GONE);
                    createNewTag();
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("UUID", currentHistoryRecord.getID().toString());
                    editor.putString("markerFlag", "Yes");
                    editor.putString("isDirectionClicked", "false");
                    editor.commit();
                fab_done.hide();
                fab_dir.show();
            }
        });

        fab_done.setOnClickListener(new View.OnClickListener() { // Floating Action Button for Done
            @Override
            public void onClick(View view) {
                directionClicked = false;
                if(!autoMode)
                    stopLocationUpdates();
                fab_dir.show();
                fab_done.hide();
                distance.setVisibility(View.GONE);
                time.setVisibility(View.GONE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("isDirectionClicked", "false");
                editor.putString("markerFlag", "No");
                editor.commit();
                removeTagDir();
            }
        });

        fab_auto.setOnClickListener(new View.OnClickListener() { // Floating Action Button to Auto "M" +
            @Override
            public void onClick(View view) {
                autoMode = true;
                directionClicked = false;
                fab_auto.hide();
                fab_manual.show();
                fab_park.hide();
                fab_dir.show();
                fab_done.hide();
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("auto", "auto");
                editor.putString("isDirectionClicked", "false");
                editor.commit();
                startActivityRecognitionUpdates();
                startLocationUpdates();
                if(!keepMarker)
                    removeTagDir();
                keepMarker = false;
            }
        });

        fab_manual.setOnClickListener(new View.OnClickListener() { // Floating Action Button to Manual "A" X
            @Override
            public void onClick(View view) {
                autoMode = false;
                directionClicked = false;
                fab_auto.show();
                fab_manual.hide();
                fab_park.show();
                fab_dir.show();
                fab_done.hide();
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("auto", "manual");
                editor.putString("isDirectionClicked", "false");
                editor.commit();
                stopActivityRecognitionUpdates();
                stopLocationUpdates();
                if(!keepMarker)
                    removeTagDir();
                keepMarker = false;
            }
        });

        createLocationRequest();
        buildGoogleApiClient();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

        fab_done.hide();

        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String savedUUID = sharedpreferences.getString("UUID", "");

        if(!savedUUID.equals("")) {
            Log.e("uuid", ""+savedUUID);
            getSaveRecord(savedUUID);
            parkedLLDest = new LatLng(currentHistoryRecord.getLatitude(), currentHistoryRecord.getLongitude());
        }

        String savedLatSource = sharedpreferences.getString("latitude_source", "");
        String savedLonSource = sharedpreferences.getString("longitude_source", "");
        if(!savedLatSource.equals("") && !savedLonSource.equals("")) {
            parkedLL = new LatLng(Double.parseDouble(savedLatSource), Double.parseDouble(savedLonSource));
        }
        Log.d("TAG!", "Dest: "+parkedLLDest);

        return rootView;
    }
    /*
    * Function for saving a new location information to the database
    * @param -
    * @return -
    */
    private void createNewTag(){
        if(queryLab != null) {
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            List<Address> addresses = null;
            String Address = "";
            try {
                addresses = geocoder.getFromLocation(parkedLLDest.latitude, parkedLLDest.longitude, 1);
                Address = addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getAddressLine(1);
            }
            catch (Exception e) {
                String message = "Unable to get location address";
                makeToast(message);
            }

            currentHistoryRecord = new HistoryRecord();
            currentHistoryRecord.setDate(getDate());
            currentHistoryRecord.setLatitude(parkedLLDest.latitude);
            currentHistoryRecord.setLongitude(parkedLLDest.longitude);
            currentHistoryRecord.setAddress(Address);
            currentHistoryRecord.setImageCount(0);

            String historyEnabled = sharedpreferences.getString("HistoryEnabled", "");
            if(historyEnabled.equals("")){
                currentHistoryRecord.setHistoryEnabled("true");
            }else{
                if(historyEnabled.equals("true")){
                    currentHistoryRecord.setHistoryEnabled("true");
                }else{
                    currentHistoryRecord.setHistoryEnabled("false");
                }
            }

            queryLab.insertRecord(currentHistoryRecord);
        }
    }
    /*
    * Function for removing the tag and the direction path from the Map
    * @param -
    * @return -
    */
    private void removeTagDir() {
        Log.d("TAG!", "RemoveTag");
        if(markDestination!=null)
            markDestination.remove();
        if(drawPath.size() != 0) {
            for(int i=0 ; i < drawPath.size() ; i++)
                drawPath.get(i).remove();
            drawPath.clear();
        }
    }
    /*
    * Function for retrieving the date
    * @param -
    * @return - Type SimpleDateFormat that returns time in milliseconds
    */
    private String getDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
        Calendar cal = Calendar.getInstance();
        Date today = new Date();
        cal.setTime(today);
        return sdf.format(cal.getTimeInMillis());
    }
    /*
    * Function for updating the current location data to the database
    * @param -
    * @return -
    */
    public void updatetoDB() {
        if(currentHistoryRecord != null && queryLab != null) {
            queryLab.updateRecord(currentHistoryRecord);
        }
    }
    /*
    * Function to create a new LocationRequest
    * @param -
    * @return -
    */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    /*
    * Function for setting a new connection
    * @param -
    * @return -
    */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this.mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                //.addApi(Fitness.SENSORS_API)
                //.addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                .build();
    }
    /*
    * Function to strat location updates
    * @param -
    * @return -
    */
    private void startLocationUpdates() {
        if(mGoogleApiClient != null) {
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } catch (SecurityException se) {
                String message = "Unable to get location updates";
                makeToast(message);
            }
        }
    }
    /*
    * Function to stop location updates
    * @param -
    * @return -
    */
    private void stopLocationUpdates() {
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }
    /*
    * Overridden function to set initial dependencies when the Map is loaded
    * @param - gMap of type GoogleMap passed by the interface OnMapReadyCallback
    * @return -
    */
    @Override
    public void onMapReady(GoogleMap gMap) {
        isMapReady = true;
        googleMap = gMap;
        try {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            //googleMap.moveCamera(CameraUpdateFactory.newLatLng(parkedLLDest));
            //googleMap.animateCamera(CameraUpdateFactory.zoomTo(19));

            sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            String markerFlagValue = sharedpreferences.getString("markerFlag", "");
            String autoMode_str = sharedpreferences.getString("auto", "");
            String Auto = "auto";
            String Yes = "Yes";
            String isDirectionEnabled = sharedpreferences.getString("isDirectionClicked", "");
            String True = "true";
            if(autoMode_str.equals(Auto)) {
                autoMode = true;
                click_auto_button();
            }
            else {
                autoMode = false;
                click_manual_button();
            }
            if(markerFlagValue.equals(Yes) && parkedLLDest != null) {
                keepMarker = true;
                markDestination = googleMap.addMarker(new MarkerOptions()
                        .position(parkedLLDest)
                        .title("You parked here!"));
            }
            if(isDirectionEnabled.equals(True)) {
                directionClicked = true;
                onDirectionClickedUpdateSourceLocation = true;
                click_dir_button();
                fab_dir.hide();
                fab_done.show();
                distance.setVisibility(View.VISIBLE);
                time.setVisibility(View.VISIBLE);
            }
        }catch(SecurityException se){
            String message = "Unable to load map";
            makeToast(message);
        }
    }
    /*
    * Function to simulate Direction button click
    * @param -
    * @return -
    */
    void click_dir_button() {
        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                fab_dir.post(new Runnable(){
                    @Override
                    public void run() {
                        fab_dir.performClick();
                    }
                });
            }
        }, 1000, TimeUnit.MILLISECONDS);
    }
    /*
    * Function to simulate Automatic Tagging button click
    * @param -
    * @return -
    */
    void click_auto_button() {
        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                fab_auto.post(new Runnable(){
                    @Override
                    public void run() {
                        Log.d("TAG!", "auto");
                        fab_auto.performClick();
                    }
                });
            }
        }, 900, TimeUnit.MILLISECONDS);
    }
    /*
    * Function to simulate Manual Tagging button click
    * @param -
    * @return -
    */
    void click_manual_button() {
        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                fab_manual.post(new Runnable(){
                    @Override
                    public void run() {
                        Log.d("TAG!", "manual");
                        fab_manual.performClick();
                    }
                });
            }
        }, 900, TimeUnit.MILLISECONDS);
    }
    /*
    * Function to simulate Park button click
    * @param -
    * @return -
    */
    void click_park_button() {
        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                fab_park.post(new Runnable(){
                    @Override
                    public void run() {
                        fab_park.performClick();
                    }
                });
            }
        }, 100, TimeUnit.MILLISECONDS);
    }
    /*
    * Overridden function to start Location and Activity  Recognition Updates when the connection is established
    * @param - bundle of type Bundle passed by the interface ConnectionCallbacks of GoogleApiClient API
    * @return -
    */
    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
        Intent intent = new Intent(mContext, ActivityRecognizedService.class);
        pendingIntent = PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        startActivityRecognitionUpdates();
    }
    /*
    * Function to Start Activity Recognition Updates
    * @param -
    * @return -
    */
    public void startActivityRecognitionUpdates() {
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGoogleApiClient, 1000, pendingIntent);
    }
    /*
    * Function to Stop Activity Recognition Updates
    * @param -
    * @return -
    */
    public void stopActivityRecognitionUpdates() {
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mGoogleApiClient, pendingIntent);
    }
    /*
    * Overridden function of interface ConnectionCallbacks of GoogleApiClient API called when the connection is suspended
    * @param - Integer "i"
    * @return -
    */
    @Override
    public void onConnectionSuspended(int i) {

    }
    /*
    * Overridden function of interface ConnectionCallbacks of GoogleApiClient API called when the connection is failed
    * @param - connectionResult of class ConnectionResult
    * @return -
    */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }
    /*
    * Overridden function of interface LocationListener called when the location is changed
    * @param - location of class Location
    * @return -
    */
    @Override
    public void onLocationChanged(Location location) {
        if(directionClicked){
            drawPathFunc();
        }
        if(autoMode && parkMode)
            autoTag();
        updateUserLocation(location);
    }
    /*
    * Function for Auto Tagging parked location
    * @param -
    * @return -
    */
    public void autoTag() {
        if(parkedLLTag == null) {
            return;
        }
        else {
            float distances[] = new float[1];
            Location.distanceBetween(parkedLLTag.latitude, parkedLLTag.longitude, parkedLL.latitude, parkedLL.longitude, distances);
            String S = "Distance: " + distances[0];
            Log.d("TAG!", S);// + distances[0]);
            makeToast(S);
            if (distances[0] > 30) {
                Log.d("TAG!", "TAGGED!");// + distances[0]);
                click_park_button();
            }
        }
    }
    /*
    * Function for updating user's current location
    * @param - myLocation of type class Location
    * @return -
    */
    public void updateUserLocation(Location myLocation){
        if(isMapReady) {
            if(!firstTimeCurrentLocation) {
                double latitude = myLocation.getLatitude();
                double longitude = myLocation.getLongitude();

                LatLng latLng = new LatLng(latitude, longitude);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(19));

                firstTimeCurrentLocation = true;
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                stopLocationUpdates();
            }
        }
    }

    private void makeToast(String message){
        Context context = this.mContext;
        int duration = Toast.LENGTH_SHORT;
        Toast.makeText(context, message, duration).show();
    }
    /*
    * Function for updating user's current location
    * @param - myLocation of type class Location
    * @return -
    */
    private void tagLocation() {
        try {
            Location myLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(drawPath.size() != 0) {
                for(int i=0 ; i < drawPath.size() ; i++)
                    drawPath.get(i).remove();
                drawPath.clear();
            }
            double latitude = myLocation.getLatitude();
            double longitude = myLocation.getLongitude();
            parkedLLDest = new LatLng(latitude, longitude);
            if(autoMode && parkedLLTag != null) {
                parkedLLDest = parkedLLTag;
            }
            if(markDestination!=null)
                markDestination.remove();
            markDestination = googleMap.addMarker(new MarkerOptions()
                    .position(parkedLLDest)
                    .title("You parked here!"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(parkedLLDest));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(19));
            parkMode = false;
        }
        catch(SecurityException se){
            String message = "Unable to tag current location on  map";
            makeToast(message);
        }
    }
    /*
    * Function implement the path on the Map
    * @param -
    * @return -
    */
    void drawPathFunc() {
        try {
            Location myLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            double latitude = myLocation.getLatitude();
            double longitude = myLocation.getLongitude();
            parkedLL = new LatLng(latitude, longitude);
            if(onDirectionClickedUpdateSourceLocation) { // Executes once for everytime direction button is clicked
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("isDirectionClicked", "true");
                editor.putString("latitude_source", Double.toString(parkedLL.latitude));
                editor.putString("longitude_source", Double.toString(parkedLL.longitude));
                editor.commit();
                onDirectionClickedUpdateSourceLocation = false;
            }
            drawPathToDestination();
        }
        catch(SecurityException se){
            String message = "Unable to get directions";
            makeToast(message);
        }
    }
    /*
    * Function to draw the path on the Map
    * @param -
    * @return -
    */
    private void drawPathToDestination()
    {
        liveUpdates();
        GoogleDirection.withServerKey(serverKey)
                .from(parkedLL)
                .to(parkedLLDest)
                .transitMode("walking")
                .transportMode("walking")
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if(drawPath.size() != 0) {
                            for(int i=0 ; i < drawPath.size() ; i++) {
                                drawPath.get(i).remove();
                            }
                            drawPath.clear();
                        }
                        if (direction.isOK()) {
                            List<Step> stepList = direction.getRouteList().get(0).getLegList().get(0).getStepList();
                            ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(mContext, stepList, 5, Color.RED, 3, Color.BLUE);
                            for (PolylineOptions polylineOption : polylineOptionList) {
                                drawPath.add(googleMap.addPolyline(polylineOption));
                            }
                        }
                    }
                    @Override
                    public void onDirectionFailure(Throwable t) {
                    }
                });
    }
    /*
     * Function to show live updates for distance and time to reach the destination
     * @param -
     * @return -
     */
    public void liveUpdates() {
        float distances[] = new float[1];
        Location.distanceBetween(parkedLLDest.latitude, parkedLLDest.longitude, parkedLL.latitude, parkedLL.longitude, distances);
        Double d = new Double(Math.round(distances[0]));
        if(d.intValue() == 1)
            distance.setText("1 m");
        else
            distance.setText(d.intValue()+" m");
        float secs = d.floatValue() / (float) 1.35;
        Float f = new Float(Math.round(secs));
        String timeStr = "";
        int res_min = f.intValue()/60;
        int res_sec = f.intValue()%60;
        String sM = "", sS = "";
            sM = " m";
            sS = " s";
        if(f.intValue() > 59)
            timeStr = res_min+sM+res_sec+sS;
        else
            timeStr = res_sec+sS;
        time.setText(timeStr);
    }
    /*
     * Function to save the record
     * @param - String type as uuid
     * @return -
     */
   public void getSaveRecord(String uuid) {
        if(queryLab != null){
            currentHistoryRecord = queryLab.getRecordWithUUID(uuid);
        }
    }
}
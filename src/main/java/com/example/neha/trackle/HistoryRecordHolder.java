package com.example.neha.trackle;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

/**
 * Created by neha on 4/5/2016.
 */
public class HistoryRecordHolder extends LinearLayout {
    private TextView mParkingTime, mLocationAddress, mParkingDate;
    private ImageButton mDeleteRecord, mShowLocation;
    private TextView mPaidParkingValue,mParkingCostValue,mParkingDurationValue,mNoteValue;
    private Animation animShow;
    private LinearLayout mDetails;
    private boolean detailsVisible = false;

    private HistoryRecord mHistoryRecord;
    private EventHandler eventHandler = null;
    private Context mContext;
    private QueryLab mQueryLab;
    private Activity mActivity;

    private int[] imageViews = new int[]{R.id.historyimageView1, R.id.historyimageView2, R.id.historyimageView3};
    private String[] mMonths = {"Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec"};

    /**
     * The function loads the layout file for the single history record.Assigns click events to button in the layout and set defaults values.
     *
     * @param context
     * @return returns the view instance.
     *
     */
    public HistoryRecordHolder(Context context) {
        super(context);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.list_item_history_record, this, true);

        this.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                showDetails();
            }
        });

        animShow = AnimationUtils.loadAnimation( mContext, R.anim.view_show);

        mParkingTime = (TextView) this.findViewById(R.id.parkingTime);
        mParkingDate = (TextView) this.findViewById(R.id.parkingDate);
        mLocationAddress = (TextView) this.findViewById(R.id.locationAddress);
        mDeleteRecord = (ImageButton) this.findViewById(R.id.deleteRecord);
        mShowLocation = (ImageButton) this.findViewById(R.id.showLocation);

        mPaidParkingValue = (TextView) this.findViewById(R.id.paidParkingValue);
        mParkingCostValue = (TextView) this.findViewById(R.id.parkingCostValue);
        mParkingDurationValue = (TextView) this.findViewById(R.id.parkingDurationValue);
        mNoteValue = (TextView) this.findViewById(R.id.noteValue);
        mDetails = (LinearLayout) this.findViewById(R.id.details);

        mDeleteRecord.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                deleteRecord();
            }
        });

        mShowLocation.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                showLocation();
            }
        });

    }

    /**
     * The function sends the location latitude and longitude to be shown on the google maps.This sends an event to the parent.
     *
     * @param -
     * @return -
     */
    private void showLocation(){
        eventHandler.showLocation(mHistoryRecord.getLatitude(), mHistoryRecord.getLongitude());
    }

    /**
     * The function opens and closes the details box when the record is clicked.
     *
     * @param -
     * @return -
     *
     */
    private void showDetails(){
        if(!detailsVisible) {
            mDetails.setVisibility(View.VISIBLE);
            mDetails.startAnimation(animShow);
            detailsVisible = true;
        }else {
            mDetails.setVisibility(View.GONE);
            detailsVisible = false;
        }
    }

    /**
     * The function sends the delete event to the parent class, so that the record can be deleted from the scroll list and database.
     *
     * @param -
     * @return -
     *
     */
    private void deleteRecord(){
        eventHandler.deleteRecord(this, mHistoryRecord);
    }

    /**
     * The function sends the delete event to the parent class, so that the record can be deleted from the scroll list and database.
     *
     * @param record contains the data for the record
     * @param queryLab contains the reference to the class which handles database queries
     * @return -
     *
     */
    public void bindCrime(HistoryRecord record, QueryLab queryLab, Activity activity) {
        mHistoryRecord = record;
        mQueryLab = queryLab;
        mActivity = activity;
        String []dateTime = record.getDate().split(" ");
        String []date = dateTime[0].split("-");
        String []time = dateTime[1].split(":");

        String dateString = date[2] + " " + mMonths[Integer.parseInt(date[1]) - 1] + ", " + date[0];
        String timeString;

        if(Integer.parseInt(time[0]) > 12){
            timeString = (Integer.parseInt(time[0]) - 12) + ":" + time[1] + " pm";
        }else {
            timeString = time[0]+ ":" + time[1] + " am";
        }

        mParkingDate.setText(dateString);
        mParkingTime.setText(timeString);
        mLocationAddress.setText(record.getAddress());

        mPaidParkingValue.setText(record.getPaidParking());
        mParkingCostValue.setText(record.getCost() + "$");
        mParkingDurationValue.setText(record.getDuration());
        mNoteValue.setText(record.getNote());
        updateAllImages();
    }

    /**
     * The function initializes the event handler .
     *
     * @param eventHandler
     * @return -
     *
     */
    public void setEventHandler(EventHandler eventHandler)
    {
        this.eventHandler = eventHandler;
    }

    /**
     * The event handler interface is defined which handles the event clicks for the record and sends these event to the parent class.
     *
     */
    public interface EventHandler{
        void deleteRecord(View view, HistoryRecord mHistoryRecord);
        void showLocation(double latitude, double longitude);
    }

    /**
     *
     * The function loads all the images in the record and assigns a click event for each photo.
     * @param -
     * @return -
     *
     *
     */
    private void updateAllImages(){
        for(int i = 0; i < 3; i++){
            ImageView tempImageView = (ImageView) this.findViewById(imageViews[i]);
            tempImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadPhoto(v.getTag().toString());
                }
            });
            File tempFile = mQueryLab.getPhotoFile(mHistoryRecord ,i);

            if(!tempFile.exists()){
                tempImageView.setImageDrawable(null);
            }else {
                Bitmap bitmap = PictureUtils.getScaledBitmap(tempFile.getPath(), mActivity);
                tempImageView.setImageBitmap(bitmap);
            }
        }
    }

    /**
     *
     * The function load a image in the appropriate image view.
     * @param -
     * @return -
     *
     *
     */
    private void loadPhoto(String tag) {
        final Dialog dialog = new Dialog(mContext);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_fullimage_dialog);

        ImageView image = (ImageView) dialog.findViewById(R.id.image);
        File tempFile;
        Bitmap bitmap;
        switch(tag){
            case "historyimageView1":
                tempFile = mQueryLab.getPhotoFile(mHistoryRecord ,0);
                bitmap = PictureUtils.getScaledBitmap(tempFile.getPath(), mActivity);
                image.setImageBitmap(bitmap);
                break;

            case "historyimageView2":
                tempFile = mQueryLab.getPhotoFile(mHistoryRecord ,1);
                bitmap = PictureUtils.getScaledBitmap(tempFile.getPath(), mActivity);
                image.setImageBitmap(bitmap);
                break;

            case "historyimageView3":
                tempFile = mQueryLab.getPhotoFile(mHistoryRecord ,2);
                bitmap = PictureUtils.getScaledBitmap(tempFile.getPath(), mActivity);
                image.setImageBitmap(bitmap);
                break;
        }

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
    }
}

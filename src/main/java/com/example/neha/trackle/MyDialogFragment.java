package com.example.neha.trackle;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.io.File;

/**
 * Created by neha on 4/15/2016.
 */
public class MyDialogFragment extends DialogFragment {

    private SeekBar parkingCostSeekBar, parkingDurationSeekBar;
    private double minCost = 0.25, costInterval = 0.25;
    private int maxDuration = 180, minDuration = 10, durationInterval = 5, maxCost = 30;
    private TextView parkingCostInput, parkingDurationInput;
    private Switch paidParkingSwitch;
    private EditText noteInputText;
    private double cost = 0;
    private int duration = 0;
    private OnDismissListener onDismissListener;
    private HistoryRecord record = null;
    private File mPhotoFile;
    private ImageView mPhotoView;
    private int[] imageViews = new int[]{R.id.imageView0, R.id.imageView1, R.id.imageView2};
    private int imageCount;
    private ImageButton cameraButton;
    private View view;
    final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    private static final int REQUEST_PHOTO= 2;
    private PackageManager packageManager;
    private QueryLab queryLab = null;

    public interface OnDismissListener {
        void onDismiss(MyDialogFragment myDialogFragment);
    }

    /**
     * The function loads the layout file for the dialog fragment.Assigns click events to button in the layout and set defaults values.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return returns the view instance.
     *
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_park_dialog, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        view = v;
        parkingCostSeekBar = (SeekBar)v.findViewById(R.id.parkingCostSeekBar);
        parkingDurationSeekBar = (SeekBar)v.findViewById(R.id.parkingDurationSeekBar);
        parkingCostInput = (TextView)v.findViewById(R.id.parkingCostInput);
        parkingDurationInput = (TextView)v.findViewById(R.id.parkingDurationInput);
        paidParkingSwitch = (Switch)v.findViewById(R.id.paidParkingSwitch);
        noteInputText = (EditText)v.findViewById(R.id.noteInputText);
        cameraButton = (ImageButton) v.findViewById(R.id.cameraButton);
        packageManager = getActivity().getPackageManager();

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        paidParkingSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    parkingDurationSeekBar.setEnabled(true);
                    parkingCostSeekBar.setEnabled(true);

                } else {
                    parkingDurationSeekBar.setEnabled(false);
                    parkingCostSeekBar.setEnabled(false);
                }
                parkingDurationSeekBar.setProgress(0);
                parkingCostSeekBar.setProgress(0);
                parkingCostInput.setText("");
                parkingDurationInput.setText("");
            }
        });

        parkingCostSeekBar.setMax(119);
        parkingCostSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser)
            {
                cost = minCost + (progress * costInterval);
                parkingCostInput.setText(cost + "");
            }
        });

        parkingDurationSeekBar.setMax(34);
        parkingDurationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
              {
                  @Override
                  public void onStopTrackingTouch(SeekBar seekBar) {}

                  @Override
                  public void onStartTrackingTouch(SeekBar seekBar) {}

                  @Override
                  public void onProgressChanged(SeekBar seekBar, int progress,
                                                boolean fromUser)
                  {
                      duration = minDuration + (progress * durationInterval);
                      parkingDurationInput.setText(duration + "");
                  }
              }
        );


        loadData();
        return v;
    }

    /**
     * The function is called by the parent class to get the data from this dialog fragment.
     *
     * @param -
     * @return returns a string made of cost, duration, notes and paid parking.
     *
     */
    public String getUserData(){
        return cost + ";" + duration + ";" + noteInputText.getText() + ";" + paidParkingSwitch.isChecked();
    }

    /**
     * The function initializes the dismiss listener for the dialog fragment.
     *
     * @param dismissListener
     * @return -
     *
     */
    public void setOnDismissListener(OnDismissListener dismissListener) {
        onDismissListener = dismissListener;
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (onDismissListener != null) {
            onDismissListener.onDismiss(this);
        }
    }

    /**
     * The function initializes the record values.
     *
     * @param record contains the data for the record
     * @param lab contains the reference to the class which handles database queries
     * @return -
     *
     */
    public void bindRecord(HistoryRecord record, QueryLab lab){
        this.queryLab = lab;
        this.record = record;
    }

    /**
     * The function initializes the UI with the record values.
     *
     * @param -
     * @return -
     *
     */
    public void loadData(){
        if(record != null) {
            noteInputText.setText(record.getNote());

            if (record.getPaidParking().equals("No")) {
                parkingCostInput.setText("");
                paidParkingSwitch.setChecked(false);
                parkingDurationInput.setText("");
                parkingDurationSeekBar.setProgress(0);
                parkingCostSeekBar.setProgress(0);
                parkingDurationSeekBar.setEnabled(false);
                parkingCostSeekBar.setEnabled(false);
            } else {
                paidParkingSwitch.setChecked(true);
                parkingCostInput.setText(record.getCost());
                parkingDurationInput.setText(record.getDuration());

                int durationProgress = (Integer.parseInt(record.getDuration()) - minDuration) / durationInterval;
                parkingDurationSeekBar.setProgress(durationProgress);

                int costProgress = (int) ((Double.parseDouble(record.getCost()) - minCost) / costInterval);
                parkingCostSeekBar.setProgress(costProgress);
                parkingDurationSeekBar.setEnabled(true);
                parkingCostSeekBar.setEnabled(true);
            }
        }

        imageCount = record.getImageCount();
        mPhotoView = (ImageView) view.findViewById(imageViews[imageCount]);
        mPhotoFile = queryLab.getPhotoFile(record, imageCount);

        setCameraSettings();
        updateAllImages();
    }

    /**
     * The function checks whether camera can be started.
     *
     * @param -
     * @return -
     *
     */
    private void setCameraSettings(){
        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        cameraButton.setEnabled(canTakePhoto);

        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
    }

    /**
     * The function loads the images associated with the record.
     *
     * @param -
     * @return -
     *
     */
    private void updateAllImages(){
        for(int i = 0; i < 3; i++){
            ImageView tempImageView = (ImageView) view.findViewById(imageViews[i]);
            File tempFile = queryLab.getPhotoFile(record ,i);

            if(!tempFile.exists()){
                tempImageView.setImageDrawable(null);
            }else {
                Bitmap bitmap = PictureUtils.getScaledBitmap(tempFile.getPath(), getActivity());
                tempImageView.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_PHOTO) {
            updatePhotoView();
        }
    }

    /**
     * The function loads the images captured by the camera.
     * Creates a new image file for a new image and if the captured images become greater than 3 then start by replacing the first images.
     * The function takes care that only 3 images can be captured for a record.
     *
     * @param -
     * @return -
     *
     */
    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }

        imageCount = imageCount + 1;

        if(imageCount > 2){
            imageCount = 0;
        }

        record.setImageCount(imageCount);
        mPhotoView = (ImageView) view.findViewById(imageViews[imageCount]);
        mPhotoFile = queryLab.getPhotoFile(record, imageCount);
        setCameraSettings();
    }
}

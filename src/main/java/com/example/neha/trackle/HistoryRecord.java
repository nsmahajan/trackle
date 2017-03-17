package com.example.neha.trackle;

import java.util.UUID;
/**
 * Created by neha on 4/4/2016.
 */
public class HistoryRecord {
    private UUID mId;
    private String mDate;
    private double mLatitude;
    private double mLongitude;
    private String mAddress;
    private String mDuration = "";
    private String mCost = "";
    private String mNote = "";
    private String mPaidParking = "No";
    private int mImageCount = 0;
    private String mHistoryEnabled = "";

    /**
     * The constructor creates a new record with a new UUID.
     *
     *
     * @param -
     * @return -
     *
     */
    public HistoryRecord(){
        this(UUID.randomUUID());
    }

    /**
     * The constructor is called if UUID of a record is already known.
     *
     *
     * @param id UUID for a record.
     * @return -
     *
     */
    public HistoryRecord(UUID id) {
        mId = id;
    }

    /**
     * The method returns the UUID associated with the record.
     *
     *
     * @param -
     * @return mID contains the UUID.
     *
     */
    public UUID getID(){
        return mId;
    }

    /**
     * The method returns the date associated with the record.
     *
     *
     * @param -
     * @return mDate contains the date when the record was created.
     *
     */
    public String getDate(){
        return mDate;
    }

    /**
     * The method returns the latitude for the location associated with the record.
     *
     *
     * @param -
     * @return mLatitude contains the latitude when the location was tagged.
     *
     */
    public double getLatitude(){
        return mLatitude;
    }

    /**
     * The method returns the longitude for the location associated with the record.
     *
     *
     * @param -
     * @return mLatitude contains the longitude when the location was tagged.
     *
     */
    public double getLongitude(){
        return mLongitude;
    }

    /**
     * The method returns the address for the location associated with the record.
     *
     *
     * @param -
     * @return mAddress contains the address when the location was tagged.
     *
     */
    public String getAddress(){
        return mAddress;
    }

    /**
     * The method returns the duration for the timed parking location associated with the record.
     *
     *
     * @param -
     * @return mDuration contains the duration for a timed parking provided by the user.
     *
     */
    public String getDuration(){
        return mDuration;
    }

    /**
     * The method returns the cost for the timed parking location associated with the record.
     *
     *
     * @param -
     * @return mCost contains the cost for a timed parking provided by the user.
     *
     */
    public String getCost(){
        return mCost;
    }

    /**
     * The method returns the note for the parking location set by the user, associated with the record.
     *
     *
     * @param -
     * @return mNote contains the note provided by the user.
     *
     */
    public String getNote(){
        return mNote;
    }

    /**
     * The method returns boolean value for timed parking.
     *
     *
     * @param -
     * @return mPaidParking contains the boolean value to check if parking is timed or not.
     *
     */
    public String getPaidParking(){
        return mPaidParking;
    }

    /**
     * The method is called to set the UUID for a record.
     *
     *
     * @param id contains the UUID.
     * @return -
     *
     */
    public void setId(UUID id){
        mId = id;
    }

    /**
     * The method is called to set the date for a record.
     *
     *
     * @param date contains the date.
     * @return -
     *
     */
    public void setDate(String date){
        mDate = date;
    }

    /**
     * The method is called to set the latitude for a location associated with the record.
     *
     *
     * @param latitude contains the latitude.
     * @return -
     *
     */
    public void setLatitude(double latitude){
        mLatitude = latitude;
    }

    /**
     * The method is called to set the longitude for a location associated with the record.
     *
     *
     * @param longitude contains the longitude.
     * @return -
     *
     */
    public void setLongitude(double longitude){
        mLongitude = longitude;
    }

    /**
     * The method is called to set the address for a location associated with the record.
     *
     *
     * @param address contains the address.
     * @return -
     *
     */
    public void setAddress(String address){
        mAddress = address;
    }

    /**
     * The method is called to set the duration for a timed parking associated with the record.
     *
     *
     * @param duration contains the duration.
     * @return -
     *
     */
    public void setDuration(String duration){
        mDuration = duration;
    }

    /**
     * The method is called to set the cost for a timed parking associated with the record.
     *
     *
     * @param cost contains the duration.
     * @return -
     *
     */
    public void setCost(String cost){
        mCost = cost;
    }

    /**
     * The method is called to set the note associated with the record.
     *
     *
     * @param note contains the duration.
     * @return -
     *
     */
    public void setNote(String note){
        mNote = note;
    }

    /**
     * The method is called to set the parking type(paid(yes)/unpaid(no)).
     *
     *
     * @param paidParking contains yes(if paid parking) otherwise no.
     * @return -
     *
     */
    public void setPaidParking(String paidParking){
        mPaidParking = paidParking;
    }

    /**
     * The method returns the number of images clicked by the user for the record.
     *
     *
     * @param -
     * @return mImageCount contains the integer values between 0-2
     *
     */
    public int getImageCount(){
        return mImageCount;
    }

    /**
     * The method helps to decide if the record is to be shown in the history fragment or not.
     *
     *
     * @param - historyEnabled contains yes(history enabled) or no(history not enabled)
     * @return -
     *
     */
    public void setHistoryEnabled(String historyEnabled){
        mHistoryEnabled = historyEnabled;
    }

    /**
     * The method helps to decide if the record is to be shown in the history fragment or not.
     *
     *
     * @param -
     * @return mHistoryEnabled contains yes(history enabled) or no(history not enabled)
     *
     */
    public String getHistoryEnabled(){
        return mHistoryEnabled;
    }

    public void setImageCount(int count){
        mImageCount = count;
    }

    /**
     * The method creates a new image file for the record.
     * If image files already exists for the imageCount then return the image file without creating a new image file.
     *
     *
     * @param imageCount contains the integer values between 0-2
     * @return imagefile contains the reference to the image file.
     *
     */
    public String getPhotoFilename(int imageCount) {
        return "IMG_" + getID().toString() + "_" + imageCount + ".jpg";
    }
}

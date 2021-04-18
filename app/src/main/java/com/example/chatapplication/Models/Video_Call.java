package com.example.chatapplication.Models;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Video_Call {

    private String userName;
    private String sRoom;
    private String rRoom;
    private String call_time;
    private static String inputFormat = "HH:mm";
    private int day,month,year;
    SimpleDateFormat inputParser = new SimpleDateFormat(inputFormat, Locale.getDefault());
    Calendar calendar = Calendar.getInstance();

    public String getsRoom() {
        return sRoom;
    }

    public void setsRoom(String sRoom) {
        this.sRoom = sRoom;
    }

    public String getrRoom() {
        return rRoom;
    }

    public void setrRoom(String rRoom) {
        this.rRoom = rRoom;
    }

    public String getCall_time() {
        return call_time;
    }

    public void setCall_time(String currentTime) {
        this.call_time = currentTime;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setUserName(String userName){
        this.userName = userName;
    }

    public String getUserName(){
        return userName;
    }

    public Video_Call(String userName,String sRoom, String rRoom, String currentTime, int day, int month, int year) {
        this.userName = userName;
        this.sRoom = sRoom;
        this.rRoom = rRoom;
        this.call_time = currentTime;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public Video_Call() {
    }

    public boolean compareDates(String sDate){

        Date date;
        Date dateCompareOne;
        Date dateCompareTwo;

        String h = String.valueOf(calendar.get(Calendar.HOUR));
        String minute_current = String.valueOf(calendar.get(Calendar.MINUTE));
        String minute_plus_five = String.valueOf(calendar.get(Calendar.MINUTE) + 5);

        String compareStringOne= h + ":" + minute_current ;
        String compareStringTwo= h + ":" + minute_plus_five;

        date = parseDate(sDate);
        dateCompareOne = parseDate(compareStringOne);
        dateCompareTwo = parseDate(compareStringTwo);

        if ( dateCompareOne.after( date ) && dateCompareTwo.before(date)) {
            return true;
        }
        return false;
    }

    private Date parseDate(String date) {

        try {
            return inputParser.parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }


}

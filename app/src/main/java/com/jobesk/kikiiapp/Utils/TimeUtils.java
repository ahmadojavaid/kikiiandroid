package com.jobesk.kikiiapp.Utils;

public class TimeUtils {
    public static String getTime(Long time) {

        long estimatedTime = System.currentTimeMillis() - time;

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long monthsInMillis = daysInMilli * 30;
        long yearsInMillis = monthsInMillis * 12;


        long elapsedYears = estimatedTime / yearsInMillis;
        estimatedTime = estimatedTime % yearsInMillis;

        long elapsedMonths = estimatedTime / monthsInMillis;
        estimatedTime = estimatedTime % monthsInMillis;

        long elapsedDays = estimatedTime / daysInMilli;
        estimatedTime = estimatedTime % daysInMilli;

        long elapsedHours = estimatedTime / hoursInMilli;
        estimatedTime = estimatedTime % hoursInMilli;

        long elapsedMinutes = estimatedTime / minutesInMilli;
        estimatedTime = estimatedTime % minutesInMilli;

        //long elapsedSeconds = estimatedTime / secondsInMilli;

        if (elapsedYears > 0) {
            return elapsedYears + "yrs ago";
        } else if (elapsedMonths > 0) {
            return elapsedMonths + "mon ago";
        } else if (elapsedDays > 0) {
            return elapsedDays + "d ago";
        } else if (elapsedHours > 0) {
            return elapsedHours + " hrs. ago";
        } else if (elapsedMinutes > 0) {
            return elapsedMinutes + " min ago";
        } else {
            return "now";
        }
     /*   System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);

        Log.d("dateeee",""+elapsedDays+":"+elapsedHours+":"+elapsedMinutes+":"+elapsedSeconds);*/
    }

}

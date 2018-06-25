package iyd2.projects.newsviewer.utils;

import android.content.Context;
import android.content.res.Resources;
import android.text.format.DateUtils;

import java.util.Calendar;
import java.text.SimpleDateFormat;

import iyd2.projects.newsviewer.R;

public class DateUtil {

    public static SimpleDateFormat getMainSimpleDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    }

    public static String getTimeAgo(long time, Context ctx) {
        if (time < 1000000000000L) {
            time *= 1000;
        }

        Resources resources = ctx.getResources();

        long now = Calendar.getInstance().getTime().getTime();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < DateUtils.MINUTE_IN_MILLIS) {
            return resources.getString(R.string.just_now);
        } else if (diff < 60 * DateUtils.MINUTE_IN_MILLIS) {
            long mins = diff / DateUtils.MINUTE_IN_MILLIS;
            StringBuilder res = new StringBuilder(String.valueOf(mins)).append(" ");
            if (mins < 2) {
                return resources.getString(R.string.minute_ago);
            } else if ((mins >= 5 && mins <= 20) || (mins % 10 == 0 && mins > 20)) {
                return res.append(resources.getString(R.string.minutes_ago_since_5)).toString();
            } else if (mins % 10 == 1) {
                return res.append(resources.getString(R.string.minute_ago)).toString();
            } else if (mins % 10 <= 4) {
                return res.append(resources.getString(R.string.minutes_ago_till_5)).toString();
            } else {
                return res.append(resources.getString(R.string.minutes_ago_since_5)).toString();
            }
        } else if (diff < 24 * DateUtils.HOUR_IN_MILLIS) {
            long hours = diff / DateUtils.HOUR_IN_MILLIS;
            StringBuilder res = new StringBuilder(String.valueOf(hours)).append(" ");
            if (hours < 2) {
                return resources.getString(R.string.hour_ago);
            } else if ((hours >= 5 && hours <= 20) || (hours % 10 == 0 && hours > 20)) {
                return res.append(resources.getString(R.string.hours_ago_since_5)).toString();
            } else if (hours % 10 == 1) {
                return res.append(resources.getString(R.string.hour_ago)).toString();
            } else if (hours % 10 <= 4) {
                return res.append(resources.getString(R.string.minutes_ago_till_5)).toString();
            } else {
                return res.append(resources.getString(R.string.minutes_ago_since_5)).toString();
            }
        } else {
            long days = diff / DateUtils.DAY_IN_MILLIS;
            StringBuilder res = new StringBuilder(String.valueOf(days)).append(" ");
            if (days < 2) {
                return resources.getString(R.string.yesterday);
            } else if (days < 3) {
                return resources.getString(R.string.days_before_yst);
            } else if ((days >= 5 && days <= 20) || (days % 10 == 0 && days > 20)) {
                return res.append(resources.getString(R.string.days_ago_since_5)).toString();
            } else if (days % 10 == 1) {
                return res.append(resources.getString(R.string.day_ago)).toString();
            } else if (days % 10 <= 4) {
                return res.append(resources.getString(R.string.days_ago_till_5)).toString();
            } else {
                return res.append(resources.getString(R.string.days_ago_since_5)).toString();
            }
        }
    }
}

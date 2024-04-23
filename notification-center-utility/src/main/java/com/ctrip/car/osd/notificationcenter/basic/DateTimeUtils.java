package com.ctrip.car.osd.notificationcenter.basic;

import cn.hutool.core.lang.Tuple;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.annotation.Obsolete;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;

/**
 * Created by xiayx on 2020/5/7.
 */
public class DateTimeUtils {
    public static final int ONE_MINUTE = 60 * 1000;
    public static final int ONE_HOUR = 60 * ONE_MINUTE;
    public static final long ONE_DAY = 24 * ONE_HOUR;
    //默认时区（北京）
    public static final int DEFAULT_TIME_ZONE = 8;

    public static final String Standard_TimeFormat = "yyyy-MM-dd HH:mm:ss";
    public static String[] Date_ParsePatterns = {"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyyMMdd", "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"};

    //当前时间===========================================================================
    public static Date dateNow() {
        Calendar calendar = Calendar.getInstance();
        Date time = calendar.getTime();
        return time;
    }

    public static Timestamp timestampNow() {
        long time = System.currentTimeMillis();
        return new Timestamp(time);
    }

    public static Calendar calendarNow() {
        Date tempDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(tempDate);
        return calendar;
    }

    public static LocalDateTime localDateNow() {
        return LocalDateTime.now();
    }

    //时间类型互转=========================================================================
    //Date->Calendar
    public static Calendar date2Calendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    //Calendar->Date
    public static Date calendar2date(Calendar date) {
        Calendar ca = Calendar.getInstance();
        return ca.getTime();
    }

    //Date->Timestamp->LocalDateTime
    //Date->LocalDateTime
    public static LocalDateTime date2LocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    //LocalDateTime->Date
    public static Date localDateTime2Date(LocalDateTime date) {
        return Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
    }

    //Date->Timestamp
    public static Timestamp date2Timestamp(Date date) {
        return new Timestamp(date.getTime());
    }

    //Timestamp->Date
    public static Date timestamp2Date(Timestamp date) {
        return new Date(date.getTime());
        //return date;
    }

    //Timestamp->LocalDateTime
    public static LocalDateTime timestamp2LocalDateTime(Timestamp date) {
        return date.toLocalDateTime();
    }

    //LocalDateTime->Timestamp
    public static Timestamp localDateTime2Timestamp(LocalDateTime date) {
        return Timestamp.valueOf(date);
    }

    //时间格式化===========================================================================
    public static String parseStr(Date dateTime) {
        return parseStr(dateTime, Standard_TimeFormat);
    }

    public static String parseStr(Date dateTime, String format) {
        String formattedStr = null;
        try {
            if (dateTime == null || StringUtils.isBlank(format)) {
                return formattedStr;
            }
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            formattedStr = sdf.format(dateTime);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return formattedStr;
    }

    public static String parseStr(LocalDateTime dateTime) {
        return parseStr(dateTime, Standard_TimeFormat);
    }

    public static String parseStr(LocalDateTime localDateTime, String format) {
        String formattedStr = null;
        try {
            if (localDateTime == null || StringUtils.isBlank(format)) {
                return formattedStr;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            formattedStr = formatter.format(localDateTime);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return formattedStr;
    }

    public static String dateNowStr() {
        return parseStr(dateNow());
    }

    public static String dateNowStr(String fromat) {
        return parseStr(localDateNow(), fromat);
    }

    //字符串解析时间========================================================================
    public static Optional<Date> tryParseDate(String str) {
        if (StringUtils.isBlank(str)) {
            return Optional.ofNullable(null);
        }
        try {
            Date date = DateUtils.parseDate(str, Date_ParsePatterns);
            return Optional.ofNullable(date);
        } catch (ParseException e) {
            return Optional.ofNullable(null);
        }
    }

    public static Optional<Calendar> tryParseCalendar(String str) {
        if (StringUtils.isBlank(str)) {
            return Optional.ofNullable(null);
        }
        try {
            Date date = DateUtils.parseDate(str, Date_ParsePatterns);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return Optional.ofNullable(calendar);
        } catch (ParseException e) {
            return Optional.ofNullable(null);
        }
    }

    public static Optional<Timestamp> tryParseTimestamp(String str) {
        if (StringUtils.isBlank(str)) {
            return Optional.ofNullable(null);
        }
        try {
            if (StringUtils.isNumeric(str)) {
                Long longStr = Long.parseLong(str) * 1000;
                Timestamp ts = new Timestamp(longStr);
                return Optional.ofNullable(ts);
            } else {
                Date date = DateUtils.parseDate(str, Date_ParsePatterns);
                Timestamp ts = new Timestamp(date.getTime());
                return Optional.ofNullable(ts);
            }
        } catch (ParseException e) {
            return Optional.ofNullable(null);
        }
    }

    public static Optional<LocalDateTime> tryParseLDate(String str) {
        if (StringUtils.isBlank(str)) {
            return Optional.ofNullable(null);
        }
        try {
            if (StringUtils.isNumeric(str)) {
                //second type timestamp(not millisecond)
                Long longStr = Long.parseLong(str) * 1000;
                Timestamp ts = new Timestamp(longStr);
                return Optional.ofNullable(ts.toLocalDateTime());
            } else {
                Date date = DateUtils.parseDate(str, Date_ParsePatterns);
                Timestamp ts = new Timestamp(date.getTime());
                //ts to localdatetime
                return Optional.ofNullable(ts.toLocalDateTime());
                //return Optional.ofNullable(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
            }
        } catch (ParseException e) {
            return Optional.ofNullable(null);
        }
    }

    @Obsolete
    public static LocalDateTime parseLDate1(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }

        try {
            Date date = DateUtils.parseDate(str, Date_ParsePatterns);
            //存在请求与服务时区不同导致结果不是预期的问题
            LocalDateTime ldt = date.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            return ldt;
        } catch (ParseException e) {
            return null;
        }
    }

    @Obsolete
    public static LocalDateTime parseLDate2(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }

        for (String parttern : Date_ParsePatterns) {
            LocalDateTime dateTime;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(parttern);
                dateTime = LocalDateTime.parse(str, formatter);
            } catch (Exception ex) {
                continue;
            }
            if (dateTime != null) {
                return dateTime;
            }
        }
        return null;
    }

    public static boolean isValidDate(String str) {
        boolean convertSuccess = true;
        //指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        SimpleDateFormat format = new SimpleDateFormat(Standard_TimeFormat);
        try {
            //设置lenient为false.
            //否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            //如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess = false;
        }
        return convertSuccess;
    }

    //时区转化===========================================================================
    public static LocalDateTime convertZone(LocalDateTime localTime, ZoneId fromZone, ZoneId toZone) {
        ZonedDateTime zonedtime = localTime.atZone(fromZone);
        ZonedDateTime converted = zonedtime.withZoneSameInstant(toZone);
        return converted.toLocalDateTime();
    }

    public static LocalDateTime localToUTC(LocalDateTime localTime) {
        ZoneId fromZone = ZoneId.systemDefault();
        ZoneId toZone = ZoneOffset.UTC;
        return convertZone(localTime, fromZone, toZone);
    }

    public static LocalDateTime utcToLocal(LocalDateTime localTime) {
        ZoneId fromZone = ZoneOffset.UTC;
        ZoneId toZone = ZoneId.systemDefault();
        return convertZone(localTime, fromZone, toZone);
    }

    public static LocalDateTime utcToLocal(String utcTime) {
        ZoneId fromZone = ZoneOffset.UTC;
        ZoneId toZone = ZoneId.systemDefault();

        LocalDateTime localDate = localDateNow();
        if (tryParseLDate(utcTime).isPresent()) {
            localDate = tryParseLDate(utcTime).get();
        }
        return convertZone(localDate, fromZone, toZone);
    }

    @Obsolete
    public static Date localToUTC(Date localDate) {
        long localTimeInMillis = localDate.getTime();
        /** long时间转换成Calendar */
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(localTimeInMillis);
        /** 取得时间偏移量 */
        int zoneOffset = calendar.get(java.util.Calendar.ZONE_OFFSET);
        /** 取得夏令时差 */
        int dstOffset = calendar.get(java.util.Calendar.DST_OFFSET);
        /** 从本地时间里扣除这些差量，即可以取得UTC时间*/
        calendar.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        /** 取得的时间就是UTC标准时间 */
        Date utcDate = new Date(calendar.getTimeInMillis());
        return utcDate;
    }

    //时间增减===========================================================================
    public static Date addMonths(Date from, int months) {
        Calendar fromDatetime = Calendar.getInstance();
        fromDatetime.setTime(from);
        fromDatetime.add(Calendar.MONTH, months);
        return fromDatetime.getTime();
    }

    public static Date addDays(Date from, int days) {
        Calendar fromDatetime = Calendar.getInstance();
        fromDatetime.setTime(from);
        fromDatetime.add(Calendar.DAY_OF_YEAR, days);
        return fromDatetime.getTime();
    }

    public static Calendar addDays(Calendar from, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(from.getTime());
        c.add(Calendar.DAY_OF_YEAR, days);
        return c;
    }

    public static Date addHours(Date from, int hours) {
        Calendar c = Calendar.getInstance();
        c.setTime(from);
        c.add(Calendar.HOUR, hours);
        return c.getTime();
    }

    public static Date addMinutes(Date from, int minutes) {
        Calendar c = Calendar.getInstance();
        c.setTime(from);
        c.add(Calendar.MINUTE, minutes);
        return c.getTime();
    }

    public static Date addSeconds(Date from, Integer seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(from);
        calendar.add(Calendar.SECOND, seconds);
        return calendar.getTime();
    }

    public static Tuple dateDiff(Date startDate, Date endDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - startDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;

        return new Tuple(day, hour, min);
    }

    public static double dateDiff(Calendar startDate, Calendar endDate) {
        if (null == startDate || null == endDate) {
            return -1;
        }
        if (endDate.getTimeInMillis() < startDate.getTimeInMillis()) {
            return -1;
        }
        return Math.ceil((double) (endDate.getTimeInMillis() - startDate.getTimeInMillis()) / (ONE_DAY));
    }

    //其他=============================================================================
    public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekDays[w];
    }

    public static int getMonth(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);

        int wd = cal.get(Calendar.MONTH) + 1;
        return wd;
    }

    public static int getWeekOfDay(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);

        int wd = cal.get(Calendar.DAY_OF_WEEK) - 1;
        return wd;
    }

    public static int getMonthOfDay(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);

        int wd = cal.get(Calendar.DAY_OF_MONTH);
        return wd;
    }

    public static int getAgeByBirth(Calendar birthday) {
        int age = 0;
        try {
            Calendar now = Calendar.getInstance();
            now.setTime(new Date());// 当前时间

            if (birthday.after(now)) {//如果传入的时间，在当前时间的后面，返回0岁
                age = 0;
            } else {
                age = now.get(Calendar.YEAR) - birthday.get(Calendar.YEAR);
                if (now.get(Calendar.DAY_OF_YEAR) > birthday.get(Calendar.DAY_OF_YEAR)) {
                    age += 1;
                }
            }
            return age;
        } catch (Exception e) {//兼容性更强,异常后返回数据
            return 0;
        }
    }

    public static long getHoursInterval(Date from, Date end) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fromStr = format.format(from);
        String endStr = format.format(end);

        try {
            Date fromtemp = format.parse(fromStr);
            Date endtemp = format.parse(endStr);
            long interval = 0;
            interval = (endtemp.getTime() - fromtemp.getTime()) / (ONE_HOUR);
            return interval;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static Integer getTotalDays(Date start, Date end) {
        Integer totalDay = (int) Math.ceil((double) (end.getTime() - start.getTime()) / (ONE_DAY));
        return totalDay;
    }

    public static double getTotalDay(Date start, Date end) {
        double totalDay = Math.ceil((double) (end.getTime()
                - start.getTime()) / (ONE_DAY));
        return totalDay;
    }

    public static double getTotalDayDouble(Date start, Date end) {
        double totalDay = (double) (end.getTime()
                - start.getTime()) / (ONE_DAY);
        return totalDay;
    }

    public static Date getOneDayBefore(Date dateEnd) {
        Calendar date = Calendar.getInstance();
        date.setTime(dateEnd);
        date.set(Calendar.DATE, date.get(Calendar.DATE) - 1);
        return date.getTime();
    }

    public static boolean isTimeCross(Date beginTime1, Date endTime1, Date beginTime2, Date endTime2) {
        boolean status = beginTime2.after(beginTime1);
        if (status) {
            boolean status2 = beginTime2.after(endTime1);
            if (status2) {
                return false;
            } else {
                return true;
            }
        } else {
            boolean status2 = endTime2.after(beginTime1);
            if (status2) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static Date convertToDate(String dateString, String pattern) {
        try {
            if (StringUtils.isBlank(dateString) || StringUtils.isBlank(pattern)) {
                return null;
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            Date result = simpleDateFormat.parse(dateString);
            return result;
        } catch (Exception ex) {
            //LogUtility.error("Convert datetime exception", ex);
        }
        return null;
    }
}

package com.glz.spring.helloworld.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Component
public class DateTimeTools {

    @Tool(description = "获取用户在指定时区的当前日期和时间，用于回答需要实时时间的问题")
    public String getCurrentTime(){
        //获取当前用户的时区偏好
        ZoneId zoneId = LocaleContextHolder.getTimeZone().toZoneId();
        ZonedDateTime now =LocalDateTime.now().atZone(zoneId);
        //格式化为 yyyy-MM-dd HH:mm:ss
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(now);
    }

    @Tool(description = "设置一个闹钟，调用此工具可在指定时间触发提醒。时间参数必须是ISO-8601格式，例如：2026-10-01 15:30:00")
    public void setAlarm(@ToolParam(description = "闹钟的触发时间，标准格式：yyyy-MM-dd HH:mm:ss")String alarmTime){
        System.out.println("闹钟已设置。将在 " + alarmTime + "提醒用户。");
        //此处可以实现业务逻辑
    }

    @Tool(description = "计算两个日期之间相差的天数")
    public long daysBetween(
            @ToolParam(description = "起始日期，格式 yyyy-MM-dd") String start,
            @ToolParam(description = "结束日期，格式 yyyy-MM-dd") String end){
        LocalDate s = LocalDate.parse(start);
        LocalDate e = LocalDate.parse(end);
        return ChronoUnit.DAYS.between(s,e);
    }
}

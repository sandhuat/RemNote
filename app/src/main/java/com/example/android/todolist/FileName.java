package com.example.android.todolist;

import java.util.Date;

public class FileName {
    private String name;
    private String shorttext;
    private Date dt;
    private long long_dt;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String content;
    private boolean reminder=false;

    public FileName(String _name) {

        this.name = _name;

    }
    public void setDate(Date d)
    {
        dt=d;
    }
    public void setLongDate(long d)
    {
        long_dt=d;
    }
    public long getLongDate()
    {
       return long_dt;
    }
    public Date getDate()
    {
        return dt;
    }

    public void setName(String _name){
        this.name = _name;
    }

    public String getName(){
        return this.name;
    }

    public void setShorttext(String _shorttext){
        this.shorttext = _shorttext;
    }

    public String getShorttext(){
        return this.shorttext;
    }
    public void reminderSet(boolean b)
    {
        reminder=b;
    }
    public boolean isReminderSet()
    {
        return reminder;
    }

}

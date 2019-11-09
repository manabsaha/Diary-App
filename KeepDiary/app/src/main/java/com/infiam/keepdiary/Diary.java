package com.infiam.keepdiary;

/**
 * Created by Manab on 11-05-2019.
 */

public class Diary {

    private String title;
    private String note;
    private String creationtime;
    private long color;


    public Diary() {
    }

    public Diary(String title, String note, String creationtime, long color) {
        this.title = title;
        this.note = note;
        this.creationtime = creationtime;
        this.color = color;
    }

    public long getColor() {
        return color;
    }

    public void setColor(long color) {
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCreationtime() {
        return creationtime;
    }

    public void setCreationtime(String creationtime) {
        this.creationtime = creationtime;
    }


}

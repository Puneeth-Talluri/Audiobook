package com.puneeth.soundsaga;

import java.io.Serializable;
import java.util.ArrayList;

public class Book implements Serializable {

    String title;
    String author;
    String date;
    String language;
    String duration;
    String imageUrl;
    ArrayList<Chapter> chaptersList;

    boolean isRead=false;
    int lastReadChapter;
    String lastChapterTime;

    String lastAccessed;

    String lastChapterDuration;

    String lastChapterTitle;

    int lastPos;

    int lastPlayerTime;

    public Book(String title, String author, String date, String language, String duration, String imageUrl, ArrayList<Chapter> chaptersList) {
        this.title = title;
        this.author = author;
        this.date = date;
        this.language = language;
        this.duration = duration;
        this.imageUrl = imageUrl;
        this.chaptersList = chaptersList;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public String getLanguage() {
        return language;
    }

    public String getDuration() {
        return duration;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public ArrayList<Chapter> getChaptersList() {
        return chaptersList;
    }

    public boolean isRead() {
        return isRead;
    }

    public int getLastReadChapter() {
        return lastReadChapter;
    }

    public String getLastChapterTime() {
        return lastChapterTime;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public void setLastReadChapter(int lastReadChapter) {
        this.lastReadChapter = lastReadChapter;
    }

    public void setLastChapterTime(String lastChapterTime) {
        this.lastChapterTime = lastChapterTime;
    }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                "last accessed='"+ lastAccessed+ '\''+
                ", author='" + author + '\'' +
                ", date='" + date + '\'' +
                ", language='" + language + '\'' +
                ", duration='" + duration + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", isRead=" + isRead +
                ", lastReadChapter='" + lastReadChapter + '\'' +
                ", lastChapterTime='" + lastChapterTime + '\'' +
                ", chaptersList=" + chaptersList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return title != null ? title.equals(book.title) : book.title == null;
    }

    @Override
    public int hashCode() {
        return title != null ? title.hashCode() : 0;
    }

    public String getLastAccessed() {
        return lastAccessed;
    }

    public void setLastAccessed(String lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    public String getLastChapterDuration() {
        return lastChapterDuration;
    }

    public void setLastChapterDuration(String lastChapterDuration) {
        this.lastChapterDuration = lastChapterDuration;
    }

    public String getLastChapterTitle() {
        return lastChapterTitle;
    }

    public void setLastChapterTitle(String lastChapterTitle) {
        this.lastChapterTitle = lastChapterTitle;
    }
}

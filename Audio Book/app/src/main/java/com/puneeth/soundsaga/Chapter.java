package com.puneeth.soundsaga;

import java.io.Serializable;

public class Chapter implements Serializable {

    int number;
    String title;
    String audioUrl;

    public Chapter(int number, String title, String audioUrl) {
        this.number = number;
        this.title = title;
        this.audioUrl = audioUrl;
    }

    @Override
    public String toString() {
        return "Chapter{" +
                "number=" + number +
                ", title='" + title + '\'' +
                ", audioUrl='" + audioUrl + '\'' +
                '}';
    }
}

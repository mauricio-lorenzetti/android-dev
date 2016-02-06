package com.mauk.app.top10downloader;

import java.text.FieldPosition;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Mauk on 09/10/2015.
 */
public class Application {

    private String name;
    private String artist;
    private String releaseDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getReleaseDate() {

        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");
        SimpleDateFormat finalFormat = new SimpleDateFormat("MMMM dd, yyyy");
        Date date = new Date();

        try {
            date = originalFormat.parse(releaseDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return finalFormat.format(date);
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public String toString() {
        return ("Name: " + getName() + "\n" +
                "Artist: " + getArtist() + "\n" +
                "Release Date: " + getReleaseDate());
    }
}

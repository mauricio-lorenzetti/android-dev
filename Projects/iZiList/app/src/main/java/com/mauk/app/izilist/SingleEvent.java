package com.mauk.app.izilist;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Mauk on 30/10/2015.
 */
public class SingleEvent implements Serializable {

    public static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private String hostname;
    private String address;
    private Date date;
    private String description;
    private String imageUri;

    public SingleEvent(String id, String title, String hostname, String address, Date date, String description, String imageUri) {
        this.id = id;
        this.title = title;
        this.hostname = hostname;
        this.address = address;
        this.date = date;
        this.description = description;
        this.imageUri = imageUri;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getHostname() {
        return hostname;
    }

    public String getAddress() {
        return address;
    }

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUri() {
        return imageUri;
    }

    @Override
    public String toString() {
        return "SingleEvent{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", hostname='" + hostname + '\'' +
                ", address='" + address + '\'' +
                ", date=" + date +
                ", description='" + description + '\'' +
                ", imageUri=" + imageUri +
                '}';
    }
}

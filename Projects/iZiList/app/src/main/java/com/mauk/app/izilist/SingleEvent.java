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
    private String price;
    private String imageUri;
    private String logoUri;
    private Boolean amIguest;

    public SingleEvent(String id, String title, String hostname, String address, Date date, String description, String price, String imageUri, String logoUri, Boolean amIguest) {
        this.id = id;
        this.title = title;
        this.hostname = hostname;
        this.address = address;
        this.date = date;
        this.description = description;
        this.price = price;
        this.imageUri = imageUri;
        this.logoUri = logoUri;
        this.amIguest = amIguest;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Boolean getAmIguest() {
        return amIguest;
    }

    public void setAmIguest(Boolean amIguest) {
        this.amIguest = amIguest;
    }

    public String getLogoUri() {
        return logoUri;
    }

    public void setLogoUri(String logoUri) {
        this.logoUri = logoUri;
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
                ", price='" + price + '\'' +
                ", imageUri='" + imageUri + '\'' +
                ", logoUri='" + logoUri + '\'' +
                ", amIguest=" + amIguest +
                '}';
    }
}

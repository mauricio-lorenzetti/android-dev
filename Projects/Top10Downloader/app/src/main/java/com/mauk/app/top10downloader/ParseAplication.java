package com.mauk.app.top10downloader;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by Mauk on 09/10/2015.
 */
public class ParseAplication {

    private String xmlData;
    private ArrayList<Application> applications;

    public ParseAplication(String xmlData) {
        this.xmlData = xmlData;
        applications = new ArrayList<Application>();
    }

    public ArrayList<Application> getApplications() {
        return applications;
    }

    public boolean process(){

        String textValue = "";
        boolean status = true;
        boolean inEntry = false;
        Application current = null;

        try {

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(this.xmlData));

            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {

                String tagName = xpp.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        //Log.d("ParseApplication", "Starting tag for: " + tagName);
                        if (tagName.equalsIgnoreCase("entry")) {
                            inEntry = true;
                            current = new Application();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        textValue = xpp.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        //Log.d("ParseApplication", "Ending tag for: " + tagName);
                        if (inEntry) {
                            if(tagName.equalsIgnoreCase("entry")) {
                                applications.add(current);
                                inEntry = false;
                            } else if (tagName.equalsIgnoreCase("name")) {
                                current.setName(textValue);
                            } else if (tagName.equalsIgnoreCase("artist")) {
                                current.setArtist(textValue);
                            } else if (tagName.equalsIgnoreCase("releaseDate")) {
                                current.setReleaseDate(textValue);
                            }
                        }
                        break;

                    default:

                }
                eventType = xpp.next();

            }

        } catch (Exception e) {
            status = false;
            e.printStackTrace();
        }

        for (Application app : applications) {
            Log.d("ParseApplication", "\nName: " +
                    app.getName() + "\n" + "Artist: " +
                    app.getArtist() + "\n" + "Release Date: " +
                    app.getReleaseDate() + "\n");
        }

        return true;

    }
}

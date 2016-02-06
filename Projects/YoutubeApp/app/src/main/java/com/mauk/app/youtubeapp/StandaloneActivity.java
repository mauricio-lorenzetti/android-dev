package com.mauk.app.youtubeapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.google.android.youtube.player.YouTubeStandalonePlayer;

public class StandaloneActivity extends AppCompatActivity implements View.OnClickListener {

    private String GOOGLE_API_KEY = "AIzaSyAUm0q39wrjl91P-eI9_8zTCXyyd2gdD90";
    private String YOUTUBE_VIDEO_ID = "oHg5SJYRHA0"; //rickroll
    private String YOUTUBE_PLAYLIST_ID = "PLsBI83Bzbnacim19bTUONClIGpT22Zfkw";

    private Button btnVideo;
    private Button btnPlaylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standalone);

        btnVideo = (Button) findViewById(R.id.btn_video);
        btnPlaylist = (Button) findViewById(R.id.btn_playlist);

        btnVideo.setOnClickListener(this);
        btnPlaylist.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_video:
                intent = YouTubeStandalonePlayer.createVideoIntent(this, GOOGLE_API_KEY, YOUTUBE_VIDEO_ID);
                break;

            case R.id.btn_playlist:
                intent = YouTubeStandalonePlayer.createPlaylistIntent(this, GOOGLE_API_KEY, YOUTUBE_PLAYLIST_ID);
                break;

            default:

        }

        if (intent != null) {
            startActivity(intent);
        }

    }
}

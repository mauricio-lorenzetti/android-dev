package com.mauk.app.intenttest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Mauk on 18/10/2015.
 */
public class SecondActivity extends AppCompatActivity {

    private Button button2;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        button2 = (Button) findViewById(R.id.button2);
        intent = getIntent();
        button2.setText(intent.getStringExtra(MainActivity.MESSAGE) + " back");

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(SecondActivity.this, "clicked...", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}

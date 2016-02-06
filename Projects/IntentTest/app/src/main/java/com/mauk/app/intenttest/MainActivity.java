package com.mauk.app.intenttest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String MESSAGE = "com.mauk.app.intenttest.MESSAGE";
    private Button button;
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);
        text = (TextView) findViewById(R.id.field);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(MainActivity.this, "clicked...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);

                intent.putExtra(MESSAGE, text.getText().toString());

                startActivity(intent);

            }
        });
    }

}

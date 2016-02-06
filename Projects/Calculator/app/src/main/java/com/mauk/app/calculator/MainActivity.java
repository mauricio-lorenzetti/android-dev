package com.mauk.app.calculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText firstNumber;
    private EditText secondNumber;

    private Button addButton;
    private Button subButton;
    private Button mulButton;
    private Button divButton;

    private Button clrButton;

    private TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firstNumber = (EditText) findViewById(R.id.firstNumber);
        secondNumber = (EditText) findViewById(R.id.secondNumber);
        addButton = (Button) findViewById(R.id.addButton);
        subButton = (Button) findViewById(R.id.subButton);
        mulButton = (Button) findViewById(R.id.mulButton);
        divButton = (Button) findViewById(R.id.divButton);
        clrButton = (Button) findViewById(R.id.clrButton);
        resultText = (TextView) findViewById(R.id.resultText);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firstNumber.getText().length() > 0 && secondNumber.getText().length() > 0 && !firstNumber.getText().equals(".") && !secondNumber.getText().equals(".")) {
                    double op1 = Double.parseDouble(firstNumber.getText().toString());
                    double op2 = Double.parseDouble(secondNumber.getText().toString());

                    double res = op1 + op2;
                    resultText.setText(Double.toString(res));
                }
                else {
                    Toast.makeText(MainActivity.this, "There are empty fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firstNumber.getText().length() > 0 && secondNumber.getText().length() > 0 && !firstNumber.getText().equals(".") && !secondNumber.getText().equals(".")) {
                    double op1 = Double.parseDouble(firstNumber.getText().toString());
                    double op2 = Double.parseDouble(secondNumber.getText().toString());

                    double res = op1 - op2;
                    resultText.setText(Double.toString(res));
                }
                else {
                    Toast.makeText(MainActivity.this, "There are empty fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mulButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firstNumber.getText().length() > 0 && secondNumber.getText().length() > 0 && !firstNumber.getText().equals(".") && !secondNumber.getText().equals(".")) {
                    double op1 = Double.parseDouble(firstNumber.getText().toString());
                    double op2 = Double.parseDouble(secondNumber.getText().toString());

                    double res = op1 * op2;
                    resultText.setText(Double.toString(res));
                }
                else {
                    Toast.makeText(MainActivity.this, "There are empty fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        divButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firstNumber.getText().length() > 0 && secondNumber.getText().length() > 0 && !firstNumber.getText().equals(".") && !secondNumber.getText().equals(".")) {
                    double op1 = Double.parseDouble(firstNumber.getText().toString());
                    double op2 = Double.parseDouble(secondNumber.getText().toString());

                    if (op2 != 0) {
                        double res = op1 / op2;
                        resultText.setText(Double.toString(res));
                    } else {
                        resultText.setText("Unable to divide by '0'");
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "There are empty fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        clrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstNumber.setText("");
                secondNumber.setText("");
                resultText.setText("0.00");
                firstNumber.requestFocus();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

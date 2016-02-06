package com.mauk.app.listviewexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] clientClubs = {"MOG", "Aldeia", "Kabana", "Sky", "Banana República",
                                "Royal Club", "Anzu Club", "Gold", "Villa Mix", "KitNet", "Fabrika",
                                "Opera7", "Altas Horas", "Pink Elephant", "Tunel do Tempo", "Armazém 31",
                                "Cartum", "Paioça", "Seu Vidotti", "Estação São Bento", "Andarilho"};

        ListAdapter theAdapter = new MyAdapter(this, clientClubs);

        ListView theListView = (ListView) findViewById(R.id.theListView);

        theListView.setAdapter(theAdapter);

        theListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String clubPicked = "You selected " + String.valueOf(parent.getItemAtPosition(position));

                Toast.makeText(MainActivity.this, clubPicked, Toast.LENGTH_SHORT).show();

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

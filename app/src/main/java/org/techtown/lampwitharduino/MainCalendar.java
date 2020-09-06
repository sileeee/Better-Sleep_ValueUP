package org.techtown.lampwitharduino;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainCalendar extends AppCompatActivity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_calendar);

        Button redBtn = findViewById(R.id.red);
        Button greenBtn = findViewById(R.id.green);
        Button yelBtn = findViewById(R.id.yellow);

        redBtn.setOnClickListener(this);
        yelBtn.setOnClickListener(this);
        greenBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if( view.getId() == R.id.red){
            Intent intent_1 = new Intent(getApplicationContext(), TueActivity.class);
            startActivity(intent_1);
        }else if(view.getId() == R.id.green){
            Intent intent_2 = new Intent(getApplicationContext(), WedActivity.class);
            startActivity(intent_2);
        }else if(view.getId() == R.id.yellow){
            Intent intent_3 = new Intent(getApplicationContext(), ThursActivity.class);
            startActivity(intent_3);
        }
    }
}
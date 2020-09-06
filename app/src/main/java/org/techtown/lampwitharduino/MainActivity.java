package org.techtown.lampwitharduino;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {



    Button btGoAlarm, btGoBluetooth, btGoSensor, btGoCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 메인에서 알람 버튼 선택
        btGoAlarm = (Button)findViewById(R.id.btGoAlarm);
        btGoAlarm.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(getApplicationContext(), MainAlarm.class);
                startActivity(intent);
            }
        });

        // 메인에서 블루투스 버튼 선택
        btGoBluetooth = (Button)findViewById(R.id.btGoBluetooth);
        btGoBluetooth.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(getApplicationContext(), Bluetooth.class);
                startActivity(intent);
            }
        });

        // 메인에서 센서 버튼 선택
        btGoSensor = (Button)findViewById(R.id.btGoSensor);
        btGoSensor.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(getApplicationContext(), shakeSensor.class);
                startActivity(intent);
            }
        });

        // 메인에서 캘린더 버튼 선택
        btGoCalendar = (Button)findViewById(R.id.btGoCalendar);
        btGoCalendar.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(getApplicationContext(), MainCalendar.class);
                startActivity(intent);
            }
        });
    }
}
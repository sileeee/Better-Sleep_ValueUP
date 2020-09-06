package org.techtown.lampwitharduino;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static android.hardware.SensorManager.SENSOR_DELAY_NORMAL;
import static org.techtown.lampwitharduino.Bluetooth.BT_CONNECTING_STATUS;
import static org.techtown.lampwitharduino.Bluetooth.BT_MESSAGE_READ;
import static org.techtown.lampwitharduino.Bluetooth.BT_UUID;

public class shakeSensor extends AppCompatActivity {

    SensorManager mSensorManager; // 센서 매니저
    SensorEventListener listener; // 센서 리스너
    Sensor mGyroscope; // 자이로스코프 센서
    Sensor mAccelerometer; // 가속도 센서

    // 자이로스코프 센서 값
    private double roll; // 3차원 x값
    private double pitch; // 3차원 y값
    private double yaw; // 3차원 z값

    private double timestamp = 0.0; // 단위시간
    private double dt;

    // 회전각
    private double rad_to_dgr = 180/Math.PI;
    private static final float NS2S = 1.0f/1000000000.0f;

    TextView axisXT, axisYT, axisZT, tiltText;
    TextView gyroXT, gyroYT, gyroZT;

    Button btGoHome;

    ConnectedBluetoothThread mmThreadConnectedBluetooth;
    BluetoothSocket mmBluetoothSocket;
    BluetoothDevice mmBluetoothDevice;
    Handler mmBluetoothHandler;
    Set<BluetoothDevice> mmPairedDevices;
    BluetoothAdapter mmBluetoothAdapter;
    List<String> mmListPairedDevices;
    TextView mmTvReceiveData;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake_sensor);

        btGoHome = (Button)findViewById(R.id.btGoHome);
        btGoHome.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });


        mmBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        listPairedDevices();

        gyroXT = (TextView)findViewById(R.id.gyroX);
        gyroYT = (TextView)findViewById(R.id.gyroY);
        gyroZT = (TextView)findViewById(R.id.gyroZ);
        axisXT = (TextView) findViewById(R.id.axisX);
        axisYT = (TextView) findViewById(R.id.axisY);
        axisZT = (TextView) findViewById(R.id.axisZ);
        tiltText = (TextView)findViewById(R.id.tiltText);

        // 센서 매니저 생성
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // 자이로스코프 센서 등록
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        // 가속도 센서 등록
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mmBluetoothHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                if(msg.what == BT_MESSAGE_READ){
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    mmTvReceiveData.setText(readMessage);
                }
            }
        };

        listener = new SensorEventListener() {
            boolean tilt = false;

            int movecount=0;
            public void onSensorChanged(SensorEvent sensorEvent) {
                switch (sensorEvent.sensor.getType()) {
                    case Sensor.TYPE_GYROSCOPE:
                        double gyroX = sensorEvent.values[0];
                        double gyroY = sensorEvent.values[1];
                        double gyroZ = sensorEvent.values[2];

                        // 단위시간 계산
                        dt = (sensorEvent.timestamp - timestamp) + NS2S;
                        timestamp = sensorEvent.timestamp;

                        // 시간 변화시
                        if (dt - timestamp * NS2S != 0) {
                            pitch = pitch + gyroY * dt;
                            roll = roll + gyroX + dt;
                            yaw = yaw + gyroZ * dt;

                            gyroXT.setText("gyroX : " + String.valueOf(roll * rad_to_dgr));
                            gyroYT.setText("gyroY : " + String.valueOf(pitch * rad_to_dgr));
                            gyroZT.setText("gyroZ : " + String.valueOf(yaw * rad_to_dgr));
                        }

                    case Sensor.TYPE_ACCELEROMETER:
                        float axisX = sensorEvent.values[0];
                        float axisY = sensorEvent.values[1];
                        float axisZ = sensorEvent.values[2];

                        if (axisX > 10 || axisX < -10 || axisY > 10 || axisY < -10 || axisZ > 10 || axisZ < -10)
                            tilt = true;
//                        else
//                            tilt = false;

//                        mBtnLightOn.setOnClickListener(new Button.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                Toast.makeText(getApplicationContext(), "조명 ON 시도.", Toast.LENGTH_LONG).show();
//                                if(mThreadConnectedBluetooth != null) {
//                                    mThreadConnectedBluetooth.write(mTvSendData.getText().toString());
//                                    mTvSendData.setText("1");
//                                }
//                                else {
//                                    Toast.makeText(getApplicationContext(), "mtheradconnectedbluetooth비어있음.", Toast.LENGTH_LONG).show();
//                                }
//                            }
//                        });

                        if (tilt) {
                            axisXT.setText("axisX : " + String.valueOf(axisX));
                            axisYT.setText("axisY : " + String.valueOf(axisY));
                            axisZT.setText("axisZ : " + String.valueOf(axisZ));
                            tiltText.setText("it's tilt!!");
                            movecount+=1;
                            tilt=false;
                        }
                        else {
                            tiltText.setText("tilt");
                        }

                        if(movecount>=10) {
                            if(mmThreadConnectedBluetooth != null) {
                                mmThreadConnectedBluetooth.write("1");
                            }
                        }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };


    }

    @Override
    protected void onResume() {
        super.onResume();
        // 반응 속도 FASTEST > GAME > UI > NORMAL
        mSensorManager.registerListener(listener, mGyroscope, SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(listener, mAccelerometer, SENSOR_DELAY_NORMAL);

    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(listener);
    }

    protected void onStop() {
        super.onStop();
    }

    void connectSelectedDevice(String selectedDeviceName) {
        for(BluetoothDevice tempDevice : mmPairedDevices) {
            if (selectedDeviceName.equals(tempDevice.getName())) {
                mmBluetoothDevice = tempDevice;
                break;
            }
        }
        try {
            mmBluetoothSocket = mmBluetoothDevice.createRfcommSocketToServiceRecord(BT_UUID);
            mmBluetoothSocket.connect();
            mmThreadConnectedBluetooth = new ConnectedBluetoothThread(mmBluetoothSocket);
            mmThreadConnectedBluetooth.start();
            mmBluetoothHandler.obtainMessage(BT_CONNECTING_STATUS, 1, -1).sendToTarget();
            Toast.makeText(getApplicationContext(), "블루투스 연결 완료", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
        }
    }

    void listPairedDevices() {
        if (mmBluetoothAdapter.isEnabled()) {
            mmPairedDevices = mmBluetoothAdapter.getBondedDevices();

            if (mmPairedDevices.size() > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("장치 선택");

                mmListPairedDevices = new ArrayList<String>();
                for (BluetoothDevice device : mmPairedDevices) {
                    mmListPairedDevices.add(device.getName());
                    //mListPairedDevices.add(device.getName() + "\n" + device.getAddress());
                }
                final CharSequence[] items = mmListPairedDevices.toArray(new CharSequence[mmListPairedDevices.size()]);
                mmListPairedDevices.toArray(new CharSequence[mmListPairedDevices.size()]);

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        connectSelectedDevice(items[item].toString());
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                Toast.makeText(getApplicationContext(), "페어링 완료.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "블루투스가 비활성화 되어 있습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    class ConnectedBluetoothThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedBluetoothThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "소켓 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.available();
                    if (bytes != 0) {
                        SystemClock.sleep(100);
                        bytes = mmInStream.available();
                        bytes = mmInStream.read(buffer, 0, bytes);
                        mmBluetoothHandler.obtainMessage(BT_MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                        Toast.makeText(getApplicationContext(), "run 완료.", Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "run 오류.", Toast.LENGTH_LONG).show();
                    break;
                }
            }
        }
        public void write(String str) {
            byte[] bytes = str.getBytes();
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "데이터 전송 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
        }
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "소켓 해제 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
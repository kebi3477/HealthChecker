package com.airforce.healthchecker.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airforce.healthchecker.MainActivity;
import com.airforce.healthchecker.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class FragmentRunning extends Fragment implements SensorEventListener {

    private LinearLayout circleLayout;
    private Button startButton;
    private TextView timeTextView, countTextView;

    public static final int INIT = 0;
    public static final int RUN = 1;
    public static final int RESTART = 2;

    public static int status = INIT;

    public long baseTime, endTime; //타이머 시간 값을 저장할 변수
    public String type;
    public static final String typeKey = "type";

    //--------------센서 변수 추가---------------------------
    private SensorManager sensorManager;
    private Sensor linearAccelSensor;
    private Sensor accelSensor;
    private Sensor orientationSensor;

    //calc
    boolean isMove = false;
    boolean isRunning = false;
    double vectorDegree = 0;
    double vectorDistance = 0;
    double[] orientationData = {0,0,0};
    double[] linearAccelData = {0,0,0};
    double[] accelTestData = {0,0,0};
    int lowSpeedCounter =0;

    //test
    double total = 0;
    double oneSecondTotal = 0;
    double oneSecondCount = 0;;
    double[] oneSecondAccel = {0,0};
    double[] currentSpeed ={0,0};
    double[] result = {0,0,0};
    double[] resultIntegral = {0,0,0};//x,y,count
    boolean isInit = false;
    double[] accelTestOffset = {0,0,0};//x,y,count

    //lpf
    public lpf lpfX;
    public lpf lpfY;
    //--------------변수 선언 끝-----------------------


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_running, container, false);
        type = getArguments().getString(typeKey);

        circleLayout = view.findViewById(R.id.circle_layout);
        startButton = (Button) view.findViewById(R.id.startButton);
        timeTextView = (TextView) view.findViewById(R.id.timeView);
        countTextView = (TextView) view.findViewById(R.id.countView);

        if(type.equals("pushUp") || type.equals("sitUp")) countTextView.setText("0회");

        startButton.setOnClickListener(new View.OnClickListener() { //버튼 클릭 이벤트
            @Override
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.startButton:
                        start();
                        break;
                }
            }
        });

        //--------------------------------------
        if(type.equals("running")){
            total=0;
            lpfX = new lpf();
            lpfY = new lpf();
            sensorManager =
                    (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
            linearAccelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
            SensorEventListener sensorListener;
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (oneSecondCount > 0 && isRunning == true) {
                        double tempAX = currentSpeed[0];
                        double tempAY = currentSpeed[1];
                        double tempBX = oneSecondAccel[0] / oneSecondCount / 2;
                        double tempBY = oneSecondAccel[1] / oneSecondCount / 2;
                        lowSpeedCounter = 0;
                        vectorDegree =
                                Math.atan((tempAX + tempBX) / (tempAY + tempBY));
                        vectorDistance =
                                Math.sqrt(pow(tempAX + tempBX) + pow(tempAY + tempBY));
                        if ((Math.sqrt(pow(tempBX) + pow(tempBX)) < 3.5)) { //이상 데이터 필터링
                            currentSpeed[0] += tempBX;
                            currentSpeed[1] += tempBY;
                        }
                        Log.d("result", String.format("%.4fm",
                                currentSpeed[0]) + "/" + String.format("%.4fm",
                                currentSpeed[1]) + "/" + String.format("%.4fm",
                                tempBX) + "/" + String.format("%.4fm",
                                tempBY) + "/");
                        total += (Math.sqrt(pow(currentSpeed[0]) + pow(currentSpeed[1])));
                        oneSecondAccel[0] = 0;
                        oneSecondAccel[1] = 0;
                        oneSecondCount = 0;
                    }
                }
            };
            Timer timer = new Timer();
            timer.schedule(timerTask, 1000, 1000);
            sensorListener = new SensorEventListener() {
                @Override
                public void onAccuracyChanged(Sensor arg0, int arg1) {
                }

                @Override
                public void onSensorChanged(SensorEvent event) {
                    Sensor sensor = event.sensor;
                    if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                        Log.d("ACCELEROMETER", "");
                        linearAccelData[0] = event.values[0];
                        linearAccelData[1] = event.values[1];
                        linearAccelData[2] = event.values[2];
                        if (isInit == false) {/*
                        accelTestOffset[0] = event.values[0];
                        accelTestOffset[1] = event.values[1];
                        accelTestOffset[2] = event.values[2];*/
                            isInit = true;
                        }
                        double accelration = Math.sqrt(pow(linearAccelData[0]) + pow(linearAccelData[1]) + pow(linearAccelData[2]));
                        if (currentSpeed[0] < 0.5 && accelration < 0.5) {
                            isMove = false;
                            currentSpeed[0] = 0;
                            currentSpeed[1] = 0;
                        } else if (currentSpeed[0] > 0.5 && accelration < 0.05) {
                            isMove = true;
                            currentSpeed[0] = 0.05;
                            currentSpeed[1] = 0.05;
                            lowSpeedCounter++;
                            if (lowSpeedCounter > 4) {
                                isMove = false;
                                currentSpeed[0] = 0;
                                currentSpeed[1] = 0;
                            }
                        } else {
                            isMove = true;
                        }
                    }
                    if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {/*

                    accelTestData[0] = event.values[0]-accelTestOffset[0];
                    accelTestData[1] = event.values[1]-accelTestOffset[1];
                    accelTestData[2] = event.values[2]-accelTestOffset[2];*/
                        accelTestData[0] = event.values[0];
                        accelTestData[1] = event.values[1];
                        accelTestData[2] = event.values[2];
                        if(isRunning==true)
                            countTextView.setText(String.format("%.2fkm",total/1000));

                        if (isMove == true) {
                            double[] buffer = {accelTestData[2], accelTestData[0], accelTestData[1]};
                            double[] resultBuffer = matrix_mul(buffer, euler(orientationData));
                            double[] filterdAccel = {0, 0};
                            resultIntegral[0] += lpfX.lpfCalc(resultBuffer[1]);
                            resultIntegral[1] += lpfY.lpfCalc(resultBuffer[2]);
                            resultIntegral[2]++;
                            result = matrix_mul(linearAccelData,
                                    euler(orientationData));


                            //=============================================================================
                            double degreeData = 0;
                            double distanceData = 0;
                            double calcDistance;
                            degreeData = atan(result[2] / result[0]);
                            distanceData =
                                    Math.sqrt(pow(result[0]) + pow(result[2]));

                            if (result[0] < 25 && result[2] < 25) {
                                oneSecondAccel[0] += result[0];
                                oneSecondAccel[1] += result[2];

                                oneSecondCount++;
                                Log.d("accel", "under 25");
                            } else {
                                Log.d("accel", String.format("%.4fm",
                                        result[0]) + "/" + String.format("%.4fm/s",
                                        result[2]));
                                Log.d("accel", "Over 25");
                            }


                        } else {
                            resultIntegral[0] += lpfX.lpfCalc(0);
                            resultIntegral[1] += lpfY.lpfCalc(0);
                            resultIntegral[2]++;
                            result = matrix_mul(linearAccelData, euler(orientationData));
                        }
                    }
                    if (sensor.getType() == Sensor.TYPE_ORIENTATION) {
                        Log.d("ORIENTATION", "");
                        orientationData[0] = event.values[0] / 180 * Math.PI;
                        orientationData[1] = event.values[1] / 180 * Math.PI;
                        orientationData[2] = event.values[2] / 180 * Math.PI;
                    }
                }
            };
            sensorManager.registerListener(sensorListener, linearAccelSensor,
                    SensorManager.SENSOR_DELAY_GAME);
            sensorManager.registerListener(sensorListener, orientationSensor,
                    SensorManager.SENSOR_DELAY_GAME);
            sensorManager.registerListener(sensorListener, accelSensor,
                    SensorManager.SENSOR_DELAY_GAME);

        }
         //---------------------------------------

        return view;
    }

    @SuppressLint("ResourceAsColor")
    private void start() {
        int circle = 0;
        if(type.equals("running"))  circle = R.drawable.circle_running;
        else if(type.equals("pushUp")) circle = R.drawable.circle_pushup;
        else circle = R.drawable.circle_situp;

        switch (status) {
            case INIT:
                isRunning=(type.equals("running"))?true:false;
                baseTime = SystemClock.elapsedRealtime();
                startButton.setText("종료"); //텍스트 변경
                circleLayout.setBackgroundResource(circle);
                status = RUN; //상태 변환
                handler.sendEmptyMessage(0);
                break;
            case RUN:
                endTime = SystemClock.elapsedRealtime();
                startButton.setText("시작");
                circleLayout.setBackgroundResource(R.drawable.circle);
                status = INIT;
                handler.removeMessages(0);
                ((MainActivity)getActivity()).showDialog("체력검정을 종료하시겠습니까?",getJsonObjectRecode(getTimeOut(), type, (String) countTextView.getText()));

                break;
            case RESTART:
                isRunning=(type.equals("running"))?true:false;
                baseTime += (SystemClock.elapsedRealtime() - endTime);
                startButton.setText("종료");
                circleLayout.setBackgroundResource(circle);
                handler.sendEmptyMessage(0);
                status = RUN;
                break;
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() { //타임워치 핸들러
        @Override
        public void handleMessage(@NonNull Message msg) {
            timeTextView.setText(getTimeOut());
            handler.sendEmptyMessage(0);
        }
    };

    String getTimeOut() { //타임워치 실시간 시간
        long now = SystemClock.elapsedRealtime();
        long outTime = now - baseTime;
        long mSec = outTime % 1000 / 10;
        long sec = outTime / 1000 % 60;
        long min = outTime / 1000 / 60;
        String easyOutTime = String.format("%02d:%02d:%02d", min, sec, mSec);
        return easyOutTime;
    }

    JSONObject getJsonObjectRecode( final String time, final String type, String count) {
        JSONObject json = new JSONObject();
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd kk:mm:ss");
        //-----------------------------

        isRunning=false;
        //-----------------------------
        try {
            json.put("date", format.format(new Date()));
            json.put("time", time);
            json.put("type", type);
            json.put("count", count);
        } catch (JSONException e) {
            json = null;
            e.printStackTrace();
        }

        return json;
    }
//------------------------------------------------

    double[][] euler(double[] data){
        double[][] result={
                {
                        cos(data[1])*cos(data[2]),
                        cos(data[1])*sin(data[2]),
                        -sin(data[1])
                },
                {
                        sin(data[0])*sin(data[1])*cos(data[2])-cos(data[0])*sin(data[2]),
                        sin(data[0])*sin(data[1])*sin(data[2])+cos(data[0])*cos(data[2]),
                        sin(data[0])*cos(data[1])
                },
                {
                        cos(data[0])*sin(data[1])*cos(data[2])+sin(data[0])*sin(data[2]),
                        cos(data[0])*sin(data[1])*sin(data[2])-sin(data[0])*cos(data[2]),
                        cos(data[0])*cos(data[1])
                }
        };
        return result;
    }

    double[] matrix_mul(double[] A, double[][]B) {
        double[] C = new double[3];
        for(int i=0; i<3; i++) {
            for(int j=0; j<3; j++) {
                C[i] += (B[i][j] * A[j]);
            }
        }
        return C;
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
        if(sensorManager!=null)
            sensorManager.unregisterListener(this);
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    double cos(double a){
        return Math.cos(a);
    }
    double sin(double a){
        return Math.sin(a);
    }
    double atan(double a){
        return Math.atan(a);
    }double pow(double a){
        return Math.pow(a,2);
    }
    //----------------------------------------------
}

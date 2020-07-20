package com.airforce.healthchecker.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
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
    boolean isRunning = false;
    double rotate[] = new double[9];
    double acc[] = new double[3];
    float[] rotationMatrix = new float[9];
    double[] speed=new double[3];
    double[] distanceOneSecond=new double[3];
    double[] delta=new double[3];
    double totalDistance=0;
    double tempDistance=0;

    float[] previousAccelData=new float[3];
    float stepThreshold = 15;
    boolean stepCounted = false;
    int smallChangeCount =0;
    int lowValueCount =0;
    long startStep = 0;
    long currentStep = 0;
    long lastTime = System.currentTimeMillis();
    long lastStepTime = System.currentTimeMillis();


    double maxSpeed=0;
    int[] recentStepPeriod={0,0,0,0,0,0,0,0,0,0};


    private SensorManager sensorManager;
    private Sensor accelSensor;
    private Sensor rotateSensor;
    private Sensor stepSensor;
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
        if(type.equals("running")) {
            sensorManager = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
            accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            rotateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    //if(totalDistance<100)
                    totalDistance += 0.8*Math.sqrt(Math.pow(distanceOneSecond[0],
                            2) + Math.pow(distanceOneSecond[1], 2));
                    distanceOneSecond[0] = 0;
                    distanceOneSecond[1] = 0;
                    Log.d("totalDistance", String.format("%.4f", totalDistance));
                }
            };
            Timer timer = new Timer();
            timer.schedule(timerTask, 1000, 1000);


            SensorEventListener sensorListener = new SensorEventListener() {

                @Override
                public void onSensorChanged(SensorEvent event) {
                    Sensor sensor = event.sensor;
                    if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                        newAcceleration(event.values);
                        if(isRunning==true)
                                countTextView.setText(String.format("%.2fkm",
                                        totalDistance/1000));

                    }
                    if (sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                            newRotationVector(event.values);
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }
            };
            sensorManager.registerListener(sensorListener, accelSensor,
                    SensorManager.SENSOR_DELAY_FASTEST);
            sensorManager.registerListener(sensorListener, rotateSensor,
                    SensorManager.SENSOR_DELAY_FASTEST);
            sensorManager.registerListener(sensorListener, stepSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
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
                speed[0]=0;
                speed[1]=0;
                totalDistance=0;
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
                tempDistance = totalDistance;
                totalDistance = 0;
                break;
            case RESTART:
                isRunning=(type.equals("running"))?true:false;
                baseTime += (SystemClock.elapsedRealtime() - endTime);
                startButton.setText("종료");
                circleLayout.setBackgroundResource(circle);
                handler.sendEmptyMessage(0);
                status = RUN;
                totalDistance = tempDistance;
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



    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
        if(sensorManager!=null)
            sensorManager.unregisterListener(this);
    }@Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
    }
    // Manage acceleration value
    private void newAcceleration(float[] acceleration) {
        float[] vector = acceleration;
        float[] orientaionValueForTest =new float[3] ;
        long interval = (System.currentTimeMillis()-lastTime);

        lastTime = System.currentTimeMillis();
        Log.d("rotationMatrix", String.format("%.4f",rotationMatrix[0])+"    "+
                String.format("%.4f",rotationMatrix[1])+"    "+
                String.format("%.4f",rotationMatrix[2])+"    "+"\n"+"                                                                         "+
                String.format("%.4f",rotationMatrix[3])+"    "+
                String.format("%.4f",rotationMatrix[4])+"    "+
                String.format("%.4f",rotationMatrix[5])+"    "+"\n"+"                                                                         "+
                String.format("%.4f",rotationMatrix[6])+"    "+
                String.format("%.4f",rotationMatrix[7])+"    "+
                String.format("%.4f",rotationMatrix[8])+"    "+"\n"
        );
        vector = preFilter(rotateVector3(vector,rotationMatrix));

        vector = postFilter(vector);



        if(Math.abs(vector[2])>stepThreshold && stepCounted==false){
            if(currentStep==0)lastStepTime=System.currentTimeMillis();
            else{
                for(int i=0;i<9;i++){
                    recentStepPeriod[i]=recentStepPeriod[i+1];
                }
                recentStepPeriod[9]=(int)(System.currentTimeMillis()-lastStepTime);
                lastStepTime=System.currentTimeMillis();
            }
            currentStep++;
            stepCounted = true;
            //if(totalDistance>100)
            totalDistance+=0.17;
        }else if(Math.abs(vector[2]) < stepThreshold-2){
            stepCounted = false;
        }


        speed[0]+=vector[0]*interval/1000.0;
        speed[1]+=vector[1]*interval/1000.0;
        distanceOneSecond[0]+=speed[0]*interval/1000;
        distanceOneSecond[1]+=speed[1]*interval/1000;

        Log.d("acceleration", "Interval:"+interval/1000.0+ "    X:"+String.format("%.4f",vector[0])+"    Y:"+String.format("%.4f",vector[1])+"    Z:"+String.format("%.4f",vector[2]));
        Log.d("speed", "X:"+String.format("%.4f",speed[0])+"    Y:"+String.format("%.4f",speed[1])+"    Z:"+String.format("%.4f",speed[2]));
        Log.d("distanceOneSecond", "X:"+String.format("%.4f",distanceOneSecond[0])+"    Y:"+String.format("%.4f",distanceOneSecond[1])+"    Z:"+String.format("%.4f",distanceOneSecond[2]));

        previousAccelData = vector;




    }

    private void newRotationVector(float[] quaternion) {
        SensorManager.getRotationMatrixFromVector(rotationMatrix, quaternion);
    }

    float[] rotateVector3(float[] vector,float[] rotationMatrix){
        float[] newVector = new float[3];
        for(int i=0;i<3;i++) {
            double s = 0;
            for(int j=0;j<3;j++) {
                s += (double)vector[j] * (double)rotationMatrix[j+i*3];
            }
            newVector[i] = (float)s;
        }
        return newVector;
    }

    float[] preFilter(float[] data){
        double movingDistance=Math.sqrt(Math.pow(data[0]-previousAccelData[0],2)+Math.pow(data[1] - previousAccelData[1],2));

        int tenStepTime = 0;
        if(recentStepPeriod[0]!=0)
            for(int i=0;i<10;i++){
                tenStepTime+=recentStepPeriod[i];
            }
        double currentSpeed =Math.sqrt(Math.pow(speed[0],2)+Math.pow(speed[1]
                ,2));
        if(currentStep>10&&currentSpeed>0.5) {
            maxSpeed =
                    (1609.344 / stepConverter(currentSpeed, 170)) / tenStepTime / 100;
        }
        if(maxSpeed>4)
            maxSpeed=4;
        if(currentSpeed>maxSpeed&&currentSpeed>1){
            if(maxSpeed!=0){
                double divisionRatio=currentSpeed/maxSpeed;
                speed[0]/=divisionRatio;
                speed[1]/=divisionRatio;
            }
        }
        if(Math.abs(movingDistance)<0.2){
            smallChangeCount++;
        }else if(movingDistance>0.4){
            smallChangeCount=0;
        }
        if(smallChangeCount>10){
            data[0]*=0.5;
            data[1]*=0.5;
        }
        if(smallChangeCount>30){
            speed[0]*=0.2;
            speed[1]*=0.2;
        }


        if(Math.abs(data[0])<0.010||Math.abs(data[1])<0.010){
            lowValueCount++;
        }
        if(lowValueCount>10){
            data[0]=0;
            data[1]=0;
            lowValueCount-=10;
        }
        tenStepTime=0;
        return data;
    }

    private void convertToDegrees(float[] vector){
        for (int i = 0; i < vector.length; i++){
            vector[i] = Math.round(Math.toDegrees(vector[i]));
        }
    }

    float[] postFilter(float[] data){
        if(Math.abs(data[0])>3)data[0]=data[0]>0?3:-3;
        if(Math.abs(data[1])>3)data[1]=data[1]>0?3:-3;


        return data;
    }
    int stepConverter(double speed,float height){
        if(speed>2) {
            float exchangedSpeed = (float) (1609.344 / speed / 60);
            return (int) (1997 - (5.31496 * (height - 152.4)) - (exchangedSpeed - 12) * 143.5);
        }else
            return 1997;
    }



    /*
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
        }*/
    /*
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
    */
    //----------------------------------------------
}

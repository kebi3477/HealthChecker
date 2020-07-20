package com.airforce.healthchecker.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.KeyEvent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.airforce.healthchecker.MainActivity;
import com.airforce.healthchecker.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class FragmentRunning extends Fragment implements SensorEventListener {

    private LinearLayout circleLayout;
    private ConstraintLayout popup;
    private Button startButton, endButton;
    private TextView timeTextView, countTextView, healthRank;
    private EditText runningValue;


    public static final int INIT = 0;
    public static final int RUN = 1;
    public static final int RESTART = 2;

    public static int status = INIT;

    public long baseTime, endTime; //타이머 시간 값을 저장할 변수
    public String type;
    public static final String typeKey = "type";
    private boolean running;
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
        startButton = view.findViewById(R.id.startButton);
        endButton = view.findViewById(R.id.endButton);
        timeTextView = view.findViewById(R.id.timeView);
        countTextView = view.findViewById(R.id.countView);
        runningValue = view.findViewById(R.id.runningValue);
        healthRank = view.findViewById(R.id.healthRank);

        if(type.equals("pushUp") || type.equals("sitUp")) {
            countTextView.setText("0개");
        }

        startButton.setOnClickListener(new View.OnClickListener() { //버튼 클릭 이벤트
            @Override
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.startButton:
                        try {
                            start();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });

        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).showDialog("체력검정을 종료하시겠습니까?",getJsonObjectRecode(getTimeOut(), type, runningValue.getText().toString(), getLiveHealthRank(changeCount(timeTextView.getText().toString()))));
                popup.setVisibility(View.GONE);
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

        runningValue.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                healthRank.setText(getLiveHealthRank(changeCount(runningValue.getText().toString())));
                return true;
            }
        });


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
    private void start() throws JSONException {
        int circle = 0;
        if(type.equals("running")) circle = R.drawable.circle_running;
        else if(type.equals("pushUp")) circle = R.drawable.circle_pushup;
        else circle = R.drawable.circle_situp;
        running = type.equals("running");

        switch (status) {
            case INIT:
                baseTime = SystemClock.elapsedRealtime();
                startButton.setText("종료"); //텍스트 변경
                circleLayout.setBackgroundResource(circle);
                status = RUN; //상태 변환
                isRunning=true;
                handler.sendEmptyMessage(0);
                break;
            case RUN:
                endTime = SystemClock.elapsedRealtime();
                startButton.setText("시작");
                circleLayout.setBackgroundResource(R.drawable.circle);
                status = INIT;
                isRunning=false;
                handler.removeMessages(0);
                showPopup(getJsonObjectRecode((String) timeTextView.getText(), type, (String) countTextView.getText(), getLiveHealthRank(0)));
                break;
            case RESTART:
                baseTime += (SystemClock.elapsedRealtime() - endTime);
                startButton.setText("종료");
                circleLayout.setBackgroundResource(circle);
                handler.sendEmptyMessage(0);
                status = RUN;
                isRunning=true;
                break;
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() { //타임워치 핸들러
        @Override
        public void handleMessage(@NonNull Message msg) {
            timeTextView.setText(getTimeOut());
            handler.sendEmptyMessage(   0);
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

    JSONObject getJsonObjectRecode( final String time, final String type, String count, String rank) {
        JSONObject json = new JSONObject();
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd kk:mm:ss");

        try {
            json.put("date", format.format(new Date()));
            json.put("time", time);
            json.put("type", type);
            json.put("count", count);
            json.put("rank", rank);
        } catch (JSONException e) {
            json = null;
            e.printStackTrace();
        }

        return json;
    }

    private void showPopup(JSONObject jsonObject) throws JSONException {
        View view = this.getView();
        TextView giftOkLength = view.findViewById(R.id.giftOkLength);
        TextView useTime = view.findViewById(R.id.useTime);
        LinearLayout view3 = view.findViewById(R.id.view3);
        boolean running = jsonObject.getString("type").equals("running");
        Float giftOkLengthValue = running ? Float.parseFloat(jsonObject.getString("count").substring(0,4)) : 0f;

        popup = view.findViewById(R.id.popup);
        if(jsonObject != null) {
            runningValue.setText(jsonObject.getString("count"));
            giftOkLength.setText(giftOkLengthValue >= 3 ? "3km" : String.valueOf(giftOkLengthValue));
            useTime.setText(jsonObject.getString("time"));
        }

        boolean pushUp = jsonObject.getString("type").equals("pushUp");
        boolean sitUp = jsonObject.getString("type").equals("sitUp");
        ImageView img = view.findViewById(R.id.imageView);
        ImageButton upButton = view.findViewById(R.id.upButton);
        ImageButton downButton = view.findViewById(R.id.downButton);

        if(pushUp) {
            view3.setBackgroundColor(getResources().getColor(R.color.colorPrimaryRed));
            img.setImageResource(R.mipmap.pushup_color);
        } else if(sitUp) {
            view3.setBackgroundColor(getResources().getColor(R.color.colorPrimaryGreen));
            img.setImageResource(R.mipmap.situp_color);
        }

        if(pushUp || sitUp) {
            TextView text = view.findViewById(R.id.textView10);
            TextView text2 = view.findViewById(R.id.textView12);

            text2.setVisibility(View.GONE);
            giftOkLength.setVisibility(View.GONE);
            healthRank.setText(getLiveHealthRank(0));
            text.setText("횟수");
        } else {
            upButton.setVisibility(View.GONE);
            downButton.setVisibility(View.GONE);
            healthRank.setText(getLiveHealthRank(changeCount(timeTextView.getText().toString())));
        }

        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upDownEvnet(+1);
            }
        });
        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upDownEvnet(-1);
            }
        });


        popup.setVisibility(View.VISIBLE);
    }

    private void upDownEvnet(int plus) {
        String value = runningValue.getText().toString();
        isRunning=false;
        //String value = (String) runningValue.getText();
        runningValue.setText(0+ "개");
        int countValue = Integer.parseInt(value.substring(0, value.length() -1)) + plus;
        if(countValue <= 0) countValue = 0;
        else if(countValue >= 99) countValue = 99;
        runningValue.setText(countValue + "개");
        healthRank.setText(getLiveHealthRank(countValue));
    }

    private String getLiveHealthRank(int count) {
        ArrayList<JSONObject> dataList = ((MainActivity) getActivity()).getObjectArrayPref("HEALTH_PREF_2020", "data");
        String returnRank = null;
        for(JSONObject json : dataList) {
            int age = 25;
            String countName, levelName;
            boolean compare;
            try {
                if(json.getString("sex").equals("m") &&
                    json.getString("sinbun").equals("s") &&
                    json.getInt("age") == age) {

                    if(type.equals("running")) {
                        countName = "count_r";
                        levelName = "level_r";
                        compare = json.getInt(countName) >= count;
                    }
                    else if(type.equals("pushUp")) {
                        countName = "count_p";
                        levelName = "level_p";
                        compare = json.getInt(countName) <= count;
                    }
                    else {
                        countName = "count_s";
                        levelName = "level_s";
                        compare = json.getInt(countName) <= count;
                    }

                    if(compare) {
                        String rank;
                        rank = json.getInt(levelName) == 0 ? "특" : json.getString(levelName);
                        returnRank =  rank + "급";
                        break;
                    } else {
                        returnRank = "불합격";
                    }
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
        return returnRank;
    }

    private int changeCount(String count) {
        int returnInt = 0;
        if(running) {
            String[] countSplit = count.split(":");
            Log.d("countSplit0",countSplit[0]);
            Log.d("countSplit1",countSplit[1]);
            returnInt = Integer.parseInt(countSplit[0] + countSplit[1]);
        } else {
            returnInt = Integer.parseInt(count.substring(0, count.length() -1));
        }
        return returnInt;
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

}

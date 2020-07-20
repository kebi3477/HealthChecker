package com.airforce.healthchecker.fragment;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.Date;

public class FragmentRunning extends Fragment {

    private LinearLayout circleLayout;
    private ConstraintLayout popup;
    private Button startButton, endButton;
    private TextView timeTextView, countTextView, runningValue;


    public static final int INIT = 0;
    public static final int RUN = 1;
    public static final int RESTART = 2;

    public static int status = INIT;

    public long baseTime, endTime; //타이머 시간 값을 저장할 변수
    public String type;
    public static final String typeKey = "type";
    private boolean running;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_running, container, false);
        type = getArguments().getString(typeKey);

        circleLayout = view.findViewById(R.id.circle_layout);
        startButton = view.findViewById(R.id.startButton);
        endButton = view.findViewById(R.id.endButton);
        timeTextView = view.findViewById(R.id.timeView);
        countTextView = view.findViewById(R.id.countView);
        runningValue = view.findViewById(R.id.runningValue);

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
                ((MainActivity)getActivity()).showDialog("체력검정을 종료하시겠습니까?",getJsonObjectRecode(getTimeOut(), type, (String) runningValue.getText()));
                popup.setVisibility(View.GONE);
            }
        });

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
                handler.sendEmptyMessage(0);
                break;
            case RUN:
                endTime = SystemClock.elapsedRealtime();
                startButton.setText("시작");
                circleLayout.setBackgroundResource(R.drawable.circle);
                status = INIT;
                handler.removeMessages(0);
                showPopup(getJsonObjectRecode((String) timeTextView.getText(), type, (String) countTextView.getText()));
                break;
            case RESTART:
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

    JSONObject getJsonObjectRecode( final String time, final String type, String count) {
        JSONObject json = new JSONObject();
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd kk:mm:ss");

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
            text.setText("횟수");
        } else {
            upButton.setVisibility(View.GONE);
            downButton.setVisibility(View.GONE);
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
        String value = (String) runningValue.getText();
        int countValue = Integer.parseInt(value.substring(0, value.length() -1)) + plus;
        if(countValue <= 0) countValue = 0;
        else if(countValue >= 99) countValue = 99;
        runningValue.setText(String.valueOf(countValue) + "개");
    }

}

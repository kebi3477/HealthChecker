package com.airforce.healthchecker.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.KeyEvent;
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

public class FragmentRunning extends Fragment {

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
                ((MainActivity)getActivity()).showDialog("체력검정을 종료하시겠습니까?",getJsonObjectRecode(getTimeOut(), type, runningValue.getText().toString(), getLiveHealthRank(changeCount(runningValue.getText().toString()))));
                popup.setVisibility(View.GONE);
            }
        });

        runningValue.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                healthRank.setText(getLiveHealthRank(changeCount(runningValue.getText().toString())));
                return true;
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
                showPopup(getJsonObjectRecode((String) timeTextView.getText(), type, (String) countTextView.getText(), getLiveHealthRank(0)));
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
            healthRank.setText(getLiveHealthRank(changeCount(runningValue.getText().toString())));
            text.setText("횟수");
        } else {
            upButton.setVisibility(View.GONE);
            downButton.setVisibility(View.GONE);
            healthRank.setText(getLiveHealthRank(changeCount(runningValue.getText().toString())));
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
            returnInt = Integer.parseInt(countSplit[0] + countSplit[1]);
        } else {
            returnInt = Integer.parseInt(count.substring(0, count.length() -1));
        }
        return returnInt;
    }

}

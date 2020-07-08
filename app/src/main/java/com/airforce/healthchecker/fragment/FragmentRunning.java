package com.airforce.healthchecker.fragment;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airforce.healthchecker.MainActivity;
import com.airforce.healthchecker.R;

public class FragmentRunning extends Fragment {

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

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_running, container, false);
        type = getArguments().getString(typeKey);

        circleLayout = view.findViewById(R.id.circle_layout);
        startButton = (Button) view.findViewById(R.id.startButton);
        timeTextView = (TextView) view.findViewById(R.id.timeView);
        countTextView = (TextView) view.findViewById(R.id.countView);

        if(type.equals("pushUp") || type.equals("sitUp")) countTextView.setText("0개");

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

        return view;
    }

    @SuppressLint("ResourceAsColor")
    private void start() {
        switch (status) {
            case INIT:
                baseTime = SystemClock.elapsedRealtime();
                startButton.setText("종료"); //텍스트 변경
                circleLayout.setBackgroundResource(R.drawable.circle_active);
                status = RUN; //상태 변환
                handler.sendEmptyMessage(0);
                break;
            case RUN:
                endTime = SystemClock.elapsedRealtime();
                startButton.setText("시작");
                circleLayout.setBackgroundResource(R.drawable.circle);
                status = INIT;
                handler.removeMessages(0);
                ((MainActivity)getActivity()).showDialog("체력검정을 종료하시겠습니까?", getTimeOut(), type);
                break;
            case RESTART:
                baseTime += (SystemClock.elapsedRealtime() - endTime);
                startButton.setText("종료");
                circleLayout.setBackgroundResource(R.drawable.circle_active);
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

}

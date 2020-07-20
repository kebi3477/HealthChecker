package com.airforce.healthchecker.fragment;

import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airforce.healthchecker.MainActivity;
import com.airforce.healthchecker.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FragmentHome extends Fragment {

    private FragmentRunning fragmentRunning = new FragmentRunning();
    private LinearLayout runningButton, pushUpButton, sitUpButton;
    private ImageView runningCheck, pushUpCheck, sitUpCheck;
    private TextView dailyTitle, dailyText;
    private BottomNavigationView navigationView;
    private ArrayList<JSONObject> recodeList;
    private boolean[] doCheck={false,false,false};
    final Bundle bundle = new Bundle();
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        navigationView = ((MainActivity)getActivity()).findViewById(R.id.navigationView); //하단 네비게이션 활성화
        runningButton = (LinearLayout) view.findViewById(R.id.runLayoutBtn);
        pushUpButton = (LinearLayout) view.findViewById(R.id.pushUpLayoutBtn);
        sitUpButton = (LinearLayout) view.findViewById(R.id.sitUpLayoutBtn);
        runningCheck = (ImageView) view.findViewById(R.id.runningCheckImage);
        pushUpCheck = (ImageView) view.findViewById(R.id.pushUpCheckImage);
        sitUpCheck = (ImageView) view.findViewById(R.id.sitUpCheckImage);
        dailyTitle = (TextView) view.findViewById(R.id.dailyCheckerTitle);
        dailyText = (TextView) view.findViewById(R.id.dailyCheckerText);


        recodeList = ((MainActivity)getActivity()).getObjectArrayPref("SHARE_PREF","recode");
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        for(JSONObject recode:recodeList){
            try {
                if(recode.get("date").toString().substring(0,10).equals(format.format(new Date()))){
                    if(recode.get("type").equals("running")){
                        doCheck[0]=true;
                    }else if(recode.get("type").equals("pushUp")){
                        doCheck[1]=true;
                    }else if(recode.get("type").equals("sitUp")){
                        doCheck[2]=true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //doCheck[0]=false;
        //doCheck[1]=false;
        //doCheck[2]=false;

        if(doCheck[0]==true){
            runningCheck.setImageResource(R.mipmap.running_check);
        }
        if(doCheck[1]==true){
            pushUpCheck.setImageResource(R.mipmap.pushup_check);
        }
        if(doCheck[2]==true){
            sitUpCheck.setImageResource(R.mipmap.situp_check);
        }


        if(doCheck[0]&doCheck[1]&doCheck[2]){
            dailyTitle.setText("잘했어요!");
            dailyText.setText("오늘은 모든 운동을 하셨군요");
        }else if(doCheck[0]||doCheck[1]||doCheck[2]){
            dailyTitle.setText("잘했어요!");
            dailyText.setText("하지않은 운동을 진행해 주세요");
        }else if(doCheck[0]||doCheck[1]||doCheck[2]==false) {
            dailyTitle.setText("노력해 주세요");
            dailyText.setText("아직 운동을 하지 않으셨습니다");
        }

        runningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString(fragmentRunning.typeKey,"running"); //번들에 데이터 넣기
                fragmentRunning.setArguments(bundle); //Fragment에 Bundle 넣기
                navigationView.getMenu().getItem(2).setChecked(true); //하단 아이콘 변경
                ((MainActivity)getActivity()).replaceFragment(fragmentRunning); //Fragment 교체
            }
        });
        pushUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putString(fragmentRunning.typeKey,"pushUp");
                fragmentRunning.setArguments(bundle);
                navigationView.getMenu().getItem(2).setChecked(true);
                ((MainActivity)getActivity()).replaceFragment(fragmentRunning);
            }
        });
        sitUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putString(fragmentRunning.typeKey,"sitUp");
                fragmentRunning.setArguments(bundle);
                navigationView.getMenu().getItem(2).setChecked(true);
                ((MainActivity)getActivity()).replaceFragment(fragmentRunning);
            }
        });

        return view;
    }
}

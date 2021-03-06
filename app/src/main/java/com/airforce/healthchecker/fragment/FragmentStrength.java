package com.airforce.healthchecker.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airforce.healthchecker.MainActivity;
import com.airforce.healthchecker.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FragmentStrength extends Fragment {

    private LinearLayout runLayoutBtn, pushUpLayoutBtn, sitUpLayoutBtn;
    private FragmentRunning fragmentRunning = new FragmentRunning();
    private BottomNavigationView navigationView;
    final Bundle bundle = new Bundle();

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_strength, container, false);

        navigationView = ((MainActivity)getActivity()).findViewById(R.id.navigationView); //하단 네비게이션 활성화
        runLayoutBtn = (LinearLayout) view.findViewById(R.id.runLayoutBtn);
        pushUpLayoutBtn = (LinearLayout) view.findViewById(R.id.pushUpLayoutBtn);
        sitUpLayoutBtn = (LinearLayout) view.findViewById(R.id.sitUpLayoutBtn);

        runLayoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString(fragmentRunning.typeKey,"running"); //번들에 데이터 넣기
                fragmentRunning.setArguments(bundle); //Fragment에 Bundle 넣기
                navigationView.getMenu().getItem(2).setChecked(true); //하단 아이콘 변경
                ((MainActivity)getActivity()).replaceFragment(fragmentRunning); //Fragment 교체
            }
        });
        pushUpLayoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putString(fragmentRunning.typeKey,"pushUp");
                fragmentRunning.setArguments(bundle);
                navigationView.getMenu().getItem(2).setChecked(true);
                ((MainActivity)getActivity()).replaceFragment(fragmentRunning);
            }
        });
        sitUpLayoutBtn.setOnClickListener(new View.OnClickListener() {
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

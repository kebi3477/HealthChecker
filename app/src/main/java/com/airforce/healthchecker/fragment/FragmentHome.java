package com.airforce.healthchecker.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airforce.healthchecker.MainActivity;
import com.airforce.healthchecker.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FragmentHome extends Fragment {

    Button runningButton, pushUpButton, sitUpButton;
    private BottomNavigationView navigationView;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        navigationView = ((MainActivity)getActivity()).findViewById(R.id.navigationView); //하단 네비게이션 활성화

        runningButton = (Button) view.findViewById(R.id.runningButton);
        runningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationView.getMenu().getItem(2).setChecked(true);
                ((MainActivity)getActivity()).replaceFragment(new FragmentRunning());
            }
        });

        return view;
    }
}

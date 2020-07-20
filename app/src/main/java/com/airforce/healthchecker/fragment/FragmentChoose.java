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

public class FragmentChoose extends Fragment {

    private LinearLayout recodeButton, competitionButton;
    private FragmentRecode fragmentRecode = new FragmentRecode();
    private FragmentCompetition fragmentCompetition = new FragmentCompetition();

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose, container, false);

        recodeButton = view.findViewById(R.id.recodeButton);
        competitionButton = view.findViewById(R.id.competitionButton);

        recodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).replaceFragment(fragmentRecode);
            }
        });

        competitionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).replaceFragment(fragmentCompetition);
            }
        });

        return view;
    }
}

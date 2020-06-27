package com.airforce.healthchecker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentHome fragmentHome = new FragmentHome();
    private FragmentRecode fragmentRecode = new FragmentRecode();
    private FragmentStrength fragmentStrength = new FragmentStrength();
    private FragmentRanking fragmentRanking = new FragmentRanking();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragmentRecode).commitAllowingStateLoss();

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());
    }

    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch(item.getItemId()) {
                case R.id.homeItem:
                    transaction.replace(R.id.frameLayout, fragmentHome).commitAllowingStateLoss();
                    break;
                case R.id.recodeItem:
                    transaction.replace(R.id.frameLayout, fragmentRecode).commitAllowingStateLoss();
                    break;
                case R.id.strengthItem:
                    transaction.replace(R.id.frameLayout, fragmentStrength).commitAllowingStateLoss();
                    break;
                case R.id.rankingItem:
                    transaction.replace(R.id.frameLayout, fragmentRanking).commitAllowingStateLoss();
                    break;
            }
            return true;
        }
    }
}

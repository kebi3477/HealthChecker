package com.airforce.healthchecker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.airforce.healthchecker.fragment.FragmentHome;
import com.airforce.healthchecker.fragment.FragmentRanking;
import com.airforce.healthchecker.fragment.FragmentRecode;
import com.airforce.healthchecker.fragment.FragmentRunning;
import com.airforce.healthchecker.fragment.FragmentStrength;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentHome fragmentHome = new FragmentHome();
    private FragmentRecode fragmentRecode = new FragmentRecode();
    private FragmentStrength fragmentStrength = new FragmentStrength();
    private FragmentRanking fragmentRanking = new FragmentRanking();
    private FragmentRunning fragmentRunning = new FragmentRunning();

    private final String PREF_NAME = "SHARE_PREF";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragmentHome).commitAllowingStateLoss();

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());
    }

    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch(item.getItemId()) {
                case R.id.homeItem:
                    replaceFragment(fragmentHome);
                    break;
                case R.id.recodeItem:
                    replaceFragment(fragmentRecode);
                    break;
                case R.id.strengthItem:
                    replaceFragment(fragmentStrength);
                    break;
                case R.id.rankingItem:
                    replaceFragment(fragmentRanking);
                    break;
            }
            return true;
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragment).commitAllowingStateLoss();
    }

    public void showDialog(String msg, final String time) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setPositiveButton("종료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ArrayList<String> list = getStringArrayPref("endTime");
                list.add(time);
                setStringArrayPref("endTime", list);
            }
        });
        builder.setNegativeButton("일시정지", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FragmentRunning.status = 2;
            }
        });
        builder.show();
    }

    private void setStringArrayPref(String key, ArrayList<String> values) {
        SharedPreferences sharePref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePref.edit();
        JSONArray jsonArr = new JSONArray();

        for (String data : values) {
            jsonArr.put(data);
        }
        if (!values.isEmpty())
            editor.putString(key, jsonArr.toString());
        else
            editor.putString(key, null);

        editor.apply();
    }

    public ArrayList<String> getStringArrayPref(String key) {
        SharedPreferences sharePref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String json = sharePref.getString(key, null);
        ArrayList<String> returnDatas = new ArrayList<String>();

        if(json != null) {
            try {
                JSONArray jsonArr = new JSONArray(json);
                for(int i = 0; i < jsonArr.length(); i++) {
                    String data = jsonArr.optString(i);
                    returnDatas.add(data);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return returnDatas;
    }
}

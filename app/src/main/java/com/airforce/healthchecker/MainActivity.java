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
import com.airforce.healthchecker.fragment.FragmentGift;
import com.airforce.healthchecker.fragment.FragmentRecode;
import com.airforce.healthchecker.fragment.FragmentRunning;
import com.airforce.healthchecker.fragment.FragmentStrength;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.opencsv.CSVReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentHome fragmentHome = new FragmentHome();
    private FragmentRecode fragmentRecode = new FragmentRecode();
    private FragmentStrength fragmentStrength = new FragmentStrength();
    private FragmentGift fragmentGift = new FragmentGift();

    private final String PREF_NAME = "SHARE_PREF";

    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getSupportActionBar().hide();
        replaceFragment(fragmentHome);

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
                    replaceFragment(fragmentGift);
                    break;
            }
            return true;
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragment).commitAllowingStateLoss();
    }

    public void showDialog(String msg, final JSONObject json) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setPositiveButton("종료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ArrayList<JSONObject> recodeArrayList = getObjectArrayPref("recode");
                recodeArrayList.add(json);
                setObjectArrayPref("recode", recodeArrayList);
                navigationView = findViewById(R.id.navigationView); //하단 네비게이션 활성화
                navigationView.getMenu().getItem(1).setChecked(true);
                replaceFragment(new FragmentRecode()); //레코드 창 띄우기
            }
        });
        builder.setNegativeButton("일시정지", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sharePref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                sharePref.edit().clear().commit();
                FragmentRunning.status = 2;
            }
        });
        builder.show();
    }

    private void setObjectArrayPref(String key, ArrayList<JSONObject> values) {
        SharedPreferences sharePref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePref.edit();

        if (!values.isEmpty())
            editor.putString(key, values.toString());
        else
            editor.putString(key, null);

        editor.apply();
    }

    public ArrayList<JSONObject> getObjectArrayPref(String key) {
        SharedPreferences sharePref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String json = sharePref.getString(key, null);
        ArrayList<JSONObject> returnDatas = new ArrayList<JSONObject>();

        if(json != null) {
            try {
                JSONArray jsonArr = new JSONArray(json);
                for(int i = 0; i < jsonArr.length(); i++) {
                    JSONObject data = jsonArr.optJSONObject(i);
                    returnDatas.add(data); //add(data);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return returnDatas;
    }
}

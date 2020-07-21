package com.airforce.healthchecker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.airforce.healthchecker.fragment.FragmentChoose;
import com.airforce.healthchecker.fragment.FragmentGift;
import com.airforce.healthchecker.fragment.FragmentHome;
import com.airforce.healthchecker.fragment.FragmentMy;
import com.airforce.healthchecker.fragment.FragmentRecode;
import com.airforce.healthchecker.fragment.FragmentRunning;
import com.airforce.healthchecker.fragment.FragmentStrength;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentHome fragmentHome = new FragmentHome();
    private FragmentRecode fragmentRecode = new FragmentRecode();
    private FragmentStrength fragmentStrength = new FragmentStrength();
    private FragmentGift fragmentGift = new FragmentGift();
    private FragmentChoose fragmentChoose = new FragmentChoose();
    private FragmentMy fragmentMy = new FragmentMy();

    private final String PREF_NAME = "SHARE_PREF";
    private final String HEALTH_PREF_NAME = "HEALTH_PREF_2020";

    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getSupportActionBar().hide();
        replaceFragment(fragmentHome);

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());
        setStandardPref();
    }

    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch(item.getItemId()) {
                case R.id.homeItem:
                    replaceFragment(fragmentHome);
                    break;
                case R.id.recodeItem:
                    replaceFragment(fragmentChoose);
                    break;
                case R.id.strengthItem:
                    replaceFragment(fragmentStrength);
                    break;
                case R.id.rankingItem:
                    replaceFragment(fragmentGift);
                    break;
                case R.id.moreItem:
                    replaceFragment(fragmentMy);
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
                ArrayList<JSONObject> recodeArrayList = getObjectArrayPref(PREF_NAME, "recode");
                recodeArrayList.add(json);
                setObjectArrayPref(PREF_NAME,"recode", recodeArrayList);
                navigationView = findViewById(R.id.navigationView); //하단 네비게이션 활성화
                navigationView.getMenu().getItem(1).setChecked(true);
                replaceFragment(fragmentRecode); //레코드 창 띄우기
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

    private void setObjectArrayPref(String fileName, String key, ArrayList<JSONObject> values) {
        SharedPreferences sharePref = getSharedPreferences(fileName, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePref.edit();

        if (!values.isEmpty())
            editor.putString(key, values.toString());
        else
            editor.putString(key, null);

        editor.apply();
    }

    public ArrayList<JSONObject> getObjectArrayPref(String fileName, String key) {
        SharedPreferences sharePref = getSharedPreferences(fileName, MODE_PRIVATE);
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

    private void setStandardPref() {
        SharedPreferences sharePref = getSharedPreferences(HEALTH_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePref.edit();
        String standardData = "\t[{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"25\",\"count_p\":\"72\",\"level_p\":\"0\",\"count_s\":\"86\",\"level_s\":\"0\",\"count_r\":\"1230\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"26\",\"count_p\":\"70\",\"level_p\":\"0\",\"count_s\":\"84\",\"level_s\":\"0\",\"count_r\":\"1245\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"31\",\"count_p\":\"68\",\"level_p\":\"0\",\"count_s\":\"80\",\"level_s\":\"0\",\"count_r\":\"1300\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"36\",\"count_p\":\"65\",\"level_p\":\"0\",\"count_s\":\"76\",\"level_s\":\"0\",\"count_r\":\"1315\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"41\",\"count_p\":\"61\",\"level_p\":\"0\",\"count_s\":\"72\",\"level_s\":\"0\",\"count_r\":\"1330\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"44\",\"count_p\":\"57\",\"level_p\":\"0\",\"count_s\":\"68\",\"level_s\":\"0\",\"count_r\":\"1345\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"47\",\"count_p\":\"54\",\"level_p\":\"0\",\"count_s\":\"65\",\"level_s\":\"0\",\"count_r\":\"1400\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"50\",\"count_p\":\"51\",\"level_p\":\"0\",\"count_s\":\"62\",\"level_s\":\"0\",\"count_r\":\"1415\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"52\",\"count_p\":\"49\",\"level_p\":\"0\",\"count_s\":\"60\",\"level_s\":\"0\",\"count_r\":\"1430\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"54\",\"count_p\":\"47\",\"level_p\":\"0\",\"count_s\":\"58\",\"level_s\":\"0\",\"count_r\":\"1445\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"56\",\"count_p\":\"44\",\"level_p\":\"0\",\"count_s\":\"56\",\"level_s\":\"0\",\"count_r\":\"1510\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"58\",\"count_p\":\"42\",\"level_p\":\"0\",\"count_s\":\"54\",\"level_s\":\"0\",\"count_r\":\"1535\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"60\",\"count_p\":\"40\",\"level_p\":\"0\",\"count_s\":\"52\",\"level_s\":\"0\",\"count_r\":\"1600\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"25\",\"count_p\":\"64\",\"level_p\":\"1\",\"count_s\":\"78\",\"level_s\":\"1\",\"count_r\":\"1332\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"26\",\"count_p\":\"62\",\"level_p\":\"1\",\"count_s\":\"76\",\"level_s\":\"1\",\"count_r\":\"1352\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"31\",\"count_p\":\"60\",\"level_p\":\"1\",\"count_s\":\"72\",\"level_s\":\"1\",\"count_r\":\"1412\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"36\",\"count_p\":\"57\",\"level_p\":\"1\",\"count_s\":\"68\",\"level_s\":\"1\",\"count_r\":\"1432\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"41\",\"count_p\":\"53\",\"level_p\":\"1\",\"count_s\":\"64\",\"level_s\":\"1\",\"count_r\":\"1449\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"44\",\"count_p\":\"49\",\"level_p\":\"1\",\"count_s\":\"60\",\"level_s\":\"1\",\"count_r\":\"1505\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"47\",\"count_p\":\"46\",\"level_p\":\"1\",\"count_s\":\"57\",\"level_s\":\"1\",\"count_r\":\"1525\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"50\",\"count_p\":\"43\",\"level_p\":\"1\",\"count_s\":\"54\",\"level_s\":\"1\",\"count_r\":\"1542\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"52\",\"count_p\":\"41\",\"level_p\":\"1\",\"count_s\":\"52\",\"level_s\":\"1\",\"count_r\":\"1602\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"54\",\"count_p\":\"39\",\"level_p\":\"1\",\"count_s\":\"50\",\"level_s\":\"1\",\"count_r\":\"1609\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"56\",\"count_p\":\"36\",\"level_p\":\"1\",\"count_s\":\"48\",\"level_s\":\"1\",\"count_r\":\"1646\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"58\",\"count_p\":\"34\",\"level_p\":\"1\",\"count_s\":\"46\",\"level_s\":\"1\",\"count_r\":\"1713\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"60\",\"count_p\":\"32\",\"level_p\":\"1\",\"count_s\":\"44\",\"level_s\":\"1\",\"count_r\":\"1740\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"25\",\"count_p\":\"56\",\"level_p\":\"2\",\"count_s\":\"70\",\"level_s\":\"2\",\"count_r\":\"1434\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"26\",\"count_p\":\"54\",\"level_p\":\"2\",\"count_s\":\"68\",\"level_s\":\"2\",\"count_r\":\"1459\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"31\",\"count_p\":\"52\",\"level_p\":\"2\",\"count_s\":\"65\",\"level_s\":\"2\",\"count_r\":\"1524\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"36\",\"count_p\":\"49\",\"level_p\":\"2\",\"count_s\":\"60\",\"level_s\":\"2\",\"count_r\":\"1549\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"41\",\"count_p\":\"45\",\"level_p\":\"2\",\"count_s\":\"56\",\"level_s\":\"2\",\"count_r\":\"1607\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"44\",\"count_p\":\"41\",\"level_p\":\"2\",\"count_s\":\"52\",\"level_s\":\"2\",\"count_r\":\"1626\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"47\",\"count_p\":\"38\",\"level_p\":\"2\",\"count_s\":\"49\",\"level_s\":\"2\",\"count_r\":\"1651\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"50\",\"count_p\":\"35\",\"level_p\":\"2\",\"count_s\":\"46\",\"level_s\":\"2\",\"count_r\":\"1709\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"52\",\"count_p\":\"33\",\"level_p\":\"2\",\"count_s\":\"44\",\"level_s\":\"2\",\"count_r\":\"1734\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"54\",\"count_p\":\"31\",\"level_p\":\"2\",\"count_s\":\"42\",\"level_s\":\"2\",\"count_r\":\"1752\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"56\",\"count_p\":\"28\",\"level_p\":\"2\",\"count_s\":\"40\",\"level_s\":\"2\",\"count_r\":\"1823\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"58\",\"count_p\":\"26\",\"level_p\":\"2\",\"count_s\":\"38\",\"level_s\":\"2\",\"count_r\":\"1851\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"60\",\"count_p\":\"24\",\"level_p\":\"2\",\"count_s\":\"36\",\"level_s\":\"2\",\"count_r\":\"1919\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"25\",\"count_p\":\"48\",\"level_p\":\"3\",\"count_s\":\"62\",\"level_s\":\"3\",\"count_r\":\"1536\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"26\",\"count_p\":\"46\",\"level_p\":\"3\",\"count_s\":\"60\",\"level_s\":\"3\",\"count_r\":\"1606\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"31\",\"count_p\":\"44\",\"level_p\":\"3\",\"count_s\":\"57\",\"level_s\":\"3\",\"count_r\":\"1636\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"36\",\"count_p\":\"41\",\"level_p\":\"3\",\"count_s\":\"52\",\"level_s\":\"3\",\"count_r\":\"1706\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"41\",\"count_p\":\"37\",\"level_p\":\"3\",\"count_s\":\"48\",\"level_s\":\"3\",\"count_r\":\"1726\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"44\",\"count_p\":\"33\",\"level_p\":\"3\",\"count_s\":\"44\",\"level_s\":\"3\",\"count_r\":\"1746\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"47\",\"count_p\":\"30\",\"level_p\":\"3\",\"count_s\":\"41\",\"level_s\":\"3\",\"count_r\":\"1816\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"50\",\"count_p\":\"27\",\"level_p\":\"3\",\"count_s\":\"38\",\"level_s\":\"3\",\"count_r\":\"1836\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"52\",\"count_p\":\"25\",\"level_p\":\"3\",\"count_s\":\"36\",\"level_s\":\"3\",\"count_r\":\"1906\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"54\",\"count_p\":\"23\",\"level_p\":\"3\",\"count_s\":\"34\",\"level_s\":\"3\",\"count_r\":\"1926\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"56\",\"count_p\":\"20\",\"level_p\":\"3\",\"count_s\":\"32\",\"level_s\":\"3\",\"count_r\":\"1959\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"58\",\"count_p\":\"18\",\"level_p\":\"3\",\"count_s\":\"30\",\"level_s\":\"3\",\"count_r\":\"2029\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"s\",\"age\":\"60\",\"count_p\":\"16\",\"level_p\":\"3\",\"count_s\":\"28\",\"level_s\":\"3\",\"count_r\":\"2059\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"25\",\"count_p\":\"35\",\"level_p\":\"0\",\"count_s\":\"71\",\"level_s\":\"0\",\"count_r\":\"1500\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"26\",\"count_p\":\"33\",\"level_p\":\"0\",\"count_s\":\"68\",\"level_s\":\"0\",\"count_r\":\"1518\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"31\",\"count_p\":\"31\",\"level_p\":\"0\",\"count_s\":\"66\",\"level_s\":\"0\",\"count_r\":\"1536\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"36\",\"count_p\":\"29\",\"level_p\":\"0\",\"count_s\":\"63\",\"level_s\":\"0\",\"count_r\":\"1554\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"41\",\"count_p\":\"26\",\"level_p\":\"0\",\"count_s\":\"60\",\"level_s\":\"0\",\"count_r\":\"1612\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"44\",\"count_p\":\"24\",\"level_p\":\"0\",\"count_s\":\"57\",\"level_s\":\"0\",\"count_r\":\"1630\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"47\",\"count_p\":\"22\",\"level_p\":\"0\",\"count_s\":\"55\",\"level_s\":\"0\",\"count_r\":\"1648\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"50\",\"count_p\":\"19\",\"level_p\":\"0\",\"count_s\":\"54\",\"level_s\":\"0\",\"count_r\":\"1706\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"52\",\"count_p\":\"17\",\"level_p\":\"0\",\"count_s\":\"53\",\"level_s\":\"0\",\"count_r\":\"1724\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"54\",\"count_p\":\"15\",\"level_p\":\"0\",\"count_s\":\"52\",\"level_s\":\"0\",\"count_r\":\"1742\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"56\",\"count_p\":\"13\",\"level_p\":\"0\",\"count_s\":\"50\",\"level_s\":\"0\",\"count_r\":\"1810\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"58\",\"count_p\":\"12\",\"level_p\":\"0\",\"count_s\":\"48\",\"level_s\":\"0\",\"count_r\":\"1835\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"60\",\"count_p\":\"11\",\"level_p\":\"0\",\"count_s\":\"46\",\"level_s\":\"0\",\"count_r\":\"1900\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"25\",\"count_p\":\"31\",\"level_p\":\"1\",\"count_s\":\"63\",\"level_s\":\"1\",\"count_r\":\"1614\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"26\",\"count_p\":\"29\",\"level_p\":\"1\",\"count_s\":\"60\",\"level_s\":\"1\",\"count_r\":\"1638\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"31\",\"count_p\":\"27\",\"level_p\":\"1\",\"count_s\":\"58\",\"level_s\":\"1\",\"count_r\":\"1702\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"36\",\"count_p\":\"25\",\"level_p\":\"1\",\"count_s\":\"55\",\"level_s\":\"1\",\"count_r\":\"1726\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"41\",\"count_p\":\"23\",\"level_p\":\"1\",\"count_s\":\"52\",\"level_s\":\"1\",\"count_r\":\"1746\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"44\",\"count_p\":\"21\",\"level_p\":\"1\",\"count_s\":\"49\",\"level_s\":\"1\",\"count_r\":\"1806\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"47\",\"count_p\":\"19\",\"level_p\":\"1\",\"count_s\":\"47\",\"level_s\":\"1\",\"count_r\":\"1830\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"50\",\"count_p\":\"17\",\"level_p\":\"1\",\"count_s\":\"46\",\"level_s\":\"1\",\"count_r\":\"1850\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"52\",\"count_p\":\"14\",\"level_p\":\"1\",\"count_s\":\"45\",\"level_s\":\"1\",\"count_r\":\"1914\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"54\",\"count_p\":\"13\",\"level_p\":\"1\",\"count_s\":\"44\",\"level_s\":\"1\",\"count_r\":\"1934\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"56\",\"count_p\":\"11\",\"level_p\":\"1\",\"count_s\":\"42\",\"level_s\":\"1\",\"count_r\":\"2003\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"58\",\"count_p\":\"10\",\"level_p\":\"1\",\"count_s\":\"41\",\"level_s\":\"1\",\"count_r\":\"2030\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"60\",\"count_p\":\"9\",\"level_p\":\"1\",\"count_s\":\"39\",\"level_s\":\"1\",\"count_r\":\"2057\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"25\",\"count_p\":\"27\",\"level_p\":\"2\",\"count_s\":\"55\",\"level_s\":\"2\",\"count_r\":\"1729\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"26\",\"count_p\":\"26\",\"level_p\":\"2\",\"count_s\":\"52\",\"level_s\":\"2\",\"count_r\":\"1759\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"31\",\"count_p\":\"23\",\"level_p\":\"2\",\"count_s\":\"50\",\"level_s\":\"2\",\"count_r\":\"1829\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"36\",\"count_p\":\"22\",\"level_p\":\"2\",\"count_s\":\"47\",\"level_s\":\"2\",\"count_r\":\"1859\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"41\",\"count_p\":\"19\",\"level_p\":\"2\",\"count_s\":\"44\",\"level_s\":\"2\",\"count_r\":\"1921\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"44\",\"count_p\":\"18\",\"level_p\":\"2\",\"count_s\":\"41\",\"level_s\":\"2\",\"count_r\":\"1943\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"47\",\"count_p\":\"16\",\"level_p\":\"2\",\"count_s\":\"39\",\"level_s\":\"2\",\"count_r\":\"2013\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"50\",\"count_p\":\"14\",\"level_p\":\"2\",\"count_s\":\"38\",\"level_s\":\"2\",\"count_r\":\"2035\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"52\",\"count_p\":\"12\",\"level_p\":\"2\",\"count_s\":\"37\",\"level_s\":\"2\",\"count_r\":\"2105\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"54\",\"count_p\":\"10\",\"level_p\":\"2\",\"count_s\":\"36\",\"level_s\":\"2\",\"count_r\":\"2127\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"56\",\"count_p\":\"9\",\"level_p\":\"2\",\"count_s\":\"34\",\"level_s\":\"2\",\"count_r\":\"2156\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"58\",\"count_p\":\"8\",\"level_p\":\"2\",\"count_s\":\"33\",\"level_s\":\"2\",\"count_r\":\"2225\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"60\",\"count_p\":\"7\",\"level_p\":\"2\",\"count_s\":\"31\",\"level_s\":\"2\",\"count_r\":\"2253\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"25\",\"count_p\":\"23\",\"level_p\":\"3\",\"count_s\":\"47\",\"level_s\":\"3\",\"count_r\":\"1843\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"26\",\"count_p\":\"22\",\"level_p\":\"3\",\"count_s\":\"45\",\"level_s\":\"3\",\"count_r\":\"1919\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"31\",\"count_p\":\"20\",\"level_p\":\"3\",\"count_s\":\"42\",\"level_s\":\"3\",\"count_r\":\"1955\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"36\",\"count_p\":\"18\",\"level_p\":\"3\",\"count_s\":\"39\",\"level_s\":\"3\",\"count_r\":\"2031\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"41\",\"count_p\":\"16\",\"level_p\":\"3\",\"count_s\":\"36\",\"level_s\":\"3\",\"count_r\":\"2055\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"44\",\"count_p\":\"15\",\"level_p\":\"3\",\"count_s\":\"33\",\"level_s\":\"3\",\"count_r\":\"2119\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"47\",\"count_p\":\"13\",\"level_p\":\"3\",\"count_s\":\"31\",\"level_s\":\"3\",\"count_r\":\"2155\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"50\",\"count_p\":\"11\",\"level_p\":\"3\",\"count_s\":\"30\",\"level_s\":\"3\",\"count_r\":\"2219\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"52\",\"count_p\":\"9\",\"level_p\":\"3\",\"count_s\":\"29\",\"level_s\":\"3\",\"count_r\":\"2255\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"54\",\"count_p\":\"8\",\"level_p\":\"3\",\"count_s\":\"28\",\"level_s\":\"3\",\"count_r\":\"2319\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"56\",\"count_p\":\"7\",\"level_p\":\"3\",\"count_s\":\"26\",\"level_s\":\"3\",\"count_r\":\"2349\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"58\",\"count_p\":\"6\",\"level_p\":\"3\",\"count_s\":\"25\",\"level_s\":\"3\",\"count_r\":\"2419\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"s\",\"age\":\"60\",\"count_p\":\"5\",\"level_p\":\"3\",\"count_s\":\"24\",\"level_s\":\"3\",\"count_r\":\"2449\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"25\",\"count_p\":\"65\",\"level_p\":\"0\",\"count_s\":\"78\",\"level_s\":\"0\",\"count_r\":\"1345\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"26\",\"count_p\":\"63\",\"level_p\":\"0\",\"count_s\":\"76\",\"level_s\":\"0\",\"count_r\":\"1402\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"31\",\"count_p\":\"61\",\"level_p\":\"0\",\"count_s\":\"72\",\"level_s\":\"0\",\"count_r\":\"1418\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"36\",\"count_p\":\"59\",\"level_p\":\"0\",\"count_s\":\"69\",\"level_s\":\"0\",\"count_r\":\"1435\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"41\",\"count_p\":\"55\",\"level_p\":\"0\",\"count_s\":\"65\",\"level_s\":\"0\",\"count_r\":\"1451\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"44\",\"count_p\":\"51\",\"level_p\":\"0\",\"count_s\":\"62\",\"level_s\":\"0\",\"count_r\":\"1508\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"47\",\"count_p\":\"49\",\"level_p\":\"0\",\"count_s\":\"59\",\"level_s\":\"0\",\"count_r\":\"1524\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"50\",\"count_p\":\"46\",\"level_p\":\"0\",\"count_s\":\"56\",\"level_s\":\"0\",\"count_r\":\"1541\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"52\",\"count_p\":\"44\",\"level_p\":\"0\",\"count_s\":\"54\",\"level_s\":\"0\",\"count_r\":\"1557\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"54\",\"count_p\":\"42\",\"level_p\":\"0\",\"count_s\":\"53\",\"level_s\":\"0\",\"count_r\":\"1614\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"56\",\"count_p\":\"40\",\"level_p\":\"0\",\"count_s\":\"52\",\"level_s\":\"0\",\"count_r\":\"1630\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"58\",\"count_p\":\"38\",\"level_p\":\"0\",\"count_s\":\"51\",\"level_s\":\"0\",\"count_r\":\"1645\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"60\",\"count_p\":\"36\",\"level_p\":\"0\",\"count_s\":\"49\",\"level_s\":\"0\",\"count_r\":\"1700\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"25\",\"count_p\":\"58\",\"level_p\":\"1\",\"count_s\":\"71\",\"level_s\":\"1\",\"count_r\":\"1453\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"26\",\"count_p\":\"56\",\"level_p\":\"1\",\"count_s\":\"69\",\"level_s\":\"1\",\"count_r\":\"1516\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"31\",\"count_p\":\"54\",\"level_p\":\"1\",\"count_s\":\"65\",\"level_s\":\"1\",\"count_r\":\"1537\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"36\",\"count_p\":\"51\",\"level_p\":\"1\",\"count_s\":\"62\",\"level_s\":\"1\",\"count_r\":\"1600\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"41\",\"count_p\":\"48\",\"level_p\":\"1\",\"count_s\":\"58\",\"level_s\":\"1\",\"count_r\":\"1618\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"44\",\"count_p\":\"44\",\"level_p\":\"1\",\"count_s\":\"54\",\"level_s\":\"1\",\"count_r\":\"1636\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"47\",\"count_p\":\"41\",\"level_p\":\"1\",\"count_s\":\"52\",\"level_s\":\"1\",\"count_r\":\"1658\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"50\",\"count_p\":\"39\",\"level_p\":\"1\",\"count_s\":\"49\",\"level_s\":\"1\",\"count_r\":\"1717\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"52\",\"count_p\":\"37\",\"level_p\":\"1\",\"count_s\":\"47\",\"level_s\":\"1\",\"count_r\":\"1738\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"54\",\"count_p\":\"35\",\"level_p\":\"1\",\"count_s\":\"45\",\"level_s\":\"1\",\"count_r\":\"1757\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"56\",\"count_p\":\"33\",\"level_p\":\"1\",\"count_s\":\"45\",\"level_s\":\"1\",\"count_r\":\"1815\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"58\",\"count_p\":\"31\",\"level_p\":\"1\",\"count_s\":\"44\",\"level_s\":\"1\",\"count_r\":\"1831\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"60\",\"count_p\":\"29\",\"level_p\":\"1\",\"count_s\":\"42\",\"level_s\":\"1\",\"count_r\":\"1850\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"25\",\"count_p\":\"50\",\"level_p\":\"2\",\"count_s\":\"63\",\"level_s\":\"2\",\"count_r\":\"1602\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"26\",\"count_p\":\"49\",\"level_p\":\"2\",\"count_s\":\"62\",\"level_s\":\"2\",\"count_r\":\"1629\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"31\",\"count_p\":\"47\",\"level_p\":\"2\",\"count_s\":\"59\",\"level_s\":\"2\",\"count_r\":\"1657\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"36\",\"count_p\":\"44\",\"level_p\":\"2\",\"count_s\":\"54\",\"level_s\":\"2\",\"count_r\":\"1724\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"41\",\"count_p\":\"41\",\"level_p\":\"2\",\"count_s\":\"51\",\"level_s\":\"2\",\"count_r\":\"1744\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"44\",\"count_p\":\"37\",\"level_p\":\"2\",\"count_s\":\"47\",\"level_s\":\"2\",\"count_r\":\"1805\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"47\",\"count_p\":\"34\",\"level_p\":\"2\",\"count_s\":\"45\",\"level_s\":\"2\",\"count_r\":\"1832\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"50\",\"count_p\":\"32\",\"level_p\":\"2\",\"count_s\":\"42\",\"level_s\":\"2\",\"count_r\":\"1852\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"52\",\"count_p\":\"30\",\"level_p\":\"2\",\"count_s\":\"40\",\"level_s\":\"2\",\"count_r\":\"1920\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"54\",\"count_p\":\"28\",\"level_p\":\"2\",\"count_s\":\"38\",\"level_s\":\"2\",\"count_r\":\"1940\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"56\",\"count_p\":\"26\",\"level_p\":\"2\",\"count_s\":\"37\",\"level_s\":\"2\",\"count_r\":\"2000\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"58\",\"count_p\":\"24\",\"level_p\":\"2\",\"count_s\":\"36\",\"level_s\":\"2\",\"count_r\":\"2018\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"60\",\"count_p\":\"22\",\"level_p\":\"2\",\"count_s\":\"34\",\"level_s\":\"2\",\"count_r\":\"2039\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"25\",\"count_p\":\"43\",\"level_p\":\"3\",\"count_s\":\"56\",\"level_s\":\"3\",\"count_r\":\"1710\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"26\",\"count_p\":\"42\",\"level_p\":\"3\",\"count_s\":\"55\",\"level_s\":\"3\",\"count_r\":\"1743\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"31\",\"count_p\":\"40\",\"level_p\":\"3\",\"count_s\":\"52\",\"level_s\":\"3\",\"count_r\":\"1816\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"36\",\"count_p\":\"37\",\"level_p\":\"3\",\"count_s\":\"47\",\"level_s\":\"3\",\"count_r\":\"1849\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"41\",\"count_p\":\"33\",\"level_p\":\"3\",\"count_s\":\"44\",\"level_s\":\"3\",\"count_r\":\"1911\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"44\",\"count_p\":\"30\",\"level_p\":\"3\",\"count_s\":\"40\",\"level_s\":\"3\",\"count_r\":\"1933\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"47\",\"count_p\":\"27\",\"level_p\":\"3\",\"count_s\":\"37\",\"level_s\":\"3\",\"count_r\":\"2006\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"50\",\"count_p\":\"24\",\"level_p\":\"3\",\"count_s\":\"35\",\"level_s\":\"3\",\"count_r\":\"2028\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"52\",\"count_p\":\"23\",\"level_p\":\"3\",\"count_s\":\"33\",\"level_s\":\"3\",\"count_r\":\"2101\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"54\",\"count_p\":\"21\",\"level_p\":\"3\",\"count_s\":\"31\",\"level_s\":\"3\",\"count_r\":\"2123\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"56\",\"count_p\":\"19\",\"level_p\":\"3\",\"count_s\":\"30\",\"level_s\":\"3\",\"count_r\":\"2144\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"58\",\"count_p\":\"17\",\"level_p\":\"3\",\"count_s\":\"29\",\"level_s\":\"3\",\"count_r\":\"2204\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"m\",\"sinbun\":\"p\",\"age\":\"60\",\"count_p\":\"15\",\"level_p\":\"3\",\"count_s\":\"27\",\"level_s\":\"3\",\"count_r\":\"2229\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"25\",\"count_p\":\"32\",\"level_p\":\"0\",\"count_s\":\"64\",\"level_s\":\"0\",\"count_r\":\"1630\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"26\",\"count_p\":\"30\",\"level_p\":\"0\",\"count_s\":\"62\",\"level_s\":\"0\",\"count_r\":\"1650\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"31\",\"count_p\":\"28\",\"level_p\":\"0\",\"count_s\":\"60\",\"level_s\":\"0\",\"count_r\":\"1710\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"36\",\"count_p\":\"26\",\"level_p\":\"0\",\"count_s\":\"57\",\"level_s\":\"0\",\"count_r\":\"1729\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"41\",\"count_p\":\"23\",\"level_p\":\"0\",\"count_s\":\"54\",\"level_s\":\"0\",\"count_r\":\"1749\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"44\",\"count_p\":\"22\",\"level_p\":\"0\",\"count_s\":\"52\",\"level_s\":\"0\",\"count_r\":\"1809\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"47\",\"count_p\":\"20\",\"level_p\":\"0\",\"count_s\":\"50\",\"level_s\":\"0\",\"count_r\":\"1829\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"50\",\"count_p\":\"17\",\"level_p\":\"0\",\"count_s\":\"49\",\"level_s\":\"0\",\"count_r\":\"1849\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"52\",\"count_p\":\"15\",\"level_p\":\"0\",\"count_s\":\"48\",\"level_s\":\"0\",\"count_r\":\"1908\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"54\",\"count_p\":\"14\",\"level_p\":\"0\",\"count_s\":\"47\",\"level_s\":\"0\",\"count_r\":\"1928\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"56\",\"count_p\":\"12\",\"level_p\":\"0\",\"count_s\":\"46\",\"level_s\":\"0\",\"count_r\":\"1945\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"58\",\"count_p\":\"11\",\"level_p\":\"0\",\"count_s\":\"45\",\"level_s\":\"0\",\"count_r\":\"2000\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"60\",\"count_p\":\"10\",\"level_p\":\"0\",\"count_s\":\"44\",\"level_s\":\"0\",\"count_r\":\"2015\",\"level_r\":\"0\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"25\",\"count_p\":\"28\",\"level_p\":\"1\",\"count_s\":\"57\",\"level_s\":\"1\",\"count_r\":\"1752\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"26\",\"count_p\":\"26\",\"level_p\":\"1\",\"count_s\":\"54\",\"level_s\":\"1\",\"count_r\":\"1818\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"31\",\"count_p\":\"24\",\"level_p\":\"1\",\"count_s\":\"53\",\"level_s\":\"1\",\"count_r\":\"1845\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"36\",\"count_p\":\"23\",\"level_p\":\"1\",\"count_s\":\"50\",\"level_s\":\"1\",\"count_r\":\"1911\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"41\",\"count_p\":\"21\",\"level_p\":\"1\",\"count_s\":\"47\",\"level_s\":\"1\",\"count_r\":\"1939\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"44\",\"count_p\":\"19\",\"level_p\":\"1\",\"count_s\":\"45\",\"level_s\":\"1\",\"count_r\":\"1955\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"47\",\"count_p\":\"17\",\"level_p\":\"1\",\"count_s\":\"43\",\"level_s\":\"1\",\"count_r\":\"2022\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"50\",\"count_p\":\"15\",\"level_p\":\"1\",\"count_s\":\"42\",\"level_s\":\"1\",\"count_r\":\"2044\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"52\",\"count_p\":\"13\",\"level_p\":\"1\",\"count_s\":\"41\",\"level_s\":\"1\",\"count_r\":\"2110\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"54\",\"count_p\":\"12\",\"level_p\":\"1\",\"count_s\":\"40\",\"level_s\":\"1\",\"count_r\":\"2132\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"56\",\"count_p\":\"10\",\"level_p\":\"1\",\"count_s\":\"39\",\"level_s\":\"1\",\"count_r\":\"2146\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"58\",\"count_p\":\"9\",\"level_p\":\"1\",\"count_s\":\"38\",\"level_s\":\"1\",\"count_r\":\"2159\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"60\",\"count_p\":\"8\",\"level_p\":\"1\",\"count_s\":\"37\",\"level_s\":\"1\",\"count_r\":\"2213\",\"level_r\":\"1\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"25\",\"count_p\":\"24\",\"level_p\":\"2\",\"count_s\":\"50\",\"level_s\":\"2\",\"count_r\":\"1913\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"26\",\"count_p\":\"23\",\"level_p\":\"2\",\"count_s\":\"47\",\"level_s\":\"2\",\"count_r\":\"1947\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"31\",\"count_p\":\"21\",\"level_p\":\"2\",\"count_s\":\"45\",\"level_s\":\"2\",\"count_r\":\"2020\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"36\",\"count_p\":\"20\",\"level_p\":\"2\",\"count_s\":\"43\",\"level_s\":\"2\",\"count_r\":\"2052\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"41\",\"count_p\":\"17\",\"level_p\":\"2\",\"count_s\":\"40\",\"level_s\":\"2\",\"count_r\":\"2129\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"44\",\"count_p\":\"16\",\"level_p\":\"2\",\"count_s\":\"37\",\"level_s\":\"2\",\"count_r\":\"2141\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"47\",\"count_p\":\"14\",\"level_p\":\"2\",\"count_s\":\"36\",\"level_s\":\"2\",\"count_r\":\"2214\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"50\",\"count_p\":\"13\",\"level_p\":\"2\",\"count_s\":\"35\",\"level_s\":\"2\",\"count_r\":\"2238\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"52\",\"count_p\":\"11\",\"level_p\":\"2\",\"count_s\":\"34\",\"level_s\":\"2\",\"count_r\":\"2311\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"54\",\"count_p\":\"9\",\"level_p\":\"2\",\"count_s\":\"33\",\"level_s\":\"2\",\"count_r\":\"2335\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"56\",\"count_p\":\"8\",\"level_p\":\"2\",\"count_s\":\"32\",\"level_s\":\"2\",\"count_r\":\"2348\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"58\",\"count_p\":\"7\",\"level_p\":\"2\",\"count_s\":\"30\",\"level_s\":\"2\",\"count_r\":\"2359\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"60\",\"count_p\":\"6\",\"level_p\":\"2\",\"count_s\":\"29\",\"level_s\":\"2\",\"count_r\":\"2411\",\"level_r\":\"2\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"25\",\"count_p\":\"21\",\"level_p\":\"3\",\"count_s\":\"43\",\"level_s\":\"3\",\"count_r\":\"2035\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"26\",\"count_p\":\"20\",\"level_p\":\"3\",\"count_s\":\"41\",\"level_s\":\"3\",\"count_r\":\"2115\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"31\",\"count_p\":\"18\",\"level_p\":\"3\",\"count_s\":\"38\",\"level_s\":\"3\",\"count_r\":\"2155\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"36\",\"count_p\":\"16\",\"level_p\":\"3\",\"count_s\":\"36\",\"level_s\":\"3\",\"count_r\":\"2234\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"41\",\"count_p\":\"15\",\"level_p\":\"3\",\"count_s\":\"33\",\"level_s\":\"3\",\"count_r\":\"2319\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"44\",\"count_p\":\"14\",\"level_p\":\"3\",\"count_s\":\"30\",\"level_s\":\"3\",\"count_r\":\"2327\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"47\",\"count_p\":\"12\",\"level_p\":\"3\",\"count_s\":\"29\",\"level_s\":\"3\",\"count_r\":\"2407\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"50\",\"count_p\":\"10\",\"level_p\":\"3\",\"count_s\":\"28\",\"level_s\":\"3\",\"count_r\":\"2433\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"52\",\"count_p\":\"8\",\"level_p\":\"3\",\"count_s\":\"27\",\"level_s\":\"3\",\"count_r\":\"2513\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"54\",\"count_p\":\"7\",\"level_p\":\"3\",\"count_s\":\"26\",\"level_s\":\"3\",\"count_r\":\"2539\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"56\",\"count_p\":\"6\",\"level_p\":\"3\",\"count_s\":\"25\",\"level_s\":\"3\",\"count_r\":\"2549\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"58\",\"count_p\":\"5\",\"level_p\":\"3\",\"count_s\":\"23\",\"level_s\":\"3\",\"count_r\":\"2559\",\"level_r\":\"3\"},\n" +
                "\t{\"sex\":\"w\",\"sinbun\":\"p\",\"age\":\"60\",\"count_p\":\"4\",\"level_p\":\"3\",\"count_s\":\"22\",\"level_s\":\"3\",\"count_r\":\"2609\",\"level_r\":\"3\"}]";

        editor.putString("data", standardData);

        editor.apply();
    }
}

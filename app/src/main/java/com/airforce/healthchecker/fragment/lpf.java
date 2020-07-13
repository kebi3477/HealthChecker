package com.airforce.healthchecker.fragment;

//LowPassFilter
public class lpf {
    double[] data = {0,0,0,0,0,0,0,0,0,0};
    int i=9;
    lpf(){

    }
    public double lpfCalc(double input){
        double sum = 0;
        for(int j=1;j<i;j++){
            data[j-1]=data[j];
            sum+=data[j];
        }
        data[i]=input;
        sum+=input;
        return sum/i;


    }
}

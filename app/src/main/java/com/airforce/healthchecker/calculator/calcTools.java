package com.airforce.healthchecker.calculator;

public class calcTools {

    double[][] euler(double[] data){
        double[][] result={
/*
                {
                    cos(data[1])*cos(data[2]),
                    -cos(data[1])*sin(data[2]),
                    sin(data[1])
                },
                {
                    sin(data[0])*sin(data[1])*cos(data[2])+cos(data[0])*sin(data[2]),
                    (-sin(data[0]))*sin(data[1])*sin(data[2])+cos(data[0])*cos(data[2]),
                    (-sin(data[0]))*cos(data[1])
                },
                {
                    cos(data[0])*sin(data[1])*cos(data[2])-sin(data[0])*sin(data[2]),
                    cos(data[0])*sin(data[1])*sin(data[2])+sin(data[0])*cos(data[2]),
                    cos(data[0])*cos(data[1])
                }*/


                {
                        cos(data[1])*cos(data[2]),
                        cos(data[1])*sin(data[2]),
                        -sin(data[1])
                },
                {
                        sin(data[0])*sin(data[1])*cos(data[2])-cos(data[0])*sin(data[2]),
                        sin(data[0])*sin(data[1])*sin(data[2])+cos(data[0])*cos(data[2]),
                        sin(data[0])*cos(data[1])
                },
                {
                        cos(data[0])*sin(data[1])*cos(data[2])+sin(data[0])*sin(data[2]),
                        cos(data[0])*sin(data[1])*sin(data[2])-sin(data[0])*cos(data[2]),
                        cos(data[0])*cos(data[1])
                }
        };
        return result;
    }

    double[] matrix_mul(double[] A, double[][]B) {
        double[] C = new double[3];
        for(int i=0; i<3; i++) {
            for(int j=0; j<3; j++) {
                C[i] += (B[i][j] * A[j]);
            }
        }
        return C;
    }

    double cos(double a){
        return Math.cos(a);
    }
    double sin(double a){
        return Math.sin(a);
    }
    double atan(double a){
        return Math.atan(a);
    }
    double pow(double a){
        return Math.pow(a,2);
    }

}

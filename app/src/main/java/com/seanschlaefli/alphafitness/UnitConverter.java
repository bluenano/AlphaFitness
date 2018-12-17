package com.seanschlaefli.alphafitness;

public class UnitConverter {

    private UnitConverter() {}

    public static float stepCountToFt(float stepCount, float ftMultipler) {
        return stepCount * ftMultipler;
    }

    public static float ftToMiles(float ft) {
        return ft / 5280f;
    }


    public static float stepCountToMiles(float stepCount, float ftMultipler) {
        float ft = stepCountToFt(stepCount, ftMultipler);
        return ftToMiles(ft);
    }

    public static float msToMinutes(long milliseconds) {
        return ((float) milliseconds / 60000f);
    }

    public static void main(String[] args) {
        System.out.println(Float.toString(msToMinutes(5483881)));
    }

}

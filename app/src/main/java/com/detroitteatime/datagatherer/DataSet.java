package com.detroitteatime.datagatherer;


import java.io.Serializable;

public class DataSet implements Serializable{
    private double lat;
    private double lng;
    private double speedGPS;
    private double speedACC;
    private String time;
    private double accelX;
    private double accelY;
    private double accelZ;
    private double linX;
    private double linY;
    private double linZ;
    private double gravX;
    private double gravY;
    private double gravZ;
    private double magX;
    private double magY;
    private double magZ;
    private double gyroX;
    private double gyroY;
    private double gyroZ;
    private double orientX;
    private double orientY;
    private double orientZ;
    private boolean positive;



    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getSpeedGPS() {
        return speedGPS;
    }

    public void setSpeedGPS(double speedGPS) {
        this.speedGPS = speedGPS;
    }

    public double getSpeedACC() {
        return speedACC;
    }

    public void setSpeedACC(double speedACC) {
        this.speedACC = speedACC;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getAccelX() {
        return accelX;
    }

    public void setAccelX(double accel) {
        this.accelX = accel;
    }

    public double getAccelY() {
        return accelY;
    }

    public void setAccelY(double accelY) {
        this.accelY = accelY;
    }

    public double getAccelZ() {
        return accelZ;
    }

    public void setAccelZ(double accelZ) {
        this.accelZ = accelZ;
    }

    public double getLinX() {
        return linX;
    }

    public void setLinX(double linX) {
        this.linX = linX;
    }

    public double getLinY() {
        return linY;
    }

    public void setLinY(double linY) {
        this.linY = linY;
    }

    public double getLinZ() {
        return linZ;
    }

    public void setLinZ(double linZ) {
        this.linZ = linZ;
    }

    public double getGravX() {
        return gravX;
    }

    public void setGravX(double gravX) {
        this.gravX = gravX;
    }

    public double getGravY() {
        return gravY;
    }

    public void setGravY(double gravY) {
        this.gravY = gravY;
    }

    public double getGravZ() {
        return gravZ;
    }

    public void setGravZ(double gravZ) {
        this.gravZ = gravZ;
    }

    public double getMagX() {
        return magX;
    }

    public void setMagX(double magX) {
        this.magX = magX;
    }

    public double getMagY() {
        return magY;
    }

    public void setMagY(double magY) {
        this.magY = magY;
    }

    public double getMagZ() {
        return magZ;
    }

    public void setMagZ(double magZ) {
        this.magZ = magZ;
    }

    public double getGyroX() {
        return gyroX;
    }

    public void setGyroX(double gyroX) {
        this.gyroX = gyroX;
    }

    public double getGyroY() {
        return gyroY;
    }

    public void setGyroY(double gyroY) {
        this.gyroY = gyroY;
    }

    public double getGyroZ() {
        return gyroZ;
    }

    public void setGyroZ(double gyroZ) {
        this.gyroZ = gyroZ;
    }

    public double getOrientX() {
        return orientX;
    }

    public void setOrientX(double orientX) {
        this.orientX = orientX;
    }

    public double getOrientY() {
        return orientY;
    }

    public void setOrientY(double orientY) {
        this.orientY = orientY;
    }

    public double getOrientZ() {
        return orientZ;
    }

    public void setOrientZ(double orientZ) {
        this.orientZ = orientZ;
    }

    public boolean isPositive() {
        return positive;
    }

    public void setPositive(boolean positive) {
        this.positive = positive;
    }

}


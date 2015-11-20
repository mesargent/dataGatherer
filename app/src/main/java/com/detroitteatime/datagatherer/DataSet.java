package com.detroitteatime.datagatherer;


import java.io.Serializable;
import java.util.Date;

public class DataSet implements Serializable, Cloneable {
    private double lat;
    private double lng;
    private double speedGPS;
    private double speedACC;
    private Date time;
    private double accelX;
    private double accelY;
    private double accelZ;
    private double d_accelX;
    private double d_accelY;
    private double d_accelZ;
    private double linX;
    private double linY;
    private double linZ;
    private double gravX;
    private double gravY;
    private double gravZ;
    private double magX;
    private double magY;
    private double magZ;
    private double d_magX;
    private double d_magY;
    private double d_magZ;
    private double gyroX;
    private double gyroY;
    private double gyroZ;
    private double d_gyroX;
    private double d_gyroY;
    private double d_gyroZ;
    private double orientX;
    private double orientY;
    private double orientZ;
    private boolean positive;

    @Override
    public String toString() {
        return "  lat; " + getLat() +
                "     lng; " + getLng() +
                "     speedGPS; " + getSpeedGPS() +
                "     speedACC; " + getSpeedACC() +
                "     time; " + getTime() +
                "     accelX; " + getAccelX() +
                "     accelY; " + getAccelY() +
                "     accelZ; " + getAccelZ() +
                "     d_accelX; " + getD_accelX() +
                "     d_accelY; " + getD_accelY() +
                "     d_accelZ; " + getD_accelZ() +
                "     linX; " + getLinX() +
                "     linY; " + getLinY() +
                "     linZ; " + getLinZ() +
                "     gravX; " + getGravX() +
                "     gravY; " + getGravY() +
                "     gravZ; " + getGravZ() +
                "     magX; " + getMagX() +
                "     magY; " + getMagY() +
                "     magZ; " + getMagZ() +
                "     d_magX; " + getD_magX() +
                "     d_magY; " + getD_magY() +
                "     d_magZ; " + getD_magZ() +
                "     gyroX; " + getGyroX() +
                "     gyroY; " + getGyroY() +
                "     gyroZ; " + getGyroZ() +
                "     d_gyroX; " + getD_gyroX() +
                "     d_gyroY; " + getD_gyroY() +
                "     d_gyroZ; " + getD_gyroZ() +
                "     orientX; " + getOrientX() +
                "     orientY; " + getOrientY() +
                "     orientZ; " + getOrientZ() +
                "    positive; " + isPositive();
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public double getD_accelX() {
        return d_accelX;
    }

    public void setD_accelX(double d_accelX) {
        this.d_accelX = d_accelX;
    }

    public double getD_accelY() {
        return d_accelY;
    }

    public void setD_accelY(double d_accelY) {
        this.d_accelY = d_accelY;
    }

    public double getD_accelZ() {
        return d_accelZ;
    }

    public void setD_accelZ(double d_accelZ) {
        this.d_accelZ = d_accelZ;
    }

    public double getD_magX() {
        return d_magX;
    }

    public void setD_magX(double d_magX) {
        this.d_magX = d_magX;
    }

    public double getD_magY() {
        return d_magY;
    }

    public void setD_magY(double d_magY) {
        this.d_magY = d_magY;
    }

    public double getD_magZ() {
        return d_magZ;
    }

    public void setD_magZ(double d_magZ) {
        this.d_magZ = d_magZ;
    }

    public double getD_gyroX() {
        return d_gyroX;
    }

    public void setD_gyroX(double d_gyroX) {
        this.d_gyroX = d_gyroX;
    }

    public double getD_gyroY() {
        return d_gyroY;
    }

    public void setD_gyroY(double d_gyroY) {
        this.d_gyroY = d_gyroY;
    }

    public double getD_gyroZ() {
        return d_gyroZ;
    }

    public void setD_gyroZ(double d_gyroZ) {
        this.d_gyroZ = d_gyroZ;
    }


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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
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


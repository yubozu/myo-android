package cn.ac.ict.myo.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.UUID;

import cn.ac.ict.myo.MyoApp;

/**
 * Author: saukymo
 * Date: 12/14/16
 */

public class PatientModel {
    public String name;
    public String age;
    public String id;
    public String info;
    public String gender;
    public Integer deviceId;
    public Boolean viewed;
    public Boolean status;
    public String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getId() {
        return id;
    }

    public String getInfo() {
        return info;
    }

    public String getName() {
        return name;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getViewed() {
        return viewed;
    }

    public void setViewed(Boolean viewed) {
        this.viewed = viewed;
    }

    public PatientModel(Integer deviceId) {
        this.deviceId = deviceId;
        this.name = "";
        this.age = "";
        this.info = "";
        this.id = "";
        this.status = false;
        this.viewed = false;
        this.uuid = UUID.randomUUID().toString();
    }

    public static PatientModel getPatientModel(Integer deviceId) {
        SharedPreferences prefs = MyoApp.getAppContext().getSharedPreferences(
                "user", Context.MODE_PRIVATE);
        String deviceJson = prefs.getString(String.valueOf(deviceId), null);
        if (deviceJson != null) {
            Gson gson = new Gson();
            try {
                return gson.fromJson(deviceJson, PatientModel.class);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                return new PatientModel(deviceId);
            }
        } else
            return new PatientModel(deviceId);
    }

    public Integer getDeviceId() {
        return this.deviceId;
    }

    public void saveToPref() {
        SharedPreferences prefs = MyoApp.getAppContext().getSharedPreferences(
                "user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(String.valueOf(this.deviceId), toJson());
        editor.apply();
    }

    private static PatientModel generateModel(Integer deviceId) {
        PatientModel deviceModel = new PatientModel(deviceId);
        deviceModel.setAge("40");
        deviceModel.setGender("男");
        deviceModel.setName("TestUser");
        deviceModel.setId("45356");
        deviceModel.setInfo("zzz");
        return deviceModel;
    }

    public String toJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}

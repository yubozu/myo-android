package cn.ac.ict.myo;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

/**
 * Author: saukymo
 * Date: 12/14/16
 */

public class DeviceModel{
    public String name;
    public String age;
    public String id;
    public String info;
    public String gender;
    public Integer deviceId;
    public Boolean status;

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

    public DeviceModel(Integer deviceId) {
        this.deviceId = deviceId;
        this.name = "";
        this.age = "";
        this.info = "";
        this.id = "";
        this.status = false;
    }

    public static DeviceModel getDeviceModel(Integer deviceId) {
        SharedPreferences prefs = MyoApp.getAppContext().getSharedPreferences(
                "user", Context.MODE_PRIVATE);
        String deviceJson = prefs.getString(String.valueOf(deviceId), null);
        if (deviceJson != null) {
            Gson gson = new Gson();
            return gson.fromJson(deviceJson, DeviceModel.class);
        } else
            return new DeviceModel(deviceId);
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

    public String toJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}

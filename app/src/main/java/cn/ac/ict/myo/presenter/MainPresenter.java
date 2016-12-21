package cn.ac.ict.myo.presenter;

import android.content.res.AssetManager;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Properties;

import cn.ac.ict.myo.model.DeviceModel;
import cn.ac.ict.myo.activity.MainActivity;

/**
 * Author: saukymo
 * Date: 12/21/16
 */

public class MainPresenter {
    private static final String TAG = "MainPresenter";
    private MainActivity mMainView;
    public MainPresenter(MainActivity mainView) {
        mMainView = mainView;
        loadProperties();
    }
    private Socket mSocket;
    private String appId;
    private String host;

    private void loadProperties() {
        AssetManager assetManager = mMainView.getResources().getAssets();
        Properties properties = new Properties();
        try {
            InputStream inputStream = assetManager.open("app.properties");
            properties.load(inputStream);
            host = properties.getProperty("host");
            appId = properties.getProperty("appId");
        } catch (IOException e) {
            Log.e(TAG, "Failed to open property file");
            e.printStackTrace();
        }
    }

    public void connect() {
        try {
            mSocket = IO.socket(host);
            mSocket.connect();
            mSocket.emit("login", appId);
            mSocket.on("alert", onAlert);
            mSocket.on("device_list", onDeviceUpdate);
            Log.d(TAG, "Success to initial socket: " + host);
        } catch (URISyntaxException e) {
            Log.e(TAG, "Failed to connect");
            e.printStackTrace();
        }
    }

    public void disconnect() {
        if (mSocket.connected()) {
            mSocket.emit("logout", appId);
            mSocket.off("alert");
            mSocket.off("device_list");
            mSocket.disconnect();
        }
    }
    private Emitter.Listener onAlert = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            mMainView.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject alertInfo = (JSONObject) args[0];
                    Integer deviceId;
                    Boolean status;
                    try{
                        deviceId = alertInfo.getInt("device_id");
                        status = alertInfo.getBoolean("status");
                    } catch (JSONException e) {
                        return;
                    }
                    mMainView.onAlert(deviceId, status);
                }
            });
        }
    };

    private Emitter.Listener onDeviceUpdate = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            mMainView.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<DeviceModel> deviceList = new ArrayList<>();
                    JSONObject data = (JSONObject) args[0];
                    try {
                        JSONArray devices = data.getJSONArray("devices");
                        for(int i=0; i<devices.length();i++){
                            deviceList.add(DeviceModel.getDeviceModel(devices.getInt(i)));
                        }
                        Log.d(TAG, "Device list: " + deviceList);
                    } catch (JSONException e) {
                        Log.d(TAG, "Failed to fetch device list");
                        e.printStackTrace();
                    }
                    mMainView.onDeviceUpdate(deviceList);
                }
            });
        }
    };
}

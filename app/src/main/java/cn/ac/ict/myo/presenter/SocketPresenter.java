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

import cn.ac.ict.myo.activity.BaseActivity;
import cn.ac.ict.myo.model.HistoryModel;
import cn.ac.ict.myo.model.PatientModel;

/**
 * Author: saukymo
 * Date: 12/21/16
 */

public class SocketPresenter {
    private static final String TAG = "SocketPresenter";
    private BaseActivity baseActivity;
    public SocketPresenter(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
        loadProperties();
    }
    private Socket mSocket;
    private String appId;
    private String host;

    private void loadProperties() {
        AssetManager assetManager = baseActivity.getResources().getAssets();
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

    public void socketInit() {
        try {
            mSocket = IO.socket(host);
            Log.d(TAG, "Success to initial socket: " + host);
        } catch (URISyntaxException e) {
            Log.e(TAG, "Failed to connect");
            e.printStackTrace();
        }
    }

    public void connect() {
        mSocket.connect();
        mSocket.emit("login", appId);
        mSocket.on("alert", onAlert);
        mSocket.on("device_list", onDeviceUpdate);
        mSocket.on("emg", onEmg);
    }

    public void disconnect() {
        mSocket.emit("logout", appId);
        mSocket.off("alert");
        mSocket.off("device_list");
        mSocket.off("emg");
        mSocket.disconnect();
    }

    public void subscribe(Integer deviceId) {
        mSocket.emit("subscribe", deviceId);
    }

    public void unsubscribe(Integer deviceId) {
        mSocket.emit("unsubscribe", deviceId);
    }

    public Boolean getSocketStatus() {
        return mSocket.connected();
    }

    private Emitter.Listener onAlert = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            baseActivity.runOnUiThread(new Runnable() {
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
                    baseActivity.onAlert(deviceId, status);
//                    Log.d(TAG, "Socket connected " + mSocket.connected());

                    saveAlert(deviceId, status);
                }
            });
        }
    };

    private void saveAlert(Integer deviceId, Boolean status) {
        PatientModel patientModel = PatientModel.getPatientModel(deviceId);
        HistoryModel historyModel = HistoryModel.getHistoryModel(patientModel.getUuid());
        if (status) {
            historyModel.start();
        } else {
            historyModel.stop();
        }
        historyModel.save();
        Log.d(TAG, historyModel.toJson());
    }
    private Emitter.Listener onDeviceUpdate = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            baseActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<PatientModel> deviceList = new ArrayList<>();
                    JSONObject data = (JSONObject) args[0];
                    try {
                        JSONArray devices = data.getJSONArray("devices");
                        for(int i=0; i<devices.length();i++){
                            deviceList.add(PatientModel.getPatientModel(devices.getInt(i)));
                        }
                        Log.d(TAG, "Device list: " + deviceList);
                    } catch (JSONException e) {
                        Log.d(TAG, "Failed to fetch device list.");
                        e.printStackTrace();
                    }
                    baseActivity.onDeviceUpdate(deviceList);
                }
            });
        }
    };

    private Emitter.Listener onEmg = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            baseActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject receiveData = (JSONObject) args[0];

                    try {
                        Double prob = receiveData.getDouble("proba");
                        ArrayList<Integer> emg = new ArrayList<>();
                        JSONArray data = receiveData.getJSONArray("emg");
                        for (int i = 0; i < data.length(); i++) {
                            emg.add(data.getInt(i));
                        }
                        baseActivity.onEmg(prob, emg);
//                        Log.d(TAG, "Probability " + prob);
                    } catch (JSONException e) {
                        Log.d(TAG, "Failed to fetch emg data.");
                        e.printStackTrace();
                    }
                }
            });
        }
    };
}

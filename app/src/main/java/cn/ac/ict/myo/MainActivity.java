package cn.ac.ict.myo;

import android.app.Activity;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;

public class MainActivity extends Activity {
    private String TAG = "MainActivity";
    private OkHttpClient client;

    @BindView(R.id.bt_socket)
    public Button button;
    @BindView(R.id.et_url)
    public EditText et_url;
    @BindView(R.id.tv_emg)
    public TextView tv_emg;
    @BindView(R.id.rc_view)
    public RecyclerView mRecyclerView;

    private Uri notification;
    private Ringtone r;
    private Boolean socket;
    private ArrayList<String> emgData = new ArrayList<>();
    private Socket mSocket;
    private String app_id = "test01";
    private ArrayList<DeviceModel> deviceList = new ArrayList<>();
    private Integer subscribeDeviceId = 0;
    private DeviceAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        init();
    }

    @OnClick({R.id.bt_socket})
    protected void onClick(View view) {
        Log.d(TAG, "Click button " + view.toString());
//        if (socket) {
//            mSocket.emit("unsubscribe", subscribeDeviceId);
//            mSocket.disconnect();
//            mSocket.off("emg", onEmg);
//            button.setText(getString(R.string.start));
//            socket = false;
//        } else {
            try {
                mSocket = IO.socket(et_url.getText().toString());
                mSocket.connect();
                mSocket.emit("login", app_id);
                mSocket.on("login", onLogin);
                mSocket.on("alert", onAlert);
                Log.d(TAG, "Socket initialize success. " + et_url.getText().toString());
                socket = true;
//                button.setText(getString(R.string.stop));
            } catch (URISyntaxException e) {
                Log.d(TAG, "Socket initialize failed.");
            }
//        }
    }

    private Emitter.Listener onAlert = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
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
                    Log.d(TAG, "Alert " + deviceId + " status: " + status);
                    if (status) {
                        if (! r.isPlaying()) r.play();
                    } else {
                        if (r.isPlaying()) r.stop();
                    }
                    mAdapter.setAlert(deviceId, status);
                }
            });
        }
    };

//    private void setText() {
//        StringBuilder sb = new StringBuilder();
//        for (String s: emgData) {
//            sb.append(s);
//            sb.append("\n");
//        }
//        tv_emg.setText(sb);
//    }

    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        deviceList.clear();
                        JSONArray devices = data.getJSONArray("devices");
                        for(int i=0; i<devices.length();i++){
                            deviceList.add(DeviceModel.getDeviceModel(devices.getInt(i)));
                        }
                        if (devices.length() > 0) {
                            subscribeDeviceId = deviceList.get(0).getDeviceId();
                        }

                    } catch (JSONException e) {
                        return;
                    }
//                    tv_status.setText(emg);
                    mAdapter = new DeviceAdapter(deviceList);
                    mRecyclerView.swapAdapter(mAdapter, true);
//                    mSocket.emit("subscribe", subscribeDeviceId);
//                    tv_emg.setText(deviceList.toString());
                }
            });
        }
    };

    protected void init() {
        socket = false;
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new DeviceAdapter(new ArrayList<DeviceModel>());
        mRecyclerView.setAdapter(mAdapter);
        autoConnect();
        notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        r = RingtoneManager.getRingtone(getApplicationContext(), notification);
    }

    private void autoConnect() {
        this.et_url.setVisibility(View.GONE);
        this.tv_emg.setVisibility(View.GONE);
    }

    @Override
    public void onResume(){
        super.onResume();
        this.onClick(findViewById(R.id.bt_socket));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (socket) {
            mSocket.emit("logout", app_id);
            mSocket.off("alert");
            mSocket.off("login");
            mSocket.disconnect();
        }
        if (r.isPlaying()) {
            r.stop();
        }
    }

}

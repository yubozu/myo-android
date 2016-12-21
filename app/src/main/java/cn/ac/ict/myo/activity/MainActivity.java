package cn.ac.ict.myo.activity;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;

import java.util.ArrayList;

import butterknife.BindView;
import cn.ac.ict.myo.DeviceAdapter;
import cn.ac.ict.myo.model.DeviceModel;
import cn.ac.ict.myo.R;
import cn.ac.ict.myo.presenter.MainPresenter;
import okhttp3.OkHttpClient;

public class MainActivity extends BaseActivity {
    public final static String TAG = "MainActivity";
    private OkHttpClient client;

    @BindView(R.id.tv_emg)
    public TextView tv_emg;
    @BindView(R.id.rc_view)
    public RecyclerView mRecyclerView;

    private Uri notification;
    private Ringtone r;
    private ArrayList<String> emgData = new ArrayList<>();
    private ArrayList<DeviceModel> deviceList = new ArrayList<>();
    private Integer subscribeDeviceId = 0;
    private DeviceAdapter mAdapter;
    private MainPresenter mainPresenter;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_main;
    }

    public void onAlert(Integer deviceId, Boolean status) {
        Log.d(TAG, "Alert " + deviceId + " status: " + status);
        if (status) {
            if (! r.isPlaying()) r.play();
        } else {
            if (r.isPlaying()) r.stop();
        }
        mAdapter.setAlert(deviceId, status);
    }

    public void onDeviceUpdate(ArrayList<DeviceModel> deviceList ) {
        mAdapter = new DeviceAdapter(deviceList);
        mRecyclerView.swapAdapter(mAdapter, true);
    }

    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

//                    tv_status.setText(emg);

//                    mSocket.emit("subscribe", subscribeDeviceId);
//                    tv_emg.setText(deviceList.toString());
                }
            });
        }
    };

    @Override
    protected void init() {
        mainPresenter = new MainPresenter(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new DeviceAdapter(new ArrayList<DeviceModel>());
        mRecyclerView.setAdapter(mAdapter);
        notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        r = RingtoneManager.getRingtone(getApplicationContext(), notification);
    }

    @Override
    public void onResume(){
        super.onResume();
//        this.onClick(findViewById(R.id.bt_socket));
        mainPresenter.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
//        if (socket) {
//            mSocket.emit("logout", app_id);
//            mSocket.off("alert");
//            mSocket.off("login");
//            mSocket.disconnect();
//        }
        mainPresenter.disconnect();
        if (r.isPlaying()) {
            r.stop();
        }
    }

}

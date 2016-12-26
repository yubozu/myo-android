package cn.ac.ict.myo.activity;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import cn.ac.ict.myo.DeviceAdapter;
import cn.ac.ict.myo.R;
import cn.ac.ict.myo.model.DeviceModel;
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

    @Override
    protected void init() {
        mainPresenter = new MainPresenter(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new DeviceAdapter(new ArrayList<DeviceModel>());
        mRecyclerView.setAdapter(mAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                LinearLayout.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        r = RingtoneManager.getRingtone(getApplicationContext(), notification);
    }

    @Override
    public void onResume(){
        super.onResume();
        mainPresenter.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        mainPresenter.disconnect();
        if (r.isPlaying()) {
            r.stop();
        }
    }

}

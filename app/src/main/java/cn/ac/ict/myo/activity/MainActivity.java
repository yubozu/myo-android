package cn.ac.ict.myo.activity;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import butterknife.BindView;
import cn.ac.ict.myo.DeviceAdapter;
import cn.ac.ict.myo.R;
import cn.ac.ict.myo.model.PatientModel;

public class MainActivity extends BaseActivity {
    public final static String TAG = "MainActivity";

    @BindView(R.id.rc_view)
    public RecyclerView mRecyclerView;
    private DeviceAdapter mAdapter;


    @Override
    public int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    public void onAlert(Integer deviceId, Boolean status) {
        super.onAlert(deviceId, status);
        mAdapter.setAlert(deviceId, status);
    }

    public void onDeviceUpdate(ArrayList<PatientModel> deviceList ) {
        mAdapter = new DeviceAdapter(deviceList);
        mRecyclerView.swapAdapter(mAdapter, true);
    }

    @Override
    protected void init() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new DeviceAdapter(new ArrayList<PatientModel>());
        mRecyclerView.setAdapter(mAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                LinearLayout.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
    }

}

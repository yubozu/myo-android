package cn.ac.ict.myo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
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
import cn.ac.ict.myo.model.DeviceModel;

/**
 * Author: saukymo
 * Date: 12/14/16
 */

public class PatientDetail extends Activity {
    private String TAG = "PatientDetail";
    private String appId = "test01";
    private Socket mSocket;
    private Integer deviceId;
    private String URL;
    private ArrayList<String> emgData = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> emg = new ArrayList<>();
//    private ArrayList<LineDataSet> dataSets = new ArrayList<>();
    private ArrayList<LineChart> mCharts= new ArrayList<>();
    private Integer index = 0;
    private Integer begin = 0;

    @BindView(R.id.et_name)
    public EditText name;
    @BindView(R.id.et_age)
    public EditText age;
    @BindView(R.id.et_gender)
    public EditText gender;
    @BindView(R.id.et_id)
    public EditText id;
    @BindView(R.id.et_info)
    public EditText info;
    @BindView(R.id.tv_emg)
    public TextView tvEmg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        ArrayList<Integer> ids = new ArrayList<Integer>() {{
            add(R.id.chart1);
            add(R.id.chart2);
            add(R.id.chart3);
            add(R.id.chart4);
            add(R.id.chart5);
            add(R.id.chart6);
            add(R.id.chart7);
            add(R.id.chart8);
        }};


        for (int i = 0; i < 8; i++) {
            LineChart Chart = (LineChart) findViewById(ids.get(i));

            Chart.setData(new LineData());
            XAxis xl = Chart.getXAxis();
            xl.setEnabled(false);

            YAxis leftAxis = Chart.getAxisLeft();
            leftAxis.setAxisMaximum(1200);
            leftAxis.setAxisMinimum(0);
            leftAxis.setEnabled(false);

            YAxis rightAxis = Chart.getAxisRight();
            rightAxis.setEnabled(false);

            Chart.setDescription(null);
            mCharts.add(Chart);
        }

        Bundle bundle = getIntent().getExtras();
        deviceId = bundle.getInt("device_id");

        DeviceModel device = DeviceModel.getDeviceModel(deviceId);
        name.setText(device.name);
        age.setText(device.age);
        gender.setText(device.gender);
        id.setText(device.id);
        info.setText(device.info);
        name.setEnabled(false);
        age.setEnabled(false);
        gender.setEnabled(false);
        id.setEnabled(false);
        info.setEnabled(false);
        init();
    }

    private void init() {
//        URL = getString(R.string.default_url)
        URL = "http://10.27.0.141:5000";
        try {
            mSocket = IO.socket(URL);
            mSocket.connect();
            mSocket.emit("login", appId);
            mSocket.emit("subscribe", deviceId);
            Log.d(TAG, "Subscribed device: " + deviceId);
            mSocket.on("emg", onEmg);
            Log.d(TAG, "Socket initialize success. " + URL);
//                button.setText(getString(R.string.stop));
        } catch (URISyntaxException e) {
            Log.d(TAG, "Socket initialize failed.");
        }
    }

    private Emitter.Listener onEmg = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject receiveData = (JSONObject) args[0];

                    try {
                        Double prob = receiveData.getDouble("proba");
                        tvEmg.setText("probability: " + prob);
                        JSONArray data = receiveData.getJSONArray("emg");
                        for (int i=0; i<data.length();i++) {
//                            emg.get(i).add(data.getInt(i));
//                            if (emg.get(i).size() > 10)
//                                emg.get(i).remove(0);
                            data.getInt(i);
                            LineData dataSet = mCharts.get(i).getData();
                            if (dataSet == null)
                                return;
                            ILineDataSet set = dataSet.getDataSetByIndex(0);
                            if (set == null) {
                                set = createSet(i);
                                dataSet.addDataSet(set);
                            }
                            dataSet.addEntry(new Entry(index, data.getInt(i)), 0);
                            if (index > 25) {
                                set.removeFirst();
                            }
                            dataSet.notifyDataChanged();
                            mCharts.get(i).notifyDataSetChanged();
                            mCharts.get(i).setVisibleXRangeMaximum(20);
                            mCharts.get(i).moveViewToX(index);
                        }
                        index += 1;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            });
        }
    };

    private LineDataSet createSet(Integer i) {
        LineDataSet set = new LineDataSet(null, "EMG" + (i + 1));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(ColorTemplate.getHoloBlue());
        set.setLineWidth(2f);
        set.setCircleRadius(1f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setDrawValues(false);
        return set;
    }

//    private void setText() {
//        StringBuilder sb = new StringBuilder();
//        for (String s: emgData) {
//            sb.append(s);
//            sb.append("\n");
//        }
//        tv_emg.setText(sb);
//    }

    public void onBackPressed() {
        mSocket.off("emg", onEmg);
        mSocket.emit("unsubscribe", deviceId);
        mSocket.emit("logout", appId);
        mSocket.disconnect();
        for (int i = 0; i < 8; i++) {
            mCharts.get(i).clear();
        }
        super.onBackPressed();
        finish();
    }
}

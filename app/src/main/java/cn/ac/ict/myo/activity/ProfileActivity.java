package cn.ac.ict.myo.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

import butterknife.BindView;
import cn.ac.ict.myo.R;
import cn.ac.ict.myo.model.PatientModel;

/**
 * Author: saukymo
 * Date: 12/26/16
 */

public class ProfileActivity extends BaseActivity {
    public final static String TAG = "ProfileActivity";
    private Integer deviceId;

    @BindView(R.id.tv_name)
    public TextView name;
    @BindView(R.id.tv_age)
    public TextView age;
    @BindView(R.id.tv_id)
    public TextView id;
    @BindView(R.id.civ_gender)
    public ImageView gender;
    @BindView(R.id.emg_chart)
    public LineChart Chart;
    @BindView(R.id.tv_prob)
    public TextView Prob;
    private Integer index = 0;
    public static final int[] CHART_COLORS = {
            rgb("#fff7fb"), rgb("#ece7f2"), rgb("#d0d1e6"), rgb("#a6bddb"),
            rgb("#74a9cf"), rgb("#3690c0"), rgb("#0570b0"), rgb("#034e7b")
    };

    public static int rgb(String hex) {
        int color = (int) Long.parseLong(hex.replace("#", ""), 16);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;
        return Color.rgb(r, g, b);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_profile;
    }

    @Override
    protected void init() {
        Bundle bundle = getIntent().getExtras();
        deviceId = bundle.getInt("device_id");

        PatientModel device = PatientModel.getPatientModel(deviceId);
        name.setText(device.getName());
        age.setText(device.getAge());
        id.setText(device.getId());
        if (device.getGender() != null && device.getGender().equals("男")) {
            gender.setImageResource(R.drawable.male);
        } else {
            gender.setImageResource(R.drawable.female);
        }

        subscribe(deviceId);

        for (int i = 0; i < 8; i++) {
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
        }
    }

    @Override
    public void onEmg(Double probability, ArrayList<Integer> emg) {
        Prob.setText(String.valueOf(probability));
        LineData dataSet = Chart.getData();
        if (dataSet == null)
            return;
        for(int i = 0; i < emg.size(); i++) {
            ILineDataSet set = dataSet.getDataSetByIndex(i);
            if (set == null) {
                set = createSet(i);
                dataSet.addDataSet(set);
            }
            set.addEntry(new Entry(index, emg.get(i)));
            if (index > 25) {
                set.removeFirst();
            }
        }
        dataSet.notifyDataChanged();
        Chart.notifyDataSetChanged();
        Chart.setVisibleXRangeMaximum(20);
        Chart.moveViewToX(index);
        index += 1;
    }

    private LineDataSet createSet(Integer i) {
        LineDataSet set = new LineDataSet(null, "EMG" + (i + 1));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(CHART_COLORS[i]);
        set.setDrawCircles(false);
        set.setLineWidth(1.5f);
        set.setDrawValues(false);
        return set;
    }

    @Override
    public void onResume() {
        super.onResume();
        subscribe(deviceId);
    }

    @Override
    public void onPause() {
        unsubscribe(deviceId);
        super.onPause();
    }

    @Override
    public void onStop(){
//        Chart.clear();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(MainActivity.class);
    }
}

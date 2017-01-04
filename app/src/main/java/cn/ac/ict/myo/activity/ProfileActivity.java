package cn.ac.ict.myo.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import butterknife.OnClick;
import cn.ac.ict.myo.MyoApp;
import cn.ac.ict.myo.R;
import cn.ac.ict.myo.model.HistoryModel;
import cn.ac.ict.myo.model.PatientModel;

/**
 * Author: saukymo
 * Date: 12/26/16
 */

public class ProfileActivity extends BaseActivity {
    public final static String TAG = "ProfileActivity";
    private Integer deviceId;
    private PatientModel patient;

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
    @BindView(R.id.delete)
    public Button delete;
    @BindView(R.id.edit)
    public Button edit;
    @BindView(R.id.history)
    public TextView history;
    @BindView(R.id.info)
    public TextView info;

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

    @OnClick({R.id.edit, R.id.delete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.delete:
                break;
            case R.id.edit:
                Intent intent = new Intent(MyoApp.getAppContext(), EditActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("device_id", deviceId); //Your id
                intent.putExtras(bundle); //Put your id to your next Intent
                MyoApp.getAppContext().startActivity(intent);
                break;
        }
    }

    @Override
    protected void init() {
        Bundle bundle = getIntent().getExtras();
        deviceId = bundle.getInt("device_id");
        patient = PatientModel.getPatientModel(deviceId);
        name.setText(patient.getName());
        age.setText(patient.getAge());
        id.setText(patient.getId());
        info.setText(patient.getInfo());
        if (patient.getGender() != null && patient.getGender().equals("男")) {
            gender.setImageResource(R.drawable.male);
        } else {
            gender.setImageResource(R.drawable.female);
        }

        loadAlertHistory();
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

    private void loadAlertHistory() {
        HistoryModel historyModel = HistoryModel.getHistoryModel(patient.getUuid());
        history.setText(historyModel.toString());
    }

    @Override
    public void onAlert(Integer deviceId, Boolean status) {
        super.onAlert(deviceId, status);
        if (deviceId.equals(this.deviceId)) {
            loadAlertHistory();
        }
    }

    private String getDegree(Double probability) {
        if (probability > 0.9) return "High";
        else if (probability > 0.6) return "Medium";
        else return "Low";
    }

    @Override
    public void onEmg(Double probability, ArrayList<Integer> emg) {
        Prob.setText("实时概率: " + String.valueOf(probability) + "\t\t\t\t\t\t发作指数: " + getDegree(probability));
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

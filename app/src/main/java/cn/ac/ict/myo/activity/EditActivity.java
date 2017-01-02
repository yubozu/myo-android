package cn.ac.ict.myo.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.OnClick;
import cn.ac.ict.myo.R;
import cn.ac.ict.myo.model.PatientModel;
import cn.ac.ict.myo.presenter.ProfilePresenter;

/**
 * Author: saukymo
 * Date: 12/28/16
 */

public class EditActivity extends BaseActivity{
    public final static String TAG = "EditActivity";
    private ProfilePresenter profilePresenter;
    private Integer deviceId;

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

    @Override
    public int getLayoutRes() {
        return R.layout.activity_edit;
    }

    @OnClick({R.id.save, R.id.cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save:
                PatientModel patientModel = PatientModel.getPatientModel(deviceId);
                patientModel.setName(name.getText().toString());
                patientModel.setAge(age.getText().toString());
                patientModel.setGender(gender.getText().toString());
                patientModel.setId(gender.getText().toString());
                patientModel.setInfo(gender.getText().toString());
                patientModel.saveToPref();
                break;
            case R.id.cancel:
                break;
        }
    }

    @Override
    protected void init() {
        Bundle bundle = getIntent().getExtras();
        deviceId = bundle.getInt("device_id");

        profilePresenter = new ProfilePresenter(this);
    }
}

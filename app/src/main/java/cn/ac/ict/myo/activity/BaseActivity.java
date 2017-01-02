package cn.ac.ict.myo.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.ButterKnife;
import cn.ac.ict.myo.model.PatientModel;
import cn.ac.ict.myo.presenter.SocketPresenter;


/**
 * Author: saukymo
 * Date: 12/21/16
 */

public abstract class BaseActivity extends Activity {
    public final static String TAG = "BaseActivity";
    private Handler mHandler = new Handler();
    private ProgressDialog mProgressDialog;
    private InputMethodManager mInputMethodManager;
    private Ringtone r;
    private SocketPresenter socketPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutRes());
        ButterKnife.bind(this);

        socketPresenter = new SocketPresenter(this);
        socketPresenter.socketInit();
        Uri notification;
        notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        r = RingtoneManager.getRingtone(getApplicationContext(), notification);

        init();
    }

    protected void init() {}

    public void onDeviceUpdate(ArrayList<PatientModel> deviceList) {

    };

    public void onEmg(Double probability, ArrayList<Integer> emg) {
        Log.d(TAG, "Probability" + probability);
    }

    public abstract int getLayoutRes();

    protected void startActivity(Class activity) {
        startActivity(activity, true);
    }

    protected void startActivity(Class activity, boolean finish) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
        if (finish) {
            finish();
        }
    }

    public void onAlert(Integer deviceId, Boolean status) {
        Log.d(TAG, "Alert " + deviceId + " status: " + status);
        if (status) {
            if (! r.isPlaying()) r.play();
        } else {
            if (r.isPlaying()) r.stop();
        }
    }

    protected void post(Runnable runnable) {
        postDelay(runnable, 0);
    }

    protected void postDelay(Runnable runnable, long millis) {
        mHandler.postDelayed(runnable, millis);
    }

    protected void subscribe(Integer deviceId) {
        Log.d(TAG, "Subscribed " + deviceId);
        socketPresenter.subscribe(deviceId);

    }

    protected void unsubscribe(Integer deviceId) {
        socketPresenter.unsubscribe(deviceId);
    }

    protected void showProgress(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(true);
        }
        mProgressDialog.setMessage(msg);
        mProgressDialog.show();
    }

    protected void hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    protected void hideKeyBoard() {
        if (mInputMethodManager == null) {
            mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        if (getCurrentFocus() != null)
            mInputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onResume(){
        super.onResume();
        socketPresenter.connect();
    }

    @Override
    public void onPause() {
        socketPresenter.disconnect();
        if (r.isPlaying()) {
            r.stop();
        }
        super.onPause();
    }
}
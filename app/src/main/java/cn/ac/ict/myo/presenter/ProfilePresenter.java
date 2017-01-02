package cn.ac.ict.myo.presenter;

import cn.ac.ict.myo.activity.EditActivity;

/**
 * Author: saukymo
 * Date: 12/28/16
 */

public class ProfilePresenter {
    private static final String TAG = "ProfilePresenter";
    private EditActivity editActivity;
    public ProfilePresenter(EditActivity editActivity) {
        this.editActivity = editActivity;
    }
}

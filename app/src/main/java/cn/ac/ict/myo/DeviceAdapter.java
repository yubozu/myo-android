package cn.ac.ict.myo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ac.ict.myo.model.DeviceModel;

/**
 * Author: saukymo
 * Date: 12/14/16
 */

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder>{
    private ArrayList<DeviceModel> mDataset;
    private View.OnClickListener mListener;

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rl)
        public RelativeLayout rl;
        @BindView(R.id.civ_gender)
        public ImageView gender;
        @BindView(R.id.tv_name)
        public TextView name;
        @BindView(R.id.tv_room_id)
        public TextView room;
        @BindView(R.id.tv_last_outbreak)
        public TextView lastOutbreak;
        @BindView(R.id.bt_view)
        public ImageView view;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public DeviceAdapter(ArrayList<DeviceModel> myDataset) {
        mDataset = myDataset;
        sortDataset();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DeviceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_patient, parent, false);
//        v.setOnClickListener(mOnClickListener);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final DeviceModel device = mDataset.get(position);
        holder.name.setText(device.name);
        holder.rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyoApp.getAppContext(), PatientDetail.class);
                Bundle bundle = new Bundle();
                bundle.putInt("device_id", device.getDeviceId()); //Your id
                intent.putExtras(bundle); //Put your id to your next Intent
                MyoApp.getAppContext().startActivity(intent);
            }
        });

        if (device.getGender() != null && device.getGender().equals("男")) {
            holder.gender.setImageResource(R.drawable.male);
        } else {
            holder.gender.setImageResource(R.drawable.female);
        }

        holder.room.setText("病房301-" + device.getDeviceId());
        holder.lastOutbreak.setText("上次发作时间：30分钟前");
//        holder.view.setOnClickListener();

    }
    private void sortDataset() {
        Collections.sort(mDataset, new Comparator<DeviceModel>() {
            @Override
            public int compare(DeviceModel device1, DeviceModel device2)
            {

                return  device1.deviceId.compareTo(device2.deviceId);
            }
        });
    }
    public void setAlert(Integer deviceId, Boolean status) {
        sortDataset();
        for (DeviceModel device: mDataset) {
            if (device.deviceId.equals(deviceId)) {
                device.setStatus(status);
            }
            if (device.getStatus() != null && device.getStatus()) {
                mDataset.remove(device);
                mDataset.add(0, device);
            }
        }
        Log.d("Adapter", mDataset.toString());
        notifyDataSetChanged();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}

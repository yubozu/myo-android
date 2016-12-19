package cn.ac.ict.myo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;

/**
 * Author: saukymo
 * Date: 12/14/16
 */

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder>{
    private ArrayList<DeviceModel> mDataset;
//    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
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
        @BindView(R.id.edit)
        public Button edit;
        @BindView(R.id.save)
        public Button save;
        @BindView(R.id.cancel)
        public Button cancel;
        @BindView(R.id.detail)
        public Button detail;
        public CardView cardView;

        public ViewHolder(View v) {
            super(v);
            name = (EditText) v.findViewById(R.id.et_name);
            age = (EditText) v.findViewById(R.id.et_age);
            gender = (EditText) v.findViewById(R.id.et_gender);
            id = (EditText) v.findViewById(R.id.et_id);
            info = (EditText) v.findViewById(R.id.et_info);
            save = (Button) v.findViewById(R.id.save);
            edit = (Button) v.findViewById(R.id.edit);
            cancel = (Button) v.findViewById(R.id.cancel);
            detail = (Button) v.findViewById(R.id.detail);
            cardView = (CardView) v.findViewById(R.id.card_view);

           info.setOnKeyListener(new View.OnKeyListener() {

                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {

                    // if enter is pressed start calculating
                    if (keyCode == KeyEvent.KEYCODE_ENTER
                            && event.getAction() == KeyEvent.ACTION_UP) {

                        // get EditText text
                        String text = ((EditText) v).getText().toString();

                        // find how many rows it cointains
                        int editTextRowCount = text.split("\\n").length;

                        // user has input more than limited - lets do something
                        // about that
                        if (editTextRowCount >= 4) {

                            // find the last break
                            int lastBreakIndex = text.lastIndexOf("\n");

                            // compose new text
                            String newText = text.substring(0, lastBreakIndex);

                            // add new text - delete old one and append new one
                            // (append because I want the cursor to be at the end)
                            ((EditText) v).setText("");
                            ((EditText) v).append(newText);

                        }
                    }

                    return false;
                }
            });
        }

        public void setEnable(Boolean status) {
            this.name.setEnabled(status);
            this.age.setEnabled(status);
            this.gender.setEnabled(status);
            this.id.setEnabled(status);
            if (status)
                this.info.setVisibility(View.VISIBLE);
            else
                this.info.setVisibility(View.GONE);
            this.info.setEnabled(status);
            this.edit.setEnabled(!status);
            this.save.setEnabled(status);
            this.cancel.setEnabled(status);
            this.detail.setEnabled(!status);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public DeviceAdapter(ArrayList<DeviceModel> myDataset) {
//        mContext = context;
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DeviceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_device, parent, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final DeviceModel device = mDataset.get(position);
        holder.name.setText(device.name);
        holder.age.setText(device.age);
        holder.gender.setText(device.gender);
        holder.id.setText(device.id);
        holder.info.setText(device.info);
        if ((device.status != null) && device.status) {
            holder.cardView.setBackgroundColor(MyoApp.getAppContext().getResources().getColor(R.color.alert));
        } else {
            holder.cardView.setBackgroundColor(MyoApp.getAppContext().getResources().getColor(R.color.normal));
        }
        holder.setEnable(false);

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.setEnable(true);
            }
        });

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                device.name = holder.name.getText().toString();
                device.age = holder.age.getText().toString();
                device.gender = holder.gender.getText().toString();
                device.id = holder.id.getText().toString();
                device.info = holder.info.getText().toString();
                holder.setEnable(false);
                device.saveToPref();
                notifyDataSetChanged();
            }
        });

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.setEnable(false);
                notifyDataSetChanged();
            }
        });

        holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Adapter", "Jump to patient detail page");
                Intent intent = new Intent(MyoApp.getAppContext(), PatientDetail.class);
                Bundle b = new Bundle();
                b.putInt("device_id", device.getDeviceId()); //Your id
                intent.putExtras(b); //Put your id to your next Intent
                MyoApp.getAppContext().startActivity(intent);
            }
        });

    }

    public void setAlert(Integer deviceId, Boolean status) {
        Collections.sort(mDataset, new Comparator<DeviceModel>() {
            @Override
            public int compare(DeviceModel device2, DeviceModel device1)
            {

                return  device1.deviceId.compareTo(device2.deviceId);
            }
        });
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

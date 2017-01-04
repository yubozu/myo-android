package cn.ac.ict.myo.model;


import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.ac.ict.myo.MyoApp;

/**
 * Author: saukymo
 * Date: 1/4/17
 */

public class HistoryModel {
    private String uuid;
    private ArrayList<RecordModel> history;

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setHistory(ArrayList<RecordModel> history) {
        this.history = history;
    }

    public ArrayList<RecordModel> getHistory() {
        return history;
    }

    private class RecordModel {
        private Date start;
        private Date end;

        public RecordModel() {
            this.start = new Date();
            this.end = null;
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        }

        public void stop() {
            this.end = new Date();
        }

        public String toString() {
            SimpleDateFormat startFormat = new SimpleDateFormat("yyyy-M-dd \t\t\t\t\t\t\t\t hh:mm:ss");
            SimpleDateFormat endFormat = new SimpleDateFormat("hh:mm:ss \t\t\t\t\t\t\t\t\t");
            String rt = startFormat.format(start) + "-";
            if (end != null) {
                rt += endFormat.format(end) + "High";
            }
            return rt;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (RecordModel record: history) {
            sb.append(record.toString() + "\n");
        }
        return sb.toString();
    }

    public HistoryModel(String uuid) {
        this.uuid = uuid;
        this.history = new ArrayList<>();
    }

    public void start() {
        if (this.history.size() == 5) {
            this.history.remove(0);
        }
        this.history.add(new RecordModel());
    }

    public void stop() {
        if (this.history.size() == 0)
            return;
        RecordModel recordModel = this.history.get(this.history.size() - 1);
        if (recordModel.end == null) {
            recordModel.stop();
        }
    }

    static public HistoryModel getHistoryModel(String uuid) {
        SharedPreferences prefs = MyoApp.getAppContext().getSharedPreferences(
                "history", Context.MODE_PRIVATE);
        String historyJson = prefs.getString(uuid, null);
        if (historyJson == null) {
            return new HistoryModel(uuid);
        } else {
            Gson gson = new Gson();
            try {
                return gson.fromJson(historyJson, HistoryModel.class);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                return new HistoryModel(uuid);
            }
        }
    }

    public void save() {
        SharedPreferences prefs = MyoApp.getAppContext().getSharedPreferences(
                "history", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(uuid, toJson());
        editor.apply();
    }

    public String toJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public String getLastOutbreak() {
        if (history.size() == 0) {
            return "未发作";
        } else {
            SimpleDateFormat startFormat = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
            return startFormat.format(history.get(history.size() - 1).start);
        }
    }
}

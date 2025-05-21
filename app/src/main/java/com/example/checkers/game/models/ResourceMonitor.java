package com.example.checkers.game.models;

import android.app.ActivityManager;
import android.content.Context;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class ResourceMonitor {
    private final Context context;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final int intervalMs = 50;

    private final List<Float> cpuUsages = new ArrayList<>();
    private final List<Long> ramUsages = new ArrayList<>();
    private final List<Integer> batteryLevels = new ArrayList<>();

    private boolean isMonitoring = false;

    public ResourceMonitor(Context context) {
        this.context = context.getApplicationContext();
    }

    public void startMonitoring() {
        if (isMonitoring) return;
        isMonitoring = true;
        handler.post(pollRunnable);
    }

    public Result stopMonitoring() {
        isMonitoring = false;
        handler.removeCallbacks(pollRunnable);

        float avgCpu = average(cpuUsages);
        long avgRam = averageLong(ramUsages);
        int avgBattery = averageInt(batteryLevels);

        cpuUsages.clear();
        ramUsages.clear();
        batteryLevels.clear();

        return new Result(avgCpu, avgRam, avgBattery);
    }

    private final Runnable pollRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isMonitoring) return;

            cpuUsages.add(readCpuUsage());
            ramUsages.add(readRamUsage());
            batteryLevels.add(readBatteryLevel());

            handler.postDelayed(this, intervalMs);
        }
    };

    public float readCpuUsage() {
            int rate = 0;
            try {
                String Result;
                Process p = Runtime.getRuntime().exec("top -n 1");
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                while ((Result = br.readLine()) !=null){
                    if (Result.contains("com.example.ch+")) {
                        String[] info = Result.trim().replaceAll(" +"," ").split(" ");
                        if(info[8].matches("[0-9.]+")){
                            return Float.parseFloat(info[8]);
                        }
                        else{
                            return Float.parseFloat(info[9]);
                        }
                    }
                }

            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
            }
            return rate;
    }



    private long readRamUsage() {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        long used = memoryInfo.totalMem - memoryInfo.availMem;
        return used/(1024*1024); // in MB
    }

    private int readBatteryLevel() {
        BatteryManager bm = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
        return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }

    private float average(List<Float> list) {
        if (list.isEmpty()) return 0;
        float sum = 0;
        for (Float f : list) sum += f;
        return sum / list.size();
    }

    private long averageLong(List<Long> list) {
        if (list.isEmpty()) return 0;
        long sum = 0;
        for (Long l : list) sum += l;
        return sum / list.size();
    }

    private int averageInt(List<Integer> list) {
        if (list.isEmpty()) return 0;
        int sum = 0;
        for (Integer i : list) sum += i;
        return sum / list.size();
    }

    public class Result{
        public final double avgCpu;
        public final long avgRam;
        public final int avgBattery;

        public Result(float cpu, long ram, int battery){
            avgCpu = cpu;
            avgRam = ram;
            avgBattery = battery;
        }
    }
}

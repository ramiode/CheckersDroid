import android.app.ActivityManager;
import android.content.Context;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class ResourceMonitor {

    public interface ResourceMonitorListener {
        void onAveragesReady(float avgCpuUsage, long avgRamUsageKB, int avgBatteryLevel);
    }

    private final Context context;
    private final ResourceMonitorListener listener;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final int intervalMs = 50;

    private final List<Float> cpuUsages = new ArrayList<>();
    private final List<Long> ramUsages = new ArrayList<>();
    private final List<Integer> batteryLevels = new ArrayList<>();

    private boolean isMonitoring = false;

    public ResourceMonitor(Context context, ResourceMonitorListener listener) {
        this.context = context.getApplicationContext();
        this.listener = listener;
    }

    public void startMonitoring() {
        if (isMonitoring) return;
        isMonitoring = true;
        handler.post(pollRunnable);
    }

    public void stopMonitoring() {
        isMonitoring = false;
        handler.removeCallbacks(pollRunnable);

        float avgCpu = average(cpuUsages);
        long avgRam = averageLong(ramUsages);
        int avgBattery = averageInt(batteryLevels);

        if (listener != null) {
            listener.onAveragesReady(avgCpu * 100, avgRam, avgBattery);
        }

        cpuUsages.clear();
        ramUsages.clear();
        batteryLevels.clear();
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

    private float readCpuUsage() {
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();
            String[] toks = load.split(" +");
            long idle1 = Long.parseLong(toks[4]);
            long cpu1 = 0;
            for (int i = 1; i < 8; i++) cpu1 += Long.parseLong(toks[i]);

            try { Thread.sleep(200); } catch (Exception ignored) {}

            reader.seek(0);
            load = reader.readLine();
            reader.close();
            toks = load.split(" +");
            long idle2 = Long.parseLong(toks[4]);
            long cpu2 = 0;
            for (int i = 1; i < 8; i++) cpu2 += Long.parseLong(toks[i]);

            return (float) (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private long readRamUsage() {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        long used = memoryInfo.totalMem - memoryInfo.availMem;
        return used / 1024; // in KB
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
}

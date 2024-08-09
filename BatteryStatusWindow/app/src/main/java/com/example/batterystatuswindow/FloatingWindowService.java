package com.example.batterystatuswindow;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class FloatingWindowService extends Service {
    private WindowManager windowManager;
    private View floatingView;
    private TextView info;
    private BatteryReceiver receiver;
    private final String TAG = "myTag...";
    private String batteryInfo = "";
    private String errorInfo = "";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service onCreate...");

        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Intent.ACTION_BATTERY_CHANGED));
        Log.d(TAG, "sendBroadcast: ACTION_BATTERY_CHANGED");

        receiver = new BatteryReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        registerReceiver(receiver, filter);
        Log.d(TAG, "RegisterReceiver");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void setWindow(){
        if(windowManager == null){
            Log.d(TAG, "setWindow...");
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

            floatingView = LayoutInflater.from(this).inflate(R.layout.window, null);
            info = floatingView.findViewById(R.id.info);
            TextView ok = floatingView.findViewById(R.id.ok);

            floatingView.clearFocus();

            final WindowManager.LayoutParams params = setParams();
            windowManager.addView(floatingView, params);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    stopMyService();
                }
            });
        }
        info.setText(batteryInfo);
    }

    public WindowManager.LayoutParams setParams(){
        Log.d(TAG, "setWindowParams...");
        final WindowManager.LayoutParams params;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }

// 设置视图的初始位置
        params.gravity = Gravity.CENTER;

        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);

        params.width = (int) (300 * dm.density);
        params.height = (int) (450 * dm.density);
        params.x = 0;
        params.y = 0;

        return params;
    }

    private class BatteryReceiver extends BroadcastReceiver {
        private String health = "";
        private int battery;
        private BatteryManager mBatteryManager;
        private boolean usbCharge;
        private boolean acCharge;
        private boolean isCharging;
        private int voltage;
        private float temperature;
        private String technology;
        private String batteryPresent;
        private int capacity;
        private long chargeCounter;
        private long currentAverage;
        private long currentNow;
        private long energyCounter;
        private long chargeTimeRemaining;
        private boolean powerSaveMode;
        private int level;
        private int scale;
        private int status;
        private int chargePlug;

        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())){
                Log.d(TAG, "onReceive...");
                updateInfo(context, intent);
                printInfo();
            }
            else{
                if(intent.ACTION_POWER_CONNECTED.equals(intent.getAction())){
                    Log.d(TAG, "StartCharging...");
                    updateInfo(context, intent);
                    printInfo();
                    Toast toast = Toast.makeText(context, "Start Charging!", Toast.LENGTH_SHORT);
                    toast.show();
                    Log.d(TAG, "StartChargingSuccessfully!");
                }
                else{
                    Log.d(TAG, "StopCharging...");
                    updateInfo(context, intent);
                    printInfo();
                    Toast toast = Toast.makeText(context, "Stop Charging!", Toast.LENGTH_SHORT);
                    toast.show();
                    Log.d(TAG, "StopChargingSuccessfully!");
                }
            }
        }

        @SuppressLint("DefaultLocale")
        @RequiresApi(api = Build.VERSION_CODES.P)
        public synchronized void updateInfo(Context context, Intent intent){
            try{
                Log.d(TAG, "updateInfo...");
                mBatteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);

                status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

                chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
                acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

                level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                battery = (int)(level * 100 / (float) scale);

                switch (intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)){
                    case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                        health = "Unknown";
                        break;
                    case BatteryManager.BATTERY_HEALTH_GOOD:
                        health = "Good";
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                        health = "OverHeat";
                        break;
                    case BatteryManager.BATTERY_HEALTH_DEAD:
                        health = "Dead";
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                        health = "OverVoltage";
                        break;
                    default:
                        health = "Failed";
                        break;
                }

                voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
                temperature = (float) (intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)/10.0);
                technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
                batteryPresent = (intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false) ? "Yes":"No");
                capacity = mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                chargeCounter = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
                currentNow = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
                currentAverage = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE);
                energyCounter = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && mBatteryManager.computeChargeTimeRemaining() != -1) {
                    chargeTimeRemaining = mBatteryManager.computeChargeTimeRemaining();
                }

                PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
                powerSaveMode = powerManager.isPowerSaveMode();
            }catch (Exception e){
                Log.e(TAG, e.toString());
            }
        }


        @SuppressLint("DefaultLocale")
        public synchronized void printInfo(){
            if(batteryInfo != null){
                batteryInfo = null;
            }
            Log.d(TAG, "printInfo...");
            StringBuilder batteryInfoBuilder = new StringBuilder();

            if (status == BatteryManager.BATTERY_STATUS_UNKNOWN || status == -1){
                batteryInfoBuilder.append("Waiting...");
            } else{
                if (status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL){
                    batteryInfoBuilder.append("Charging Status: Charging\n");
                } else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
                    batteryInfoBuilder.append("Charging Status: Discharging\n");
                } else if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
                    batteryInfoBuilder.append("Charging Status: Not charging\n");
                }

                if (chargePlug != -1){
                    batteryInfoBuilder.append("Charging over USB: ").append((usbCharge) ? "Yes" : "No").append("\n");
                    batteryInfoBuilder.append("Charging over AC: ").append((acCharge) ? "Yes" : "No").append("\n");
                }

                batteryInfoBuilder.append("Battery Present: ").append(batteryPresent).append("\n");

                if (battery < 0 || battery > 100 || scale == -1 || level == -1){
                    Log.e(TAG, "'Battery' is incorrect!");
                    batteryInfoBuilder.append("Battery: Invalid Value\n");
                }else {
                    batteryInfoBuilder.append("Battery: ").append(battery).append("%\n");
                }

                if (capacity < 0 || capacity > 100){
                    Log.e(TAG, "'Capacity' is incorrect!");
                    batteryInfoBuilder.append("Capacity: Invalid Value\n");
                }
                else {
                    batteryInfoBuilder.append("Capacity: ").append(capacity).append("%\n");
                }

                if(health.equals("Failed")){
                    Log.e(TAG, "'Battery Health' is incorrect!");
                }
                batteryInfoBuilder.append("Battery Health: ").append(health).append("\n");

                if((3 <= voltage  && voltage <= 10)){
                    batteryInfoBuilder.append("Voltage: ").append(voltage).append("V\n");
                } else if (3000 <= voltage && voltage <= 10000) {
                    batteryInfoBuilder.append("Voltage: ").append(voltage/1000.0).append("V\n");
                }else {
                    Log.e(TAG, "'Voltage' is incorrect!");
                    batteryInfoBuilder.append("Voltage: Invalid Value\n");
                }

                if (temperature < 0 || temperature > 100){
                    Log.e(TAG, "'Temperature' is incorrect!");
                    batteryInfoBuilder.append("Temperature: Invalid Value\n");
                }
                else {
                    batteryInfoBuilder.append("Temperature: ").append(temperature).append("℃\n");
                }

                if(technology == null || technology.isEmpty()){
                    batteryInfoBuilder.append("Technology: Unknown\n");
                }else {
                    batteryInfoBuilder.append("Technology: ").append(technology).append("\n");
                }

                if(chargeCounter < 0 || chargeCounter > 10000000){//0~10000mA
                    Log.e(TAG, "'Charge Counter' is incorrect!");
                }
                else {
                    batteryInfoBuilder.append("Charge Counter: ").append(String.format("%3.3f", chargeCounter / 1000000.0)).append("Wh\n");
                }

                if(energyCounter < 0 || energyCounter > 100000){
                    Log.e(TAG, "'Energy Counter' is incorrect!");
                }
                else {
                    batteryInfoBuilder.append("Energy Counter: ").append(String.format("%3.3f", energyCounter / 1000000000.0)).append("Wh\n");
                }

                if(Math.abs(currentNow) <10000 || Math.abs(currentNow) > 10000000){//10mA~10A
                    Log.e(TAG, "'Current Now' is incorrect!");
                }
                else {
                    batteryInfoBuilder.append("Current Now: ").append(String.format("%3.3f", currentNow / 1000000.0)).append("A\n");
                }
                if(Math.abs(currentAverage) < 1000 || Math.abs(currentAverage) > 10000000){//1mA~10A
                    Log.e(TAG, "'Current Average' is incorrect!");
                }
                else {
                    batteryInfoBuilder.append("Current Average: ").append(String.format("%3.3f", currentAverage / 1000000.0)).append("A\n");
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && chargeTimeRemaining != -1) {
                    if(chargeTimeRemaining <= 60000){
                        batteryInfoBuilder.append("Charge Time Remaining: ").append(String.format("%3.3f", chargeTimeRemaining / 1000.0)).append("s\n");

                    }else {
                        float seconds = (float) (chargeTimeRemaining / 1000.0);
                        int minute = (int)seconds / 60;
                        int restSeconds = (int) seconds - 60 * minute;
                        if(minute >= 60){
                            int hour = minute / 60;
                            minute = minute - hour;
                            restSeconds = (int) seconds - 60 * 60 * hour - 60 * minute;
                            batteryInfoBuilder.append("Charge Time Remaining: ").append(String.format("%d h %d min %d s\n", hour, minute, restSeconds));
                        }
                        batteryInfoBuilder.append("Charge Time Remaining: ").append(String.format("%d min %d s\n", minute, restSeconds));
                    }
                }
                else {
                    batteryInfoBuilder.append("Charge Time Remaining: Unknown\n");
                }

                batteryInfoBuilder.append("Power Save Mode: ").append(powerSaveMode ? "Enabled" : "Disabled").append("\n");

                batteryInfo = batteryInfoBuilder.toString();
                Log.i(TAG, batteryInfo);
                batteryInfoBuilder.setLength(0);

                setWindow();
            }
        }
    }

    public void stopMyService(){
        try{
            Log.d(TAG, "Stop Service");
            windowManager.removeView(floatingView);
            Log.d(TAG, "View removed successfully");
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
            Log.d(TAG, "Unregister Receiver successfully");
            Intent intent = new Intent("PROGRAM_STOPPED");
            sendBroadcast(intent);
            stopSelf();
        }catch(Exception e){
            Log.e(TAG, "Error during onDestroyFloatingWindowService");
        }
    }
}


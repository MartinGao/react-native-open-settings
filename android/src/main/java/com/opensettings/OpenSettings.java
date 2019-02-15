package com.opensettings;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;
import java.util.Arrays;
import java.text.SimpleDateFormat;

import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Bundle;
import android.provider.Settings;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.util.Log;
 import java.util.GregorianCalendar; 
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;


class ExecuteAtSpecificTimeReceiver extends BroadcastReceiver {

  private ReactContext reactContext;

  public ExecuteAtSpecificTimeReceiver(final ReactContext reactContext) {
    super();
    this.reactContext = reactContext;
  }
  private void sendEvent(ReactContext reactContext, String eventName, String payload) {
    reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, payload);
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    sendEvent(reactContext, "executeAtSpecificTimeEventFromBroadcastReceiver", intent.getStringExtra("targetTaskName"));
  }
}

public class OpenSettings extends ReactContextBaseJavaModule {

    private ReactContext reactContext;
    private static final String TAG = "Ruijia";

    public OpenSettings(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNOpenSettings";
    }

    //region React Native Methods
    @ReactMethod
    public void openSettings() {
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + reactContext.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        reactContext.startActivity(i);
    }

    @ReactMethod
    public void openSystemSettings() {
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_SETTINGS);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        reactContext.startActivity(i);
    }

    @ReactMethod
    public void reboot() {
      // PowerManager pm = (PowerManager)this.reactContext.getSystemService(reactContext.POWER_SERVICE);
      // pm.reboot("bootloader");
      try {
        Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", "reboot" });
        proc.waitFor();
      } catch (Exception ex) {
        // Log.i(TAG, "Could not reboot", ex);
      }
    }

    @ReactMethod
    public void rebootZX() {
      Intent mIntent = new Intent("android.intent.action.ZX_REBOOT");
      reactContext.sendBroadcast(mIntent);
    }

    @ReactMethod
    public void setDeviceTime(final String dateString) {
      try {
        Process proc = Runtime.getRuntime().exec(new String[] { "su", "0", "toolbox", "date", "-s", dateString });
        proc.waitFor();
      } catch (Exception ex) {
        // Log.i(TAG, "Could not reboot", ex);
      }
    }

    @ReactMethod
    public void setDeviceTimeMillis(final Double currentTimeMillis) {
      try {
        Process procOne = Runtime.getRuntime().exec(new String[] { "su", "chmod", "666", "/dev/alarm" });
        procOne.waitFor();
        SystemClock.setCurrentTimeMillis(currentTimeMillis.longValue());
        Process procTwo = Runtime.getRuntime().exec(new String[] { "su", "chmod", "664", "/dev/alarm" });
        procTwo.waitFor();
      } catch (Exception ex) {
        // Log.i(TAG, "Could not reboot", ex);
      }
    }

    public static int[] turnDate(Date aa) {
      SimpleDateFormat newsimpleFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
  		int[] timeArray = new int[5];
  		String currentData = newsimpleFormat.format(aa);
  		timeArray[0] = Integer.parseInt(currentData.split("-")[0]);
  		timeArray[1] = Integer.parseInt(currentData.split("-")[1]);
  		timeArray[2] = Integer.parseInt(currentData.split("-")[2]);
  		timeArray[3] = Integer.parseInt(currentData.split("-")[3]);
  		timeArray[4] = Integer.parseInt(currentData.split("-")[4]);
  		return timeArray;
  	}

    @ReactMethod
    public void autoShutdownAndRestart(Double onTimeMillis, Double offTimeMillis, Promise promise) {
      Calendar c = Calendar.getInstance();
      Calendar c2 = Calendar.getInstance();
      Date onDate = new Date(onTimeMillis.longValue());
      Date offDate = new Date(offTimeMillis.longValue());

      Intent mIntent = new Intent("android.56iq.intent.action.setpoweronoff");
      mIntent.putExtra("timeon", turnDate(onDate));
      mIntent.putExtra("timeoff", turnDate(offDate));
      mIntent.putExtra("enable", true);
      reactContext.sendBroadcast(mIntent);
      promise.resolve("Off: " + Arrays.toString(turnDate(offDate)) + " -> On: " +Arrays.toString(turnDate(onDate)));
    }

    @ReactMethod
    public void autoShutdownAndRestartZX(Double onTimeMillis, Double offTimeMillis, Promise promise) {
      Date onDate = new Date(onTimeMillis.longValue());
      Date offDate = new Date(offTimeMillis.longValue());

      Log.i(TAG, "autoShutdownAndRestartZX" + " | " + "onTimeMillis: " + onTimeMillis.toString() + " - "+ Arrays.toString(turnDate(onDate)) + " | " + "offTimeMillis: " + offTimeMillis.toString() + " - " + Arrays.toString(turnDate(offDate)));

      Intent mIntent = new Intent("android.zx.intent.action.AUTOPOWERONOFF");
      mIntent.putExtra("timeon", onTimeMillis);
      mIntent.putExtra("timeoff", offTimeMillis);
      mIntent.putExtra("enable", true);
      reactContext.sendBroadcast(mIntent);

      promise.resolve("Off: " + Arrays.toString(turnDate(offDate)) + " -> On: " +Arrays.toString(turnDate(onDate)));
    }

    @ReactMethod
    public void autoShutdownAndRestart7Inch(Double onTimeMillis, Double offTimeMillis, Promise promise) {
      Date onDate = new Date(onTimeMillis.longValue());
      Date offDate = new Date(offTimeMillis.longValue());

      Calendar calendarON = GregorianCalendar.getInstance();
      calendarON.setTime(onDate);
      int hourON = calendarON.get(Calendar.HOUR_OF_DAY);
      int minuteON = calendarON.get(Calendar.MINUTE); 

      Calendar calendarOFF = GregorianCalendar.getInstance();
      calendarOFF.setTime(offDate);
      int hourOFF = calendarOFF.get(Calendar.HOUR_OF_DAY);
      int minuteOFF = calendarOFF.get(Calendar.MINUTE); 

      Log.i(TAG, "autoShutdownAndRestart7Inch" + " | " + "onTimeMillis: " + onTimeMillis.toString() + " - "+ Arrays.toString(turnDate(onDate)) + " | " + "offTimeMillis: " + offTimeMillis.toString() + " - " + Arrays.toString(turnDate(offDate)));

      Intent onIntent = new Intent("android.intent.action.ALARMRECEIVER");
      // Turn on
      Bundle dataON = new Bundle();
      dataON.putInt("id", 1);
      dataON.putBoolean("enabled", true);
      dataON.putInt("hour", hourON);
      dataON.putInt("minutes", minuteON);
      dataON.putInt("dayofweek", 127);
      onIntent.putExtra("data", dataON);
      reactContext.sendBroadcast(onIntent);

      Intent offIntent = new Intent("android.intent.action.ALARMRECEIVER");
      // Turn off
      Bundle dataOFF = new Bundle();
      dataOFF.putInt("id", 2);
      dataOFF.putBoolean("enabled", true);
      dataOFF.putInt("hour", hourOFF);
      dataOFF.putInt("minutes", minuteOFF);
      dataOFF.putInt("dayofweek", 127);
      offIntent.putExtra("data", dataOFF);
      reactContext.sendBroadcast(offIntent);

      promise.resolve("Off: " +  String.valueOf(hourOFF) + ":" +  String.valueOf(minuteOFF) + " | On: " +  String.valueOf(hourON) + ":" +  String.valueOf(minuteON) );
      // promise.resolve("Off: " +  String.valueOf(hourOFF) + ":" +  String.valueOf(minuteOFF));
    }

    @ReactMethod
    public void turnOffLCDBacklight(Promise promise) {
      Intent mIntent = new Intent("com.szdrcc.lcd.backlight.Off");
      reactContext.sendBroadcast(mIntent);
      // promise.resolve("Done. LCD Backlight Turned Off.");
    }

    @ReactMethod
    public void turnOnLCDBacklight(Promise promise) {
      Intent mIntent = new Intent("com.szdrcc.lcd.backlight.On");
      reactContext.sendBroadcast(mIntent);
      // promise.resolve("Done. LCD Backlight Turned On.");
    }

    @ReactMethod
    public void getMemoryInfo(Promise promise) {
      MemoryInfo mi = new MemoryInfo();
      ActivityManager activityManager = (ActivityManager) reactContext.getSystemService(reactContext.ACTIVITY_SERVICE);
      activityManager.getMemoryInfo(mi);
      WritableMap memoryInfo = Arguments.createMap();
      memoryInfo.putInt("total", (int) mi.totalMem);
      memoryInfo.putInt("avail", (int) mi.availMem);
      promise.resolve(memoryInfo);
    }

    private void sendEvent(ReactContext reactContext, String eventName) {
      reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, null);
    }

    @ReactMethod
    public void executeOneTaskAtSpecificTime(Double timeMillis, String targetTaskName) {
      AlarmManager alarmManager = (AlarmManager)reactContext.getSystemService(reactContext.ALARM_SERVICE);
      ExecuteAtSpecificTimeReceiver executeAtSpecificTimeReceiver = new ExecuteAtSpecificTimeReceiver(reactContext);
      reactContext.getCurrentActivity().registerReceiver(executeAtSpecificTimeReceiver, new IntentFilter("com.martin.cool." + Double.toString(timeMillis)));
      Intent intent = new Intent("com.martin.cool."+Double.toString(timeMillis));
      intent.putExtra("targetTaskName", targetTaskName);
      PendingIntent pendingIntent = PendingIntent.getBroadcast(reactContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
      alarmManager.set(AlarmManager.RTC_WAKEUP, timeMillis.longValue(), pendingIntent);
    }

    //endregion
}

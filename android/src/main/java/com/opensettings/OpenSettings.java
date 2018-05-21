package com.opensettings;

import android.content.Intent;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.Settings;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

public class OpenSettings extends ReactContextBaseJavaModule {

    private ReactContext reactContext;

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
    public void setDeviceTime(final String dateString) {
      try {
        Process proc = Runtime.getRuntime().exec(new String[] { "su", "0", "toolbox", "date", "-s", dateString });
        proc.waitFor();
      } catch (Exception ex) {
        // Log.i(TAG, "Could not reboot", ex);
      }
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

    //endregion
}

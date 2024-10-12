package com.backgroundsync

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.work.WorkManager
import com.backgroundsync.scheduler.OneTimeScheduler
import com.backgroundsync.scheduler.PeriodicScheduler
import com.backgroundsync.utils.isWorkCancelled
import com.backgroundsync.utils.isWorkScheduled
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap


class BackgroundSyncModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {
    private val TAG=BackgroundSyncModule::class.java.simpleName;

  private var workManager: WorkManager? = null

  override fun getName(): String {
    return NAME
  }

  override fun initialize() {
    super.initialize()
    if (workManager == null) {
      Log.d(TAG, "Initialising WorkManager");
      workManager = WorkManager.getInstance(reactApplicationContext)
    }
  }


  private fun schedulePeriodic(params: ReadableMap): Boolean {
    val periodicScheduler=PeriodicScheduler(workManager);
    periodicScheduler.schedule(params);
    val taskKey=params.getString("taskKey")
    if(isWorkScheduled(workManager,taskKey)){
      Log.d(TAG, "Successfully scheduled: $taskKey");
      return true;
    }
    return false;
  }

  private fun scheduleOneTime(params: ReadableMap): Boolean {
    val oneTimeScheduler= OneTimeScheduler(workManager);
    oneTimeScheduler.schedule(params);
    val taskKey=params.getString("taskKey")
    if(isWorkScheduled(workManager,taskKey)){
      Log.d(TAG, "Successfully scheduled: $taskKey");
      return true;
    }
    return false;
  }


  @ReactMethod
  fun schedule(params: ReadableMap, promise: Promise) {

    try{
      val type= params.getString("type")
      val result = when(type){
        "PERIODIC" -> schedulePeriodic(params)
        "ONE_TIME" -> scheduleOneTime(params)
        else -> null
      }

      promise.resolve(result);
    }
    catch (e:Exception){
      promise.reject(e);
    }

  }

  @ReactMethod
  fun cancel(taskKey: String?, promise: Promise) {
    try{
      workManager!!.cancelAllWorkByTag(taskKey!!)
      if (isWorkCancelled(workManager,taskKey)) {
        promise.resolve(true)
      } else {
        promise.reject(false as String)
      }
    }
    catch (e:Exception){
      promise.reject(e);
    }
  }

  @ReactMethod
  fun disableAppIgnoringBatteryOptimization() {
    val packageName = reactApplicationContext.packageName
    val pm = reactApplicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
    if (!pm.isIgnoringBatteryOptimizations(packageName)) {
      val intent = Intent()
      intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      intent.setData(Uri.parse("package:$packageName"))
      reactApplicationContext.startActivity(intent)
    }
  }


  companion object {
    const val NAME = "BackgroundSync"
  }
}

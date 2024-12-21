package com.backgroundsync.utils

import android.util.Log
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.ReadableType
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit


fun convertToTimeUnit(type: String?): TimeUnit {
  val timeUnit: TimeUnit = when (type) {
    "HOUR" -> TimeUnit.HOURS
    "DAY" -> TimeUnit.DAYS
    "SECOND" -> TimeUnit.SECONDS
    else -> {
      TimeUnit.MINUTES
    }
  }
  return timeUnit
}

fun convertWorkerPolicy(type: String?): ExistingPeriodicWorkPolicy {
  val workerPolicy: ExistingPeriodicWorkPolicy = when (type) {
    "KEEP" -> ExistingPeriodicWorkPolicy.KEEP
    "REPLACE" -> ExistingPeriodicWorkPolicy.REPLACE
    "UPDATE" -> ExistingPeriodicWorkPolicy.UPDATE
    else -> {
      ExistingPeriodicWorkPolicy.KEEP
    }
  }
  return workerPolicy
}


fun generateHeadlessConfig(params: ReadableMap): Data {
  val config = Data.Builder()
    .putString("taskKey", params.getString("taskKey"))

  val extras = params.getMap("extras")
  extras?.let {
    val iterator = it.keySetIterator()
    while (iterator.hasNextKey()) {
      val key = iterator.nextKey()
      Log.d("TAG", "generateHeadlessConfig: $key")
      when (it.getType(key)) {
        ReadableType.String -> config.putString(key, it.getString(key))
        ReadableType.Number -> config.putDouble(key, it.getDouble(key))
        ReadableType.Boolean -> config.putBoolean(key, it.getBoolean(key))
        ReadableType.Null -> config.putString(key, null)
        ReadableType.Map -> {
          val nestedMap = it.getMap(key)?.toHashMap()
          nestedMap?.let { map ->
            config.putAll(map.mapKeys { "${key}_${it.key}" })
          }
        }
        ReadableType.Array -> {
          val array = it.getArray(key)?.toArrayList()
          array?.forEachIndexed { index, value ->
            when (value) {
              is String -> config.putString("${key}_$index", value)
              is Number -> config.putDouble("${key}_$index", value.toDouble())
              is Boolean -> config.putBoolean("${key}_$index", value)
            }
          }
        }
      }
    }
  }


  if(params.hasKey("maxRetryAttempts")) config.putInt("maxRetryAttempts", params.getInt("maxRetryAttempts"))
  if(params.hasKey("retryDelay")) config.putInt("retryDelay", params.getDouble("retryDelay").toInt())
  if(params.hasKey("taskTimeout")) config.putLong("taskTimeout", params.getDouble("taskTimeout").toLong())
  if(params.hasKey("allowedInForeground")) config.putBoolean("allowedInForeground", params.getBoolean("allowedInForeground"))

  return config.build()
}

fun isWorkScheduled(workManager: WorkManager?,tag: String?): Boolean {
  val statuses: ListenableFuture<List<WorkInfo>> = workManager!!.getWorkInfosByTag(tag!!)
  try {
    var running = false
    val workInfoList = statuses.get()
    for (workInfo in workInfoList) {
      val state: WorkInfo.State = workInfo.state
      running = (state == WorkInfo.State.RUNNING) or (state == WorkInfo.State.ENQUEUED)
    }
    return running
  } catch (e: ExecutionException) {
    e.printStackTrace()
    return false
  } catch (e: InterruptedException) {
    e.printStackTrace()
    return false
  }
}

fun isWorkCancelled(workManager: WorkManager?,tag: String?): Boolean {
  val statuses: ListenableFuture<List<WorkInfo>> = workManager!!.getWorkInfosByTag(tag!!)
  try {
    var cancelled = false
    val workInfoList = statuses.get()
    for (workInfo in workInfoList) {
      val state: WorkInfo.State = workInfo.state
      cancelled = state == WorkInfo.State.CANCELLED
    }
    return cancelled
  } catch (e: ExecutionException) {
    e.printStackTrace()
    return false
  } catch (e: InterruptedException) {
    e.printStackTrace()
    return false
  }
}


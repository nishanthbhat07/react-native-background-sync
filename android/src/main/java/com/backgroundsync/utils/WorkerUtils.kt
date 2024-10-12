package com.backgroundsync.utils

import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.facebook.react.bridge.ReadableMap
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
  return  Data.Builder()
    .putInt("maxRetryAttempts", params.getInt("maxRetryAttempts"))
    .putLong("retryDelay", params.getDouble("retryDelay").toLong())
    .putLong("taskTimeout", params.getDouble("taskTimeout").toLong())
    .putBoolean("allowedInForeground", params.getBoolean("allowedInForeground"))
    .putString("taskKey", params.getString("taskKey"))
    .build()
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


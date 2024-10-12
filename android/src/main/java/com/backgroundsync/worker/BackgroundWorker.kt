package com.backgroundsync.worker

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.WorkerParameters
import com.facebook.react.bridge.Arguments
import com.facebook.react.jstasks.HeadlessJsTaskConfig
import com.facebook.react.jstasks.HeadlessJsTaskRetryPolicy
import com.facebook.react.jstasks.LinearCountingRetryPolicy

class BackgroundSyncWorker(context: Context, workerParams: WorkerParameters) :
  HeadlessWorker(context, workerParams) {
  override fun getTaskConfig(data: Data?): HeadlessJsTaskConfig? {
    Log.d("TAG", "getTaskConfig: $data")
    val taskKey= data?.getString("taskKey")
    val maxRetryAttempts = data?.getInt("maxRetryAttempts", 3)
    val retryDelay = data?.getInt("retryDelay", 4000)
    val taskTimeout = data?.getLong("taskTimeout", 10000L)
    val allowedInForeground = data?.getBoolean("allowedInForeground", true)

    val retryPolicy: HeadlessJsTaskRetryPolicy = LinearCountingRetryPolicy(
      maxRetryAttempts as Int,  // Max number of retry attempts
      retryDelay as Int, // Delay between each retry attempt
    )
    if (data != null) {
      return HeadlessJsTaskConfig(
        taskKey,
        Arguments.makeNativeMap(data.keyValueMap),
        taskTimeout as Long,  // timeout for the task
        allowedInForeground as Boolean,  // optional: defines whether or not  the task is allowed in foreground. Default is false
        retryPolicy
      )
    }
    return null
  }
}



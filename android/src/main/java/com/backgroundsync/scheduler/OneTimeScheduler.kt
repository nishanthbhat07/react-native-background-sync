package com.backgroundsync.scheduler

import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.backgroundsync.utils.generateHeadlessConfig
import com.backgroundsync.worker.BackgroundSyncWorker
import com.facebook.react.bridge.ReadableMap

class OneTimeScheduler(private val workManager: WorkManager?) {

  fun schedule(params: ReadableMap){
    val constraints = Constraints.Builder()
      .setRequiredNetworkType(NetworkType.CONNECTED)
      .build()

    val taskKey=params.getString("taskKey")

    val headlessConfig = generateHeadlessConfig(params)

    val oneTimeWorkRequest = OneTimeWorkRequestBuilder<BackgroundSyncWorker>()
      .setConstraints(constraints)
      .setInputData(headlessConfig)
      .addTag(taskKey as String)
      .build()

    workManager!!.enqueueUniqueWork(taskKey as String, ExistingWorkPolicy.REPLACE, oneTimeWorkRequest)
  }
}

package com.backgroundsync.scheduler

import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.backgroundsync.utils.convertToTimeUnit
import com.backgroundsync.utils.convertWorkerPolicy
import com.backgroundsync.utils.generateHeadlessConfig
import com.backgroundsync.worker.BackgroundSyncWorker
import com.facebook.react.bridge.ReadableMap

class PeriodicScheduler(private val workManager: WorkManager?) {

 fun schedule(params: ReadableMap){
   val constraints = Constraints.Builder()
     .setRequiredNetworkType(NetworkType.CONNECTED)
     .build()

   val syncInterval=params.getInt("syncInterval")
   val syncIntervalType=params.getString("syncIntervalType")
   val syncFlexTime=params.getInt("syncFlexTime")
   val syncFlexTimeType=params.getString("syncFlexTimeType")
   val taskKey=params.getString("taskKey")
   val workerPolicy=params.getString("workerPolicy")

   val headlessConfig = generateHeadlessConfig(params)

   val periodicWorkRequest = PeriodicWorkRequest.Builder(
     BackgroundSyncWorker::class.java,
     syncInterval as Long,
     convertToTimeUnit(syncIntervalType),
     syncFlexTime as Long,
     convertToTimeUnit(syncFlexTimeType)
   ).setConstraints(constraints).setInputData(headlessConfig).addTag(taskKey as String).build()

   workManager!!.enqueueUniquePeriodicWork(taskKey as String, convertWorkerPolicy(workerPolicy), periodicWorkRequest)
 }
}

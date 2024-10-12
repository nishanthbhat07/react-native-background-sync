package com.backgroundsync.worker

import android.content.Context
import android.util.Log
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.facebook.react.ReactApplication
import com.facebook.react.ReactInstanceManager
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.UiThreadUtil
import com.facebook.react.jstasks.HeadlessJsTaskConfig
import com.facebook.react.jstasks.HeadlessJsTaskContext
import com.facebook.react.jstasks.HeadlessJsTaskEventListener


abstract class HeadlessWorker(context: Context, workerParams: WorkerParameters) :
  ListenableWorker(context, workerParams), HeadlessJsTaskEventListener {
  private val reactNativeHost = (this.getApplicationContext() as ReactApplication).reactNativeHost
  private var taskId = 0
  private var mCompleter: CallbackToFutureAdapter.Completer<Result>? = null

  private fun invokeStartTask(reactContext: ReactContext, taskConfig: HeadlessJsTaskConfig) {
    val headlessJsTaskContext =
      HeadlessJsTaskContext.getInstance(reactContext)
    headlessJsTaskContext.addTaskEventListener(this)
    Log.d(TAG, "invokeStartTask:")
    UiThreadUtil.runOnUiThread { taskId = headlessJsTaskContext.startTask(taskConfig) }
  }


  protected abstract fun getTaskConfig(data: Data?): HeadlessJsTaskConfig?


  override fun startWork(): com.google.common.util.concurrent.ListenableFuture<Result> {
    return CallbackToFutureAdapter.getFuture { completer ->
      val taskConfig =
        getTaskConfig(this.getInputData())
      Log.d(TAG, "startWork: ")
      mCompleter = completer
      if (taskConfig != null) {
        this.startTask(taskConfig)
      } else {
        mCompleter!!.set(Result.failure())
      }
      ""
    }
  }

  protected fun startTask(taskConfig: HeadlessJsTaskConfig) {
    val reactInstanceManager = reactNativeHost.reactInstanceManager
    val reactContext = reactInstanceManager.currentReactContext
    if (reactContext == null) {
      reactInstanceManager.addReactInstanceEventListener(
        object : ReactInstanceManager.ReactInstanceEventListener {
          override fun onReactContextInitialized(reactContext: ReactContext) {
            invokeStartTask(reactContext, taskConfig)
            reactInstanceManager.removeReactInstanceEventListener(this)
          }
        })
      reactInstanceManager.createReactContextInBackground()
    } else {
      invokeStartTask(reactContext, taskConfig)
    }
  }


  override fun onStopped() {
    super.onStopped()
    cleanUpTask()
  }

  override fun onHeadlessJsTaskStart(taskId: Int) {
  }

  private fun cleanUpTask() {
    if (reactNativeHost.hasInstance()) {
      val reactInstanceManager = reactNativeHost.reactInstanceManager
      val reactContext = reactInstanceManager.currentReactContext
      if (reactContext != null) {
        val headlessJsTaskContext = HeadlessJsTaskContext.getInstance(reactContext)
        headlessJsTaskContext.removeTaskEventListener(this)
      }
    }
  }

  override fun onHeadlessJsTaskFinish(taskId: Int) {
    if (this.taskId == taskId) {
      if (this.mCompleter != null) {
        mCompleter!!.set(Result.success())
        cleanUpTask()
      }
    }
  }

  companion object {
    private val TAG: String = HeadlessWorker::class.java.simpleName
  }
}


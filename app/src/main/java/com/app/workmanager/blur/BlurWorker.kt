package com.app.workmanager.blur

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

private const val TAG = "BlurWorker"

@Suppress("BlockingMethodInNonBlockingContext")
class BlurWorker(
    private val context: Context,
    private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val appContext = applicationContext
        val resourceUri = inputData.getString(KEY_IMAGE_URI)
        delay(5000)
        return resourceUri?.let {
            return withContext(Dispatchers.IO) {
                try {
                    val resolver = appContext.contentResolver
                    val picture =
                        BitmapFactory.decodeStream(resolver.openInputStream(Uri.parse(resourceUri)))
                    val output = blurBitmap(picture, appContext)
                    // Write bitmap to a temp file
                    val outputUri = writeBitmapToFile(appContext, output)
                    val outputData = workDataOf(KEY_IMAGE_URI to outputUri.toString())
                    Result.success(outputData)
                } catch (throwable: Throwable) {
                    Log.e(TAG, "Error applying blur")
                    throwable.printStackTrace()
                    Result.failure()
                }
            }
        } ?: Result.failure()
    }
}


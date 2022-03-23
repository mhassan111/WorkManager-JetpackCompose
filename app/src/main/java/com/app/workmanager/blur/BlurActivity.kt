package com.app.workmanager.blur

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.work.*
import coil.compose.rememberImagePainter
import com.app.workmanager.R
import com.app.workmanager.ui.theme.WorkManagerGuideTheme

class BlurActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val builder = Data.Builder()
        builder.putString(KEY_IMAGE_URI, getImageUri(this).toString())
        val blurImageRequest = OneTimeWorkRequestBuilder<BlurWorker>()
            .setInputData(builder.build())
            .build()
        val workManager = WorkManager.getInstance(applicationContext)

        setContent {
            WorkManagerGuideTheme {
                val workInfos = workManager
                    .getWorkInfosForUniqueWorkLiveData("blurImage")
                    .observeAsState()
                    .value
                val blurInfo = remember(key1 = workInfos) {
                    workInfos?.find { it.id == blurImageRequest.id }
                }

                val imageUri by derivedStateOf {
                    val blurImageUri = blurInfo?.outputData?.getString(KEY_IMAGE_URI)
                    blurImageUri?.toUri()
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    imageUri?.let { uri ->
                        Image(
                            painter = rememberImagePainter(
                                data = uri
                            ),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    Button(
                        onClick = {
                            workManager
                                .beginUniqueWork(
                                    "blurImage",
                                    ExistingWorkPolicy.KEEP,
                                    blurImageRequest
                                )
                                .enqueue()
                        },
                        enabled = blurInfo?.state != WorkInfo.State.RUNNING
                    ) {
                        Text(text = "Blur Image")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    when (blurInfo?.state) {
                        WorkInfo.State.RUNNING -> Text("Blurring...", color = Color.Black)
                        WorkInfo.State.SUCCEEDED -> Text(
                            "Blur Image succeeded",
                            color = Color.Black
                        )
                        WorkInfo.State.FAILED -> Text("Blurring failed", color = Color.Black)
                        WorkInfo.State.CANCELLED -> Text("Blurring cancelled", color = Color.Black)
                        WorkInfo.State.ENQUEUED -> Text("Blurring enqueued", color = Color.Black)
                        WorkInfo.State.BLOCKED -> Text("Blurring blocked", color = Color.Black)
                    }
                }
            }
        }
    }

    private fun getImageUri(context: Context): Uri {
        val resources = context.resources
        return Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(resources.getResourcePackageName(R.drawable.android_cupcake))
            .appendPath(resources.getResourceTypeName(R.drawable.android_cupcake))
            .appendPath(resources.getResourceEntryName(R.drawable.android_cupcake))
            .build()
    }
}
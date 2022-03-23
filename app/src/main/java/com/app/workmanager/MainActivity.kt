package com.app.workmanager

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.workmanager.blur.BlurActivity
import com.app.workmanager.ui.theme.WorkManagerGuideTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WorkManagerGuideTheme {

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            startActivity(Intent(this@MainActivity, BlurActivity::class.java))
                        },
                    ) {
                        Text(text = "Blur Image")
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            startActivity(Intent(this@MainActivity, FilterActivity::class.java))
                        },
                    ) {
                        Text(text = "Apply Filter to Image")
                    }
                }

            }
        }
    }
}

sealed class Screen(val route: String) {
    object FilterScreen : Screen("filter_screen")
    object BlurScreen : Screen("blur_screen")
}
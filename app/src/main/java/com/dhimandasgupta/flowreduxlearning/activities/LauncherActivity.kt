package com.dhimandasgupta.flowreduxlearning.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

class LauncherActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val columnState = rememberScrollState()
            val context = LocalContext.current

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = colorScheme.onBackground
                    )
                    .verticalScroll(columnState),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    modifier = Modifier
                        .padding(16.dp),
                    onClick = {
                        context.startActivity(
                            Intent(context, AppAndActivityStateActivity::class.java)
                        )
                    }
                ) {
                    Text(text = "Example of App State and Activity State initialization")
                }

                Button(
                    modifier = Modifier
                        .padding(16.dp),
                    onClick = {
                        context.startActivity(
                            Intent(context, CounterActivity::class.java)
                        )
                    }
                ) {
                    Text(text = "Example of Counter App")
                }

                Button(
                    modifier = Modifier
                        .padding(16.dp),
                    onClick = {
                        context.startActivity(
                            Intent(context, NewsActivity::class.java)
                        )
                    }
                ) {
                    Text(text = "Example of News App")
                }
            }
        }
    }
}
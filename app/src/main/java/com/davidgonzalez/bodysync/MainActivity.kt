package com.davidgonzalez.bodysync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.davidgonzalez.bodysync.navigation.AppNavigation
import com.davidgonzalez.bodysync.ui.screens.onboarding.PersonalDataScreen
import com.davidgonzalez.bodysync.ui.theme.BodySyncTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BodySyncTheme {
                AppNavigation()
            }
        }
    }
}

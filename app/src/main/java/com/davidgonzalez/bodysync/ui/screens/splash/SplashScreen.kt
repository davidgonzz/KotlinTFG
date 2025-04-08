package com.davidgonzalez.bodysync.ui.screens.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.davidgonzalez.bodysync.MainActivity
import com.davidgonzalez.bodysync.R
import com.davidgonzalez.bodysync.ui.screens.onboarding.ChooseScreen
import com.davidgonzalez.bodysync.ui.theme.BodySyncTheme
import com.google.firebase.auth.FirebaseAuth

class SplashScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SplashScreenUI()
        }

        // Esperar 2 segundos antes de decidir a dónde ir
        Handler(Looper.getMainLooper()).postDelayed({
            val currentUser = FirebaseAuth.getInstance().currentUser

            val intent = if (currentUser != null) {
                // Usuario ya logueado → ir a ChooseScreen
                Intent(this, ChooseScreen::class.java)
            } else {
                // Usuario no logueado → ir a MainActivity
                Intent(this, MainActivity::class.java)
            }

            startActivity(intent)
            finish() // Cierra la SplashScreen
        }, 2000)
    }
}

@Composable
fun SplashScreenUI() {
    BodySyncTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo de BodySync",
                    modifier = Modifier
                        .size(450.dp)
                        .padding(bottom = 16.dp)
                )

                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(4.dp),
                    color = colorResource(id = R.color.progress_line)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SplashScreenUI()
}

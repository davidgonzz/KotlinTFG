package com.davidgonzalez.bodysync.ui.screens.splash

import android.os.Bundle
import android.os.Handler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.content.Intent
import com.davidgonzalez.bodysync.R
import com.davidgonzalez.bodysync.ui.theme.BodySyncTheme
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.res.colorResource
import com.davidgonzalez.bodysync.ui.screens.auth.LoginScreen
import com.davidgonzalez.bodysync.ui.screens.choose.ChooseScreen
import com.google.firebase.auth.FirebaseAuth

class SplashScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplashScreenUI()
        }

        // Lanza la pantalla Login/Registro o ChooseScreen después de 2 segundos
        Handler().postDelayed({
            // Verificar si el usuario está autenticado
            val currentUser = FirebaseAuth.getInstance().currentUser
            val intent = if (currentUser != null) {
                // Si el usuario está autenticado, ir a la pantalla de elección entre nutrición y gimnasio
                Intent(this, ChooseScreen::class.java)
            } else {
                // Si no está autenticado, ir a la pantalla de inicio de sesión
                Intent(this, LoginScreen::class.java)
            }
            startActivity(intent)
            finish() // Termina la SplashScreen
        }, 2000) // 2 segundos de espera
    }
}

@Composable
fun SplashScreenUI() {
    BodySyncTheme {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center // Centra la imagen
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally, // Centra la columna
                verticalArrangement = Arrangement.Center // Alinea los elementos verticalmente
            ) {
                // Imagen centrada
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo de BodySync",
                    modifier = Modifier
                        .size(450.dp) // Ajusta el tamaño de la imagen si es necesario
                        .padding(bottom = 16.dp) // Espacio entre la imagen y la barra
                )

                // Barra de progreso (opcional)
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp) // Ajusta el padding si es necesario
                        .height(4.dp), // Ajusta la altura de la barra de progreso
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

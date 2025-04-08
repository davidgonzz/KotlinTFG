package com.davidgonzalez.bodysync.ui.screens.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.davidgonzalez.bodysync.ui.screens.choose.ChooseScreen
import com.davidgonzalez.bodysync.ui.theme.BodySyncTheme
import com.google.firebase.auth.FirebaseAuth

class LoginScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginScreenUI()
        }
    }
}

@Composable
fun LoginScreenUI() {
    BodySyncTheme {
        val context = LocalContext.current // Obtiene el contexto de la actividad

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Iniciar sesión",
                style = MaterialTheme.typography.headlineLarge
            )

            // Aquí puedes agregar campos para que el usuario ingrese el email y la contraseña

            Button(
                onClick = { loginUser(context, "email@example.com", "password") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar sesión")
            }
        }
    }
}

fun loginUser(context: android.content.Context, email: String, password: String) {
    val auth = FirebaseAuth.getInstance()
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, ChooseScreen::class.java)
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Error en el inicio de sesión", Toast.LENGTH_SHORT).show()
            }
        }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreenUI()
}

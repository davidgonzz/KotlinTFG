package com.davidgonzalez.bodysync.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.davidgonzalez.bodysync.R
import com.davidgonzalez.bodysync.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onLoginExitoso: () -> Unit,
    onIrARegistro: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mantenerSesion by remember { mutableStateOf(true) }
    var errorTexto by remember { mutableStateOf<String?>(null) }

    val estadoLogin by viewModel.estadoLogin.collectAsState()

    LaunchedEffect(estadoLogin) {
        estadoLogin.exceptionOrNull()?.let {
            errorTexto = it.message
        }

        if (estadoLogin.isSuccess && estadoLogin.getOrDefault(false)) {
            onLoginExitoso()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.logo_2),
            contentDescription = "Logo BodySync",
            modifier = Modifier
                .size(100.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = "¡Bienvenido de nuevo!",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Email o nombre de usuario") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                "¿Olvidaste tu contraseña?",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    // Aquí podrías navegar a una pantalla de recuperación de contraseña
                }
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = mantenerSesion,
                onCheckedChange = { mantenerSesion = it }
            )
            Text("Mantener sesión iniciada")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (correo.isBlank() || contrasena.isBlank()) {
                    errorTexto = "Completa todos los campos"
                } else {
                    errorTexto = null
                    viewModel.loginUsuario(correo, contrasena)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Iniciar sesión")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Línea separadora con puntito
        Divider(color = Color.Gray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.size(8.dp).align(Alignment.CenterHorizontally).padding(4.dp)) {
            Divider(
                color = Color.Gray,
                modifier = Modifier
                    .width(8.dp)
                    .height(2.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { /* Luego se conecta con Google */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Email, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Continuar con Google")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { /* Luego se conecta con Apple */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Continuar con Apple")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text("¿No tienes cuenta? ")
            Text(
                text = "Regístrate",
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    onIrARegistro()
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        errorTexto?.let {
            Text(it, color = Color.Red)
        }
    }
}

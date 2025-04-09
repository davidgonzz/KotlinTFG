package com.davidgonzalez.bodysync.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
    val primaryColor = colorResource(id = R.color.progress_line)

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
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.logo_2),
            contentDescription = "Logo",
            modifier = Modifier
                .size(140.dp)
                .padding(bottom = 8.dp)
        )

        Text(
            text = "¡Bienvenido de nuevo!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Email
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Email") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Email, contentDescription = "Email")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Contraseña
        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Lock, contentDescription = "Contraseña")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "¿Olvidaste tu contraseña?",
                fontSize = 12.sp,
                color = primaryColor,
                modifier = Modifier.clickable { }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = mantenerSesion,
                onCheckedChange = { mantenerSesion = it },
                colors = CheckboxDefaults.colors(checkedColor = primaryColor)
            )
            Text(text = "Mantener sesión iniciada")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón principal
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
                .height(52.dp),
            shape = MaterialTheme.shapes.extraLarge,
            colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
        ) {
            Text("Iniciar sesión", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Línea con la "o" en el centro
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Divider(
                modifier = Modifier.weight(1f),
                thickness = 1.dp,
                color = Color.LightGray
            )
            Text(
                text = "  o  ",
                color = Color.Gray,
                fontSize = 14.sp
            )
            Divider(
                modifier = Modifier.weight(1f),
                thickness = 1.dp,
                color = Color.LightGray
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón Google
        OutlinedButton(
            onClick = { /* TODO: Login con Google */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.extraLarge,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.Black // letras negras
            )
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon_google),
                contentDescription = "Google",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text("Continuar con Google")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón Apple
        OutlinedButton(
            onClick = { /* TODO: Login con Apple */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.extraLarge,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.Black // letras negras
            )
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon_apple),
                contentDescription = "Apple",
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text("Continuar con Apple")
        }


        Spacer(modifier = Modifier.height(24.dp))

        Row {
            Text("¿No tienes cuenta? ")
            Text(
                text = "Regístrate",
                color = primaryColor,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { onIrARegistro() }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        errorTexto?.let {
            Text(it, color = Color.Red)
        }
    }
}

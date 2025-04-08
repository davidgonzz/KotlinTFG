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
import com.davidgonzalez.bodysync.viewmodel.AuthViewModel
import com.davidgonzalez.bodysync.R

@Composable
fun RegistrationScreen(
    onRegistroExitoso: () -> Unit,
    onIrALogin: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }
    var aceptarTerminos by remember { mutableStateOf(false) }
    var errorTexto by remember { mutableStateOf<String?>(null) }

    val estadoRegistro by viewModel.estadoRegistro.collectAsState()

    LaunchedEffect(estadoRegistro) {
        estadoRegistro.exceptionOrNull()?.let {
            errorTexto = it.message
        }

        if (estadoRegistro.isSuccess && estadoRegistro.getOrDefault(false)) {
            onRegistroExitoso()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo arriba
        Image(
            painter = painterResource(id = R.drawable.logo_2), // Usa tu logo aquí
            contentDescription = "Logo BodySync",
            modifier = Modifier
                .size(160.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = "Crea tu cuenta",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre completo") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico") },
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
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = confirmarContrasena,
            onValueChange = { confirmarContrasena = it },
            label = { Text("Confirmar contraseña") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        if (contrasena != confirmarContrasena) {
            Text(
                "Las contraseñas no coinciden",
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (nombre.isBlank() || correo.isBlank() || contrasena.isBlank() || confirmarContrasena.isBlank()) {
                    errorTexto = "Por favor, completa todos los campos."
                } else if (contrasena != confirmarContrasena) {
                    errorTexto = "Las contraseñas no coinciden."
                } else if (!aceptarTerminos) {
                    errorTexto = "Debes aceptar los Términos."
                } else {
                    errorTexto = null
                    viewModel.registrarUsuario(
                        nombre,
                        correo,
                        contrasena,
                        aceptarTerminos
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Registrarse")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Línea con la "o"
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Divider(
                color = Color.Gray,
                thickness = 1.dp,
                modifier = Modifier.weight(1f)
            )
            Text(
                "  o  ",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Divider(
                color = Color.Gray,
                thickness = 1.dp,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón ficticio de Google
        OutlinedButton(
            onClick = { /* luego lo conectamos */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Email, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Registrarse con Google")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón ficticio de Apple
        OutlinedButton(
            onClick = { /* luego lo conectamos */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Registrarse con Apple")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = aceptarTerminos,
                onCheckedChange = { aceptarTerminos = it }
            )
            Text(
                text = "Acepto los ",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Términos y Condiciones",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp,
                textDecoration = TextDecoration.Underline
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Error de validación
        errorTexto?.let {
            Text(it, color = Color.Red)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Ya tienes cuenta
        Text(
            text = "¿Ya tienes una cuenta? Inicia sesión",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable {
                onIrALogin()
            }
        )
    }
}

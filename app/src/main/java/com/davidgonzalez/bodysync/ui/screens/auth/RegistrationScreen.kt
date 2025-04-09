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
        Image(
            painter = painterResource(id = R.drawable.logo_2),
            contentDescription = "Logo BodySync",
            modifier = Modifier.size(160.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Crea tu cuenta",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre completo") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
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
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
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
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        )

        if (contrasena != confirmarContrasena) {
            Text(
                text = "Las contraseñas no coinciden",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = aceptarTerminos,
                onCheckedChange = { aceptarTerminos = it },
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2C5704))
            )
            Row {
                Text(
                    text = "Acepto los ",
                    fontSize = 12.sp
                )
                Text(
                    text = "Términos y Condiciones",
                    fontSize = 12.sp,
                    color = Color(0xFF2C5704),
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        // Aquí puedes abrir un diálogo o web con los términos
                    }
                )
            }
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
                .height(52.dp),
            shape = MaterialTheme.shapes.extraLarge,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C5704))
        ) {
            Text("Registrarse")
        }

        Spacer(modifier = Modifier.height(24.dp))

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

        OutlinedButton(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.extraLarge,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon_google),
                contentDescription = "Google",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Registrarse con Google")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.extraLarge,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon_apple),
                contentDescription = "Apple",
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Registrarse con Apple")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text("¿Ya tienes una cuenta? ")
            Text(
                text = "Inicia sesión",
                color = Color(0xFF2C5704),
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    onIrALogin()
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        errorTexto?.let {
            Text(it, color = Color.Red)
        }
    }
}

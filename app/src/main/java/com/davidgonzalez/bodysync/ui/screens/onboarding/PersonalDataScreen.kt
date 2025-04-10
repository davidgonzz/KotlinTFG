package com.davidgonzalez.bodysync.ui.screens.onboarding

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.davidgonzalez.bodysync.R
import com.davidgonzalez.bodysync.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalDataScreen(
    onContinuar: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val primaryColor = colorResource(id = R.color.progress_line)
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val opcionesObjetivo = listOf("Adelgazar", "Engordar", "Mantenerse")

    var altura by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("") }
    var objetivo by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Cuéntanos sobre ti", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Usaremos estos datos para adaptar tus planes a ti.", fontSize = 14.sp)

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = altura,
            onValueChange = { altura = it },
            label = { Text("Altura (cm)") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF2C5704),
                cursorColor = Color(0xFF2C5704)
            )
        )


        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = peso,
            onValueChange = { peso = it },
            label = { Text("Peso (kg)") },
            leadingIcon = { Icon(Icons.Default.Build, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF2C5704),
                cursorColor = Color(0xFF2C5704)

            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = genero == "Masculino",
                    onClick = { genero = "Masculino" },
                    colors = RadioButtonDefaults.colors(selectedColor = primaryColor)
                )
                Text("Masculino")
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = genero == "Femenino",
                    onClick = { genero = "Femenino" },
                    colors = RadioButtonDefaults.colors(selectedColor = primaryColor)
                )
                Text("Femenino")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            OutlinedTextField(
                value = objetivo,
                onValueChange = {},
                readOnly = true,
                label = { Text("Objetivo") },
                leadingIcon = { Icon(Icons.Default.Star, contentDescription = null) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
                    .height(64.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF2C5704),
                    cursorColor = Color(0xFF2C5704)
                )
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                opcionesObjetivo.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            objetivo = opcion
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (altura.isBlank() || peso.isBlank() || genero.isBlank() || objetivo.isBlank()) {
                    Toast.makeText(context, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.guardarDatosFisicos(
                        altura = altura,
                        peso = peso,
                        genero = genero,
                        objetivo = objetivo,
                        onSuccess = {
                            Toast.makeText(context, "Datos guardados", Toast.LENGTH_SHORT).show()
                            onContinuar()
                        },
                        onError = {
                            Toast.makeText(context, "Error: $it", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = MaterialTheme.shapes.extraLarge,
            colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
        ) {
            Text("Continuar", fontSize = 16.sp)
        }
    }
}


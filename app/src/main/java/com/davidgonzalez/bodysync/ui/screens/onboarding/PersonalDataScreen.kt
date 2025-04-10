package com.davidgonzalez.bodysync.ui.screens.onboarding

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
    var showDialog by remember { mutableStateOf(false) }

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
        Text(
            text = "Cuéntanos sobre ti",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.progress_line) // ✅ tu verde habitual
        )
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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .border(
                    width = 1.dp,
                    color = Color(0xFF2C5704),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color.Gray
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = if (objetivo.isNotBlank()) objetivo else "Objetivo",
                    color = if (objetivo.isNotBlank()) Color.Black else Color.Gray,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = { showDialog = true },
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Seleccionar objetivo",
                        tint = Color.Gray
                    )
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = { showDialog = false }
                    ) {
                        Text("Aceptar", color = Color(0xFF2C5704))
                    }
                },
                title = {
                    Text(
                        "Selecciona tu objetivo",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                containerColor = Color(0xFFE9F5E5), // verde claro
                text = {
                    Column {
                        val opcionesObjetivo = listOf("Adelgazar", "Engordar", "Mantenerse")
                        opcionesObjetivo.forEach { opcion ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        objetivo = opcion
                                    }
                            ) {
                                Checkbox(
                                    checked = objetivo == opcion,
                                    onCheckedChange = { seleccionado ->
                                        if (seleccionado) objetivo = opcion
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2C5704))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(opcion, fontSize = 16.sp)
                            }
                        }
                    }
                }
            )
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


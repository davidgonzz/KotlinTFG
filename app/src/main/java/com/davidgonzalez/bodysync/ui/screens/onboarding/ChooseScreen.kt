package com.davidgonzalez.bodysync.ui.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidgonzalez.bodysync.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ChooseScreenUI(
    onElegirNutricion: () -> Unit = {},
    onElegirGimnasio: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val interactionSourceNutricion = remember { MutableInteractionSource() }
    val interactionSourceGimnasio = remember { MutableInteractionSource() }
    val scope = rememberCoroutineScope()

    var colorNutricion by remember { mutableStateOf(Color(0xFFF1F8EE)) }
    var colorGimnasio by remember { mutableStateOf(Color(0xFFE5EEF6)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        Text(
            text = "Elige tu camino",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF2C5704)
        )

        Spacer(modifier = Modifier.height(48.dp))

        //NUTRICIÓN
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .clickable(
                    interactionSource = interactionSourceNutricion,
                    indication = null
                ) {
                    // Cambio de color momentáneo para dar feedback
                    colorNutricion = Color(0xFFCDE6B8) // tono más fuerte
                    scope.launch {
                        delay(150)
                        colorNutricion = Color(0xFFF1F8EE) // vuelve al original
                        onElegirNutricion() // navegación
                    }
                },
            color = colorNutricion,
            shape = RoundedCornerShape(28.dp),
            shadowElevation = 6.dp
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0xFFD9EDD0), shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_nutricion),
                        contentDescription = "Nutrición",
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(24.dp))
                Text(
                    text = "Nutrición",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // GIMNASIO
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .clickable(
                    interactionSource = interactionSourceGimnasio,
                    indication = null
                ) {
                    colorGimnasio = Color(0xFFBCD7ED)
                    scope.launch {
                        delay(150)
                        colorGimnasio = Color(0xFFE5EEF6)
                        onElegirGimnasio()
                    }
                },
            color = colorGimnasio,
            shape = RoundedCornerShape(28.dp),
            shadowElevation = 6.dp
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0xFFBCD7ED), shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_gym),
                        contentDescription = "Gimnasio",
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(24.dp))
                Text(text = "Gimnasio", fontSize = 20.sp, fontWeight = FontWeight.Medium)
            }
        }

        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = "Cerrar sesión",
            color = Color(0xFF2C5704),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.clickable {
                FirebaseAuth.getInstance().signOut()
                onLogout()
            }
        )
    }
}

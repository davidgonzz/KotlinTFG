package com.davidgonzalez.bodysync.ui.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidgonzalez.bodysync.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ChooseScreenUI(
    onElegirNutricion: () -> Unit = {},
    onElegirGimnasio: () -> Unit = {},
    onLogout: () -> Unit = {} // por si luego quieres redirigir a login
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Elige tu camino",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF1F8EE), shape = RoundedCornerShape(24.dp))
                .padding(vertical = 16.dp, horizontal = 20.dp)
                .clickable { onElegirNutricion() }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFFD9EDD0), shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_nutricion),
                        contentDescription = "Nutrición",
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "Nutrición", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE5EEF6), shape = RoundedCornerShape(24.dp))
                .padding(vertical = 16.dp, horizontal = 20.dp)
                .clickable { onElegirGimnasio() }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFFBCD7ED), shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_gym),
                        contentDescription = "Gimnasio",
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "Gimnasio", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Botón Logout tipo link
        Text(
            text = "Cerrar sesión",
            color = Color(0xFF2C5704),
            fontSize = 14.sp,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable {
                FirebaseAuth.getInstance().signOut()
                onLogout()
            }
        )
    }
}

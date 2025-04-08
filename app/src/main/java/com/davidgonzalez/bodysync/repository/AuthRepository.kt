package com.davidgonzalez.bodysync.repository

import com.davidgonzalez.bodysync.ui.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun registrarUsuario(
        nombre: String,
        correo: String,
        contrasena: String,
        terminosAceptados: Boolean,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(correo, contrasena)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener

                val usuario = Usuario(
                    nombreCompleto = nombre,
                    correo = correo,
                    fechaRegistro = Date().toInstant().toString(),
                    terminosAceptados = terminosAceptados
                )

                firestore.collection("usuarios").document(uid)
                    .set(usuario)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e -> onError("Error Firestore: ${e.message}") }
            }
            .addOnFailureListener { e ->
                onError("Error Auth: ${e.message}")
            }
    }
}

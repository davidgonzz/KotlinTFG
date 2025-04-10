package com.davidgonzalez.bodysync.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidgonzalez.bodysync.ui.model.Usuario
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Estado del registro
    private val _estadoRegistro = MutableStateFlow<Result<Boolean>>(Result.success(false))
    val estadoRegistro: StateFlow<Result<Boolean>> = _estadoRegistro

    // Estado del login
    private val _estadoLogin = MutableStateFlow<Result<Boolean>>(Result.success(false))
    val estadoLogin: StateFlow<Result<Boolean>> = _estadoLogin

    // Registro de usuario
    fun registrarUsuario(
        nombre: String,
        apellido: String,
        correo: String,
        contrasena: String,
        terminosAceptados: Boolean,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(correo, contrasena)
                .addOnSuccessListener { result ->
                    val uid = result.user?.uid ?: return@addOnSuccessListener

                    val usuario = Usuario(
                        nombre = nombre,
                        apellidos = apellido,
                        correo = correo,
                        fechaRegistro = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
                        terminosAceptados = terminosAceptados
                    )

                    firestore.collection("usuarios").document(uid).get()
                        .addOnSuccessListener { document ->
                            if (!document.exists()) {
                                firestore.collection("usuarios").document(uid)
                                    .set(usuario)
                                    .addOnSuccessListener {
                                        println("✅ Usuario nuevo guardado en Firestore")
                                        _estadoRegistro.value = Result.success(true)
                                        onSuccess()
                                    }
                                    .addOnFailureListener { e ->
                                        println("❌ Error Firestore al guardar: ${e.message}")
                                        _estadoRegistro.value = Result.failure(e)
                                    }
                            } else {
                                println("⚠️ Usuario ya existía en Firestore")
                                _estadoRegistro.value = Result.success(true)
                                onSuccess()
                            }
                        }
                        .addOnFailureListener { e ->
                            println("❌ Error al comprobar existencia: ${e.message}")
                            _estadoRegistro.value = Result.failure(e)
                        }
                }
                .addOnFailureListener { e ->
                    println("❌ Error Auth: ${e.message}")
                    _estadoRegistro.value = Result.failure(e)
                }
        }
    }

    // Login de usuario
    fun loginUsuario(correo: String, contrasena: String) {
        auth.signInWithEmailAndPassword(correo, contrasena)
            .addOnSuccessListener {
                println("✅ Login correcto")
                _estadoLogin.value = Result.success(true)
            }
            .addOnFailureListener { e ->
                println("❌ Error al iniciar sesión: ${e.message}")
                _estadoLogin.value = Result.failure(e)
            }
    }

    // Recuperación de contraseña
    fun enviarCorreoRecuperacion(
        email: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.message ?: "Error desconocido") }
    }

    // Login con Google
    fun loginConGoogle(
        credential: AuthCredential,
        mantenerSesion: Boolean,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInWithCredential(credential)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener
                firestore.collection("usuarios").document(uid).get()
                    .addOnSuccessListener { document ->
                        if (!document.exists()) {
                            val usuario = Usuario(
                                nombre = result.user?.displayName ?: "",
                                correo = result.user?.email ?: "",
                                fechaRegistro = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
                                terminosAceptados = true,
                                proveedor = "google"
                            )
                            firestore.collection("usuarios").document(uid)
                                .set(usuario)
                        }
                        onSuccess()
                    }
                    .addOnFailureListener { onError("Firestore error: ${it.message}") }
            }
            .addOnFailureListener { e -> onError(e.message ?: "Error desconocido") }
    }

    // Guardar datos físicos
    fun guardarDatosFisicos(
        altura: String,
        peso: String,
        genero: String,
        objetivo: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            val datos = mapOf(
                "altura" to altura,
                "peso" to peso,
                "genero" to genero,
                "objetivo" to objetivo
            )

            firestore.collection("usuarios").document(uid)
                .update(datos)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e -> onError(e.message ?: "Error desconocido") }
        } else {
            onError("Usuario no logueado")
        }
    }
}

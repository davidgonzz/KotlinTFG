package com.davidgonzalez.bodysync.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidgonzalez.bodysync.ui.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

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
        correo: String,
        contrasena: String,
        terminosAceptados: Boolean
    ) {
        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(correo, contrasena)
                .addOnSuccessListener { result ->
                    val uid = result.user?.uid ?: return@addOnSuccessListener

                    val usuario = Usuario(
                        nombreCompleto = nombre,
                        correo = correo,
                        fechaRegistro = Date().toInstant().toString(),
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
                                    }
                                    .addOnFailureListener { e ->
                                        println("❌ Error Firestore al guardar: ${e.message}")
                                        _estadoRegistro.value = Result.failure(e)
                                    }
                            } else {
                                println("⚠️ Usuario ya existía en Firestore")
                                _estadoRegistro.value = Result.success(true)
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
}

package com.davidgonzalez.bodysync.ui.model

data class Usuario(
    val nombreCompleto: String = "",
    val correo: String = "",
    val fechaRegistro: String = "",
    val terminosAceptados: Boolean = false,
    val proveedor: String = "email",
    val rol: String = "cliente"
)

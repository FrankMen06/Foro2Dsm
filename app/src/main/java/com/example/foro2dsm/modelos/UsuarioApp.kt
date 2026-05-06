package com.example.foro2dsm.modelos

data class UsuarioApp(
    val uid: String = "",
    val nombre: String = "",
    val apellido: String = "",
    val email: String = "",
    val telefono: String = "",
    val fotoPerfil: String = "",
    val proveedor: String = "",
    val fechaRegistro: Long = System.currentTimeMillis(),
    val ultimoAcceso: Long = System.currentTimeMillis(),
    val estado: Boolean = true
)
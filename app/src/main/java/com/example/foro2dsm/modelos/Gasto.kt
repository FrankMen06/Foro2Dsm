package com.example.foro2dsm.modelos

data class Gasto(
    val id: String = "",
    val uidUsuario: String = "",
    val nombre: String = "",
    val monto: Double = 0.0,
    val categoria: String = "",
    val fechaMillis: Long = System.currentTimeMillis(),
    val fechaRegistro: Long = System.currentTimeMillis()
)

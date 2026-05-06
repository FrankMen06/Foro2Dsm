package com.example.foro2dsm.pantallas.componentes

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun MensajeDialog(
    titulo: String,
    mensaje: String,
    onCerrar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCerrar,
        title = {
            Text(text = titulo)
        },
        text = {
            Text(text = mensaje)
        },
        confirmButton = {
            Button(onClick = onCerrar) {
                Text("Aceptar")
            }
        }
    )
}
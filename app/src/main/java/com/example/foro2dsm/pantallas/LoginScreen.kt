package com.example.foro2dsm.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foro2dsm.firebase.AuthService
import kotlinx.coroutines.launch
import com.example.foro2dsm.pantallas.componentes.MensajeDialog
import com.example.foro2dsm.utils.traducirErrorFirebase

@Composable
fun LoginScreen(
    onLoginExitoso: () -> Unit,
    onIrRegistro: () -> Unit
) {
    val authService = remember { AuthService() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var cargando by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf("") }
    var mostrarDialogoError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Iniciar sesión",
            fontSize = 28.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null)
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        if (mensajeError.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = mensajeError)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                mensajeError = ""

                if (email.isBlank() || password.isBlank()) {
                    mensajeError = "Correo y contraseña son obligatorios."
                    mostrarDialogoError = true
                    return@Button
                }

                scope.launch {
                    cargando = true

                    val resultado = authService.loginConCorreo(
                        email = email,
                        password = password
                    )

                    cargando = false

                    resultado
                        .onSuccess {
                            onLoginExitoso()
                        }
                        .onFailure {
                            mensajeError = traducirErrorFirebase(it)
                            mostrarDialogoError = true
                        }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !cargando
        ) {
            Text("Iniciar sesión")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                scope.launch {
                    cargando = true

                    val resultado = authService.loginConGoogle(context)

                    cargando = false

                    resultado
                        .onSuccess {
                            onLoginExitoso()
                        }
                        .onFailure {
                            mensajeError = it.message ?: "Error al iniciar con Google."
                        }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !cargando
        ) {
            Text("Continuar con Google")
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onIrRegistro) {
            Text("No tengo cuenta, registrarme")
        }

        if (cargando) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }

        if (mostrarDialogoError) {
            MensajeDialog(
                titulo = "Aviso",
                mensaje = mensajeError,
                onCerrar = {
                    mostrarDialogoError = false
                }
            )
        }
    }
}
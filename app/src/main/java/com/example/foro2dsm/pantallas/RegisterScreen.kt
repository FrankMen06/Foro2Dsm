package com.example.foro2dsm.pantallas

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foro2dsm.firebase.AuthService
import com.example.foro2dsm.pantallas.componentes.MensajeDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onRegistroExitoso: () -> Unit,
    onIrLogin: () -> Unit
) {
    val authService = remember { AuthService() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }

    var cargando by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf("") }
    var mostrarDialogoError by remember { mutableStateOf(false) }

    // 🔥 GOOGLE LAUNCHER
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->

        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)

            scope.launch {
                cargando = true
                val res = authService.loginConGoogleAccount(account)
                cargando = false

                res.onSuccess {
                    onRegistroExitoso()
                }.onFailure {
                    mensajeError = it.message ?: "Error Google"
                    mostrarDialogoError = true
                }
            }

        } catch (e: Exception) {
            mensajeError = e.message ?: "Error Google Sign-In"
            mostrarDialogoError = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Crear cuenta", fontSize = 28.sp)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            leadingIcon = { Icon(Icons.Default.Person, null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo") },
            leadingIcon = { Icon(Icons.Default.Email, null) },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmarPassword,
            onValueChange = { confirmarPassword = it },
            label = { Text("Confirmar contraseña") },
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // -------------------------
        // REGISTRO NORMAL
        // -------------------------
        Button(
            onClick = {
                mensajeError = ""

                if (nombre.isBlank() || email.isBlank() || password.isBlank()) {
                    mensajeError = "Completa todos los campos"
                    mostrarDialogoError = true
                    return@Button
                }

                if (password != confirmarPassword) {
                    mensajeError = "Las contraseñas no coinciden"
                    mostrarDialogoError = true
                    return@Button
                }

                scope.launch {
                    cargando = true
                    val res = authService.registrarConCorreo(nombre, email, password)
                    cargando = false

                    res.onSuccess {
                        onRegistroExitoso()
                    }.onFailure {
                        mensajeError = it.message ?: "Error"
                        mostrarDialogoError = true
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !cargando
        ) {
            Text("Registrarme")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // -------------------------
        // GOOGLE LOGIN
        // -------------------------
        OutlinedButton(
            onClick = {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(context.getString(com.example.foro2dsm.R.string.default_web_client_id))
                    .requestEmail()
                    .build()

                val client = GoogleSignIn.getClient(context, gso)

                launcher.launch(client.signInIntent)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !cargando
        ) {
            Text("Registrarme con Google")
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onIrLogin) {
            Text("Ya tengo cuenta")
        }

        if (cargando) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }

        if (mostrarDialogoError) {
            MensajeDialog(
                titulo = "Error",
                mensaje = mensajeError,
                onCerrar = { mostrarDialogoError = false }
            )
        }
    }
}
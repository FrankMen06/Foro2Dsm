package com.example.foro2dsm.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.foro2dsm.firebase.AuthService
import com.example.foro2dsm.modelos.UsuarioApp
import com.example.foro2dsm.pantallas.componentes.MensajeDialog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAgregarGasto: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val authService = remember { AuthService() }
    val usuarioFirebase = authService.usuarioActual()

    var usuarioApp by remember { mutableStateOf<UsuarioApp?>(null) }
    var cargando by remember { mutableStateOf(true) }
    var menuExpandido by remember { mutableStateOf(false) }

    var mostrarDialogo by remember { mutableStateOf(false) }
    var tituloDialogo by remember { mutableStateOf("Aviso") }
    var mensajeDialogo by remember { mutableStateOf("") }

    fun mostrarMensaje(titulo: String, mensaje: String) {
        tituloDialogo = titulo
        mensajeDialogo = mensaje
        mostrarDialogo = true
    }

    LaunchedEffect(usuarioFirebase?.uid) {
        if (usuarioFirebase?.uid != null) {
            try {
                val snapshot = FirebaseFirestore.getInstance()
                    .collection("usuarios")
                    .document(usuarioFirebase.uid)
                    .get()
                    .await()

                usuarioApp = snapshot.toObject(UsuarioApp::class.java)
            } catch (e: Exception) {
                usuarioApp = null
            } finally {
                cargando = false
            }
        } else {
            cargando = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Control de Gastos",
                    fontWeight = FontWeight.Bold
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            actions = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AvatarUsuario(
                        fotoPerfil = usuarioApp?.fotoPerfil.orEmpty()
                    )

                    Box(
                        modifier = Modifier.wrapContentSize(Alignment.TopEnd)
                    ) {
                        IconButton(
                            onClick = { menuExpandido = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Opciones"
                            )
                        }

                        DropdownMenu(
                            expanded = menuExpandido,
                            onDismissRequest = { menuExpandido = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Cerrar sesión") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.ExitToApp,
                                        contentDescription = null
                                    )
                                },
                                onClick = {
                                    menuExpandido = false
                                    authService.cerrarSesion()
                                    onCerrarSesion()
                                }
                            )
                        }
                    }
                }
            }
        )

        if (cargando) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val nombreMostrar = usuarioApp?.nombre
                ?.takeIf { it.isNotBlank() }
                ?: "Usuario"

            val emailMostrar = usuarioApp?.email
                ?.takeIf { it.isNotBlank() }
                ?: usuarioFirebase?.email
                ?: ""

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Bienvenido",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = nombreMostrar,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = emailMostrar,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Opciones",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AccionCard(
                        titulo = "Agregar gasto",
                        subtitulo = "Registrar nuevo gasto",
                        icono = Icons.Default.AddCircleOutline,
                        modifier = Modifier.weight(1f),
                        onClick = onAgregarGasto
                    )

                    AccionCard(
                        titulo = "Historial",
                        subtitulo = "Ver gastos guardados",
                        icono = Icons.Default.ReceiptLong,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            mostrarMensaje(
                                "Próximamente",
                                "La opción de historial de gastos estará disponible más adelante."
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AccionCard(
                        titulo = "Filtrar",
                        subtitulo = "Por categoría o mes",
                        icono = Icons.Default.FilterList,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            mostrarMensaje(
                                "Próximamente",
                                "La opción de filtrar gastos estará disponible más adelante."
                            )
                        }
                    )

                    AccionCard(
                        titulo = "Total mensual",
                        subtitulo = "Ver resumen del mes",
                        icono = Icons.Default.Analytics,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            mostrarMensaje(
                                "Próximamente",
                                "La opción de total mensual estará disponible más adelante."
                            )
                        }
                    )
                }
            }
        }
    }

    if (mostrarDialogo) {
        MensajeDialog(
            titulo = tituloDialogo,
            mensaje = mensajeDialogo,
            onCerrar = {
                mostrarDialogo = false
            }
        )
    }
}

@Composable
fun AvatarUsuario(
    fotoPerfil: String
) {
    if (fotoPerfil.isNotBlank()) {
        AsyncImage(
            model = fotoPerfil,
            contentDescription = "Foto de perfil",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(start = 8.dp)
                .size(40.dp)
                .clip(CircleShape)
        )
    } else {
        Box(
            modifier = Modifier
                .padding(start = 8.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Usuario",
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
fun AccionCard(
    titulo: String,
    subtitulo: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = titulo,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = subtitulo,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
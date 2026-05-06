package com.example.foro2dsm.pantallas

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.foro2dsm.firebase.GastoService
import com.example.foro2dsm.pantallas.componentes.MensajeDialog
import com.example.foro2dsm.utils.traducirErrorFirebase
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarGastoScreen(
    onVolver: () -> Unit,
    onGastoGuardado: () -> Unit
) {
    val gastoService = remember { GastoService() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val categorias = listOf(
        "Alimentación",
        "Transporte",
        "Servicios",
        "Educación",
        "Salud",
        "Entretenimiento",
        "Otros"
    )

    val formatoFecha = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    }

    val calendar = remember { Calendar.getInstance() }

    var nombre by remember { mutableStateOf("") }
    var montoTexto by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf(categorias.first()) }
    var fechaMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }

    var menuCategoriaAbierto by remember { mutableStateOf(false) }
    var cargando by remember { mutableStateOf(false) }

    var mensajeDialogo by remember { mutableStateOf("") }
    var tituloDialogo by remember { mutableStateOf("Aviso") }
    var mostrarDialogo by remember { mutableStateOf(false) }

    fun mostrarMensaje(titulo: String, mensaje: String) {
        tituloDialogo = titulo
        mensajeDialogo = mensaje
        mostrarDialogo = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo gasto") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.Top
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del gasto") },
                leadingIcon = {
                    Icon(Icons.Default.Description, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = montoTexto,
                onValueChange = { montoTexto = it },
                label = { Text("Monto") },
                leadingIcon = {
                    Icon(Icons.Default.AttachMoney, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = menuCategoriaAbierto,
                onExpandedChange = {
                    menuCategoriaAbierto = !menuCategoriaAbierto
                }
            ) {
                OutlinedTextField(
                    value = categoria,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    leadingIcon = {
                        Icon(Icons.Default.Category, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )

                DropdownMenu(
                    expanded = menuCategoriaAbierto,
                    onDismissRequest = { menuCategoriaAbierto = false }
                ) {
                    categorias.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                categoria = item
                                menuCategoriaAbierto = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    val fechaActual = Calendar.getInstance()
                    fechaActual.timeInMillis = fechaMillis

                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            calendar.set(year, month, dayOfMonth)
                            fechaMillis = calendar.timeInMillis
                        },
                        fechaActual.get(Calendar.YEAR),
                        fechaActual.get(Calendar.MONTH),
                        fechaActual.get(Calendar.DAY_OF_MONTH)
                    ).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null
                )
                Text(
                    text = "  Fecha: ${formatoFecha.format(fechaMillis)}"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val monto = montoTexto.replace(",", ".").toDoubleOrNull()

                    if (nombre.isBlank()) {
                        mostrarMensaje("Aviso", "Ingrese el nombre del gasto.")
                        return@Button
                    }

                    if (monto == null || monto <= 0.0) {
                        mostrarMensaje("Aviso", "Ingrese un monto válido.")
                        return@Button
                    }

                    scope.launch {
                        cargando = true

                        val resultado = gastoService.guardarGasto(
                            nombre = nombre.trim(),
                            monto = monto,
                            categoria = categoria,
                            fechaMillis = fechaMillis
                        )

                        cargando = false

                        resultado
                            .onSuccess {
                                mostrarMensaje("Éxito", "El gasto fue guardado correctamente.")
                                nombre = ""
                                montoTexto = ""
                                categoria = categorias.first()
                                fechaMillis = System.currentTimeMillis()
                            }
                            .onFailure {
                                mostrarMensaje("Error", traducirErrorFirebase(it))
                            }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !cargando
            ) {
                Text("Guardar gasto")
            }

            if (cargando) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }
        }
    }

    if (mostrarDialogo) {
        MensajeDialog(
            titulo = tituloDialogo,
            mensaje = mensajeDialogo,
            onCerrar = {
                mostrarDialogo = false

                if (tituloDialogo == "Éxito") {
                    onGastoGuardado()
                }
            }
        )
    }
}
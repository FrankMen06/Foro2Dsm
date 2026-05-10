package com.example.foro2dsm.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.foro2dsm.firebase.GastoService
import com.example.foro2dsm.modelos.Gasto
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltrarGastosScreen(
    onVolver: () -> Unit
) {

    val gastoService = remember { GastoService() }
    val scope = rememberCoroutineScope()

    var gastos by remember { mutableStateOf<List<Gasto>>(emptyList()) }
    var gastosFiltrados by remember { mutableStateOf<List<Gasto>>(emptyList()) }

    var categoriaSeleccionada by remember { mutableStateOf("Todos") }
    var filtroMes by remember { mutableStateOf("") }

    var cargando by remember { mutableStateOf(true) }

    val categorias = listOf(
        "Todos",
        "Alimentación",
        "Transporte",
        "Servicios",
        "Educación",
        "Salud",
        "Entretenimiento",
        "Otros"
    )

    val meses = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )

    val formatoFecha = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    }

    LaunchedEffect(Unit) {
        scope.launch {
            val result = gastoService.obtenerGastos()

            result.onSuccess {
                gastos = it
                gastosFiltrados = it
            }

            cargando = false
        }
    }

    LaunchedEffect(gastos, categoriaSeleccionada, filtroMes) {

        var lista = gastos

        // 📂 FILTRO CATEGORÍA
        if (categoriaSeleccionada != "Todos") {
            lista = lista.filter {
                it.categoria == categoriaSeleccionada
            }
        }

        // 📅 FILTRO MES
        if (filtroMes.isNotBlank()) {
            lista = lista.filter { gasto ->
                val cal = Calendar.getInstance()
                cal.timeInMillis = gasto.fechaMillis

                val mes = cal.get(Calendar.MONTH) + 1
                val anio = cal.get(Calendar.YEAR)

                "%04d-%02d".format(anio, mes) == filtroMes
            }
        }

        gastosFiltrados = lista
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Filtrar gastos") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            // 📂 SELECTOR CATEGORÍA
            var expandedCat by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expandedCat,
                onExpandedChange = { expandedCat = !expandedCat }
            ) {

                OutlinedTextField(
                    value = categoriaSeleccionada,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCat)
                    }
                )

                ExposedDropdownMenu(
                    expanded = expandedCat,
                    onDismissRequest = { expandedCat = false }
                ) {

                    categorias.forEach {

                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                categoriaSeleccionada = it
                                expandedCat = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 📅 SELECTOR MES
            var expandedMes by remember { mutableStateOf(false) }

            val calendar = Calendar.getInstance()
            val yearActual = calendar.get(Calendar.YEAR)

            ExposedDropdownMenuBox(
                expanded = expandedMes,
                onExpandedChange = { expandedMes = !expandedMes }
            ) {

                OutlinedTextField(
                    value = if (filtroMes.isBlank()) "Seleccionar mes"
                    else {
                        val parts = filtroMes.split("-")
                        val mesIndex = parts[1].toInt() - 1
                        "${meses[mesIndex]} ${parts[0]}"
                    },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Mes") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMes)
                    }
                )

                ExposedDropdownMenu(
                    expanded = expandedMes,
                    onDismissRequest = { expandedMes = false }
                ) {

                    meses.forEachIndexed { index, mes ->

                        DropdownMenuItem(
                            text = { Text(mes) },
                            onClick = {
                                filtroMes = "%04d-%02d".format(yearActual, index + 1)
                                expandedMes = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 📋 LISTA
            if (cargando) {
                CircularProgressIndicator()
            } else {

                LazyColumn {

                    items(gastosFiltrados) { gasto ->

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                        ) {

                            Column(modifier = Modifier.padding(16.dp)) {

                                Text(gasto.nombre, style = MaterialTheme.typography.titleMedium)
                                Text("💰 $${gasto.monto}")
                                Text("📂 ${gasto.categoria}")
                                Text("📅 ${formatoFecha.format(Date(gasto.fechaMillis))}")
                            }
                        }
                    }
                }
            }
        }
    }
}
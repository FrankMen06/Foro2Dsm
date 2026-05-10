package com.example.foro2dsm.pantallas


import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TotalMensualScreen(
    onVolver: () -> Unit
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val service = remember { GastoService() }

    var gastos by remember { mutableStateOf<List<Gasto>>(emptyList()) }
    var gastosMes by remember { mutableStateOf<List<Gasto>>(emptyList()) }

    var mesSeleccionado by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(true) }

    val categorias = listOf(
        "Alimentación",
        "Transporte",
        "Servicios",
        "Educación",
        "Salud",
        "Entretenimiento",
        "Otros"
    )

    // 🔵 Cargar datos
    LaunchedEffect(Unit) {
        scope.launch {
            val result = service.obtenerGastos()

            result.onSuccess {
                gastos = it
            }

            cargando = false
        }
    }

    // 🔎 FILTRO POR MES
    fun filtrar() {

        if (mesSeleccionado.isBlank()) {
            gastosMes = emptyList()
            return
        }

        gastosMes = gastos.filter { gasto ->

            val cal = Calendar.getInstance()
            cal.timeInMillis = gasto.fechaMillis

            val mes = cal.get(Calendar.MONTH) + 1
            val anio = cal.get(Calendar.YEAR)

            "%04d-%02d".format(anio, mes) == mesSeleccionado
        }
    }

    // 📊 TOTAL GENERAL
    val total = gastosMes.sumOf { it.monto }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Total mensual") },
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

            // 📅 Selector de mes
            val calendar = Calendar.getInstance()

            OutlinedButton(
                onClick = {
                    DatePickerDialog(
                        context,
                        { _, year, month, _ ->
                            mesSeleccionado = "%04d-%02d".format(year, month + 1)
                            filtrar()
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (mesSeleccionado.isBlank())
                        "Seleccionar mes"
                    else
                        "Mes: $mesSeleccionado"
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (cargando) {
                CircularProgressIndicator()
                return@Column
            }

            // 💰 TOTAL GENERAL
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Total gastado",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "$${"%.2f".format(total)}",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Por categoría",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn {

                categorias.forEach { categoria ->

                    val suma = gastosMes
                        .filter { it.categoria == categoria }
                        .sumOf { it.monto }

                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(categoria)
                                Text("💰 $${"%.2f".format(suma)}")
                            }
                        }
                    }
                }
            }
        }
    }
}
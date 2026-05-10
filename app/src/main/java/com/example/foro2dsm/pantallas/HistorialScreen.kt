package com.example.foro2dsm.pantallas

//package com.example.foro2dsm.pantallas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.foro2dsm.firebase.GastoService
import com.example.foro2dsm.modelos.Gasto
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    onVolver: () -> Unit
) {

    val gastoService = remember { GastoService() }
    val scope = rememberCoroutineScope()

    var gastos by remember { mutableStateOf<List<Gasto>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf("") }

    val formatoFecha = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    }

    // 🔵 Cargar datos al abrir pantalla
    LaunchedEffect(Unit) {
        scope.launch {
            val resultado = gastoService.obtenerGastos()

            resultado
                .onSuccess {
                    gastos = it
                }
                .onFailure {
                    error = it.message ?: "Error desconocido"
                }

            cargando = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de gastos") },
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
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            when {

                // ⏳ Loading
                cargando -> {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(24.dp)
                    )
                }

                // ❌ Error
                error.isNotEmpty() -> {
                    Text(
                        text = error,
                        modifier = Modifier.padding(24.dp)
                    )
                }

                // 📋 Lista
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {

                        items(gastos) { gasto ->

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                            ) {

                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {

                                    Text(
                                        text = gasto.nombre,
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text("💰 $${gasto.monto}")

                                    Text("📂 ${gasto.categoria}")

                                    Text(
                                        "📅 ${
                                            formatoFecha.format(Date(gasto.fechaMillis))
                                        }"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
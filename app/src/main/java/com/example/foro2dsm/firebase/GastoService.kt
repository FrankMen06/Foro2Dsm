package com.example.foro2dsm.firebase

import com.example.foro2dsm.modelos.Gasto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class GastoService {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun guardarGasto(
        nombre: String,
        monto: Double,
        categoria: String,
        fechaMillis: Long
    ): Result<Unit> {
        return try {
            val user = auth.currentUser
                ?: return Result.failure(Exception("No hay usuario autenticado."))

            val gastoRef = db.collection("usuarios")
                .document(user.uid)
                .collection("gastos")
                .document()

            val gasto = Gasto(
                id = gastoRef.id,
                uidUsuario = user.uid,
                nombre = nombre,
                monto = monto,
                categoria = categoria,
                fechaMillis = fechaMillis,
                fechaRegistro = System.currentTimeMillis()
            )

            gastoRef.set(gasto).await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
package com.example.foro2dsm.firebase

import com.example.foro2dsm.modelos.UsuarioApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.tasks.await

class AuthService {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun usuarioActual() = auth.currentUser

    fun cerrarSesion() {
        auth.signOut()
    }

    // -------------------------
    // REGISTRO CON CORREO
    // -------------------------
    suspend fun registrarConCorreo(
        nombre: String,
        email: String,
        password: String
    ): Result<UsuarioApp> {
        return try {

            val resultado = auth.createUserWithEmailAndPassword(email, password).await()

            val user = resultado.user
                ?: return Result.failure(Exception("No se pudo obtener el usuario."))

            val usuarioApp = UsuarioApp(
                uid = user.uid,
                nombre = nombre,
                apellido = "",
                email = email,
                telefono = "",
                fotoPerfil = "",
                proveedor = "correo",
                fechaRegistro = System.currentTimeMillis(),
                ultimoAcceso = System.currentTimeMillis(),
                estado = true
            )

            db.collection("usuarios")
                .document(user.uid)
                .set(usuarioApp)
                .await()

            Result.success(usuarioApp)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // -------------------------
    // LOGIN CON CORREO
    // -------------------------
    suspend fun loginConCorreo(
        email: String,
        password: String
    ): Result<UsuarioApp> {
        return try {

            val resultado = auth.signInWithEmailAndPassword(email, password).await()

            val user = resultado.user
                ?: return Result.failure(Exception("No se pudo obtener el usuario."))

            val usuarioApp = UsuarioApp(
                uid = user.uid,
                nombre = user.displayName ?: "",
                apellido = "",
                email = user.email ?: "",
                telefono = "",
                fotoPerfil = user.photoUrl?.toString() ?: "",
                proveedor = "correo",
                fechaRegistro = System.currentTimeMillis(),
                ultimoAcceso = System.currentTimeMillis(),
                estado = true
            )

            db.collection("usuarios")
                .document(user.uid)
                .update("ultimoAcceso", System.currentTimeMillis())
                .await()

            Result.success(usuarioApp)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // -------------------------
    // LOGIN CON GOOGLE (FIREBASE FINAL)
    // -------------------------
    suspend fun loginConGoogleAccount(
        account: GoogleSignInAccount
    ): Result<UsuarioApp> {
        return try {

            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            val authResult = auth.signInWithCredential(credential).await()

            val user = authResult.user
                ?: return Result.failure(Exception("No se pudo iniciar sesión con Google."))

            val usuarioApp = UsuarioApp(
                uid = user.uid,
                nombre = user.displayName ?: "",
                apellido = "",
                email = user.email ?: "",
                telefono = "",
                fotoPerfil = user.photoUrl?.toString() ?: "",
                proveedor = "google",
                fechaRegistro = System.currentTimeMillis(),
                ultimoAcceso = System.currentTimeMillis(),
                estado = true
            )

            db.collection("usuarios")
                .document(user.uid)
                .set(usuarioApp)
                .await()

            Result.success(usuarioApp)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
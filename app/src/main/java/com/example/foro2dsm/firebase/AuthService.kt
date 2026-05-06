package com.example.foro2dsm.firebase

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.example.foro2dsm.R
import com.example.foro2dsm.modelos.UsuarioApp
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthService {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun usuarioActual() = auth.currentUser

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

    suspend fun loginConGoogle(context: Context): Result<UsuarioApp> {
        return try {
            val credentialManager = CredentialManager.create(context)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                context = context,
                request = request
            )

            val credential = result.credential

            val googleIdTokenCredential = GoogleIdTokenCredential
                .createFrom(credential.data)

            val firebaseCredential = GoogleAuthProvider.getCredential(
                googleIdTokenCredential.idToken,
                null
            )

            val authResult = auth.signInWithCredential(firebaseCredential).await()

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

    fun cerrarSesion() {
        auth.signOut()
    }
}
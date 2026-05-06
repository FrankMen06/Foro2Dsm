package com.example.foro2dsm.utils

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestoreException

fun traducirErrorFirebase(error: Throwable): String {
    return when (error) {
        is FirebaseNetworkException -> {
            "No se pudo conectar con Firebase. Revisa tu conexión a internet e intenta nuevamente."
        }

        is FirebaseAuthUserCollisionException -> {
            "Ya existe una cuenta registrada con este correo electrónico."
        }

        is FirebaseAuthInvalidCredentialsException -> {
            "El correo o la contraseña no son válidos. Revisa los datos ingresados."
        }

        is FirebaseAuthInvalidUserException -> {
            "No existe una cuenta registrada con este correo electrónico."
        }

        is FirebaseFirestoreException -> {
            "No se pudo guardar la información en la base de datos. Intenta nuevamente."
        }

        is FirebaseAuthException -> {
            when (error.errorCode) {
                "ERROR_EMAIL_ALREADY_IN_USE" -> "Este correo electrónico ya está registrado."
                "ERROR_INVALID_EMAIL" -> "El correo electrónico ingresado no es válido."
                "ERROR_WEAK_PASSWORD" -> "La contraseña es muy débil. Debe tener al menos 6 caracteres."
                "ERROR_WRONG_PASSWORD" -> "La contraseña ingresada es incorrecta."
                "ERROR_USER_NOT_FOUND" -> "No existe una cuenta con este correo electrónico."
                "ERROR_USER_DISABLED" -> "Esta cuenta ha sido deshabilitada."
                "ERROR_TOO_MANY_REQUESTS" -> "Se realizaron demasiados intentos. Intenta nuevamente más tarde."
                "ERROR_NETWORK_REQUEST_FAILED" -> "Error de conexión. Revisa tu internet e intenta nuevamente."
                else -> "Ocurrió un error al autenticar el usuario. Intenta nuevamente."
            }
        }

        else -> {
            val mensaje = error.message.orEmpty()

            when {
                mensaje.contains("network", ignoreCase = true) ->
                    "Error de conexión. Revisa tu internet e intenta nuevamente."

                mensaje.contains("timeout", ignoreCase = true) ->
                    "La conexión tardó demasiado. Intenta nuevamente."

                mensaje.contains("password", ignoreCase = true) ->
                    "La contraseña no es válida o es demasiado débil."

                mensaje.contains("email", ignoreCase = true) ->
                    "El correo electrónico ingresado no es válido."

                else ->
                    "Ocurrió un error inesperado. Intenta nuevamente."
            }
        }
    }
}
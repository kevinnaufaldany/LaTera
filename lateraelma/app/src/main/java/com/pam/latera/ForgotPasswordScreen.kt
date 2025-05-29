package com.pam.latera

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun ForgotPasswordScreen(
    onBackToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val auth = Firebase.auth

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0F7FA))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Lupa Kata Sandi", fontSize = 24.sp, color = Color.Black)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = null
                message = null
            },
            label = { Text("Masukkan email Anda") },
            isError = emailError != null,
            modifier = Modifier.fillMaxWidth()
        )

        emailError?.let {
            Text(it, color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isBlank()) {
                    emailError = "Email tidak boleh kosong"
                    return@Button
                } else if (!email.endsWith("@student.itera.ac.id")) {
                    emailError = "Gunakan email mahasiswa ITERA"
                    return@Button
                }

                isLoading = true
                // Check if email is registered
                auth.fetchSignInMethodsForEmail(email)
                    .addOnSuccessListener { result ->
                        val signInMethods = result.signInMethods ?: emptyList()
                        if (signInMethods.isEmpty()) {
                            emailError = "Email tidak terdaftar"
                            isLoading = false
                        } else {
                            // Email is registered, proceed with password reset
                            auth.sendPasswordResetEmail(email)
                                .addOnSuccessListener {
                                    message = "Link reset kata sandi telah dikirim ke $email"
                                    isLoading = false
                                }
                                .addOnFailureListener {
                                    emailError = "Gagal mengirim email: ${it.message}"
                                    isLoading = false
                                }
                        }
                    }
                    .addOnFailureListener {
                        emailError = "Gagal memeriksa email: ${it.message}"
                        isLoading = false
                    }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLoading) "Mengirim..." else "Kirim Link Reset")
        }

        Spacer(modifier = Modifier.height(12.dp))

        message?.let {
            Text(it, color = Color.Green, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBackToLogin) {
            Text("Kembali ke Login")
        }
    }
}
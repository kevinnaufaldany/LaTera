package com.pam.latera

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

// ga jadi pake
@Composable
fun ResetPasswordScreen(
    oobCode: String?,
    onResetSuccess: () -> Unit,
    onResetFailure: (String) -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Reset Password",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = newPassword,
            onValueChange = {
                newPassword = it
                errorMessage = null
            },
            label = { Text("Password Baru") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                errorMessage = null
            },
            label = { Text("Konfirmasi Password Baru") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        errorMessage?.let {
            Text(text = it, color = Color.Red, modifier = Modifier.padding(bottom = 12.dp))
        }

        Button(
            onClick = {
                if (oobCode.isNullOrBlank()) {
                    errorMessage = "Kode reset password tidak valid."
                    return@Button
                }
                if (newPassword.isBlank() || confirmPassword.isBlank()) {
                    errorMessage = "Semua kolom harus diisi."
                    return@Button
                }
                if (newPassword != confirmPassword) {
                    errorMessage = "Password tidak sama."
                    return@Button
                }
                if (newPassword.length < 6) {
                    errorMessage = "Password minimal 6 karakter."
                    return@Button
                }

                isLoading = true
                Firebase.auth.confirmPasswordReset(oobCode, newPassword)
                    .addOnCompleteListener { task ->
                        isLoading = false
                        if (task.isSuccessful) {
                            onResetSuccess()
                        } else {
                            onResetFailure(task.exception?.localizedMessage ?: "Gagal reset password")
                        }
                    }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Memproses..." else "Reset Password")
        }
    }
}

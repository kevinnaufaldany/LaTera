package com.pam.latera

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun RegisterScreen(
    onRegisterSuccess: (String, String) -> Unit,
    onBackToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var nim by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var nimError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    var generalError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val auth = Firebase.auth
    val db = Firebase.firestore

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0F7FA))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Registrasi", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                nameError = null
            },
            label = { Text("Nama Lengkap") },
            isError = nameError != null,
            modifier = Modifier.fillMaxWidth()
        )
        nameError?.let {
            Text(it, color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = nim,
            onValueChange = {
                nim = it
                nimError = null
            },
            label = { Text("NIM") },
            isError = nimError != null,
            modifier = Modifier.fillMaxWidth()
        )
        nimError?.let {
            Text(it, color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = null
            },
            label = { Text("Email") },
            isError = emailError != null,
            modifier = Modifier.fillMaxWidth()
        )
        emailError?.let {
            Text(it, color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = null
            },
            label = { Text("Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else
                    Icons.Filled.VisibilityOff

                val description = if (passwordVisible) "Sembunyikan password" else "Tampilkan password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            },
            isError = passwordError != null,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
        )
        passwordError?.let {
            Text(it, color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        generalError?.let {
            Text(text = it, color = Color.Red)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                // Reset all errors
                nameError = null
                nimError = null
                emailError = null
                passwordError = null
                generalError = null

                var isValid = true

                // Email validation
                if (email.isBlank()) {
                    emailError = "Email tidak boleh kosong"
                    isValid = false
                } else if (!email.endsWith("@student.itera.ac.id")) {
                    emailError = "Email harus berakhiran @student.itera.ac.id"
                    isValid = false
                }

                // Name validation
                if (name.isBlank()) {
                    nameError = "Nama lengkap tidak boleh kosong"
                    isValid = false
                } else if (name.length < 3) {
                    nameError = "Nama lengkap minimal 3 karakter"
                    isValid = false
                }

                // NIM validation
                if (nim.isBlank()) {
                    nimError = "NIM tidak boleh kosong"
                    isValid = false
                } else if (nim.length != 9 || !nim.matches(Regex("\\d+"))) {
                    nimError = "NIM harus 9 digit angka"
                    isValid = false
                } else if (nim[3] != '1' || nim[4] != '4') {
                    nimError = "Anda bukan Mahasiswa Informatika ITERA"
                    isValid = false
                }

                // Password validation
                if (password.isBlank()) {
                    passwordError = "Password tidak boleh kosong"
                    isValid = false
                } else if (password.length < 6) {
                    passwordError = "Password minimal 6 karakter"
                    isValid = false
                }

                if (!isValid) return@Button

                isLoading = true
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val uid = auth.currentUser?.uid ?: ""
                            val user = hashMapOf(
                                "name" to name,
                                "nim" to nim,
                                "email" to email
                            )
                            db.collection("users").document(uid).set(user)
                                .addOnSuccessListener {
                                    Log.d("REGISTER", "User data saved to Firestore")
                                    isLoading = false
                                    onRegisterSuccess(name, nim)
                                }
                                .addOnFailureListener {
                                    isLoading = false
                                    generalError = "Gagal menyimpan data: ${it.message}"
                                }
                        } else {
                            isLoading = false
                            generalError = "Registrasi gagal: ${task.exception?.message}"
                        }
                    }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Mendaftarkan..." else "Daftar")
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onBackToLogin) {
            Text("Sudah punya akun? Login")
        }
    }
}

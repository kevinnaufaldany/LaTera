package com.pam.latera

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {

    private var resetPasswordOobCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Tangkap parameter oobCode dari deep link jika ada
        resetPasswordOobCode = intent?.data?.getQueryParameter("oobCode")

        val auth = Firebase.auth
        val currentUser = auth.currentUser
        val isLoggedIn = currentUser != null
        val email = currentUser?.email ?: ""
        val nama = email.substringBefore("@").replaceFirstChar { it.uppercase() }
        val nim = ""
        setContent {
            AppContent(
                startScreen = when {
                    resetPasswordOobCode != null -> "reset_password"
                    isLoggedIn -> "home"
                    else -> "onboarding"
                },
                resetOobCode = resetPasswordOobCode,
                defaultNama = if (isLoggedIn) nama else "",
                defaultNim = if (isLoggedIn) nim else ""
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val newOobCode = intent.data?.getQueryParameter("oobCode")
        if (newOobCode != null) {
            resetPasswordOobCode = newOobCode
            // Bisa implementasikan mekanisme state management agar navigasi ke reset_password screen
            // Contohnya lewat shared ViewModel atau event bus
        }
    }
}

@Composable
fun AppContent(
    startScreen: String = "onboarding",
    resetOobCode: String? = null,  // *** Pastikan parameter ini ada dan dipakai ***
    defaultNama: String = "",
    defaultNim: String = ""
) {
    var currentScreen by remember { mutableStateOf(startScreen) }
    var oobCode by remember { mutableStateOf(resetOobCode) }
    var namaUser by remember { mutableStateOf(defaultNama) }
    var nimUser by remember { mutableStateOf(defaultNim) }

    LaunchedEffect(Unit) {
        val user = Firebase.auth.currentUser
        val uid = user?.uid
        if (uid != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    namaUser = document.getString("name") ?: ""
                    nimUser = document.getString("nim") ?: ""
                }
        }
    }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            when (currentScreen) {
                "onboarding" -> OnboardingScreen(onFinish = { currentScreen = "login" })

                "login" -> LoginScreen(
                    onLoginSuccess = {
                        val user = Firebase.auth.currentUser
                        val uid = user?.uid
                        if (uid != null) {
                            val db = FirebaseFirestore.getInstance()
                            db.collection("users").document(uid).get()
                                .addOnSuccessListener { document ->
                                    namaUser = document.getString("name") ?: ""
                                    nimUser = document.getString("nim") ?: ""
                                    currentScreen = "home"
                                }
                        }
                    },
                    onNavigateToRegister = { currentScreen = "register" },
                    onNavigateToForgotPassword = { currentScreen = "forgot_password" }
                )

                "register" -> RegisterScreen(
                    onRegisterSuccess = { nama, nim ->
                        namaUser = nama
                        nimUser = nim
                        currentScreen = "login"
                    },
                    onBackToLogin = { currentScreen = "login" }
                )

                "forgot_password" -> ForgotPasswordScreen(
                    onBackToLogin = { currentScreen = "login" }
                )

                "reset_password" -> ResetPasswordScreen(
                    oobCode = oobCode,
                    onResetSuccess = {
                        currentScreen = "login"
                    },
                    onResetFailure = { errorMsg ->
                        // Bisa tampilkan error di UI jika mau, saat ini langsung ke login
                        currentScreen = "login"
                    }
                )

                "home" -> HomeScreen(
                    onNavigateToAkademik = { currentScreen = "akademik" },
                    onNavigateToInformatika = { currentScreen = "informatika" },
                    onNavigateToFTI = { currentScreen = "fti" },
                    onNavigateToKuliah = { currentScreen = "kuliah" },
                    onNavigateToChatBot = { currentScreen = "chatbot" },
                    onLogout = {
                        Firebase.auth.signOut()
                        currentScreen = "login"
                    },
                    nama = namaUser,
                    nim = nimUser
                )

                "akademik" -> SecondScreen(onBack = { currentScreen = "home" })
                "informatika" -> LayananMahasiswaScreen(onBack = { currentScreen = "home" })
                "fti" -> LayananFTIScreen(onBack = { currentScreen = "home" })
                "kuliah" -> JadwalKuliahApp(onBackToHome = { currentScreen = "home" })
                "chatbot" -> ChatBotScreen(onBack = { currentScreen = "home" })
            }
        }
    }
}


@Composable
fun HomeScreen(
    onNavigateToAkademik: () -> Unit,
    onNavigateToInformatika: () -> Unit,
    onNavigateToFTI: () -> Unit,
    onNavigateToKuliah: () -> Unit,
    onNavigateToChatBot: () -> Unit, // ✅ Tambahkan ini
    onLogout: () -> Unit,
    nama: String,
    nim: String
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0F7FA))
            .padding(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_logout),
                    contentDescription = "Logout",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { showLogoutDialog = true }
                )
            }
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Konfirmasi Logout") },
                text = { Text("Apakah kamu yakin ingin logout?") },
                confirmButton = {
                    TextButton(onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }) {
                        Text("Ya")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }

        HeaderSection(nama = nama, nim = nim)
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 12.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFD0E8F2))
                .padding(18.dp)
        ) {
            Text(
                text = "Aplikasi LaTera adalah aplikasi Smart Campus Assistant yang dirancang khusus untuk mahasiswa program studi Teknik Informatika Institut Teknologi Sumatera (ITERA).",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Menu Aplikasi",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        MenuSection(
            onNavigateToAkademik = onNavigateToAkademik,
            onNavigateToInformatika = onNavigateToInformatika,
            onNavigateToFTI = onNavigateToFTI,
            onNavigateToKuliah = onNavigateToKuliah
        )

        Spacer(modifier = Modifier.weight(1f))

        ChatBotSection(onNavigateToChatBot = onNavigateToChatBot) // ✅ Gunakan parameter
    }
}

@Composable
fun HeaderSection(nama: String, nim: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Selamat Datang, $nama!",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = nim,
            color = Color.Gray
        )
    }
}

@Composable
fun ChatBotSection(onNavigateToChatBot: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = onNavigateToChatBot,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
            shape = CircleShape,
            modifier = Modifier.size(80.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_chatbot),
                contentDescription = "Chat Bot",
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "CHAT BOT", fontWeight = FontWeight.Bold, color = Color.Gray)
    }
}

@Composable
fun MenuSection(
    onNavigateToAkademik: () -> Unit,
    onNavigateToInformatika: () -> Unit,
    onNavigateToFTI: () -> Unit,
    onNavigateToKuliah: () -> Unit
) {
    val menuItems = listOf(
        Triple(R.drawable.ic_people, "Layanan FTI ITERA", onNavigateToFTI),
        Triple(R.drawable.ic_informatics, "Layanan Mahasiswa Informatika", onNavigateToInformatika),
        Triple(R.drawable.ic_calendar, "Manajemen Kuliah", onNavigateToKuliah),
        Triple(R.drawable.ic_blank, "AKADEMIK", onNavigateToAkademik)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = {
            items(menuItems.size) { index ->
                val (icon, label, onClick) = menuItems[index]
                MenuItem(iconRes = icon, label = label, onClick = onClick)
            }
        }
    )
}

@Composable
fun MenuItem(iconRes: Int, label: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD0E8F2)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                modifier = Modifier.size(40.dp),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

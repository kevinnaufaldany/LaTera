package com.pam.latera

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay


@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val totalPages = 5
    var currentPage by remember { mutableStateOf(0) }

    // Auto slide setiap 5 detik
    LaunchedEffect(currentPage) {
        delay(5000L)
        if (currentPage < totalPages - 1) {
            currentPage++
        } else {
            onFinish() // Pindah ke login setelah slide terakhir
        }
    }

    val images = listOf(
        R.drawable.latera,
        R.drawable.layanan,
        R.drawable.chatbot,
        R.drawable.atur,
        R.drawable.informatika // Ganti sesuai gambar slide ke-5 jika ada
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = images[currentPage]),
                contentDescription = "Gambar Slide ${currentPage + 1}",
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "Welcome to LATERA!",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(32.dp))

            when (currentPage) {
                0 -> Text(
                    text = buildAnnotatedString {
                        append("Aplikasi Layanan Mahasiswa Informatika Itera")
                    },
                    style = MaterialTheme.typography.bodyLarge
                )
                1 -> Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Fitur Layanan Administrasi:\n\n")
                        }
                        append("Pengajuan surat izin dan peminjaman kini lebih mudah dengan dokumen yang dapat langsung diunduh kapan saja, tanpa harus menunggu proses manual.")
                    },
                    style = MaterialTheme.typography.bodyLarge
                )
                2 -> Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Navigasi Kampus:\n\n")
                        }
                        append("Nikmati kemudahan menjelajah kampus dengan chatbot interaktif yang menyediakan peta gedung lengkap.")
                    },
                    style = MaterialTheme.typography.bodyLarge
                )
                3 -> Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Manajemen Jadwal Kuliah:\n\n")
                        }
                        append("Atur jadwal kuliah Anda secara otomatis dengan fitur pembuatan jadwal pintar, tanpa perlu mencatat secara manual.")
                    },
                    style = MaterialTheme.typography.bodyLarge
                )
                4 -> Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Akses ke Layanan Mahasiswa Informatika:\n\n")
                        }
                        append("Semua layanan mahasiswa terintegrasi dalam satu aplikasi praktis, menggantikan sistem yang sebelumnya terpisah berdasarkan platform.")
                    },
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    currentPage = if (currentPage > 0) currentPage - 1 else totalPages - 1
                },
                modifier = Modifier.padding(end = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
            }
            Button(
                onClick = {
                    if (currentPage < totalPages - 1) {
                        currentPage++
                    } else {
                        onFinish() // ✅ Pindah ke login jika sudah selesai
                    }
                },
                modifier = Modifier.padding(start = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next", tint = Color.Black)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    OnboardingScreen(onFinish = {})
}

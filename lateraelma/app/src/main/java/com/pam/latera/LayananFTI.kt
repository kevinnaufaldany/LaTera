package com.pam.latera

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LayananFTIScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    val layananList = listOf(
        "Form pengajuan Kerja Praktik",
        "Form penilaian Kerja Praktik",
        "Form dispensasi kuliah",
        "Form cuti mahasiswa",
        "-",
        "-",
        "-",
        "-"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0F7FA))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onBack() }
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Layanan FTI ITERA",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(Color.Transparent)
                    .padding(4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(layananList) { label ->
                LayananFTIItem(label = label) {
                    when (label) {
                        "Form pengajuan Kerja Praktik" -> {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://fti.itera.ac.id/wp-content/uploads/2025/03/FORM-SURAT-PERMOHONAN-KERJA-PRAKTIK.pdf")
                            )
                            context.startActivity(intent)
                        }


                        "Form penilaian Kerja Praktik" -> {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://fti.itera.ac.id/wp-content/uploads/2023/03/FORM-NILAI-KP-2.pdf")
                            )
                            context.startActivity(intent)
                        }

                        else -> {
                            // Item lain belum punya link
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LayananFTIItem(label: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFD0E8F2)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_dokumen),
                contentDescription = "Dokumen Icon",
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

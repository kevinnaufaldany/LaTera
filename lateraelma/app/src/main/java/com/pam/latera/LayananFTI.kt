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
import androidx.core.net.toUri

@Composable
fun LayananFTIScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    val layananList = listOf(
        "Input Pengajuan Berkas",
        "Cek Status Dokumen",
        "Form Pengajuan Kerja Praktik",
        "Tanda Bukti Menerima Berkas",
        "Form Penilaian Kerja Praktik",
        "Form Cuti Mahasiswa",
        "Form Pengajuan Pengantar Kerja Praktik",
        "Form Pengajuan Pengantar Kuliah Lapangan",
        "Form Dispensasi Kuliah",
        "Form Permohonan Pengantar Izin/Permintaan Data Tugas Akhir (Transkrip/KHS tidak perlu di ttd)",
        "Form Pendaftaran Yudisium",
        "Form Pengunduran untuk mahasiswa TPB",
        "Form Pengunduran Diri Mahasiswa FTI",
        "Form Rekomendasi Mahasiswa",
        "Form Peminjaman Alat Laboratorium (Dalam Fakultas)",
        "Form Izin Penelitian dan Penggunaan Alat Laboratorium Lintas Fakultas (dari FTI ke Fakultas lain)",
        "Form Izin Penelitian dan Penggunaan Alat Laboratorium Lintas Fakultas (dari FTI ke Fakultas lain, Peminjaman Berkelompok)",
        "Form Izin Penelitian dan Penggunaan Laboratorium MM / TIK",
        "Format Permohonan Peminjaman Laboratorium",
        "Format Permohonan Izin Penelitian diluar jam ITERA",
        "Surat Izin Kegiatan Senin-Jumat HIMA",
        "Surat Izin Kegiatan Sabtu & Minggu HIMA",
        "Surat Izin Kegiatan Luar Kampus HIMA",
        "Surat Izin Peminjaman Ruang Kelas",
        "Layanan Perubahan KRS",
        "Layanan Pengisian KRS",
        "Layanan Penyembunyian Matakuliah",
        "Layanan Tampil Matakuliah",
        "Layanan Surat Tugas Mahasiswa",
        "Layanan Surat Pengantar Magang",
        "Format Undangan Penguji dan Pembimbing",
        "Format Perubahan Nilai",
        "Surat Keterangan Turnitin",
        "Surat Keterangan Bebas Prodi dan Surat Pernyataan Mahasiswa",
        "Form Kesepakatan Jadwal Sidang Seminar Tugas Akhir",
        "Form Permohonan Seminar Sidang Tugas Akhir",
        "Form Pernyataan Sudah menyelesaikan KP untuk KKN",
        "Form Permohonan Translate Ijazah",
        "Form Permohonan Kunjungan untuk Matakuliah (Tugas Praktikum ke luar instansi)",
        "Form Kelakuan Baik",
        "Form Permohonan Perubahan Data Mahasiswa",
        "Form Permohonan Dosen Pembimbing Luar Instansi"
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
                        "Input Pengajuan Berkas" -> {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                "https://fti.itera.ac.id/kendali/jtpi/user/student_index.php".toUri()
                            )
                            context.startActivity(intent)
                        }
                        "Cek Status Dokumen" -> {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                "https://fti.itera.ac.id/kendali/jtpi/view_studentindexstatus.php".toUri()
                            )
                            context.startActivity(intent)
                        }
                        "Tanda Bukti Menerima Berkas" -> {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                "https://fti.itera.ac.id/kendali/jtpi/user/studenttt_form.php".toUri()
                            )
                            context.startActivity(intent)
                        }
                        "Form pengajuan Kerja Praktik" -> {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                "https://fti.itera.ac.id/wp-content/uploads/2025/03/FORM-SURAT-PERMOHONAN-KERJA-PRAKTIK.pdf".toUri()
                            )
                            context.startActivity(intent)
                        }
                        "Form penilaian Kerja Praktik" -> {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                "https://fti.itera.ac.id/wp-content/uploads/2023/03/FORM-NILAI-KP-2.pdf".toUri()
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

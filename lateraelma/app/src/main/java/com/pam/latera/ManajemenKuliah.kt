package com.pam.latera

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.serialization.Serializable

val BlueColor = Color(0xFF80B9FF)
val WhiteColor = Color(0xFFE0F7FA)
val CardColor = Color(0xFFD0E8F2)

@Serializable
data class Schedule(
    val day: String,
    val subject: String,
    val time: String
)

class ScheduleViewModel : ViewModel() {
    var scheduleList by mutableStateOf<List<Schedule>>(emptyList())
        private set

    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    private fun getUserId() = auth.currentUser?.uid

    fun saveSchedule(newSchedule: List<Schedule>) {
        scheduleList = newSchedule
        val userId = getUserId()
        if (userId != null) {
            val data = newSchedule.map {
                mapOf("day" to it.day, "subject" to it.subject, "time" to it.time)
            }
            db.collection("schedules").document(userId).set(mapOf("list" to data))
        }
    }

    fun loadSchedule() {
        val userId = getUserId()
        if (userId != null) {
            db.collection("schedules").document(userId).get()
                .addOnSuccessListener { doc ->
                    val list = doc["list"] as? List<Map<String, String>>
                    if (list != null) {
                        scheduleList = list.map {
                            Schedule(
                                day = it["day"] ?: "",
                                subject = it["subject"] ?: "",
                                time = it["time"] ?: ""
                            )
                        }
                    } else {
                        scheduleList = emptyList()
                    }
                }
                .addOnFailureListener {
                    scheduleList = emptyList()
                }
        } else {
            scheduleList = emptyList()
        }
    }

    fun clearSchedule() {
        scheduleList = emptyList()
        val userId = getUserId()
        if (userId != null) {
            db.collection("schedules").document(userId).delete()
        }
    }
}

@Composable
fun JadwalKuliahApp(onBackToHome: () -> Unit) {
    var currentPage by remember { mutableStateOf("home") }
    val scheduleViewModel: ScheduleViewModel = viewModel()
    val context = LocalContext.current

    DisposableEffect(Unit) {
        val authListener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            if (user != null) {
                scheduleViewModel.loadSchedule()
            } else {
                scheduleViewModel.clearSchedule()
            }
        }
        FirebaseAuth.getInstance().addAuthStateListener(authListener)
        onDispose {
            FirebaseAuth.getInstance().removeAuthStateListener(authListener)
        }
    }

    LaunchedEffect(Unit) {
        scheduleViewModel.loadSchedule()
    }

    when (currentPage) {
        "home" -> JadwalHomeScreen(
            onNavigateToInput = { currentPage = "input" },
            onNavigateToView = { currentPage = "table" },
            onBackToHome = onBackToHome
        )
        "input" -> JadwalInputScreen(
            viewModel = scheduleViewModel,
            onSelesai = {
                Toast.makeText(context, "Jadwal berhasil disimpan", Toast.LENGTH_SHORT).show()
                currentPage = "table"
            },
            onBack = { currentPage = "home" }
        )
        "table" -> ScheduleTableScreen(
            schedule = scheduleViewModel.scheduleList,
            viewModel = scheduleViewModel,
            onBack = { currentPage = "home" }
        )
    }
}

@Composable
fun JadwalHomeScreen(
    onNavigateToInput: () -> Unit,
    onNavigateToView: () -> Unit,
    onBackToHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteColor)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBackToHome) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali"
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Manajemen Kuliah",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onNavigateToInput,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = BlueColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Buat Jadwal", color = Color.White)
            }

            Button(
                onClick = onNavigateToView,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = BlueColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Lihat Jadwal", color = Color.White)
            }
        }
    }
}

@Composable
fun JadwalInputScreen(
    viewModel: ScheduleViewModel,
    onSelesai: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val hariList = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat")
    val jadwal = remember { mutableStateMapOf<String, Pair<TextFieldValue, TextFieldValue>>() }

    hariList.forEach {
        if (jadwal[it] == null) jadwal[it] = TextFieldValue("") to TextFieldValue("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(WhiteColor)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali"
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Buat Jadwal Kuliah", style = MaterialTheme.typography.headlineSmall)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(6.dp),
            colors = CardDefaults.cardColors(containerColor = CardColor)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                hariList.forEach { hari ->
                    val (mataKuliah, jam) = jadwal[hari]!!

                    Text(
                        text = hari,
                        style = MaterialTheme.typography.titleMedium,
                        color = BlueColor
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        OutlinedTextField(
                            value = mataKuliah,
                            onValueChange = { jadwal[hari] = it to jam },
                            label = { Text("Mata Kuliah") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = jam,
                            onValueChange = { jadwal[hari] = mataKuliah to it },
                            label = { Text("Jam") },
                            modifier = Modifier.width(150.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val scheduleList = mutableListOf<Schedule>()
                        hariList.forEach {
                            val (mk, jam) = jadwal[it]!!
                            if (mk.text.isNotEmpty() && jam.text.isNotEmpty()) {
                                scheduleList.add(Schedule(it, mk.text, jam.text))
                            }
                        }
                        if (scheduleList.isNotEmpty()) {
                            viewModel.saveSchedule(scheduleList)
                            onSelesai()
                        } else {
                            Toast.makeText(context, "Lengkapi semua jadwal!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(containerColor = BlueColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Simpan Jadwal", color = Color.White)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleTableScreen(
    schedule: List<Schedule>,
    viewModel: ScheduleViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tabel Jadwal") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlueColor)
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize()
                    .background(WhiteColor),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                if (schedule.isEmpty()) {
                    Text(
                        text = "Belum ada jadwal yang ditambahkan.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(schedule) { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .background(Color(0xFFf2f2f2))
                                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.day,
                                    modifier = Modifier.weight(1f).padding(8.dp),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = BlueColor
                                )
                                Text(
                                    text = item.subject,
                                    modifier = Modifier.weight(2f).padding(8.dp),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = BlueColor
                                )
                                Text(
                                    text = item.time,
                                    modifier = Modifier.weight(1f).padding(8.dp),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = BlueColor
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.clearSchedule()
                            Toast.makeText(context, "Semua jadwal berhasil dihapus", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Hapus Semua Jadwal", color = Color.White)
                    }
                }
            }
        }
    )
}

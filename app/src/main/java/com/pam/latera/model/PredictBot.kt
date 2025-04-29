package com.pam.latera.model

import android.content.Context
import org.json.JSONObject
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.Charset

class PredictBot(context: Context) {
    private val interpreter: Interpreter
    private val wordIndex: Map<String, Int>
    private val labels: List<String>
    private val responses: Map<String, List<String>>
    private val labelAliases: Map<String, String>

    init {
        val options = Interpreter.Options()
        interpreter = Interpreter(loadModelFile(context, "navbot_model.tflite"), options)
        wordIndex = loadTokenizer(context, "tokenizer.json")
        labels = loadLabels(context, "labels.txt")
        responses = setupResponses()
        labelAliases = setupLabelAliases()
    }

    fun predict(userInput: String): String {
        val input = preprocessInput(userInput)
        val inputArray = arrayOf(input)
        val output = Array(1) { FloatArray(labels.size) }
        interpreter.run(inputArray, output)
        val predictedIndex = output[0].indices.maxByOrNull { output[0][it] } ?: 0
        val predictedLabel = labels[predictedIndex]

        // Gunakan alias jika ada
        val resolvedLabel = labelAliases[predictedLabel] ?: predictedLabel

        val possibleResponses = responses[resolvedLabel]
        return possibleResponses?.random() ?: "Maaf, saya belum mengerti pertanyaan itu."
    }

    private fun preprocessInput(text: String): FloatArray {
        val tokens = text.lowercase().split(" ")
        val sequence = FloatArray(10) { 0f } // HARUS sesuai input_length saat training
        for (i in 0 until minOf(tokens.size, 10)) {
            sequence[i] = (wordIndex[tokens[i]] ?: 0).toFloat()
        }
        return sequence
    }

    private fun loadModelFile(context: Context, filename: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(filename)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.declaredLength)
    }

    private fun loadTokenizer(context: Context, filename: String): Map<String, Int> {
        val map = mutableMapOf<String, Int>()
        val jsonStr = context.assets.open(filename).bufferedReader().use { it.readText() }
        val jsonObj = JSONObject(jsonStr)

        val configObj = jsonObj.getJSONObject("config")
        val wordIndexString = configObj.getString("word_index")
        val wordIndexObj = JSONObject(wordIndexString)

        wordIndexObj.keys().forEach { key ->
            map[key] = wordIndexObj.getInt(key)
        }

        return map
    }

    private fun loadLabels(context: Context, filename: String): List<String> {
        return context.assets.open(filename).bufferedReader(Charset.forName("UTF-8")).readLines()
    }

    // Mapping label-label alternatif (alias) ke label utama
    private fun setupLabelAliases(): Map<String, String> {
        return mapOf(
            "lokasi_masjid_baim" to "Baitul Ilmi",
            "lokasi_masjid_Baitul Ilmi" to "Baitul Ilmi",
            "lokasi_masjid_baitul ilmu" to "Baitul Ilmi",
            "lokasi_masjid_At Tanwir" to "At Tanwir",
            "lokasi_masjid_at tanwir" to "At Tanwir",
            "lokasi_masjid_At tanwir" to "At Tanwir",
            "lokasi_masjid_At-Tanwir" to "At Tanwir",
            "lokasi_masjid_at-tanwir" to "At Tanwir",
            "lokasi_masjid_At-tanwir" to "At Tanwir",
            "lokasi_tempat_kebun raya" to "Kebun Raya",
            "lokasi_tempat_Kebun Raya" to "Kebun Raya",
            "lokasi_tempat_Kebun raya" to "Kebun Raya",
            "lokasi_tempat_poliklinik" to "Poliklinik",
            "lokasi_tempat_poliklinik itera" to "Poliklinik",
            "lokasi_tempat_Poliklinik" to "Poliklinik",
            "lokasi_tempat_klinik" to "Poliklinik",
            "lokasi_tempat_Klinik" to "Poliklinik",
            "lokasi_tempat_Poliklinik itera" to "Poliklinik",
            "lokasi_tempat_poliklinik ITERA" to "Poliklinik",
            "lokasi_tempat_Poliklinik ITERA" to "Poliklinik",
            "lokasi_tempat_Fasilitas Kesehatan" to "Poliklinik",
            "lokasi_tempat_kesehatan" to "Poliklinik",
            "lokasi_tempat_Kesehatan" to "Poliklinik",
            "lokasi_tempat_wifi corner" to "Wifi Corner",
            "lokasi_tempat_Wifi Corner" to "Wifi Corner",
            "lokasi_tempat_Wifi corner" to "Wifi Corner",
            "lokasi_tempat_WiFi corner" to "Wifi Corner",
            "lokasi_tempat_WiFi Corner" to "Wifi Corner",
            "lokasi_tempat_Area Wifi" to "Wifi Corner",
            "lokasi_tempat_area wifi" to "Wifi Corner",
            "lokasi_tempat_Area WiFi" to "Wifi Corner",
            "lokasi_tempat_Area Akses WiFi" to "Wifi Corner",
            "lokasi_tempat_Area akses WiFi" to "Wifi Corner",
            "lokasi_tempat_Area akses wifi" to "Wifi Corner",
            "lokasi_tempat_Area akses Wifi" to "Wifi Corner",
            "lokasi_tempat_gym" to "GYM",
            "lokasi_tempat_Gym" to "GYM",
            "lokasi_tempat_Gym itera" to "GYM",
            "lokasi_tempat_gym itera" to "GYM",
            "lokasi_tempat_GYM" to "GYM",
            "lokasi_tempat_GYM ITERA" to "GYM",
            "lokasi_tempat_GYM Itera" to "GYM",
            "lokasi_tempat_Gym Itera" to "GYM",
            "lokasi_tempat_GYM itera" to "GYM",
            "lokasi_tempat_observatorium itera" to "Observatorium",
            "lokasi_tempat_observatorium" to "Observatorium",
            "lokasi_tempat_Observatorium Itera" to "Observatorium",
            "lokasi_tempat_Observatorium" to "Observatorium",
            "lokasi_tempat_Observatorium ITERA" to "Observatorium",
            "lokasi_tempat_PLTS" to "PLTS",
            "lokasi_tempat_plts itera" to "PLTS",
            "lokasi_tempat_PLTS ITERA" to "PLTS",
            "lokasi_tempat_plts Itera" to "PLTS",
            "lokasi_tempat_PLTS Itera" to "PLTS",
            "lokasi_tempat_panel surya" to "PLTS",
            "lokasi_tempat_surya" to "PLTS",
            "lokasi_tempat_plts terbesar" to "PLTS",
            "lokasi_tempat_GSG" to "GSG",
            "lokasi_tempat_Gsg" to "GSG",
            "lokasi_tempat_Perpustakaan" to "Perpustakaan",
            "lokasi_tempat_perpustakaan itera" to "Perpustakaan",
            "lokasi_tempat_perpustakaan" to "Perpustakaan",
            "lokasi_tempat_Perpustakaan Itera" to "Perpustakaan",
            "lokasi_tempat_Perpustakaan ITERA" to "Perpustakaan",
            "lokasi_tempat_perpustakaan Itera" to "Perpustakaan",
            "lokasi_tempat_perpustakaan ITERA" to "Perpustakaan",
            "lokasi_tempat_perpus" to "Perpustakaan",
            "lokasi_tempat_Perpus" to "Perpustakaan",
            "lokasi_tempat_Perpus Itera" to "Perpustakaan",
            "lokasi_tempat_Perpus ITERA" to "Perpustakaan",
            "lokasi_tempat_RIMA" to "RIMA",
            "lokasi_tempat_Rima" to "RIMA",
            "lokasi_tempat_rima" to "RIMA",
            "lokasi_tempat_rumah ibadah" to "RIMA",
            "lokasi_tempat_rumah ibadah multiagama" to "RIMA",
            "lokasi_tempat_Rumah Ibadah" to "RIMA",
            "lokasi_tempat_Rumah ibadah" to "RIMA",
            "lokasi_tempat_Rumah Ibadah Multiagama" to "RIMA",
            "lokasi_tempat_Gku2" to "Gk2",
            "lokasi_tempat_gku2" to "Gk2",
            "lokasi_tempat_Gk1" to "Gk1",
            "lokasi_tempat_gk1" to "Gk1",
            "lokasi_tempat_GK1" to "Gk1",
            "lokasi_tempat_GKU1" to "Gk1",
            "lokasi_tempat_Gku 1" to "Gk1",
            "lokasi_tempat_gku 1" to "Gk1",
            "lokasi_tempat_Gk 1" to "Gk1",
            "lokasi_tempat_gk 1" to "Gk1",
            "lokasi_tempat_GK 1" to "Gk1",
            "lokasi_tempat_GKU 1" to "Gk1",
            "lokasi_tempat_Gk2" to "Gk2",
            "lokasi_tempat_gk2" to "Gk2",
            "lokasi_tempat_GK2" to "Gk2",
            "lokasi_tempat_GKU2" to "Gk2",
            "lokasi_tempat_Gku 2" to "Gk2",
            "lokasi_tempat_gku 2" to "Gk2",
            "lokasi_tempat_Gk 2" to "Gk2",
            "lokasi_tempat_gk 2" to "Gk2",
            "lokasi_tempat_GK 2" to "Gk2",
            "lokasi_tempat_GKU 2" to "Gk2",
            "lokasi_tempat_Galeri" to "Galeri",
            "lokasi_tempat_galeri" to "Galeri",
            "lokasi_tempat_Galeri Itera" to "Galeri",
            "lokasi_tempat_Galeri ITERA" to "Galeri",
            "lokasi_tempat_Gladiator" to "Gladiator",
            "lokasi_tempat_gladiator" to "Gladiator",
            "lokasi_tempat_Gladiator Itera" to "Gladiator",
            "lokasi_tempat_Gladiator ITERA" to "Gladiator",
            "lokasi_tempat_Aula" to "Aula",
            "lokasi_tempat_aula" to "Aula",
            "lokasi_tempat_Aula Itera" to "Aula",
            "lokasi_tempat_Aula ITERA" to "Aula",
            "lokasi_lab_lab 1" to "Labtek1",
            "lokasi_lab_Lab 1" to "Labtek1",
            "lokasi_lab_Labtek 1" to "Labtek1",
            "lokasi_lab_labtek 1" to "Labtek1",
            "lokasi_lab_laboratorium 1" to "Labtek1",
            "lokasi_lab_Laboratorium 1" to "Labtek1",
            "lokasi_lab_laboratorium teknik 1" to "Labtek1",
            "lokasi_lab_Laboratorium teknik 1" to "Labtek1",
            "lokasi_lab_Laboratorium Teknik 1" to "Labtek1",
            "lokasi_lab_lab1" to "Labtek1",
            "lokasi_lab_Lab1" to "Labtek1",
            "lokasi_lab_Labtek1" to "Labtek1",
            "lokasi_lab_labtek1" to "Labtek1",
            "lokasi_lab_laboratorium1" to "Labtek1",
            "lokasi_lab_Laboratorium1" to "Labtek1",
            "lokasi_lab_laboratorium teknik1" to "Labtek1",
            "lokasi_lab_Laboratorium teknik1" to "Labtek1",
            "lokasi_lab_Laboratorium Teknik1" to "Labtek1",
            "lokasi_lab_lab 2" to "Labtek2",
            "lokasi_lab_Lab 2" to "Labtek2",
            "lokasi_lab_Labtek 2" to "Labtek2",
            "lokasi_lab_labtek 2" to "Labtek2",
            "lokasi_lab_laboratorium 2" to "Labtek2",
            "lokasi_lab_Laboratorium 2" to "Labtek2",
            "lokasi_lab_laboratorium teknik 2" to "Labtek2",
            "lokasi_lab_Laboratorium teknik 2" to "Labtek2",
            "lokasi_lab_Laboratorium Teknik 2" to "Labtek2",
            "lokasi_lab_lab2" to "Labtek2",
            "lokasi_lab_Lab2" to "Labtek2",
            "lokasi_lab_Labtek2" to "Labtek2",
            "lokasi_lab_labtek2" to "Labtek2",
            "lokasi_lab_laboratorium2" to "Labtek2",
            "lokasi_lab_Laboratorium2" to "Labtek2",
            "lokasi_lab_laboratorium teknik2" to "Labtek2",
            "lokasi_lab_Laboratorium teknik2" to "Labtek2",
            "lokasi_lab_Laboratorium Teknik2" to "Labtek2",
            "lokasi_lab_lab 3" to "Labtek3",
            "lokasi_lab_Lab 3" to "Labtek3",
            "lokasi_lab_Labtek 3" to "Labtek3",
            "lokasi_lab_labtek 3" to "Labtek3",
            "lokasi_lab_laboratorium 3" to "Labtek3",
            "lokasi_lab_Laboratorium 3" to "Labtek3",
            "lokasi_lab_laboratorium teknik 3" to "Labtek3",
            "lokasi_lab_Laboratorium teknik 3" to "Labtek3",
            "lokasi_lab_Laboratorium Teknik 3" to "Labtek3",
            "lokasi_lab_lab3" to "Labtek3",
            "lokasi_lab_Lab3" to "Labtek3",
            "lokasi_lab_Labtek3" to "Labtek3",
            "lokasi_lab_labtek3" to "Labtek3",
            "lokasi_lab_laboratorium3" to "Labtek3",
            "lokasi_lab_Laboratorium3" to "Labtek3",
            "lokasi_lab_laboratorium teknik3" to "Labtek3",
            "lokasi_lab_Laboratorium teknik3" to "Labtek3",
            "lokasi_lab_Laboratorium Teknik3" to "Labtek3",
            "lokasi_lab_lab 4" to "Labtek4",
            "lokasi_lab_Lab 4" to "Labtek4",
            "lokasi_lab_Labtek 4" to "Labtek4",
            "lokasi_lab_labtek 4" to "Labtek4",
            "lokasi_lab_laboratorium 4" to "Labtek4",
            "lokasi_lab_Laboratorium 4" to "Labtek4",
            "lokasi_lab_laboratorium teknik 4" to "Labtek4",
            "lokasi_lab_Laboratorium teknik 4" to "Labtek4",
            "lokasi_lab_Laboratorium Teknik 4" to "Labtek4",
            "lokasi_lab_lab4" to "Labtek4",
            "lokasi_lab_Lab4" to "Labtek4",
            "lokasi_lab_Labtek4" to "Labtek4",
            "lokasi_lab_labtek4" to "Labtek4",
            "lokasi_lab_laboratorium4" to "Labtek4",
            "lokasi_lab_Laboratorium4" to "Labtek4",
            "lokasi_lab_laboratorium teknik4" to "Labtek4",
            "lokasi_lab_Laboratorium teknik4" to "Labtek4",
            "lokasi_lab_Laboratorium Teknik4" to "Labtek4",
            "lokasi_lab_lab 5" to "Labtek OZT",
            "lokasi_lab_Lab 5" to "Labtek OZT",
            "lokasi_lab_Labtek 5" to "Labtek OZT",
            "lokasi_lab_labtek 5" to "Labtek OZT",
            "lokasi_lab_laboratorium 5" to "Labtek OZT",
            "lokasi_lab_Laboratorium 5" to "Labtek OZT",
            "lokasi_lab_laboratorium teknik 5" to "Labtek OZT",
            "lokasi_lab_Laboratorium teknik 5" to "Labtek OZT",
            "lokasi_lab_Laboratorium Teknik 5" to "Labtek OZT",
            "lokasi_lab_lab5" to "Labtek OZT",
            "lokasi_lab_Lab5" to "Labtek OZT",
            "lokasi_lab_Labtek5" to "Labtek OZT",
            "lokasi_lab_labtek5" to "Labtek OZT",
            "lokasi_lab_laboratorium5" to "Labtek OZT",
            "lokasi_lab_Laboratorium5" to "Labtek OZT",
            "lokasi_lab_laboratorium teknik5" to "Labtek OZT",
            "lokasi_lab_Laboratorium teknik5" to "Labtek OZT",
            "lokasi_lab_Laboratorium Teknik5" to "Labtek OZT",
            "lokasi_lab_lab ozt" to "Labtek OZT",
            "lokasi_lab_Lab ozt" to "Labtek OZT",
            "lokasi_lab_Labtek ozt" to "Labtek OZT",
            "lokasi_lab_labtek ozt" to "Labtek OZT",
            "lokasi_lab_laboratorium ozt" to "Labtek OZT",
            "lokasi_lab_Laboratorium ozt" to "Labtek OZT",
            "lokasi_lab_laboratorium teknik ozt" to "Labtek OZT",
            "lokasi_lab_Laboratorium teknik ozt" to "Labtek OZT",
            "lokasi_lab_Laboratorium Teknik ozt" to "Labtek OZT",
            "lokasi_lab_labozt" to "Labtek OZT",
            "lokasi_lab_Labozt" to "Labtek OZT",
            "lokasi_lab_Labtekozt" to "Labtek OZT",
            "lokasi_lab_labtekozt" to "Labtek OZT",
            "lokasi_lab_laboratoriumozt" to "Labtek OZT",
            "lokasi_lab_Laboratoriumozt" to "Labtek OZT",
            "lokasi_lab_laboratorium teknikozt" to "Labtek OZT",
            "lokasi_lab_Laboratorium teknikozt" to "Labtek OZT",
            "lokasi_lab_Laboratorium Teknikozt" to "Labtek OZT",
            "lokasi_lab_lab OZT" to "Labtek OZT",
            "lokasi_lab_Lab OZT" to "Labtek OZT",
            "lokasi_lab_Labtek OZT" to "Labtek OZT",
            "lokasi_lab_labtek OZT" to "Labtek OZT",
            "lokasi_lab_laboratorium OZT" to "Labtek OZT",
            "lokasi_lab_Laboratorium OZT" to "Labtek OZT",
            "lokasi_lab_laboratorium teknik OZT" to "Labtek OZT",
            "lokasi_lab_Laboratorium teknik OZT" to "Labtek OZT",
            "lokasi_lab_Laboratorium Teknik OZT" to "Labtek OZT",
            "lokasi_lab_labOZT" to "Labtek OZT",
            "lokasi_lab_LabOZT" to "Labtek OZT",
            "lokasi_lab_LabtekOZT" to "Labtek OZT",
            "lokasi_lab_labtekOZT" to "Labtek OZT",
            "lokasi_lab_laboratoriumOZT" to "Labtek OZT",
            "lokasi_lab_LaboratoriumOZT" to "Labtek OZT",
            "lokasi_lab_laboratorium teknikOZT" to "Labtek OZT",
            "lokasi_lab_Laboratorium teknikOZT" to "Labtek OZT",
            "lokasi_lab_Laboratorium TeknikOZT" to "Labtek OZT",
            "lokasi_lab_lab iot" to "Labtek IOT",
            "lokasi_lab_Lab iot" to "Labtek IOT",
            "lokasi_lab_Labtek iot" to "Labtek IOT",
            "lokasi_lab_labtek iot" to "Labtek IOT",
            "lokasi_lab_laboratorium iot" to "Labtek IOT",
            "lokasi_lab_Laboratorium iot" to "Labtek IOT",
            "lokasi_lab_laboratorium teknik iot" to "Labtek IOT",
            "lokasi_lab_Laboratorium teknik iot" to "Labtek IOT",
            "lokasi_lab_Laboratorium Teknik iot" to "Labtek IOT",
            "lokasi_lab_labiot" to "Labtek IOT",
            "lokasi_lab_Labiot" to "Labtek IOT",
            "lokasi_lab_Labtekiot" to "Labtek IOT",
            "lokasi_lab_labtekiot" to "Labtek IOT",
            "lokasi_lab_laboratoriumiot" to "Labtek IOT",
            "lokasi_lab_Laboratoriumiot" to "Labtek IOT",
            "lokasi_lab_laboratorium teknikiot" to "Labtek IOT",
            "lokasi_lab_Laboratorium teknikiot" to "Labtek IOT",
            "lokasi_lab_Laboratorium Teknikiot" to "Labtek IOT",
            "lokasi_lab_lab IOT" to "Labtek IOT",
            "lokasi_lab_Lab IOT" to "Labtek IOT",
            "lokasi_lab_Labtek IOT" to "Labtek IOT",
            "lokasi_lab_labtek IOT" to "Labtek IOT",
            "lokasi_lab_laboratorium IOT" to "Labtek IOT",
            "lokasi_lab_Laboratorium IOT" to "Labtek IOT",
            "lokasi_lab_laboratorium teknik IOT" to "Labtek IOT",
            "lokasi_lab_Laboratorium teknik IOT" to "Labtek IOT",
            "lokasi_lab_Laboratorium Teknik IOT" to "Labtek IOT",
            "lokasi_lab_labIOT" to "Labtek IOT",
            "lokasi_lab_LabIOT" to "Labtek IOT",
            "lokasi_lab_LabtekIOT" to "Labtek IOT",
            "lokasi_lab_labtekIOT" to "Labtek IOT",
            "lokasi_lab_laboratoriumIOT" to "Labtek IOT",
            "lokasi_lab_LaboratoriumIOT" to "Labtek IOT",
            "lokasi_lab_laboratorium teknikIOT" to "Labtek IOT",
            "lokasi_lab_Laboratorium teknikIOT" to "Labtek IOT",
            "lokasi_lab_Laboratorium TeknikIOT" to "Labtek IOT",
            "lokasi_lab_lab IoT" to "Labtek IOT",
            "lokasi_lab_Lab IoT" to "Labtek IOT",
            "lokasi_lab_Labtek IoT" to "Labtek IOT",
            "lokasi_lab_labtek IoT" to "Labtek IOT",
            "lokasi_lab_laboratorium IoT" to "Labtek IOT",
            "lokasi_lab_Laboratorium IoT" to "Labtek IOT",
            "lokasi_lab_laboratorium teknik IoT" to "Labtek IOT",
            "lokasi_lab_Laboratorium teknik IoT" to "Labtek IOT",
            "lokasi_lab_Laboratorium Teknik IoT" to "Labtek IOT",
            "lokasi_lab_labIoT" to "Labtek IOT",
            "lokasi_lab_LabIoT" to "Labtek IOT",
            "lokasi_lab_LabtekIoT" to "Labtek IOT",
            "lokasi_lab_labtekIoT" to "Labtek IOT",
            "lokasi_lab_laboratoriumIoT" to "Labtek IOT",
            "lokasi_lab_LaboratoriumIoT" to "Labtek IOT",
            "lokasi_lab_laboratorium teknikIoT" to "Labtek IOT",
            "lokasi_lab_Laboratorium teknikIoT" to "Labtek IOT",
            "lokasi_lab_Laboratorium TeknikIoT" to "Labtek IOT",
            "lokasi_kantin_RK" to "RK",
            "lokasi_kantin_rk" to "RK",
            "lokasi_kantin_Rk" to "RK",
            "lokasi_kantin_rumah kayu" to "RK",
            "lokasi_kantin_Rumah kayu" to "RK",
            "lokasi_kantin_Baim" to "RK",
            "lokasi_kantin_BKL" to "BKL",
            "lokasi_kantin_bkl" to "BKL",
            "lokasi_kantin_Bkl" to "BKL",
            "lokasi_kantin_Bukit Kiara Indah" to "BKL",
            "lokasi_kantin_Bukit kiara indah" to "BKL",
            "lokasi_kantin_asrama" to "BKL",
            "lokasi_kantin_Asrama" to "BKL",
            "lokasi_embung_A" to "Embung A",
            "lokasi_embung_B" to "Embung B",
            "lokasi_embung_C" to "Embung C",
            "lokasi_embung_D" to "Embung D",
            "lokasi_embung_E" to "Embung E",
            "lokasi_embung_F" to "Embung F",
            "lokasi_embung_a" to "Embung A",
            "lokasi_embung_b" to "Embung B",
            "lokasi_embung_c" to "Embung C",
            "lokasi_embung_d" to "Embung D",
            "lokasi_embung_e" to "Embung E",
            "lokasi_embung_f" to "Embung F",
            "lokasi_embung_sumatera" to "Embung Kebun Raya",
            "lokasi_embung_kebun raya" to "Embung Kebun Raya",
            "lokasi_embung_Sumatera" to "Embung Kebun Raya",
            "lokasi_embung_Kebun Raya" to "Embung Kebun Raya",
            "lokasi_lapangan_bola" to "Lapangan Sepak Bola",
            "lokasi_lapangan_Bola" to "Lapangan Sepak Bola",
            "lokasi_lapangan_Sepak Bola" to "Lapangan Sepak Bola",
            "lokasi_lapangan_sepak bola" to "Lapangan Sepak Bola",
            "lokasi_lapangan_basket" to "Lapangan Basket",
            "lokasi_lapangan_Basket" to "Lapangan Basket",
            "lokasi_lapangan_voly" to "Lapangan Voly",
            "lokasi_lapangan_Voly" to "Lapangan Voly",
            "lokasi_lapangan_baseball" to "Lapangan Baseball",
            "lokasi_lapangan_Baseball" to "Lapangan Baseball",
            "lokasi_lapangan_base ball" to "Lapangan Baseball",
            "lokasi_lapangan_Base ball" to "Lapangan Baseball",
            "lokasi_lapangan_futsal" to "Lapangan Futsal",
            "lokasi_lapangan_Futsal" to "Lapangan Futsal",
            "lokasi_lapangan_panjat tebing" to "Lapangan Panjat Tebing",
            "lokasi_lapangan_Panjat Tebing" to "Lapangan Panjat Tebing",
            "lokasi_lapangan_Panjat tebing" to "Lapangan Panjat Tebing",
            "lokasi_gerbang_barat" to "Lokasi Gerbang Barat",
            "lokasi_gerbang_Barat" to "Lokasi Gerbang Barat",
            "lokasi_gerbang_Utama" to "Lokasi Gerbang Utama",
            "lokasi_gerbang_utama" to "Lokasi Gerbang Utama",
            "lokasi_gerbang_depan" to "Lokasi Gerbang Utama",
            "lokasi_gerbang_Depan" to "Lokasi Gerbang Utama",
            "lokasi_gedung_A" to "Gedung A",
            "lokasi_gedung_B" to "Gedung B",
            "lokasi_gedung_C" to "Gedung C",
            "lokasi_gedung_D" to "Gedung D",
            "lokasi_gedung_E" to "Gedung E",
            "lokasi_gedung_F" to "Gedung F",
            "lokasi_gedung_a" to "Gedung A",
            "lokasi_gedung_b" to "Gedung B",
            "lokasi_gedung_c" to "Gedung C",
            "lokasi_gedung_d" to "Gedung D",
            "lokasi_gedung_e" to "Gedung E",
            "lokasi_gedung_FTI" to "Gedung FTI",
            "lokasi_gedung_fti" to "Gedung FTI",
            "lokasi_gedung_Fakultas Teknologi Industri" to "Gedung FTI",
            "lokasi_gedung_fakultas" to "Gedung FTI",
            "lokasi_gedung_Serba Guna" to "GSG",
            "lokasi_gedung_serba guna" to "GSG",
            "lokasi_gedung_Serba guna" to "GSG",
            "lokasi_gedung_serbaguna" to "GSG",
            "lokasi_gedung_Serbaguna" to "GSG",
            "lokasi_gedung_akademik" to "Gedung B",
            "lokasi_gedung_Akademik" to "Gedung B",
            "lokasi_gedung_Rektor" to "Gedung A",
            "lokasi_gedung_rektor" to "Gedung A",
            "lokasi_gedung_Keuangan" to "Gedung B",
            "lokasi_gedung_keuangan" to "Gedung B",
            "lokasi_gedung_Ke uangan" to "Gedung B",
            "lokasi_gedung_ke uangan" to "Gedung B",
            "lokasi_gedung_bandar lampung" to "Gedung E",
            "lokasi_gedung_Bandar Lampung" to "Gedung E",
            "lokasi_gedung_Bandar lampung" to "Gedung E",
            "lokasi_gedung_student center" to "SC",
            "lokasi_gedung_Student center" to "SC",
            "lokasi_gedung_Student Center" to "SC",
            "lokasi_gedung_sc" to "SC",
            "lokasi_gedung_SC" to "SC",
            "lokasi_gedung_kuliah 1" to "Gk1",
            "lokasi_gedung_Kuliah 1" to "Gk1",
            "lokasi_gedung_Kuliah Umum 1" to "Gk1",
            "lokasi_gedung_Kuliah umum 1" to "Gk1",
            "lokasi_gedung_kuliah1" to "Gk1",
            "lokasi_gedung_Kuliah1" to "Gk1",
            "lokasi_gedung_Kuliah Umum1" to "Gk1",
            "lokasi_gedung_Kuliah umum1" to "Gk1",
            "lokasi_gedung_kuliah 2" to "Gk2",
            "lokasi_gedung_Kuliah 2" to "Gk2",
            "lokasi_gedung_Kuliah Umum 2" to "Gk2",
            "lokasi_gedung_Kuliah umum 2" to "Gk2",
            "lokasi_gedung_kuliah2" to "Gk2",
            "lokasi_gedung_Kuliah2" to "Gk2",
            "lokasi_gedung_Kuliah Umum2" to "Gk2",
            "lokasi_gedung_Kuliah umum2" to "Gk2",
            "lokasi_asrama_TB1" to "TB1",
            "lokasi_asrama_tb1" to "TB1",
            "lokasi_asrama_Tb1" to "TB1",
            "lokasi_asrama_TB 1" to "TB1",
            "lokasi_asrama_tb 1" to "TB1",
            "lokasi_asrama_Tb 1" to "TB1",
            "lokasi_asrama_TB2" to "TB2",
            "lokasi_asrama_Depan" to "TB1",
            "lokasi_asrama_TB3" to "TB3",
            "lokasi_asrama_TB4" to "TB4",
            "lokasi_asrama_TB5" to "TB5",
            "lokasi_asrama_putra" to "TBPutra",
            "lokasi_asrama_Putra" to "TBPutra",
            "lokasi_asrama_laki" to "TBPutra",
            "lokasi_asrama_laki-laki" to "TBPutra",
            "lokasi_asrama_pria" to "TBPutra",
            "lokasi_asrama_Pria" to "TBPutra",
            "lokasi_asrama_putri" to "TBPutri",
            "lokasi_asrama_Putri" to "TBPutri",
            "lokasi_asrama_perempuan" to "TBPutri",
            "lokasi_asrama_Perempuan" to "TBPutri",
            "lokasi_asrama_wanita" to "TBPutri",
            "lokasi_asrama_Wanita" to "TBPutri",
            "lokasi_ruangan_000" to "Lantai 0",
            "lokasi_ruangan_002" to "Lantai 0",
            "lokasi_ruangan_003" to "Lantai 0",
            "lokasi_ruangan_004" to "Lantai 0",
            "lokasi_ruangan_005" to "Lantai 0",
            "lokasi_ruangan_006" to "Lantai 0",
            "lokasi_ruangan_007" to "Lantai 0",
            "lokasi_ruangan_008" to "Lantai 0",
            "lokasi_ruangan_009" to "Lantai 0",
            "lokasi_ruangan_010" to "Lantai 0",
            "lokasi_ruangan_011" to "Lantai 0",
            "lokasi_ruangan_012" to "Lantai 0",
            "lokasi_ruangan_013" to "Lantai 0",
            "lokasi_ruangan_014" to "Lantai 0",
            "lokasi_ruangan_015" to "Lantai 0",
            "lokasi_ruangan_016" to "Lantai 0",
            "lokasi_ruangan_017" to "Lantai 0",
            "lokasi_ruangan_018" to "Lantai 0",
            "lokasi_ruangan_019" to "Lantai 0",
            "lokasi_ruangan_020" to "Lantai 0",
            "lokasi_ruangan_100" to "Lantai 1",
            "lokasi_ruangan_101" to "Lantai 1",
            "lokasi_ruangan_102" to "Lantai 1",
            "lokasi_ruangan_103" to "Lantai 1",
            "lokasi_ruangan_104" to "Lantai 1",
            "lokasi_ruangan_105" to "Lantai 1",
            "lokasi_ruangan_106" to "Lantai 1",
            "lokasi_ruangan_107" to "Lantai 1",
            "lokasi_ruangan_108" to "Lantai 1",
            "lokasi_ruangan_109" to "Lantai 1",
            "lokasi_ruangan_110" to "Lantai 1",
            "lokasi_ruangan_111" to "Lantai 1",
            "lokasi_ruangan_112" to "Lantai 1",
            "lokasi_ruangan_113" to "Lantai 1",
            "lokasi_ruangan_114" to "Lantai 1",
            "lokasi_ruangan_115" to "Lantai 1",
            "lokasi_ruangan_116" to "Lantai 1",
            "lokasi_ruangan_117" to "Lantai 1",
            "lokasi_ruangan_118" to "Lantai 1",
            "lokasi_ruangan_119" to "Lantai 1",
            "lokasi_ruangan_120" to "Lantai 1",
            "lokasi_ruangan_200" to "Lantai 2",
            "lokasi_ruangan_201" to "Lantai 2",
            "lokasi_ruangan_202" to "Lantai 2",
            "lokasi_ruangan_203" to "Lantai 2",
            "lokasi_ruangan_204" to "Lantai 2",
            "lokasi_ruangan_205" to "Lantai 2",
            "lokasi_ruangan_206" to "Lantai 2",
            "lokasi_ruangan_207" to "Lantai 2",
            "lokasi_ruangan_208" to "Lantai 2",
            "lokasi_ruangan_209" to "Lantai 2",
            "lokasi_ruangan_210" to "Lantai 2",
            "lokasi_ruangan_211" to "Lantai 2",
            "lokasi_ruangan_212" to "Lantai 2",
            "lokasi_ruangan_213" to "Lantai 2",
            "lokasi_ruangan_214" to "Lantai 2",
            "lokasi_ruangan_215" to "Lantai 2",
            "lokasi_ruangan_216" to "Lantai 2",
            "lokasi_ruangan_217" to "Lantai 2",
            "lokasi_ruangan_218" to "Lantai 2",
            "lokasi_ruangan_219" to "Lantai 2",
            "lokasi_ruangan_220" to "Lantai 2",
            "lokasi_ruangan_300" to "Lantai 3",
            "lokasi_ruangan_301" to "Lantai 3",
            "lokasi_ruangan_302" to "Lantai 3",
            "lokasi_ruangan_303" to "Lantai 3",
            "lokasi_ruangan_304" to "Lantai 3",
            "lokasi_ruangan_305" to "Lantai 3",
            "lokasi_ruangan_306" to "Lantai 3",
            "lokasi_ruangan_307" to "Lantai 3",
            "lokasi_ruangan_308" to "Lantai 3",
            "lokasi_ruangan_309" to "Lantai 3",
            "lokasi_ruangan_310" to "Lantai 3",
            "lokasi_ruangan_311" to "Lantai 3",
            "lokasi_ruangan_312" to "Lantai 3",
            "lokasi_ruangan_313" to "Lantai 3",
            "lokasi_ruangan_314" to "Lantai 3",
            "lokasi_ruangan_315" to "Lantai 3",
            "lokasi_ruangan_316" to "Lantai 3",
            "lokasi_ruangan_317" to "Lantai 3",
            "lokasi_ruangan_318" to "Lantai 3",
            "lokasi_ruangan_319" to "Lantai 3",
            "lokasi_ruangan_320" to "Lantai 3",
            "lokasi_ruangan_400" to "Lantai 4",
            "lokasi_ruangan_401" to "Lantai 4",
            "lokasi_ruangan_402" to "Lantai 4",
            "lokasi_ruangan_403" to "Lantai 4",
            "lokasi_ruangan_404" to "Lantai 4",
            "lokasi_ruangan_405" to "Lantai 4",
            "lokasi_ruangan_406" to "Lantai 4",
            "lokasi_ruangan_407" to "Lantai 4",
            "lokasi_ruangan_408" to "Lantai 4",
            "lokasi_ruangan_409" to "Lantai 4",
            "lokasi_ruangan_410" to "Lantai 4",
            "lokasi_ruangan_411" to "Lantai 4",
            "lokasi_ruangan_412" to "Lantai 4",
            "lokasi_ruangan_413" to "Lantai 4",
            "lokasi_ruangan_414" to "Lantai 4",
            "lokasi_ruangan_415" to "Lantai 4",
            "lokasi_ruangan_416" to "Lantai 4",
            "lokasi_ruangan_417" to "Lantai 4",
            "lokasi_ruangan_418" to "Lantai 4",
            "lokasi_ruangan_419" to "Lantai 4",
            "lokasi_ruangan_420" to "Lantai 4",
        )
    }

    private fun setupResponses(): Map<String, List<String>> {
        return mapOf(
            "At Tanwir" to listOf(
                "Informasi mengenai At Tanwir dapat ditemukan di area kampus.",
                "At Tanwir adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi At Tanwir untuk informasi lebih lanjut."
            ),
            "Aula" to listOf(
                "Informasi mengenai Aula dapat ditemukan di area kampus.",
                "Aula adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Aula untuk informasi lebih lanjut."
            ),
            "BKL" to listOf(
                "Informasi mengenai BKL dapat ditemukan di area kampus.",
                "BKL adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi BKL untuk informasi lebih lanjut."
            ),
            "Baitul Ilmi" to listOf(
                "Informasi mengenai Baitul Ilmi dapat ditemukan di area kampus.",
                "Baitul Ilmi adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Baitul Ilmi untuk informasi lebih lanjut."
            ),
            "Embung A" to listOf(
                "Informasi mengenai Embung A dapat ditemukan di area kampus.",
                "Embung A adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Embung A untuk informasi lebih lanjut."
            ),
            "Embung B" to listOf(
                "Informasi mengenai Embung B dapat ditemukan di area kampus.",
                "Embung B adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Embung B untuk informasi lebih lanjut."
            ),
            "Embung C" to listOf(
                "Informasi mengenai Embung C dapat ditemukan di area kampus.",
                "Embung C adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Embung C untuk informasi lebih lanjut."
            ),
            "Embung D" to listOf(
                "Informasi mengenai Embung D dapat ditemukan di area kampus.",
                "Embung D adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Embung D untuk informasi lebih lanjut."
            ),
            "Embung E" to listOf(
                "Informasi mengenai Embung E dapat ditemukan di area kampus.",
                "Embung E adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Embung E untuk informasi lebih lanjut."
            ),
            "Embung F" to listOf(
                "Informasi mengenai Embung F dapat ditemukan di area kampus.",
                "Embung F adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Embung F untuk informasi lebih lanjut."
            ),
            "Embung Kebun Raya" to listOf(
                "Informasi mengenai Embung Kebun Raya dapat ditemukan di area kampus.",
                "Embung Kebun Raya adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Embung Kebun Raya untuk informasi lebih lanjut."
            ),
            "GSG" to listOf(
                "Informasi mengenai GSG dapat ditemukan di area kampus.",
                "GSG adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi GSG untuk informasi lebih lanjut."
            ),
            "GYM" to listOf(
                "Informasi mengenai GYM dapat ditemukan di area kampus.",
                "GYM adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi GYM untuk informasi lebih lanjut."
            ),
            "Galeri" to listOf(
                "Informasi mengenai Galeri dapat ditemukan di area kampus.",
                "Galeri adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Galeri untuk informasi lebih lanjut."
            ),
            "Gedung A" to listOf(
                "Informasi mengenai Gedung A dapat ditemukan di area kampus.",
                "Gedung A adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Gedung A untuk informasi lebih lanjut."
            ),
            "Gedung B" to listOf(
                "Informasi mengenai Gedung B dapat ditemukan di area kampus.",
                "Gedung B adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Gedung B untuk informasi lebih lanjut."
            ),
            "Gedung C" to listOf(
                "Informasi mengenai Gedung C dapat ditemukan di area kampus.",
                "Gedung C adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Gedung C untuk informasi lebih lanjut."
            ),
            "Gedung D" to listOf(
                "Informasi mengenai Gedung D dapat ditemukan di area kampus.",
                "Gedung D adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Gedung D untuk informasi lebih lanjut."
            ),
            "Gedung E" to listOf(
                "Informasi mengenai Gedung E dapat ditemukan di area kampus.",
                "Gedung E adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Gedung E untuk informasi lebih lanjut."
            ),
            "Gedung F" to listOf(
                "Informasi mengenai Gedung F dapat ditemukan di area kampus.",
                "Gedung F adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Gedung F untuk informasi lebih lanjut."
            ),
            "Gedung FTI" to listOf(
                "Informasi mengenai Gedung FTI dapat ditemukan di area kampus.",
                "Gedung FTI adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Gedung FTI untuk informasi lebih lanjut."
            ),
            "Gk1" to listOf(
                "Informasi mengenai Gk1 dapat ditemukan di area kampus.",
                "Gk1 adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Gk1 untuk informasi lebih lanjut."
            ),
            "Gk2" to listOf(
                "Informasi mengenai Gk2 dapat ditemukan di area kampus.",
                "Gk2 adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Gk2 untuk informasi lebih lanjut."
            ),
            "Gladiator" to listOf(
                "Informasi mengenai Gladiator dapat ditemukan di area kampus.",
                "Gladiator adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Gladiator untuk informasi lebih lanjut."
            ),
            "Kebun Raya" to listOf(
                "Informasi mengenai Kebun Raya dapat ditemukan di area kampus.",
                "Kebun Raya adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Kebun Raya untuk informasi lebih lanjut."
            ),
            "Labtek IOT" to listOf(
                "Informasi mengenai Labtek IOT dapat ditemukan di area kampus.",
                "Labtek IOT adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Labtek IOT untuk informasi lebih lanjut."
            ),
            "Labtek OZT" to listOf(
                "Informasi mengenai Labtek OZT dapat ditemukan di area kampus.",
                "Labtek OZT adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Labtek OZT untuk informasi lebih lanjut."
            ),
            "Labtek1" to listOf(
                "Informasi mengenai Labtek1 dapat ditemukan di area kampus.",
                "Labtek1 adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Labtek1 untuk informasi lebih lanjut."
            ),
            "Labtek2" to listOf(
                "Informasi mengenai Labtek2 dapat ditemukan di area kampus.",
                "Labtek2 adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Labtek2 untuk informasi lebih lanjut."
            ),
            "Labtek3" to listOf(
                "Informasi mengenai Labtek3 dapat ditemukan di area kampus.",
                "Labtek3 adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Labtek3 untuk informasi lebih lanjut."
            ),
            "Labtek4" to listOf(
                "Informasi mengenai Labtek4 dapat ditemukan di area kampus.",
                "Labtek4 adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Labtek4 untuk informasi lebih lanjut."
            ),
            "Lantai 0" to listOf(
                "Informasi mengenai Lantai 0 dapat ditemukan di area kampus.",
                "Lantai 0 adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Lantai 0 untuk informasi lebih lanjut."
            ),
            "Lantai 1" to listOf(
                "Informasi mengenai Lantai 1 dapat ditemukan di area kampus.",
                "Lantai 1 adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Lantai 1 untuk informasi lebih lanjut."
            ),
            "Lantai 2" to listOf(
                "Informasi mengenai Lantai 2 dapat ditemukan di area kampus.",
                "Lantai 2 adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Lantai 2 untuk informasi lebih lanjut."
            ),
            "Lantai 3" to listOf(
                "Informasi mengenai Lantai 3 dapat ditemukan di area kampus.",
                "Lantai 3 adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Lantai 3 untuk informasi lebih lanjut."
            ),
            "Lantai 4" to listOf(
                "Informasi mengenai Lantai 4 dapat ditemukan di area kampus.",
                "Lantai 4 adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Lantai 4 untuk informasi lebih lanjut."
            ),
            "Lapangan Baseball" to listOf(
                "Informasi mengenai Lapangan Baseball dapat ditemukan di area kampus.",
                "Lapangan Baseball adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Lapangan Baseball untuk informasi lebih lanjut."
            ),
            "Lapangan Basket" to listOf(
                "Informasi mengenai Lapangan Basket dapat ditemukan di area kampus.",
                "Lapangan Basket adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Lapangan Basket untuk informasi lebih lanjut."
            ),
            "Lapangan Futsal" to listOf(
                "Informasi mengenai Lapangan Futsal dapat ditemukan di area kampus.",
                "Lapangan Futsal adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Lapangan Futsal untuk informasi lebih lanjut."
            ),
            "Lapangan Panjat Tebing" to listOf(
                "Informasi mengenai Lapangan Panjat Tebing dapat ditemukan di area kampus.",
                "Lapangan Panjat Tebing adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Lapangan Panjat Tebing untuk informasi lebih lanjut."
            ),
            "Lapangan Sepak Bola" to listOf(
                "Informasi mengenai Lapangan Sepak Bola dapat ditemukan di area kampus.",
                "Lapangan Sepak Bola adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Lapangan Sepak Bola untuk informasi lebih lanjut."
            ),
            "Lapangan Voly" to listOf(
                "Informasi mengenai Lapangan Voly dapat ditemukan di area kampus.",
                "Lapangan Voly adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Lapangan Voly untuk informasi lebih lanjut."
            ),
            "Lokasi Gerbang Barat" to listOf(
                "Informasi mengenai Lokasi Gerbang Barat dapat ditemukan di area kampus.",
                "Lokasi Gerbang Barat adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Lokasi Gerbang Barat untuk informasi lebih lanjut."
            ),
            "Lokasi Gerbang Utama" to listOf(
                "Informasi mengenai Lokasi Gerbang Utama dapat ditemukan di area kampus.",
                "Lokasi Gerbang Utama adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Lokasi Gerbang Utama untuk informasi lebih lanjut."
            ),
            "Observatorium" to listOf(
                "Informasi mengenai Observatorium dapat ditemukan di area kampus.",
                "Observatorium adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Observatorium untuk informasi lebih lanjut."
            ),
            "PLTS" to listOf(
                "Informasi mengenai PLTS dapat ditemukan di area kampus.",
                "PLTS adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi PLTS untuk informasi lebih lanjut."
            ),
            "Perpustakaan" to listOf(
                "Informasi mengenai Perpustakaan dapat ditemukan di area kampus.",
                "Perpustakaan adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Perpustakaan untuk informasi lebih lanjut."
            ),
            "Poliklinik" to listOf(
                "Informasi mengenai Poliklinik dapat ditemukan di area kampus.",
                "Poliklinik adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Poliklinik untuk informasi lebih lanjut."
            ),
            "RIMA" to listOf(
                "Informasi mengenai RIMA dapat ditemukan di area kampus.",
                "RIMA adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi RIMA untuk informasi lebih lanjut."
            ),
            "RK" to listOf(
                "Informasi mengenai RK dapat ditemukan di area kampus.",
                "RK adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi RK untuk informasi lebih lanjut."
            ),
            "SC" to listOf(
                "Informasi mengenai SC dapat ditemukan di area kampus.",
                "SC adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi SC untuk informasi lebih lanjut."
            ),
            "TB1" to listOf(
                "Informasi mengenai TB1 dapat ditemukan di area kampus.",
                "TB1 adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi TB1 untuk informasi lebih lanjut."
            ),
            "TB2" to listOf(
                "Informasi mengenai TB2 dapat ditemukan di area kampus.",
                "TB2 adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi TB2 untuk informasi lebih lanjut."
            ),
            "TB3" to listOf(
                "Informasi mengenai TB3 dapat ditemukan di area kampus.",
                "TB3 adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi TB3 untuk informasi lebih lanjut."
            ),
            "TB4" to listOf(
                "Informasi mengenai TB4 dapat ditemukan di area kampus.",
                "TB4 adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi TB4 untuk informasi lebih lanjut."
            ),
            "TB5" to listOf(
                "Informasi mengenai TB5 dapat ditemukan di area kampus.",
                "TB5 adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi TB5 untuk informasi lebih lanjut."
            ),
            "TBPutra" to listOf(
                "Informasi mengenai TBPutra dapat ditemukan di area kampus.",
                "TBPutra adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi TBPutra untuk informasi lebih lanjut."
            ),
            "TBPutri" to listOf(
                "Informasi mengenai TBPutri dapat ditemukan di area kampus.",
                "TBPutri adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi TBPutri untuk informasi lebih lanjut."
            ),
            "Wifi Corner" to listOf(
                "Informasi mengenai Wifi Corner dapat ditemukan di area kampus.",
                "Wifi Corner adalah salah satu lokasi penting di lingkungan kampus.",
                "Silakan kunjungi Wifi Corner untuk informasi lebih lanjut."
            ),
        )
    }
}

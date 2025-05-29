package com.pam.latera.logic


import android.content.Context
import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException
import kotlin.io.use

object PredictBot {
    private lateinit var context: Context
    private var initialized = false

    // Ganti dengan URL API yang sudah kamu deploy di Railway
    private const val API_URL = "https://api-navbot-production.up.railway.app/predict"

    fun init(appContext: Context) {
        if (initialized) return
        context = appContext.applicationContext
        // Kalau ada inisialisasi lain, tambahkan di sini
        initialized = true
    }

    // Ubah predict jadi asynchronous, pakai callback untuk hasilnya
    fun predict(input: String, callback: (String) -> Unit) {
        if (!initialized) {
            Log.w("PredictBot", "PredictBot belum diinisialisasi!")
            callback("Prediksi belum diimplementasi")
            return
        }

        val client = OkHttpClient()

        val json = JSONObject()
        json.put("text", input)

        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            json.toString()
        )

        val request = Request.Builder()
            .url(API_URL)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("PredictBot", "Failed to call API", e)
                callback("Maaf, terjadi kesalahan koneksi.")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        Log.e("PredictBot", "Unexpected code $response")
                        callback("Maaf, API error.")
                        return
                    }
                    val body = it.body?.string()
                    if (body == null) {
                        callback("Maaf, response kosong.")
                        return
                    }
                    try {
                        val jsonResponse = JSONObject(body)
                        val answer = jsonResponse.optString("answer", "Maaf, saya belum mengerti.")
                        callback(answer)
                    } catch (e: Exception) {
                        Log.e("PredictBot", "Failed parse JSON", e)
                        callback("Maaf, terjadi kesalahan parsing.")
                    }
                }
            }
        })
    }
}

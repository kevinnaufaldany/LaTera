package com.pam.latera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.FirebaseApp
import com.pam.latera.model.PredictBot
import com.pam.latera.tampilan.ChatScreen
import com.pam.latera.ui.theme.LateraTheme
import com.pam.latera.utils.FirestoreHelper
import com.pam.latera.utils.SoundPlayer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this) // <-- INI HARUS DI SINI DULU sebelum FirestoreHelper!!

        val soundPlayer = SoundPlayer(this)
        val firestoreHelper = FirestoreHelper()
        val predictBot = PredictBot(this)

        setContent {
            LateraTheme {
                ChatScreen(
                    predictBot = predictBot,
                    firestoreHelper = firestoreHelper,
                    soundPlayer = soundPlayer
                )
            }
        }
    }
}

package com.pam.latera.utils

import android.content.Context
import android.media.MediaPlayer
import com.pam.latera.R

class SoundPlayer(private val context: Context) {
    fun playSendSound() {
        val mediaPlayer = MediaPlayer.create(context, R.raw.pop)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener { mp ->
            mp.release()
        }
    }
}

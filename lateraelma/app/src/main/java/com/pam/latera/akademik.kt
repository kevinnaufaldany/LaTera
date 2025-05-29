package com.pam.latera

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource


@Composable
fun SecondScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0F7FA))
            .padding(16.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onBack() }
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "akademik",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Item E-Learning
        AkademikItem(
            label = "E-Learning",
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://kuliah.itera.ac.id/"))
                context.startActivity(intent)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Item SIAKAD
        AkademikItem(
            label = "SIAKAD",
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://siakad.itera.ac.id/"))
                context.startActivity(intent)
            }
        )
    }
}

@Composable
fun AkademikItem(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color(0xFFD0E8F2), shape = RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_itera),
            contentDescription = "$label Icon",
            modifier = Modifier
                .size(32.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = label,
            fontWeight = FontWeight.Bold
        )
    }
}

# 📱 LaTera — Smart Campus Assistant for Informatics ITERA

**LaTera** adalah aplikasi Android Smart Campus Assistant yang dirancang khusus untuk mahasiswa Program Studi Teknik Informatika Institut Teknologi Sumatera (ITERA). Aplikasi ini bertujuan untuk menyederhanakan akses ke berbagai layanan akademik, administrasi, dan informasi penting kampus dalam satu platform modern.

---

## 🚀 Fitur Unggulan

- **ChatBot Navigasi Kampus**
  - Fitur interaktif (berbasis NavBot API) untuk panduan lokasi gedung secara real-time
- **Manajemen Kuliah**
  - Buat dan atur jadwal kuliah
- **Layanan Mahasiswa**
  - Akses cepat ke kalender akademik, form layanan, dll.
- **Layanan FTI ITERA**
  - Permintaan dokumen administratif
- **SIAKAD & E-Learning**
  - Shortcut menuju layanan akademik daring

---

## 🛠️ Teknologi yang Digunakan

| Komponen | Teknologi             |
|----------|------------------------|
| UI       | Jetpack Compose        |
| Auth     | Firebase Authentication |
| DB       | Firebase Firestore     |
| API Bot  | [NavBot API](https://github.com/kevinnaufaldany/API-NavBot)         |

---

## 📂 Struktur Navigasi

```text
MainActivity.kt
└── AppContent()
    ├── OnboardingScreen
    ├── LoginScreen
    ├── RegisterScreen
    ├── ForgotPasswordScreen
    ├── ResetPasswordScreen
    ├── HomeScreen
        ├── MenuSection
        ├── ChatBotSection
        ├── HeaderSection
    ├── AkademikScreen
    ├── LayananMahasiswaScreen
    ├── LayananFTIScreen
    ├── JadwalKuliahScreen
    └── ChatBotScreen
```

---

## 📸 Tampilan Aplikasi

📦 **Download LaTera v1.0 APK:**  
🔗 [Download Aplikasi](https://github.com/kevinnaufaldany/LaTera/releases/download/v1.0-LaTera/LaTera.apk)

Berikut adalah tampilan antarmuka aplikasi **LaTera**:

<p align="center">
  <img src="https://github.com/user-attachments/assets/00f11474-2374-4986-9cb1-b46c03d89342" width="80%"/>
</p>


**Keterangan:**

- Gambar 1–2: Tampilan beranda dan menu layanan mahasiswa.
- Gambar 3–4: ChatBot dan fitur layanan FTI.
- Gambar 5: Menu akademik (SIAKAD & E-Learning).
- Gambar 6: Formulir pembuatan jadwal kuliah.

---

## 📜 Lisensi
Proyek ini merupakan bagian dari Tugas Besar Pengembangan Aplikasi Mobile di ITERA dan tersedia untuk keperluan edukasi.

---

## 👨‍💻 Developer Team (Kelompok 11)

*   Elma Nurul Fatika (122140069)  
*   Kevin Naufal Dany (122140222)  
*   Khoirul Rijal Wicaksono (122140234)  

---

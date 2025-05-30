# 📱 LaTera — Smart Campus Assistant for Informatics ITERA

**LaTera** adalah aplikasi Android Smart Campus Assistant yang dirancang khusus untuk mahasiswa Program Studi Teknik Informatika Institut Teknologi Sumatera (ITERA). Aplikasi ini bertujuan untuk menyederhanakan akses ke berbagai layanan akademik, administrasi, dan informasi penting kampus dalam satu platform modern.

---

## 🚀 Fitur Unggulan

- **ChatBot Navigasi Kampus**
  - Fitur interaktif (berbasis TensorFlow API) untuk panduan lokasi gedung secara real-time
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
| NLP Bot  | TensorFlow API         |
| Deep Link| Handle `oobCode` dari Intent |

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
🔗 [https://github.com/kevinnaufaldany/LaTera/releases/download/v1.0-LaTera/LaTera.apk](https://github.com/kevinnaufaldany/LaTera/releases/download/v1.0-LaTera/LaTera.apk)

Berikut adalah tampilan antarmuka aplikasi **LaTera**:

<p align="center">
  <img src="https://github.com/user-attachments/assets/058634f6-e6a6-4c2c-9843-919f0bbef032" width="45%" alt="Tampilan 1" />
  <img src="https://github.com/user-attachments/assets/1ff393df-d834-4013-9c76-6032bc5c8615" width="45%" alt="Tampilan 2" />
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/2a16f8c3-8310-4ebd-8c3e-6ffb5f76c202" width="45%" alt="Tampilan 3" />
  <img src="https://github.com/user-attachments/assets/52bdc2b0-5bf3-45ec-9847-40de837a501a" width="45%" alt="Tampilan 4" />
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/c897ea41-51bb-4b5f-9fe1-ae8d72bb9321" width="45%" alt="Tampilan 5" />
  <img src="https://github.com/user-attachments/assets/a477b548-f77d-4eb4-a90e-600360783471" width="45%" alt="Tampilan 6" />
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/5d5693bb-0bcb-4fe6-b01d-6807171364bf" width="45%" alt="Tampilan 7" />
</p>

**Keterangan:**

- Gambar 1–2: Tampilan beranda dan menu layanan mahasiswa.
- Gambar 3–4: ChatBot dan fitur layanan FTI.
- Gambar 5–6: Menu akademik (SIAKAD & E-Learning).
- Gambar 7: Formulir pembuatan jadwal kuliah.

---

## 📜 Lisensi
Proyek ini merupakan bagian dari Tugas Besar Pengembangan Aplikasi Mobile di ITERA dan tersedia untuk keperluan edukasi.

---

## 👨‍💻 Developer Team (Kelompok 11)

*   Elma Nurul Fatika (122140069)  
*   Kevin Naufal Dany (122140222)  
*   Khoirul Rijal Wicaksono (122140234)  

---

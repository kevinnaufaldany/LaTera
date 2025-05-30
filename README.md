# 📱 LaTera — Smart Campus Assistant for Informatics ITERA

**LaTera** adalah aplikasi Android Smart Campus Assistant yang dirancang khusus untuk mahasiswa Program Studi Teknik Informatika Institut Teknologi Sumatera (ITERA). Aplikasi ini bertujuan untuk menyederhanakan akses ke berbagai layanan akademik, administrasi, dan informasi penting kampus dalam satu platform modern.

---

## 🚀 Fitur Unggulan

- **Autentikasi Firebase**
  - Login, Register, Lupa Password, dan Reset Password via *deep link*
- **Navigasi Menu Modular**
  - Onboarding → Login/Register → Home → Fitur Akademik
- **Manajemen Profil**
  - Pengambilan nama dan NIM otomatis dari Firestore setelah login
- **Manajemen Kuliah**
  - Buat dan atur jadwal kuliah
- **Layanan Mahasiswa**
  - Akses cepat ke kalender akademik, form layanan, dll.
- **Layanan FTI ITERA**
  - Permintaan dokumen administratif
- **SIAKAD & E-Learning**
  - Shortcut menuju layanan akademik daring
- **ChatBot Navigasi Kampus**
  - Fitur interaktif (berbasis TensorFlow API) untuk panduan lokasi gedung secara real-time

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

## 👨‍💻 Developer Team (Kelompok 11)
----------------------------------

*   Elma Nurul Fatika (122140069)  
*   Kevin Naufal Dany (122140222)  
*   Khoirul Rijal Wicaksono (122140234)  

---

## 📌 Catatan
----------

*   Pastikan Anda sudah mengatur Firebase project dan `google-services.json`.  
*   OOBCode untuk reset password didukung melalui intent deep link.  
*   Layout telah disesuaikan untuk perangkat layar kecil-menengah dengan desain responsif menggunakan Compose.  

---

## 📜 Lisensi
----------

Proyek ini merupakan bagian dari Tugas Besar _Pengembangan Aplikasi Mobile_ di ITERA dan tersedia untuk keperluan edukasi.

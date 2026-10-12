# Anilynime Android App (Native Kotlin)

Aplikasi Android Native open-source untuk streaming anime dan donghua dengan multi-server streaming cepat.

- **Repositori**: [github.com/faloryn/anilynime-apkv](https://github.com/faloryn/anilynime-apkv)
- **Teknologi**: Kotlin, Retrofit, Coroutines, Glide, Material Components, GitHub Actions Cloud Build.

---

## ⚡ Build Otomatis (Tanpa Komputer Lokal)
Proyek ini dikonfigurasi dengan **GitHub Actions CI/CD** di [.github/workflows/build-apk.yml](.github/workflows/build-apk.yml).
Setiap ada pembaruan kode yang di-push ke branch `main`, GitHub server akan otomatis:
1. Memasang Android SDK & Java 17.
2. Mengkompilasi kode Kotlin dan dependensi.
3. Menghasilkan file APK siap install (`app-release.apk` atau `app-debug.apk`).
4. Mengunggah file APK ke tab **Releases** & **Artifacts** di repositori ini.

---

## 📥 Cara Download APK
1. Buka halaman repositori: [https://github.com/faloryn/anilynime-apkv/releases](https://github.com/faloryn/anilynime-apkv/releases)
2. Klik versi rilis terbaru dan unduh file `.apk`.
3. Pasang di smartphone Android Anda.

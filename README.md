<p align="center"><img src="assets/lin_pipe_banner.gif" width="760" alt="Lin Pipe"></p>

<p align="center">
<a href="https://www.gnu.org/licenses/gpl-3.0" alt="License: GPLv3"><img src="https://img.shields.io/badge/License-GPL%20v3-blue.svg"></a>
<img src="https://img.shields.io/badge/Android-6.0%2B-3DDC84.svg" alt="Android 6.0+">
</p>

<hr>
<p align="center"><a href="#tentang-lin-pipe">Tentang</a> &bull; <a href="#fitur">Fitur</a> &bull; <a href="#layanan-yang-didukung">Layanan</a> &bull; <a href="#persyaratan">Persyaratan</a> &bull; <a href="#membangun-aplikasi">Membangun</a> &bull; <a href="#kredit-dan-proyek-asli">Kredit</a> &bull; <a href="#lisensi">Lisensi</a> &bull; <a href="#english">English</a></p>
<hr>

> [!warning]
> **Lin Pipe adalah fork tidak resmi dari NewPipe dan tidak berafiliasi dengan tim NewPipe.**
> Aplikasi masih dalam tahap pengembangan sehingga bug mungkin ditemukan.
> Proyek aslinya ada di sini:
> - Repositori NewPipe: <https://github.com/TeamNewPipe/NewPipe>
> - Situs web NewPipe: <https://newpipe.net>
> - NewPipe Extractor (mesin pengambil konten): <https://github.com/TeamNewPipe/NewPipeExtractor>

## Tentang Lin Pipe

Lin Pipe adalah aplikasi pemutar video dan audio yang berjalan sepenuhnya di perangkat Anda, tanpa akun, tanpa layanan Google, dan tanpa pelacakan. Aplikasi ini dibangun di atas kode sumber NewPipe 0.29.3 dengan tampilan baru, tata letak baru, dan beranda yang terasa seperti situs video populer.

## Fitur

- **Beranda baru** dengan deretan chip kategori (Untuk Anda, Trending, Musik, Game, Berita, Film, Olahraga, Teknologi, Belajar, Memasak).
- **Rekomendasi dari riwayat tontonan.** Chip "Untuk Anda" mengambil video terkait dari tontonan terakhir Anda, menggabungkan, menghapus duplikat, dan tidak menampilkan video yang sudah Anda tonton. Jika riwayat masih kosong, yang tampil adalah halaman trending. Semua proses terjadi di perangkat, tidak ada profil yang dikirim ke server.
- **Topik dari pencarian Anda.** Kata kunci yang baru Anda cari otomatis muncul sebagai chip di beranda, tersimpan di perangkat saja.
- **Tarik untuk menyegarkan** beranda agar rekomendasi diacak ulang.
- **Tampilan baru yang segar**: garis tepi tebal, bayangan keras, warna cerah, dan tipografi tegas.
- **Mode terang, gelap, dan hitam** yang semuanya mempertahankan gaya yang sama.
- **Responsif**: kartu besar satu kolom di ponsel, grid beberapa kolom di tablet dan layar lebar, serta mendukung mode lanskap.
- **Animasi**: daftar video muncul dengan transisi halus, kartu dan chip memberi respons saat ditekan.
- **Bilah tab di bawah** secara bawaan agar mudah dijangkau satu tangan (bisa dipindah di Pengaturan > Tampilan).
- Semua kemampuan NewPipe tetap ada: pemutar latar belakang, jendela mengambang (picture-in-picture), unduhan, langganan tanpa akun, daftar putar, riwayat, subtitle, dan lainnya.

## Layanan yang didukung

Lin Pipe memakai NewPipe Extractor sehingga mendukung layanan yang sama dengan NewPipe:

* YouTube ([situs](https://www.youtube.com/)) dan YouTube Music ([situs](https://music.youtube.com/))
* PeerTube ([situs](https://joinpeertube.org/)) beserta semua instansnya
* Bandcamp ([situs](https://bandcamp.com/))
* SoundCloud ([situs](https://soundcloud.com/))
* media.ccc.de ([situs](https://media.ccc.de/))

## Rencana

- Dukungan untuk layanan tambahan seperti bilibili.tv sedang dipertimbangkan. Layanan baru memerlukan modul extractor tersendiri, sehingga belum tersedia saat ini.

## Persyaratan

- Android **6.0 (API 23)** atau lebih baru.

## Membangun aplikasi

Anda memerlukan JDK 21 dan Android SDK (compileSdk 37).

```bash
# build debug
./gradlew assembleDebug

# hasil APK
app/build/outputs/apk/debug/
```

Atau buka folder proyek dengan Android Studio, lalu jalankan konfigurasi `app`.

## Kredit dan proyek asli

Lin Pipe tidak akan ada tanpa kerja keras para pengembang NewPipe dan kontributornya. Jika Anda menyukai mesin di balik aplikasi ini, silakan kunjungi dan dukung proyek aslinya:

- **NewPipe**: <https://github.com/TeamNewPipe/NewPipe>
- **Situs NewPipe**: <https://newpipe.net>
- **Blog NewPipe**: <https://newpipe.net/blog/>
- **FAQ NewPipe**: <https://newpipe.net/FAQ/>
- **Donasi untuk NewPipe**: <https://newpipe.net/donate>
- **Kebijakan privasi NewPipe**: <https://newpipe.net/legal/privacy/>

"NewPipe" adalah merek dagang milik NewPipe e.V. dan tunduk pada [kebijakan merek dagangnya](https://newpipe-ev.de/policy/trademark/). Lin Pipe memakai nama dan ikon sendiri.

Aplikasi ini juga tidak boleh diunggah ke Google Play Store, karena melanggar ketentuan mereka sebagaimana juga dijelaskan oleh proyek NewPipe.

## Lisensi

[![GNU GPLv3](https://www.gnu.org/graphics/gplv3-127x51.png)](https://www.gnu.org/licenses/gpl-3.0.en.html)

Lin Pipe adalah perangkat lunak bebas (copyleft): Anda dapat menggunakan, mempelajari, membagikan, dan memperbaikinya. Anda dapat menyebarluaskan dan/atau mengubahnya di bawah ketentuan GNU General Public License versi 3 atau (sesuai pilihan Anda) versi yang lebih baru, sebagaimana diterbitkan oleh Free Software Foundation. Karena berasal dari NewPipe, setiap perubahan juga harus tetap berlisensi GPLv3 dan kode sumbernya harus tersedia.

---

## English

**Lin Pipe** is an unofficial fork of [NewPipe](https://github.com/TeamNewPipe/NewPipe) 0.29.3: a lightweight, libre streaming front-end for Android with a new look, a new layout and a YouTube-like home screen.

- **Home screen** with category chips (For you, Trending, Music, Gaming, News, Movies, Sports, Tech, Learning, Cooking).
- **Recommendations from your watch history**, computed locally on your device: related videos of what you watched recently, merged, de-duplicated and shuffled. Falls back to Trending when your history is empty.
- **Topics from your recent searches** appear as chips on the home screen, and **pull to refresh** reshuffles the feed.
- **Fresh new design** with thick outlines, hard shadows and bright colors, in light, dark and black modes.
- **Responsive layout**: big cards on phones, multi-column grid on tablets and landscape.
- **Animations** for lists, cards and chips.
- **Requires Android 6.0 (API 23) or newer.**
- Everything NewPipe offers is still there: background play, popup player, downloads, accountless subscriptions, playlists, history and more.

Upstream projects and credits:

- NewPipe: <https://github.com/TeamNewPipe/NewPipe>
- NewPipe website: <https://newpipe.net>
- NewPipe Extractor: <https://github.com/TeamNewPipe/NewPipeExtractor>

Licensed under the [GNU GPL v3 or later](https://www.gnu.org/licenses/gpl-3.0).

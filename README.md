# Document Searcher
Tugas Besar IF4042 Sistem Temu Balik Informasi.

# File Properties untuk testing
1. Buatlah file `backend/src/main/test/testConfig.properties`
2. Isi file tersebut seperti ini <br/>
```
ADIDocSet = <path ke dataset ADI, contoh D:\\source\\DocumentSearcher\\dataset\\ADI\\adi.all >
ADITestDocuments = <path ke test documents ADI, contoh D:\\source\\DocumentSearcher\\dataset\\ADI\\test.obj >
CISIDocSet = <patha ke dataset CISI, contoh D:\\source\\DocumentSearcher\\dataset\\CISI\\cisi.all >
```

# Setting IDEA untuk menggunakan testNG
1. buka File > Project Structure
2. buka Module, tambahkan jar file.
3. buka folder dimana anda menginstall Intellij IDEA, misalnya: D:\Program Files (x86)\JetBrains\IntelliJ IDEA 14.1.5
4. buka folder plugins/testng/lib
5. pilih testng.jar


# Setting Play Framework 2.X untuk Intellij IDEA
1. Download Scala (File > Settings > Plugins > Install JetBrains plugin > Gunakan keyword "play")
2. Restart Intellij IDEA
3. Buka File > New > project from existing sources > documentSearcher
4. Pilih import project from external model (SBT)
5. Klik OK
6. Klik kanan di file app/controllers/Application.java kemudian Run Play 2 App
7. Buka browser, masukkan alamat localhost:9000


# NOTE
Update terus bower package di folder public
```
cd public && sudo bower update
```

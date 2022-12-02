<a name="readme-top"></a>
<br />
<div align="center">
  <a href="https://gitlab.informatika.org/if3110-2022-k01-02-32/ngnotify-soap-service">
    <img src="assets/ngnotify.png" alt="Logo">
  </a>

  <h3 align="center">Ngnotify SOAP Service</h3>

  <p align="center">
    :indonesia: Backend Subscription Lagu Karya Anak Bangsa :indonesia:<br>
    Penantang Search Engine Gatot Kaca Sebagai Karya Terbaik Anak Bangsa
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#dependencies">Dependencies</a></li>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#contributions">Contributions</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

Ngnotify SOAP Service merupakan backbone kedua dari layanan ngnotify premium app. SOAP service menyimpan data subscription antara user dan penyanyi. Web app ini dibuat untuk memenuhi ~Tugas Besar 2 IF3110 Teknik Informatika ITB~ panggilan negara untuk mencintai produk karya anak bangsa. Karena itu, kami membuat sebuah produk lokal agar terdapat produk Indonesia untuk dicintai :heart:heart:heart

Sebagai backbone dari Ngnotify Premium yang membantu Ngnotify app dalam menyaingi  Search Engine Gatot Kaca untuk gelar aplikasi anak bangsa terbaik, Ngnotify SOAP Service memiliki fitur yang sangat ~tidak~ lengkap. Ngnotify SOAP Service memiliki endpoint-endpoint yang dapat dipanggil oleh service lain untuk mendapatkan subscription, subscription, dan subscription.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Built With

* Java-JDK 11
* Maven

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- GETTING STARTED -->
## Getting Started

Tanpa Berlama-lama inilah cara menjalankan web app

### Dependencies
Agar Program dapat berjalan dengan baik, silahkan install juga repo-repo berikut:

```
https://gitlab.informatika.org/if3110-2022-k01-02-32/ngnotify-app
```
```
https://gitlab.informatika.org/if3110-2022-k01-02-32/ngnotify-premium-app
```
```
https://gitlab.informatika.org/if3110-2022-k01-02-32/ngnotify-rest-service
```

### Prerequisites

* Docker
    ```sh
    https://www.docker.com/
    ```
* Git
    ```sh
    https://git-scm.com/downloads
    ```
* Maven
    ```sh
    https://maven.apache.org/download.cgi
    ```
* Java JDK 11
    ```sh
    https://www.oracle.com/java/technologies/javase-jdk11-downloads.html
    ```
* Mailtrap (bikin user)
    ```sh
    https://mailtrap.io/
    ```
* Internet (karena Database online)
* Kesabaran (karena akses data sering kali lemot - <i>maklum gratis</i>)
* Jika ingin mengubah database menjadi local, silahkan konfigurasi di
    ```sh
    webservice/src/main/java/ngnotify/services/DB.java
    ```
setelah itu silahkan setting sendiri databasenya dari file dump :smile:. Jika tidak bisa, silahkan tanya yang bisa (Baca: google/stackoverflow/temen~kalo punya~)

### Installation



1. Clone Repo ini dengan menggunakan cmd pada directory yang diinginkan
    ```sh
    C:\Users\MerekLaptop> 
    git clone https://gitlab.informatika.org/if3110-2022-k01-02-32/ngnotify-soap-service
    ```
2. Buka Directory tempat file di clone
   ```sh
   C:\Users\MerekLaptop> cd ngnotify-soap-service
   C:\Users\MerekLaptop\ngnotify-soap-service>
   ```
3. Konfigurasi Mailtrap
    ```
    Buka file webservice/src/main/java/ngnotify/services/Sendmail.java
    Ubah bagian Configuration dengan data dari mailtrap
    ```
4. CARA 1 MELALUI DOCKER: Jalankan docker (GAGAL DI CALLBACK)
   ```sh
   C:\Users\MerekLaptop\ngnotify-soap-service>
   docker-compose up -d
   ```
   CARA 2 MELALUI LOCAL: menggunakan maven
    1. Pindah ke directory webservice
        ```sh
        C:\Users\MerekLaptop\ngnotify-soap-service> cd webservice
        C:\Users\MerekLaptop\ngnotify-soap-service\webservice>
        ```
    2.  Jalankan maven
        ```sh
        C:\Users\MerekLaptop\ngnotify-soap-service\webservice> mvn clean install
        ```
    3. Jalankan Main
        ```sh
        Buka file webservice/src/main/java/ngnotify/services/App.java
        Gunakan Code Runner (run Java) untuk menjalankan file tersebut
        ```


4. Ngnotify SOAP Service siap untuk dipakai secara terbatas 
5. Untuk menggunakan seluruh layanan web, jalankan seluruh (<a href="#dependencies">dependencies</a>) yang ada
6. Jika dependencies sudah dijalankan, Ngnotify Soap Service siap untuk dipakai secara full :smile:
<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- USAGE EXAMPLES -->
## Usage

1. Service dipanggil melalui Ngnotify Premium App ataupun Ngnotify App
2. Jika ingin memanggil service secara langsung, Anda harus mengetahui SOAP Messagenya. Disarankan untuk menggunakan SOAP UI untuk menggenerate message dari file wsdl
3. Untuk mendapatkan wsdl, silahkan buka 
    ```
    http://localhost:8070/webservice/ngnotify?wsdl
    ```
    Jangan lupa bahwa file tersebut hanya dapat diakses jika SOAP Service sudah dijalankan
4. Silahkan gunakan message yang telah digenerate tersebut untuk memanggil service


<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- CONTRIBUTING -->
## Contributions
|feature|13520019|13520034|13520040|
|-------|--------|--------|--------|
|DB|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|
|HTTP|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|
|Ngnotify|:heavy_check_mark:|heavy_check_mark|:heavy_check_mark:|
|SendEmail|:heavy_check_mark:|heavy_check_mark|:heavy_check_mark:|

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- LICENSE -->
## License

Distributed under the NG License. See you again for more information.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTACT -->
## Contact
|Name|Contact|Github|
|----|-------|------|
|Maharani Ayu Putri Irawan | 13520019@std.stei.itb.ac.id| <a href="https://www.github.com/rannnayy">@rannnayy</a>|
|Bryan Bernigen | 13520034@std.stei.itb.ac.id |<a href="https://www.github.com/Nk-Kyle">@Nk-Kyle</a>|
|Ng Kyle | 13520040@std.stei.itb.ac.id|<a href="https://www.github.com/bryanbernigen">@bryanbernigen</a>|

<p align="right">(<a href="#readme-top">back to top</a>)</p>

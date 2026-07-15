# Caliak Coklat Backend
Backend untuk **Caliak Coklat**, sebuah sistem monitoring perkebunan kakao berbasis Internet of Things (IoT). Repository ini berisi seluruh layanan backend yang dibangun menggunakan arsitektur microservices untuk mengelola autentikasi, perangkat, data sensor, notifikasi, dashboard, serta pelaporan.

## Latar Belakang
Caliak Coklat dikembangkan sebagai solusi untuk membantu petani memantau kondisi lingkungan perkebunan kakao secara lebih mudah. Data dari berbagai sensor dikirim oleh perangkat IoT ke backend, kemudian diproses dan disajikan melalui aplikasi web sehingga pengguna dapat memantau kondisi lahan secara real-time maupun historis.

## Fitur
* Autentikasi pengguna menggunakan JWT
* API Gateway sebagai pintu masuk seluruh layanan
* Service Discovery menggunakan Eureka
* Manajemen perangkat IoT
* Pencatatan data sensor
* Dashboard monitoring
* Sistem notifikasi via Whatsapp
* Grafik data mingguan
* Riwayat notifikasi
* Monitoring aplikasi menggunakan Prometheus, Grafana, dan ELK Stack

## Arsitektur
Project ini menggunakan arsitektur **Microservices**

Layanan yang tersedia:

* API Gateway
* Authentication Service
* Eureka Server
* Device Service
* Dashboard Service
* Notification Service
* Suhu Tanah Service
* Suhu Udara Service
* Kelembapan Tanah Service
* Kelembapan Udara Service
* pH Tanah Service
* Weekly Report Service

## Teknologi yang Digunakan
### Backend:
* Java
* Spring Boot
* Spring Security
* Spring Cloud Gateway
* Spring Cloud Netflix Eureka
* Spring Data JPA
### Database:
* MySQL
### Message Broker:
* RabbitMQ
### Monitoring:
* Prometheus
* Grafana
* ELK Stack
### Deployment:
* Docker
* Docker Compose
### IoT:
* ESP32
* REST API

## Struktur Project

```text
CaliakCoklat-Backend
├── api-gateway
├── authentication-service
├── dashboard-service
├── deployment
├── device-service
├── eureka
├── monitoring
├── notification-service
├── ph-tanah-service
├── suhu-tanah-service
├── suhu-udara-service
├── kelembapan-tanah-service
├── kelembapan-udara-service
└── weekly-report-service
```

## Status Project

Project ini dikembangkan sebagai bagian dari **Project Based Learning (PBL)** di **Politeknik Negeri Padang** dan masih terus dikembangkan untuk penambahan fitur serta penyempurnaan sistem.

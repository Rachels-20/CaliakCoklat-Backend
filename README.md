Caliak Coklat Backend

Backend untuk Caliak Coklat, sebuah sistem monitoring perkebunan kakao berbasis Internet of Things (IoT). Repository ini berisi seluruh layanan backend yang dibangun menggunakan arsitektur microservices untuk mengelola autentikasi, perangkat, data sensor, notifikasi, dashboard, serta pelaporan.

Latar Belakang
Caliak Coklat dikembangkan sebagai solusi untuk membantu petani memantau kondisi lingkungan perkebunan kakao secara lebih mudah. Data dari berbagai sensor dikirim oleh perangkat IoT ke backend, kemudian diproses dan disajikan melalui aplikasi web sehingga pengguna dapat memantau kondisi lahan secara real-time maupun historis.

Fitur
1. Autentikasi pengguna menggunakan JWT
2. API Gateway sebagai pintu masuk seluruh layanan
3. Service Discovery menggunakan Eureka
4. Manajemen perangkat IoT
5. Pencatatan data sensor
6. Dashboard monitoring
7. Sistem notifikasi via Whatsapp
8. Grafik data mingguan
9. Riwayat notifikasi
10. Monitoring aplikasi menggunakan Prometheus, Grafana, dan ELK Stack

Arsitektur
Project ini menggunakan arsitektur Microservices

Layanan yang tersedia:

-API Gateway
-Authentication Service
-Eureka Server
-Device Service
-Dashboard Service
-Notification Service
-Suhu Tanah Service
-Suhu Udara Service
-Kelembapan Tanah Service
-Kelembapan Udara Service
-pH Tanah Service
-Weekly Report Service

Teknologi yang Digunakan
Backend:
-Java
-Spring Boot
-Spring Security
-Spring Cloud Gateway
-Spring Cloud Netflix Eureka
-Spring Data JPA
Database:
-MySQL
Message Broker:
-RabbitMQ
Monitoring:
-Prometheus
-Grafana
-ELK Stack
Deployment:
-Docker
-Docker Compose
IoT
-ESP32
-REST API

Struktur Project

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

Status Project

Project ini dibuat sebagai bagian dari Project Based Learning (PBL) di Politeknik Negeri Padang.
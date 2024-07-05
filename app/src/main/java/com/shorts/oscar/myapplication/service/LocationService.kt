package com.shorts.oscar.myapplication.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.shorts.oscar.myapplication.MainActivity
import com.shorts.oscar.myapplication.R

//Этот класс представляет собой службу (Service) в Android, которая отслеживает местоположение пользователя с помощью FusedLocationProviderClient.

class LocationService : Service() {

    // Клиент для получения данных о местоположении
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Обратный вызов для обработки результатов местоположения
    private lateinit var locationCallback: LocationCallback

    // Последнее известное местоположение пользователя
    private var lastLocation: Location? = null

    // Статические константы для ключей и намерений
    companion object {
        const val LOCATION_UPDATE = "com.example.LOCATION_UPDATE"
        const val EXTRA_LATITUDE = "latitude"
        const val EXTRA_LONGITUDE = "longitude"
        const val EXTRA_SPEED = "speed"
        const val EXTRA_DISTANCE = "distance"
        const val EXTRA_SATELLITES = "satellites"
    }

    override fun onCreate() {
        super.onCreate()

        // Инициализация клиента для получения данных о местоположении
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Инициализация обратного вызова для обработки результатов местоположения
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    handleNewLocation(location)
                }
            }
        }

        // Запуск службы в режиме foreground с уведомлением
        startForegroundService()

        // Начало получения обновлений местоположения
        startLocationUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()

        // Остановка получения обновлений местоположения
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    // Обработка нового местоположения
    private fun handleNewLocation(location: Location) {
        // Извлечение данных о местоположении
        val latitude = location.latitude
        val longitude = location.longitude
        val speed = location.speed
        val satellites = location.extras?.getInt("satellites") ?: 0

        // Вычисление расстояния до предыдущего местоположения
        var distance = 0.0f
        lastLocation?.let {
            distance = it.distanceTo(location)
        }

        // Обновление последнего известного местоположения
        lastLocation = location

        // Отправка данных о местоположении через локальный Broadcast
        val intent = Intent(LOCATION_UPDATE).apply {
            putExtra(EXTRA_LATITUDE, latitude)
            putExtra(EXTRA_LONGITUDE, longitude)
            putExtra(EXTRA_SPEED, speed)
            putExtra(EXTRA_DISTANCE, distance)
            putExtra(EXTRA_SATELLITES, satellites)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    // Запуск службы в режиме foreground
    private fun startForegroundService() {
        // Создание канала уведомлений для службы
        val channelId = "location_channel"
        val channelName = "Location Service"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)


        // Создаем PendingIntent для открытия активности приложения
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Создание уведомления для службы
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Location Service")
            .setContentText("Tracking your location")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent) // Устанавливаем PendingIntent
            .build()

        // Запуск службы в режиме foreground с уведомлением
        startForeground(1, notification)
    }

    // Начало получения обновлений местоположения
    private fun startLocationUpdates() {
        // Создание запроса на обновления местоположения
        val locationRequest = LocationRequest.create().apply {
            interval = 5000 // Интервал обновлений
            fastestInterval = 3000 // Наименьший интервал обновлений
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY // Высокая точность
        }

        // Проверка разрешений на доступ к местоположению
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // Запрос на получение обновлений местоположения
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}

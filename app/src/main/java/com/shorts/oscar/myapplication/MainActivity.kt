package com.shorts.oscar.myapplication

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import com.shorts.oscar.myapplication.databinding.ActivityMainBinding
import com.shorts.oscar.myapplication.presentation.utils.PermissionHelper
import com.shorts.oscar.myapplication.service.LocationService

//Класс контейнер для фрагментов, содержащий боковое меню и меню тулбара
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var permissionHelper: PermissionHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.apply {
            itemIconTintList = null
        }
        permissionHelper = PermissionHelper(this)
        permissionHelper.initPermissionLauncher(this,
            onGranted = {
                // Код, который нужно выполнить, если разрешения предоставлены
                startLocationService()
            },
            onDenied = {
                // Код, который нужно выполнить, если разрешения отклонены
            }
        )
        // Запрос разрешений
        permissionHelper.requestPermissions()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    //Функция запускает сервис
    private fun startLocationService() {
        val intent = Intent(this, LocationService::class.java)
        startForegroundService(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_start_service -> {
                checkPermission("start")
                true
            }
            R.id.action_stop_service -> {
                checkPermission("stop")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //Функция проверки на разрешения
    private fun checkPermission(isStartOrStop : String){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionHelper.showSettingsDialog()
        } else{
            if (isStartOrStop == "start"){
                if (!checkService()){
                    startLocationService()
                    return
                }
            } else {
                if (checkService()){
                    stopLocationService()
                }
            }
        }
    }

    //Функция проверки запущен сервис или нет
    private fun checkService() : Boolean{
        val isServiceRunning = isLocationServiceRunning(LocationService::class.java)
        if (isServiceRunning) {
            // Служба запущена
            Log.d("YourActivity", "LocationService is running")
            return true
        } else {
            // Служба не запущена
            Log.d("YourActivity", "LocationService is not running")
            return false
        }
    }

    //Функция остановки сервиса
    private fun stopLocationService() {
        val intent = Intent(this, LocationService::class.java)
        stopService(intent)
    }

    //Проверка запущен ли сервис
    private fun isLocationServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}
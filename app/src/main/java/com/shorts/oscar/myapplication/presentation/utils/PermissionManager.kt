package com.shorts.oscar.myapplication.presentation.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class PermissionHelper(private val context: Context) {

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    private var onPermissionsGranted: (() -> Unit)? = null
    private var onPermissionsDenied: (() -> Unit)? = null

    private val requiredPermissions = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.POST_NOTIFICATIONS
    )

    //Инициализация разрешений для фрагментов
    fun initPermissionLauncher(
        fragment: Fragment,
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ) {
        onPermissionsGranted = onGranted
        onPermissionsDenied = onDenied
        requestPermissionLauncher = fragment.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            handlePermissionsResult(permissions)
        }
    }

    //Инициализация разрешений для активити
    fun initPermissionLauncher(
        activity: androidx.appcompat.app.AppCompatActivity,
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ) {
        onPermissionsGranted = onGranted
        onPermissionsDenied = onDenied

        requestPermissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            handlePermissionsResult(permissions)
        }
    }

    private fun handlePermissionsResult(permissions: Map<String, Boolean>) {
        val allPermissionsGranted = permissions.all {
            it.value
        }
        if (allPermissionsGranted) {
            Log.d("PermissionHelper", "Все разрешения предоставлены")
            onPermissionsGranted?.invoke()
        } else {
            Log.d("PermissionHelper", "Разрешения отклонены")
            showSettingsDialog()
        }
    }

    private fun arePermissionsGranted(): Boolean {
        return requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    //Запрос на разрешения
    fun requestPermissions() {
        if (::requestPermissionLauncher.isInitialized) {
            if (arePermissionsGranted()) {
                Log.d("PermissionHelper", "Все разрешения уже предоставлены")
                onPermissionsGranted?.invoke()
            } else {
                Log.d("PermissionHelper", "Запрос разрешений")
                requestPermissionLauncher.launch(requiredPermissions)
            }
        } else {
            Log.e("PermissionHelper", "requestPermissionLauncher не инициализирован")
        }
    }

    //Вызов диалога с переходом в настройки
    fun showSettingsDialog() {
        AlertDialog.Builder(context)
            .setTitle("Требуются разрешения")
            .setMessage("Для корректной работы приложения необходимо предоставить доступ к местоположению. Вы можете включить их в настройках.")
            .setPositiveButton("Перейти в настройки") { _, _ ->
                context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = android.net.Uri.fromParts("package", context.packageName, null)
                })
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
                onPermissionsDenied?.invoke()
            }
            .show()
    }
}







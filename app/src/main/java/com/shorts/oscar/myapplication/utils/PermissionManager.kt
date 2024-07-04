package com.shorts.oscar.myapplication.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.appcompat.app.AlertDialog

class PermissionManager(private val context: Activity) {

    // Проверка наличия разрешений
    fun checkPermissions(vararg permissions: String): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    // Запрос разрешений
    fun requestPermissions(vararg permissions: String) {
        ActivityCompat.requestPermissions(context, permissions, PERMISSION_REQUEST_CODE)
    }

    // Обработка результатов запроса разрешений
    fun handlePermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                onGranted.invoke()
            } else {
                onDenied.invoke()
            }
        }
    }

    // Показать диалог с предложением перейти в настройки
    fun showGoToSettingsDialog() {
        AlertDialog.Builder(context)
            .setTitle("Доступ к местоположению")
            .setMessage("Для использования приложения необходимо разрешение доступа " +
                    "к местоположению. Перейдите в настройки и предоставьте разрешение.")
            .setPositiveButton("Перейти") { _, _ ->
                val intent = Intent().apply {
                    action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    companion object {
        const val PERMISSION_REQUEST_CODE = 1001
    }
}


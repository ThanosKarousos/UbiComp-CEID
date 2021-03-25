package com.example.ubicomp.Utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.icu.lang.UCharacter
import android.os.Build
import androidx.annotation.RequiresApi
import pub.devrel.easypermissions.EasyPermissions

object Permissions {

    fun getPerms(): Array<String> {
        return this.perms
    }

    private val perms = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACTIVITY_RECOGNITION
    )

    fun hasRequiredPermissions(context: Context): Boolean {
        var result = false
        perms.forEach {
            result = result && EasyPermissions.hasPermissions(context, it)
        }
        return result //EasyPermissions.hasPermissions(context, *perms)
    }

    fun hasFineLocationPermission(act: Activity): Boolean {
        return EasyPermissions.hasPermissions(act, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    fun hasActivityRecognitionPermission(act: Activity): Boolean {
        return EasyPermissions.hasPermissions(act, Manifest.permission.ACTIVITY_RECOGNITION)
    }

    fun requestFineLocationPermission(act: Activity) {
        EasyPermissions.requestPermissions(act, "Provide Fine Location Permission to use this Service", Constants.FINE_LOCATION_PERMISSION_REQUEST_CODE, perms[0])
    }
    fun requestActivityRecognitionPermission(act: Activity) {
        EasyPermissions.requestPermissions(act, "Provide Activity Recognition Permission to use this Service", Constants.ACTIVITY_RECOGNITION_PERMISSION_REQUEST_CODE, perms[1])
    }

    fun requestAllPermissions(act: Activity) {
        requestFineLocationPermission(act)
        requestActivityRecognitionPermission(act)
    }
}


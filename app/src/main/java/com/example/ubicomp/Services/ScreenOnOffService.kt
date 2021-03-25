package com.example.ubicomp.Services

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import androidx.annotation.Nullable
import com.example.ubicomp.ScreenOnOffReceiver


class ScreenOnOffService : Service() {
    private var mScreenReceiver: ScreenOnOffReceiver? = null
    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        registerScreenStatusReceiver()
        Log.i("ScreenOnOffService", "ScreenOnOffService created")
    }

    override fun onDestroy() {
        //unregisterScreenStatusReceiver()
        Log.i("ScreenOnOffService", "ScreenOnOffService destroyed")
    }

    // TODO Consider adding all services in one bind service
    private fun registerScreenStatusReceiver() {
        mScreenReceiver = ScreenOnOffReceiver()
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        filter.addAction(Intent.ACTION_SCREEN_ON)
        registerReceiver(mScreenReceiver, filter)
    }

    private fun unregisterScreenStatusReceiver() {
        try {
            if (mScreenReceiver != null) {
                unregisterReceiver(mScreenReceiver)
            }
        } catch (e: IllegalArgumentException) {
        }
    }
}
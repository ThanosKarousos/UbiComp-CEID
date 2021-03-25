package com.example.ubicomp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class ScreenOnOffReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        if (intent.action.equals(Intent.ACTION_SCREEN_OFF)) {
            Log.i("ScreenOnOffReceiver", "Screen Off")
        } else if (intent.action.equals(Intent.ACTION_SCREEN_ON)) {
            Log.i("ScreenOnOffReceiver", "Screen On")
        }
    }
}
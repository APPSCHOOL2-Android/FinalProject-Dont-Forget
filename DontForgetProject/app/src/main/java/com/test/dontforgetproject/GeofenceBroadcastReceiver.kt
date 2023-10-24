package com.test.dontforgetproject

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent != null && !geofencingEvent.hasError()) {
            val transitionType = geofencingEvent.geofenceTransition
            if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) {
                // Geofence 진입 이벤트 처리
                // 알림 표시 등의 작업 수행
                showNotification(context, "진입 이벤트 발생")
                Log.d("Geofence", "Geofence 진입 이벤트 발생")
            } else if (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
                // Geofence 이탈 이벤트 처리
                // 알림 표시 등의 작업 수행
                showNotification(context, "이탈 이벤트 발생")
                Log.d("Geofence", "Geofence 이탈 이벤트 발생")
            }
        }
    }

    private fun showNotification(context: Context, message: String) {
        // 알림을 생성하고 표시하는 코드를 작성
        // NotificationCompat.Builder를 사용하여 알림을 생성하고 NotificationManager를 사용하여 표시
        // 자세한 알림 생성 및 표시 방법은 Android 개발 문서를 참조하세요.
    }
}

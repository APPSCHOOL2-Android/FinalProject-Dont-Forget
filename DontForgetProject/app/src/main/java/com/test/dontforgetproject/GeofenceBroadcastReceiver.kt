package com.test.dontforgetproject

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if(geofencingEvent!!.hasError()){
            Log.e("지오펜싱","broadcast오류")
        }else{
            val transitionType = geofencingEvent.geofenceTransition
            if(transitionType == Geofence.GEOFENCE_TRANSITION_ENTER){
                Log.e("지오펜싱","목표에 들어옴")
            }else if(transitionType == Geofence.GEOFENCE_TRANSITION_EXIT){
                Log.e("지오펜싱","목표에서 나감")
            }
        }
    }
}
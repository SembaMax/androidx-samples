/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.semba.androidsamples.LocationUpdatesWithBroadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log

import com.google.android.gms.location.LocationResult
import com.semba.androidsamples.Helper.PagedApplication
import com.semba.androidsamples.Shared.NotificationManager
import javax.inject.Inject

/**
 * Receiver for handling location updates.
 *
 * For apps targeting API level O
 * [android.app.PendingIntent.getBroadcast] should be used when
 * requesting location updates. Due to limits on background services,
 * [android.app.PendingIntent.getService] should not be used.
 *
 * Note: Apps running on "O" devices (regardless of targetSdkVersion) may receive updates
 * less frequently than the interval specified in the
 * [com.google.android.gms.location.LocationRequest] when the app is no longer in the
 * foreground.
 */
class LocationUpdatesBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationManager: NotificationManager

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if (ACTION_PROCESS_UPDATES == action) {
                val result = LocationResult.extractResult(intent)
                if (result != null) {
                    (context.applicationContext as PagedApplication).utilsComponent.inject(this)
                    val locations = result.locations
                    val locationResultHelper = LocationResultHelper(context, locations)
                    locationResultHelper.saveResults()
                    notificationManager.makeStatusNotification(locationResultHelper.locationResultTitle, locationResultHelper.locationResultText, context)
                }
            }
        }
    }

    companion object {
        private val TAG = "LUBroadcastReceiver"

        internal val ACTION_PROCESS_UPDATES =
            "com.google.android.gms.location.sample.backgroundlocationupdates.action" + ".PROCESS_UPDATES"
    }
}

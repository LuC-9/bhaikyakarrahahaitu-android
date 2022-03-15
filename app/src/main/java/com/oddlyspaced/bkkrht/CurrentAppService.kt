package com.oddlyspaced.bkkrht

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class CurrentAppService: AccessibilityService() {

    companion object {
        const val TAG = "CurrentAppService"
    }

    private val savedPackages = arrayListOf<String>()
    private var currentFocusedPackage = ""

    override fun onServiceConnected() {
        Log.d(TAG, "Service Connected!")
        savedPackages.addAll(SharedPreferenceManager(applicationContext).readAppsList())
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // Log.d(TAG, "Accessibility Event!")
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (event.packageName != null && event.className != null) {
                val componentName = ComponentName(event.packageName.toString(), event.className.toString())
                val activityInfo = getActivityName(componentName)
                activityInfo?.let { info ->
                    val tempPackageName = info.packageName.trim()
                    if (tempPackageName.isNotEmpty() && currentFocusedPackage != tempPackageName) {
                        currentFocusedPackage = tempPackageName
                        if (savedPackages.contains(currentFocusedPackage)) {
                            Log.d(TAG, "Saved Package in context: $currentFocusedPackage")
                            // bhai kya kar raha hai tu ?
                            startActivity(Intent(applicationContext,BhaiActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            })
                        }
                    }
                }
            }
        }
    }

    private fun getActivityName(componentName: ComponentName): ActivityInfo? {
        return try {
            packageManager.getActivityInfo(componentName, 0)
        }
        catch (e: PackageManager.NameNotFoundException) {
            // e.printStackTrace()
            null
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "Service Interrupted!")
    }
}
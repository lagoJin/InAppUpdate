package kr.lago.app.update

import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.ActivityResult.RESULT_IN_APP_UPDATE_FAILED
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

class AppUpdate(private val activity: Activity, private val type: TYPE) :
    InstallStateUpdatedListener {

    private val TAG = "InAppUpdate"

    enum class TYPE(val value: Int) {
        FLEXIBLE(0),
        IMMEDIATE(1)
    }

    companion object {
        const val REQUEST_UPDATE_CODE = 1000
    }

    private lateinit var appUpdateManager: AppUpdateManager

    fun initAppUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(activity)

        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(type.value)
            ) {
                requestUpdate(activity, appUpdateInfo, type.value)
            }
        }
        appUpdateManager.registerListener(this)

    }

    private fun requestUpdate(activity: Activity, appUpdateInfo: AppUpdateInfo, type: Int) {
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            type,
            activity,
            REQUEST_UPDATE_CODE
        )
    }

    override fun onStateUpdate(state: InstallState) {
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            popupSnackBarForCompleteUpdate(activity.window.decorView)
        }
    }

    private fun popupSnackBarForCompleteUpdate(view: View) {
        Snackbar.make(
            view,
            "An Update Has Just Been Downloaded",
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction("RESTART") { appUpdateManager.completeUpdate() }
            show()
            appUpdateManager.unregisterListener(this@AppUpdate)
        }
    }

    fun activityResult(requestCode: Int, resultCode: Int) {
        if (requestCode == REQUEST_UPDATE_CODE) {
            when (resultCode) {
                RESULT_OK -> {
                    //The user has accepted the update. For immediate updates, you might not receive
                    // this callback because the update should already be completed by
                    // Google Play by the time the control is given back to your app.
                    showToast(activity, "App Update Completed")
                    Log.d(TAG, "App Update Completed")
                }
                RESULT_CANCELED -> {
                    //The user has denied or cancelled the update.
                    showToast(activity, "App Update Canceled")
                    Log.d(TAG, "App Update Completed")
                }
                RESULT_IN_APP_UPDATE_FAILED -> {
                    //Some other error prevented either the user from providing consent or the update to proceed.
                    showToast(activity, "App Update Failed")
                    Log.d(TAG, "App Update Failed")
                }
            }
        }
    }


}
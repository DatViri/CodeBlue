package leo.me.la.codeblue

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import android.content.IntentSender
import com.google.android.gms.common.api.ResolvableApiException
import android.app.Activity
import android.app.AlertDialog


class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestGPSEnabled()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            1000 -> when (resultCode) {
                Activity.RESULT_OK -> {
                    BluetoothAdapter.getDefaultAdapter().enable()
                    startActivity(Intent(this, AugmentedImageActivity::class.java))
                    finish()
                }
                Activity.RESULT_CANCELED -> {
                    AlertDialog.Builder(this).setMessage(getString(R.string.request_gps))
                        .setPositiveButton(
                            getString(R.string.enable_gps)
                        ) { dialog, _ ->
                            dialog.dismiss()
                            requestGPSEnabled()
                        }
                        .setNegativeButton(
                            getString(R.string.quit)
                        ) { _, _ ->
                            System.exit(0)
                        }
                        .show()
                }
                else -> {
                }
            }
        }
    }

    private fun requestGPSEnabled() {
        val locationRequest = LocationRequest.create()
            .also {
                it.priority = LocationRequest.PRIORITY_LOW_POWER
                it.interval = 15 * 60 * 1000
                it.fastestInterval = 2 * 60 * 1000
            }
        val locationSettingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)
            .build()

        LocationServices.getSettingsClient(this)
            .checkLocationSettings(locationSettingsRequest)
            .addOnSuccessListener {
                BluetoothAdapter.getDefaultAdapter().enable()
                startActivity(Intent(this, AugmentedImageActivity::class.java))
                finish()
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException){
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        exception.startResolutionForResult(this@DashboardActivity, 1000)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }
                }
            }
    }
}

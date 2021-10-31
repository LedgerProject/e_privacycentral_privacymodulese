package foundation.e.eprivacymoduledemo

import android.app.AppOpsManager
import android.app.AppOpsManager.*
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Process.myUid
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import foundation.e.eprivacymoduledemo.databinding.ActivityMainBinding
import foundation.e.privacymodules.location.FakeLocation
import foundation.e.privacymodules.location.IFakeLocation
import foundation.e.privacymodules.permissions.PermissionsPrivacyModule
import foundation.e.privacymodules.permissions.data.AppOpModes
import foundation.e.privacymodules.permissions.data.ApplicationDescription

class MainActivity : AppCompatActivity() {

    private val fakeLocationModule: IFakeLocation by lazy { FakeLocation(this) }
    private val permissionsModule by lazy { PermissionsPrivacyModule(this) }

    private lateinit var binding: ActivityMainBinding

    private val appDesc by lazy {
        ApplicationDescription(
            packageName = packageName,
            uid = myUid(),
            label = getString(R.string.app_name),
            icon = null
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        getActionBar()?.setDisplayHomeAsUpEnabled(true);

        binding.view = this
        updateData("")
    }

    override fun onResume() {
        super.onResume()
        updateData("")
    }



    fun updateData(mockedLocation: String) {
        binding.granted = permissionsModule.getAppOpMode(appDesc, AppOpsManager.OPSTR_MOCK_LOCATION) == AppOpModes.ALLOWED

        binding.mockedLocation = mockedLocation
    }

    private val listener = object: LocationListener {
        override fun onLocationChanged(location: Location?) {
            binding.currentLocation = if (location == null) "null"
            else "lat: ${location.latitude} - lon: ${location.longitude}"
        }

        override fun onProviderEnabled(provider: String?) {
            binding.providerInfo = "onProdivderEnabled: $provider"
        }

        override fun onProviderDisabled(provider: String?) {
            binding.providerInfo = "onProdivderDisabled: $provider"
        }
    }

    fun onClickToggleListenLocation(view: View?) {
        //permissionsModule.toggleDangerousPermission(appDesc, android.Manifest.permission.ACCESS_FINE_LOCATION, true)

//        permissionsModule.setAppOpMode(
//            appDesc, AppOpsManager.OPSTR_COARSE_LOCATION,
//            AppOpModes.ALLOWED
//        )
//        permissionsModule.setAppOpMode(
//            appDesc, AppOpsManager.OPSTR_FINE_LOCATION,
//            AppOpModes.ALLOWED
//        )


        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (binding.toggleListenLocation.isChecked) {
            try {
                Log.e("DebugLoc", "requestLocationUpdates")
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, // TODO: tight this with fakelocation module.
                    1000L,
                    1f,
                    listener
                )
                binding.currentLocation = "listening started"
            } catch (se: SecurityException) {
                Log.e("DebugLoc", "Missing permission", se)
            }
        } else {
            locationManager.removeUpdates(listener)
            binding.currentLocation = "no listening"
        }
    }


    fun onClickPermission(view: View?) {
        val manualAction = permissionsModule.setAppOpMode(appDesc, AppOpsManager.OPSTR_MOCK_LOCATION,
            AppOpModes.ALLOWED)

        if (manualAction == null) {
            updateData("")
        } else {
            //dev mode disabled
            val alertDialog = AlertDialog.Builder(this)
            alertDialog
                .setTitle(manualAction.title)
                .setMessage(manualAction.instructions)
                .setPositiveButton("Ok") { dialog, which ->
                    startActivityForResult(manualAction.intent, 0)
                }
                .setNegativeButton("Cancel") { dialog, which ->
                    //user cancel
                }
            alertDialog.create().show()
        }
    }

    fun onClickReset(view: View?) {
        try {
            fakeLocationModule.stopFakeLocation()
        } catch(e: Exception) {
            Log.e("FakeLoc", "Can't stop FakeLocation", e)
        }
    }

    private fun setFakeLocation(latitude: Double, longitude: Double) {
        try {
            fakeLocationModule.startFakeLocation()
        } catch(e: Exception) {
            Log.e("FakeLoc", "Can't startFakeLocation", e)
        }
        fakeLocationModule.setFakeLocation(latitude, longitude)
        updateData("lat: ${latitude} - lon: ${longitude}")
    }

    fun onClickParis(view: View?) {
        setFakeLocation(48.8502282, 2.3542286)
    }

    fun onClickLondon(view: View?) {
        setFakeLocation(51.5287718, -0.2416803)
    }

    fun onClickAmsterdam(view: View?) {
        setFakeLocation(52.3547498, 4.8339211)
    }
}
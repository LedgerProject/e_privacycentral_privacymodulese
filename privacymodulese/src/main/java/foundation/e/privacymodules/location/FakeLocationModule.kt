package foundation.e.privacymodules.location

import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.SystemClock
import android.util.Log

/**
 * Implementation of the functionality of fake location.
 * All of them are available for normal application, so just one version is enough.
 *
 * @param context an Android context, to retrieve system services for example.
 */
class FakeLocationModule(protected val context: Context): IFakeLocationModule {

    /**
     * List of all the Location provider that will be mocked.
     */
    private val providers = listOf(LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER)

    /**
     * Handy accessor to the locationManager service.
     * We avoid getting it on module initialization to wait for the context to be ready.
     */
    private val locationManager: LocationManager get() =
        context.getSystemService(LOCATION_SERVICE) as LocationManager

    /**
     * @see IFakeLocationModule.startFakeLocation
     */
    override fun startFakeLocation() {
        providers.forEach { provider ->
            try {
                locationManager.removeTestProvider(provider)
            } catch(e: Exception) {
                Log.d("FakeLocationModule", "Test provider $provider already removed.")
            }

            locationManager.addTestProvider(
                provider,
                false,
                false,
                false,
                false,
                false,
                true,
                true,
                Criteria.POWER_LOW, Criteria.ACCURACY_FINE)
            locationManager.setTestProviderEnabled(provider, true)
        }
    }

    override fun setFakeLocation(latitude: Double, longitude: Double) {
        context.startService(FakeLocationService.buildFakeLocationIntent(context, latitude, longitude))
    }

    internal fun setTestProviderLocation(latitude: Double, longitude: Double) {
        providers.forEach { provider ->
            val location = Location(provider)
            location.latitude = latitude
            location.longitude = longitude

            // Set default value for all the other required fields.
            location.altitude = 3.0
            location.time = System.currentTimeMillis()
            location.speed = 0.01f
            location.bearing = 1f
            location.accuracy = 3f
            location.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                location.bearingAccuracyDegrees = 0.1f
                location.verticalAccuracyMeters = 0.1f
                location.speedAccuracyMetersPerSecond = 0.01f
            }

            locationManager.setTestProviderLocation(provider, location)
        }
    }

    /**
     * @see IFakeLocationModule.stopFakeLocation
     */
    override fun stopFakeLocation() {
        context.stopService(FakeLocationService.buildStopIntent(context))
        providers.forEach { provider ->
            try {
                locationManager.setTestProviderEnabled(provider, false)
                locationManager.removeTestProvider(provider)
            } catch (e: Exception) {
                Log.d("FakeLocationModule", "Test provider $provider already removed.")
            }
        }
    }
}

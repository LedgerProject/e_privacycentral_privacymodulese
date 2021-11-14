package foundation.e.privacymodules.location


import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log

class FakeLocationService: Service() {

    enum class Actions {
        START_FAKE_LOCATION
    }

    companion object {
        private const val PERIOD_LOCATION_UPDATE = 1000L
        private const val PERIOD_UPDATES_SERIE = 2 * 60 * 1000L

        private const val PARAM_LATITUDE = "PARAM_LATITUDE"
        private const val PARAM_LONGITUDE = "PARAM_LONGITUDE"

        fun buildFakeLocationIntent(context: Context, latitude: Double, longitude: Double): Intent {
            return Intent(context, FakeLocationService::class.java).apply {
                action = Actions.START_FAKE_LOCATION.name
                putExtra(PARAM_LATITUDE, latitude)
                putExtra(PARAM_LONGITUDE, longitude)
            }
        }

        fun buildStopIntent(context: Context) = Intent(context, FakeLocationService::class.java)
    }

    private lateinit var fakeLocationModule: FakeLocationModule

    private var countDownTimer: CountDownTimer? = null

    private var fakeLocation: Pair<Double, Double>? = null

    override fun onCreate() {
        super.onCreate()
        fakeLocationModule = FakeLocationModule(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (Actions.valueOf(it.action)) {
                Actions.START_FAKE_LOCATION -> {

                    fakeLocation = Pair(
                        it.getDoubleExtra(PARAM_LATITUDE, 0.0),
                        it.getDoubleExtra(PARAM_LONGITUDE, 0.0)
                    )
                    initTimer()
                }
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        countDownTimer?.cancel()
        super.onDestroy()
    }


    private fun initTimer() {
        countDownTimer?.cancel()
        countDownTimer = object: CountDownTimer(PERIOD_UPDATES_SERIE, PERIOD_LOCATION_UPDATE) {
            override fun onTick(millisUntilFinished: Long) {
                fakeLocation?.let {
                    try {
                        fakeLocationModule.setTestProviderLocation(
                            it.first,
                            it.second
                        )
                    } catch (e: Exception) {
                        Log.d("FakeLocationService", "setting fake location", e)
                    }
                }
            }

            override fun onFinish() {
                initTimer()
            }
        }.start()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}

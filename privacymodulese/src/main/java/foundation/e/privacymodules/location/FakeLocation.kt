package foundation.e.privacymodules.location

import android.app.AppOpsManager
import android.content.Context
import android.os.Process
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import foundation.e.privacymodules.commons.ManualAction

/**
 * Implements [IFakeLocation] with all privileges of a system app.
 */
class FakeLocation(context: Context): AFakeLocation(context) {

    /**
     * @see IFakeLocation.requirePermission
     * Always return null, permission is granted trough privileged API.
     */
    override fun requirePermission(): ManualAction? {
        val appOps = context.getSystemService(AppCompatActivity.APP_OPS_SERVICE) as AppOpsManager
        try {
            appOps.setMode(
                AppOpsManager.OP_MOCK_LOCATION,
                Process.myUid(), context.packageName,

                AppOpsManager.MODE_ALLOWED
            )
        } catch (e: Exception) {
            Log.e("FakeLocation", "grant perm", e)
            // TODO.
        }
        return null
    }
}

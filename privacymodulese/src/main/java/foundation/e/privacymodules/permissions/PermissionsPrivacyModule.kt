package foundation.e.privacymodules.permissions

import android.app.AppOpsManager
import android.app.AppOpsManager.*
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import foundation.e.privacymodules.commons.ManualAction
import foundation.e.privacymodules.location.IFakeLocation

/**
 * Implements [IPermissionsPrivacyModule] with all privileges of a system app.
 */
class PermissionsPrivacyModule(context: Context): APermissionsPrivacyModule(context) {

    private val appOpsManager: AppOpsManager get()
        = context.getSystemService(AppCompatActivity.APP_OPS_SERVICE) as AppOpsManager

    /**
     * @see IPermissionsPrivacyModule.getPermissionStatus
     * Return the status for the specified permission and app.
     */
    override fun getPermissionStatus(uid: Int, packageName: String, permissionName: String): PermissionStatus {
        val opStr = AppOpsManager.permissionToOp(permissionName)
        if (opStr != null) {
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                val mode = appOpsManager.checkOpNoThrow(opStr, uid, packageName)
                when (mode) {
                    MODE_ALLOWED -> PermissionStatus.GRANTED
                    else -> PermissionStatus.DENIED
                }
            } else {
                val mode = appOpsManager.unsafeCheckOpRawNoThrow(opStr, uid, packageName)
                when (mode) {
                    MODE_ALLOWED -> PermissionStatus.GRANTED
                    MODE_FOREGROUND -> PermissionStatus.FOREGROUND
                    else -> PermissionStatus.DENIED
                }
            }
        }
        return PermissionStatus.DENIED
    }

    /**
     * @see IPermissionsPrivacyModule.setRuntimePermission
     * Always return null, permission is set using privileged capacities.
     */
    override fun setRuntimePermission(uid: Int,
        packageName: String,
        permissionName: String,
        status: PermissionStatus
    ): ManualAction? {
        val op = permissionToOpCode(permissionName)
        val toSet = when(status) {
            PermissionStatus.GRANTED -> MODE_ALLOWED
            PermissionStatus.FOREGROUND -> MODE_FOREGROUND
            PermissionStatus.DENIED -> MODE_IGNORED
        }
        if (op != OP_NONE) {
            appOpsManager.setMode(op, uid, packageName, toSet)
        }

        return null
    }
}
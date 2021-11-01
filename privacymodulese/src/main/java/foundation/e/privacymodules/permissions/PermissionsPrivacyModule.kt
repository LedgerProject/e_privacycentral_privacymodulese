package foundation.e.privacymodules.permissions

import android.app.AppOpsManager
import android.app.AppOpsManager.*
import android.content.Context
import android.net.IConnectivityManager
import android.os.ServiceManager
import android.os.UserHandle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import foundation.e.privacymodules.commons.ManualAction
import foundation.e.privacymodules.permissions.data.AppOpModes
import foundation.e.privacymodules.permissions.data.ApplicationDescription
import java.util.*

/**
 * Implements [IPermissionsPrivacyModule] with all privileges of a system app.
 */
class PermissionsPrivacyModule(context: Context): APermissionsPrivacyModule(context) {

    private val appOpsManager: AppOpsManager get()
        = context.getSystemService(AppCompatActivity.APP_OPS_SERVICE) as AppOpsManager

    /**
     * @see IPermissionsPrivacyModule.toggleDangerousPermission
     * Always return null, permission is set using privileged capacities.
     */
    override fun toggleDangerousPermission(appDesc: ApplicationDescription,
                                           permissionName: String,
                                           grant: Boolean): ManualAction? {
        try {
            if (grant) {
                context.packageManager.grantRuntimePermission(
                    appDesc.packageName,
                    permissionName,
                    android.os.Process.myUserHandle()
                )
            } else {
                context.packageManager.revokeRuntimePermission(
                    appDesc.packageName,
                    permissionName,
                    android.os.Process.myUserHandle()
                )
            }
        } catch (e: Exception) {
            Log.e("Permissions-e", "Exception while setting permission", e)
        }

        return null
    }

    override fun setAppOpMode(
        appDesc: ApplicationDescription,
        permissionName: String,
        mode: AppOpModes
    ): ManualAction? {
        val op = strOpToOp(permissionName)
        if (op != OP_NONE) {
            appOpsManager.setMode(op, appDesc.uid, appDesc.packageName, mode.modeValue)
        }
        return null
    }

    override fun isAccessibilityEnabled(): Boolean? = null

    override fun setVpnPackageAuthorization(packageName: String): Boolean {
        val service: IConnectivityManager = IConnectivityManager.Stub.asInterface(
            ServiceManager.getService(Context.CONNECTIVITY_SERVICE))

        try {
            if (service.prepareVpn(null, packageName, UserHandle.myUserId())) {
                // Authorize this app to initiate VPN connections in the future without user
                // intervention.
                service.setVpnPackageAuthorization(packageName, UserHandle.myUserId(), true)
                return true
            }
        } catch (e: java.lang.Exception) {
            Log.e("Permissions-e", "Exception while setting VpnPackageAuthorization", e)
        } catch (e: NoSuchMethodError) {
            Log.e("Permissions-e", "Bad android sdk version", e)
        }
        return false
    }

    // WIP, get history of permissions use.
    fun testPermissionHistory(appDesc: ApplicationDescription,
                              permission: String) {
        // when we change with setAppOpsPermissiosn ...
//        appOpsManager.startWatchingMode(permissionToOp(permission), appDesc.packageName, OnOpChangedListener { op, packageName ->
//            Log.d("WatchMode", "op: $op for! $packageName")
//        })


        val ops = appOpsManager.getOpsForPackage(appDesc.uid, appDesc.packageName,
            OPSTR_CAMERA)
        val tag = "TestGetOps"

        Log.d(tag, "opsForPackage.size: ${ops.size}")

        ops.forEach {
            Log.d(tag, "pkg: ${it.packageName} - ops.size: ${it.ops.size}")
            it.ops.forEach { opEntry ->
                Log.d(tag, "time: ${Date(opEntry.time)} - duration: ${it.ops.firstOrNull()?.duration}ms, mode: ${opEntry.mode}, op: ${opEntry.opStr}")
            }

        }
        // Not available in API 24.
//        appOpsManager.startWatchingActive(
//            arrayOf(permissionToOpCode(permission)).toIntArray(),
//            //appDesc.packageName,
//            OnOpActiveChangedListener { code, uid, packageName, active ->
//                Log.d("WatchMode", "Op active: $active - op: ${opToName(code)} uid: $uid for $packageName")
//            })

    }


}
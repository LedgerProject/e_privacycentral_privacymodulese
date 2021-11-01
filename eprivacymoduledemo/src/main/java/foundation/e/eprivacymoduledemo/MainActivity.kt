package foundation.e.eprivacymoduledemo

import android.content.Context
import android.content.Intent
import android.net.IConnectivityManager
import android.os.Bundle
import android.os.ServiceManager
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import foundation.e.eprivacymoduledemo.databinding.MainActivityBinding
import android.os.UserHandle
import android.util.Log
import foundation.e.privacymodules.permissions.PermissionsPrivacyModule
import java.lang.Exception
import java.security.PermissionCollection


class MainActivity : AppCompatActivity() {
    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity)
        binding.view = this
    }


    fun onClickFakeLocation(view: View) {
        startActivity(Intent(this, FakeLocationActivity::class.java))
    }

    fun onClickPermissions(view: View) {
        //startActivity(Intent(this, PermissionsListActivity::class.java))
    }

    fun onClickQuickTest(view: View) {
        val permissionsPrivacyModule = PermissionsPrivacyModule(this)
        permissionsPrivacyModule.setVpnPackageAuthorization("foundation.e.privacycentralapp.e")
//        val mService: IConnectivityManager = IConnectivityManager.Stub.asInterface(
//            ServiceManager.getService(Context.CONNECTIVITY_SERVICE))
//
//        val mPackage = "foundation.e.privacycentralapp.e"
//        try {
//            if (mService.prepareVpn(null, mPackage, UserHandle.myUserId())) {
//                // Authorize this app to initiate VPN connections in the future without user
//                // intervention.
//                mService.setVpnPackageAuthorization(mPackage, UserHandle.myUserId(), true)
//                binding.quickTestLog = "Done!"
//            }
//        } catch (e: Exception) {
//            Log.e("QuickTest", "onClick", e)
//            binding.quickTestLog = e.message
//        }

    }
}

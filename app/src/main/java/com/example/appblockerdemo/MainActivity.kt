package com.example.appblockerdemo

import android.app.admin.DevicePolicyManager
import android.content.*
import android.content.pm.PackageInfo
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager


class MainActivity : AppCompatActivity() {

    private val packageList = ArrayList<PackageInfo>()
    private lateinit var adapter: PackageAdapter

    private val adminComponentName: ComponentName by lazy {
        ComponentName(this, DevAdminReceiver::class.java)
    }

    private val devicePolicyManager: DevicePolicyManager by lazy {
        getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    }

    private val receiver = object: BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            if(intent?.action == ACTION_UNINSTALL_RESULT) {
                Log.d(
                    TAG, "uninstall result: " + intent?.getStringExtra(PackageInstaller.EXTRA_PACKAGE_NAME) + "|status="
                        + intent?.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE))
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val mWorkManager = WorkManager.getInstance()
        val mRequest = OneTimeWorkRequest.Builder(UnInstallerWorker::class.java).build()
        mWorkManager.enqueue(mRequest)


        if (!devicePolicyManager.isDeviceOwnerApp(packageName)) {
            Toast.makeText(applicationContext, "You need to make this app device owner first!",
                Toast.LENGTH_LONG).show()
            finish()
            return
        }

        //Hiding app icon.
        val p = packageManager
        val componentName = ComponentName(this, MainActivity::class.java)
        p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)

    }

    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, IntentFilter(ACTION_UNINSTALL_RESULT))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    companion object {
        const val TAG = "MainActivity"
        const val CODE_UNINSTALL_RESULT = 1235
        const val ACTION_UNINSTALL_RESULT = "eu.sisik.removehideaps.ACTION_UNINSTALL_RESULT"
    }
}

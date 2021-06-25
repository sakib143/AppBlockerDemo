package com.example.appblockerdemo

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters


class UnInstallerWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    override fun doWork(): Result {

        Log.e("=> ", "doWork() is calling !!!")

        val intentSender = PendingIntent.getBroadcast(
            applicationContext,
            MainActivity.CODE_UNINSTALL_RESULT,
            Intent(MainActivity.ACTION_UNINSTALL_RESULT),
            0
        ).intentSender

        val pm: PackageManager = applicationContext.getPackageManager()
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        for (packageInfo in packages) {
            if(packageInfo.packageName.contains("pubg")) {
                val pi = applicationContext.packageManager.packageInstaller
                pi.uninstall(packageInfo.packageName, intentSender)
            }
        }
        //Hide India pubg.
        removeBattleGroundIndia(intentSender)
        val outputData = Data.Builder().putString(WORK_RESULT, "Jobs Finished").build()
        return Result.success(outputData)
    }

    private fun removeBattleGroundIndia(intentSender: IntentSender) {
        val pi = applicationContext.packageManager.packageInstaller
        pi.uninstall("com.pubg.imobile", intentSender)
    }

    companion object {
        private const val WORK_RESULT = "work_result"
    }
}
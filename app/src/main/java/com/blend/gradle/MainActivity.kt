package com.blend.gradle

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.blend.annotation.InjectTimestamp

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getChannel()
    }

    @InjectTimestamp
    private fun getChannel() {
        //读取渠道信息
        val appInfo = packageManager
            .getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        val channelName = appInfo.metaData.getString("MTA_CHANNEL")
        Log.d(TAG, "Blend Channel = $channelName")

//        BuildConfig
//        getString(R.string.greeting)
    }

    companion object {
        private const val TAG = "MainActivity"
    }

}
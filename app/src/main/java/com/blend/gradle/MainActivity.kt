package com.blend.gradle

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.blend.annotation.InjectTimestamp

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        test()
    }

    @InjectTimestamp
    private fun test() {
        Log.d(TAG, "MainActivity: BlendTest")
    }

    companion object {
        private const val TAG = "MainActivity"
    }

}
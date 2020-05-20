package com.example.stepikandroidstudio

import android.app.Activity
import android.os.Bundle

class SecondActivity:Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //задает UI который будет изображен на экране
        setContentView(R.layout.second_activity)
    }
}
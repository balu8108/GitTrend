package com.bala.gittrend

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bala.gittrend.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityHomeBinding.inflate(layoutInflater).root)
    }
}
package com.thekusch.nerdeyesem

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.thekusch.nerdeyesem.ui.FragmentLoginScreen

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().replace(R.id.mainActivity_FrameLayout,FragmentLoginScreen()).commit()
    }
}

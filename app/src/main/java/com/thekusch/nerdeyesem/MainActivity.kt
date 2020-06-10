package com.thekusch.nerdeyesem

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import com.github.ajalt.timberkt.Timber
import com.thekusch.nerdeyesem.ui.FragmentLoginScreen

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
        supportFragmentManager.beginTransaction().replace(R.id.mainActivity_FrameLayout,FragmentLoginScreen())
            .addToBackStack(getString(R.string.fragment_login))
            .commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0 ){
            supportFragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}

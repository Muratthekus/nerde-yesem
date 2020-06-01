package com.thekusch.nerdeyesem.fingerprint

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.CancellationSignal
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Gravity
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.thekusch.nerdeyesem.R

class LoginHelper(private var context: Context) : FingerprintManager.AuthenticationCallback() {

    //After our job is finish with fingerprint, other apps can be use this features. For this we must use cancellationSignal
    private lateinit var cancellationSignal: CancellationSignal
    var isSuccess:Boolean=false
    fun startAuthentication(fingerprintManager: FingerprintManager, crypto:FingerprintManager.CryptoObject){
        cancellationSignal = CancellationSignal()
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fingerprintManager.authenticate(crypto,cancellationSignal,0,this,null)
    }

    override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
        showToast(errString!!)
        vibrate()
    }

    override fun onAuthenticationFailed() {
        showToast(context.getString(R.string.fingerprint_authentication_failed))
        vibrate()
    }

    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult?) {
        showToast(context.getString(R.string.fingerprint_authentication_success))
        isSuccess=true
    }
    private fun showToast(msg:CharSequence){
        val toast = Toast.makeText(context,msg,Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER,0,0)
        toast.show()
    }
    private fun vibrate(){
        val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
        //deprecated in API 26
        v.vibrate(500);
    }
    }

}
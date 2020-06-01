package com.thekusch.nerdeyesem.ui

import android.Manifest
import android.app.KeyguardManager
import android.content.Context.FINGERPRINT_SERVICE
import android.content.Context.KEYGUARD_SERVICE
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.Bundle
import android.os.Handler
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.thekusch.nerdeyesem.databinding.FragmentLoginScreenBinding
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import com.thekusch.nerdeyesem.R
import com.thekusch.nerdeyesem.fingerprint.LoginHelper
import java.io.File
import java.io.FileInputStream
import java.lang.RuntimeException
import java.security.NoSuchAlgorithmException
import javax.crypto.spec.IvParameterSpec


class FragmentLoginScreen : Fragment() {
    private lateinit var binding:FragmentLoginScreenBinding

    //Fingerprint
    private lateinit var fingerprintManager :FingerprintManager
    private lateinit var keyguardManager: KeyguardManager
    private lateinit var crypto : FingerprintManager.CryptoObject
    private val KEY = "nerdeYesemApp"
    //rovides access to implementations of cryptographic ciphers for encryption and decryption.
    private lateinit var cipher: Cipher
    private lateinit var keyStore: KeyStore
    private lateinit var keyGenerator: KeyGenerator

    //Threads
    private val handler = Handler()
    private val checkRequirementsRunn = Runnable { checkRequirements() }
    private lateinit var checkResultRunn : Runnable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginScreenBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //initialize
        fingerprintManager = activity!!.getSystemService(FINGERPRINT_SERVICE) as FingerprintManager
        keyguardManager = activity!!.getSystemService(KEYGUARD_SERVICE) as KeyguardManager

        //start checking fingerprint login requirements
        handler.post(checkRequirementsRunn)

    }
    private fun checkRequirements(){
        val msg = requirementCheckResult()
        if(msg==null){
            handler.removeCallbacks(checkRequirementsRunn)
            getKey()
            if(initializeCipher()){
                crypto = FingerprintManager.CryptoObject(cipher)
                val loginHelper = LoginHelper(context!!)
                loginHelper.startAuthentication(fingerprintManager,crypto)
                checkResultRunn = Runnable {
                    checkIfSuccess(loginHelper)
                }
                handler.postDelayed(checkResultRunn,100)
            }
        }
        else{
            Toast.makeText(context,msg,Toast.LENGTH_SHORT).show()
        }
    }
    //control the finger print login result
    private fun checkIfSuccess(loginHelper: LoginHelper){
        if(loginHelper.isSuccess){
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.mainActivity_FrameLayout,FragmentHomeScreen())
                .commit()
        }
        else{
            handler.postDelayed(checkResultRunn,200)
        }
    }
    private fun requirementCheckResult():String?{
        if(!fingerprintManager.isHardwareDetected)
            return getString(R.string.hardware_detected_error)
        if(!fingerprintManager.hasEnrolledFingerprints())
            return getString(R.string.no_configured_fingerprint)
        if(!keyguardManager.isKeyguardSecure)
            return getString(R.string.enable_lockScreen_security)
        if(ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED){
            return getString(R.string.fingerprint_permission_request)
        }
        return null
    }

    //Access to android keyStore and generate encryption key
    private fun getKey(){
        keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore")
        keyStore.load(null)//initialize as empty
        keyGenerator.init(
            KeyGenParameterSpec
            .Builder(KEY, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setUserAuthenticationRequired(true)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .build())
        keyGenerator.generateKey()
    }
    private fun initializeCipher():Boolean{
        try{
            cipher = Cipher.getInstance(
                KeyProperties.KEY_ALGORITHM_AES+"/"
                        +KeyProperties.BLOCK_MODE_CBC+"/"
                        +KeyProperties.ENCRYPTION_PADDING_PKCS7
            )
        }
        catch (e:NoSuchAlgorithmException){
            throw RuntimeException("Can not get cipher",e)
        }
        return try{
            keyStore.load(null)
            val key = keyStore.getKey(KEY,null)

            cipher.init(Cipher.ENCRYPT_MODE, key)
            val iv = cipher.iv
            val ivSpec = IvParameterSpec(iv)
            cipher.init(Cipher.DECRYPT_MODE,key,ivSpec)
            true
        } catch (e : KeyPermanentlyInvalidatedException) {
            //Chiper couldn't initialize
            false;
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(checkResultRunn)
    }
}

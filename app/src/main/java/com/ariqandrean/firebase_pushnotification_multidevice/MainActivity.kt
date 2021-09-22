package com.ariqandrean.firebase_pushnotification_multidevice

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.ariqandrean.firebase_pushnotification_multidevice.api.RetrofitInstance
import com.ariqandrean.firebase_pushnotification_multidevice.databinding.ActivityMainBinding
import com.ariqandrean.firebase_pushnotification_multidevice.firebase.FirebaseService
import com.ariqandrean.firebase_pushnotification_multidevice.model.NotificationData
import com.ariqandrean.firebase_pushnotification_multidevice.model.PushNotification
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val TOPIC = "/topics/myTopic"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /** Active this if you want pushNotification By Token/ user */
//        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)

        /** Active this if you want pushNotification By Token/ user */
//        FirebaseMessaging.getInstance().token.addOnCompleteListener {
//            if (it.isComplete){
//                FirebaseService.token = it.result.toString()
//                binding.mainTokenEditText.setText(it.result.toString())
//            }
//        }
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        binding.mainSendButton.setOnClickListener {
            val title = binding.mainTITLEEditText.text.toString()
            val message = binding.mainFIELDEditText.text.toString()
            val recipientToken = binding.mainTokenEditText.text.toString()

            if (title.isNotEmpty() && message.isNotEmpty() && recipientToken.isNotEmpty()){
                PushNotification(
                    NotificationData(title, message),
                    TOPIC
                    /** Active this if you want pushNotification By Token/ user */
//                        recipientToken
                ).also {
                    sendNotification(it)
                }
            }
        }
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch{
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if (response.isSuccessful){
                Log.d(TAG, "Response: ${Gson().toJson(response)}")
            } else {
                Log.e(TAG, response.errorBody().toString())
            }
        } catch (e: Exception){
            Log.e(TAG, e.toString())
        }
    }
}
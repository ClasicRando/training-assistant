package com.trainingassistant.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.trainingassistant.R
import com.trainingassistant.ui.client.ClientFragment

class ClientActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val id = intent.getStringExtra("id") ?: ""
        savedInstanceState?.putString("id", id)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client)
        if (savedInstanceState == null) {
            val fragment = ClientFragment.newInstance().apply {
                arguments = Bundle().apply {
                    putString("id", id)
                }
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commitNow()
        }
    }

    override fun onBackPressed() {
//        val fragment = supportFragmentManager.findFragmentById("")
        super.onBackPressed()
    }
}
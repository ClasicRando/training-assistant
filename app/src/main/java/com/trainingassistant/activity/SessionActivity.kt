package com.trainingassistant.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.trainingassistant.R
import com.trainingassistant.ui.session.SessionFragment

class SessionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val id = intent.getStringExtra("id") ?: ""
        savedInstanceState?.putString("id", id)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session)
        if (savedInstanceState == null) {
            val fragment = SessionFragment.newInstance().apply {
                arguments = Bundle().apply {
                    putString("id", id)
                }
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commitNow()
        }
    }
}
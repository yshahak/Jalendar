package com.thedroidboy.jalendar.activities

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.thedroidboy.jalendar.R
import com.thedroidboy.jalendar.databinding.ActivityCustomReccurenceBinding
import com.thedroidboy.jalendar.utils.onAttach
import kotlinx.android.synthetic.main.activity_custom_reccurence.*

class CustomRecurrenceActivity : AppCompatActivity() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base.onAttach())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding : ActivityCustomReccurenceBinding = DataBindingUtil.setContentView(this, R.layout.activity_custom_reccurence)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeButtonEnabled(true)
        }
    }

}

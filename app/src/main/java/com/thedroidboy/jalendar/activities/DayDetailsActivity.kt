package com.thedroidboy.jalendar.activities

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.thedroidboy.jalendar.R
import com.thedroidboy.jalendar.databinding.ActivityDayDetailsBinding
import com.thedroidboy.jalendar.model.DayTimes
import com.thedroidboy.jalendar.utils.Constants
import com.thedroidboy.jalendar.utils.onAttach
import kotlinx.android.synthetic.main.activity_day_details.*

class DayDetailsActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding : ActivityDayDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_day_details)
        setSupportActionBar(my_toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeButtonEnabled(true)
        }
        title = ""
        val time = intent.getLongExtra(Constants.KEY_TIME, System.currentTimeMillis())
        binding.dayTimes = DayTimes.create(time)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base.onAttach())
    }

}

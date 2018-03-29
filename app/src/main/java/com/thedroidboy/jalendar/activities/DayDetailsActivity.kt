package com.thedroidboy.jalendar.activities

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.thedroidboy.jalendar.R
import com.thedroidboy.jalendar.databinding.ActivityDayDetailsBinding
import com.thedroidboy.jalendar.model.DayTimes

class DayDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding : ActivityDayDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_day_details)
        binding.dayTimes = DayTimes.create()
    }

}

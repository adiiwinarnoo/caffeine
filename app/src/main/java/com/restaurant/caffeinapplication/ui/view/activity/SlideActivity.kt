package com.restaurant.caffeinapplication.ui.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.restaurant.caffeinapplication.databinding.ActivitySlideBinding

class SlideActivity : AppCompatActivity() {

    lateinit var binding : ActivitySlideBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySlideBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
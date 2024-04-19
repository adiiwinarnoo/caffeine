package com.restaurant.caffeinapplication.ui.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.restaurant.caffeinapplication.databinding.ActivityNoticeDetailBinding

class NoticeDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoticeDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoticeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
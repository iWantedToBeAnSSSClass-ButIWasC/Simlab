package com.sss.simplab

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sss.simplab.databinding.ActivityRecommendResultBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RecommendResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecommendResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecommendResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initIndicator()
    }

    private fun initIndicator() {
        // 3초 후 indicator 제거
        lifecycleScope.launch {
            delay(3000)

            binding.indicator.visibility = View.GONE
            binding.resultLayout.visibility = View.VISIBLE
        }
    }
}
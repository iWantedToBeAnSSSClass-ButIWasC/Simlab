package com.sss.simplab

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.sss.simplab.databinding.ActivityRecommendResultBinding
import com.sss.simplab.network.Perfume
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

            val perfume = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(MainActivity.INTENT_NAME_PERFUME, Perfume::class.java)
            } else {
                intent.getParcelableExtra(MainActivity.INTENT_NAME_PERFUME)
            }

            binding.appTitle.text = perfume?.name
            binding.content.text = perfume?.content

            Glide.with(this@RecommendResultActivity).load(perfume?.url).into(binding.resultImageView)
        }
    }
}
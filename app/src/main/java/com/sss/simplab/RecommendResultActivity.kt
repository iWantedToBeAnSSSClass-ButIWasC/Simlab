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
        val perfume = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(MainActivity.INTENT_NAME_PERFUME, Perfume::class.java)
        } else {
            intent.getParcelableExtra(MainActivity.INTENT_NAME_PERFUME)
        }

        binding.appTitle.text = perfume?.name
        binding.content.text = perfume?.content

        if (perfume?.url == null) {
            binding.resultImageCardView.visibility = View.GONE
        } else {
            Glide.with(this@RecommendResultActivity).load(perfume?.url).into(binding.resultImageView)
        }
    }
}
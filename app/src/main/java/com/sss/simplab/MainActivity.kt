package com.sss.simplab

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.Manifest
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.sss.simplab.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initButton()
        initRangeSlider()
        initImageView()
    }

    private fun initButton() {
        // '추천 받기' 버튼 클릭 시 화면 전환
        binding.btnRecommend.setOnClickListener {
            startActivity(Intent(this@MainActivity, RecommendResultActivity::class.java))
        }
    }

    private fun initRangeSlider() {
        // slider 값 초기화
        binding.rangeSlider.stepSize = 10000.0F
        binding.rangeSlider.valueFrom = 10000.0F
        binding.rangeSlider.valueTo = 100000.0F
        binding.rangeSlider.setValues(10000F, 100000F)

        // 바늘 색상
        binding.rangeSlider.thumbTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.seek_bar_thumb))

        // 트랙 색상
        binding.rangeSlider.trackActiveTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.seek_bar_track_active))
        binding.rangeSlider.trackInactiveTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.seek_bar_track_inactive))
    }

    private fun initImageView() {
        // 이미지 클릭 시 '갤러리에서 이미지 가져오기' 동작
        binding.uploadImageView.setOnClickListener {
            // 권한 확인
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) -> {
                    // 이미 권한이 허용되어 있을 경우의 동작 -> 이미지 가져오기
                    getImageFromGallery.launch("image/*")
                }

                else -> {
                    // 안드로이드 버전에 따라 알맞은 권한 요청
                    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Manifest.permission.READ_MEDIA_IMAGES
                    } else {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }

                    // 권한 미허용 시 권한 요청
                    locationPermissionRequest.launch(arrayOf(permission))
                }
            }
        }
    }

    // 갤러리에서 이미지 가져오기
    private val getImageFromGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                // 이미지 uri 정보를 ImageView 에 반영
                binding.uploadImageView.setImageURI(it)

                // '추천 받기' 버튼 활성화
                binding.btnRecommend.isEnabled = true
            }
        }

    // 이미지 권한 요청 선언
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // 안드로이드 버전에 따라 알맞은 권한 요청
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        }

        when {
            permissions.getOrDefault(permission, false) -> {
                // 권한 허용 시 -> 이미지 가져오기
                getImageFromGallery.launch("image/*")
            }

            else -> {
                // 위치 권한 미허용 시 -> 권한 허용 화면으로 이동
                Toast.makeText(this@MainActivity, "이미지를 업로드하려면 권한이 필요합니다.", Toast.LENGTH_SHORT)
                    .show()

                val intent: Intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(
                    Uri.parse("package:${packageName}")
                )
                startActivity(intent)
            }
        }
    }
}
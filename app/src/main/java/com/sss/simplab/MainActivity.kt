package com.sss.simplab

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.Manifest
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.gson.GsonBuilder
import com.sss.simplab.databinding.ActivityMainBinding
import com.sss.simplab.network.Perfume
import com.sss.simplab.network.RecommendPerfumeService
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class MainActivity : AppCompatActivity() {

    companion object {
        const val INTENT_NAME_PERFUME = "perfume"
    }

    private lateinit var binding: ActivityMainBinding

    private var imageFile: MultipartBody.Part? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initButton()
        initRangeSlider()
        initImageView()
    }

    override fun onResume() {
        super.onResume()

        binding.indicator.visibility = View.GONE
        binding.layout.visibility = View.VISIBLE
    }

    private fun initButton() {
        // '추천 받기' 버튼 클릭 시 화면 전환
        binding.btnRecommend.setOnClickListener {
            if (imageFile == null) {
                 Toast.makeText(this@MainActivity, "이미지를 업로드해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                binding.indicator.visibility = View.VISIBLE
                binding.layout.visibility = View.GONE

                val gson = GsonBuilder().setLenient().create()

                val retrofit = Retrofit
                    .Builder()
                    .baseUrl("http://10.0.2.2:5000/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()

                val service = retrofit.create(RecommendPerfumeService::class.java)

                service.recommend(imageFile!!).enqueue(object : Callback<Perfume> {
                    override fun onResponse(
                        call: Call<Perfume>,
                        response: Response<Perfume>
                    ) {
                        if (!response.isSuccessful) {
                            // 실패
                            Log.e("테스트", "실패")
                            return
                        }

                        val perfume = response.body()
                        val intent = Intent(this@MainActivity, RecommendResultActivity::class.java)
                        intent.putExtra(INTENT_NAME_PERFUME, perfume)
                        startActivity(intent)
                    }

                    override fun onFailure(call: Call<Perfume>, t: Throwable) {
                        Log.e("테스트", t.message.toString())
                    }
                })
            }
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
                val file = File(absolutelyPath(uri, this))
                val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
                imageFile = MultipartBody.Part.createFormData("image", file.name, requestFile)

                // 이미지 uri 정보를 ImageView 에 반영
                binding.uploadImageView.setImageURI(it)
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

    // 절대경로 변환
    fun absolutelyPath(path: Uri?, context: Context): String {
        var proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        var c: Cursor? = context.contentResolver.query(path!!, proj, null, null, null)
        var index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c?.moveToFirst()

        var result = c?.getString(index!!)

        return result!!
    }
}
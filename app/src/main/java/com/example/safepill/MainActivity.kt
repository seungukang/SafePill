package com.example.safepill

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var btnCamera: View
    private var photoUri: Uri? = null

    // 카메라 촬영 콜백 (실제 사진 파일 저장)
    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (!success || photoUri == null) {
                Toast.makeText(this, "촬영 취소됨", Toast.LENGTH_SHORT).show()
                return@registerForActivityResult
            }

            // 촬영 성공 → ResultActivity로 URI 전달
            val intent = Intent(this, ResultActivity::class.java).apply {
                putExtra("image_uri", photoUri.toString())
            }
            startActivity(intent)
        }

    // 카메라 권한 요청 콜백
    private val requestCamPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                startCameraCapture()
            } else {
                Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val content: View = findViewById(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(content) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom)
            insets
        }

        // 카메라 버튼
        btnCamera = findViewById(R.id.btnCamera)
        btnCamera.setOnClickListener { ensureCameraThenShoot() }

        // 119 버튼 (다이얼만 열림)
        val btn119: View = findViewById(R.id.btn119)
        btn119.setOnClickListener {
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:119")))
        }

        // ✅ 내 정보 버튼 → ProfileActivity로 이동
        val btnProfile: View = findViewById(R.id.btnProfile)
        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    /** 카메라 권한 확인 후 촬영 시작 */
    private fun ensureCameraThenShoot() {
        val granted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            startCameraCapture()
        } else {
            requestCamPermission.launch(Manifest.permission.CAMERA)
        }
    }

    /** 실제로 사진 파일을 만들고 카메라 앱 실행 */
    private fun startCameraCapture() {
        val imageFile = createImageFile()
        val uri = FileProvider.getUriForFile(
            this,
            "${applicationContext.packageName}.fileprovider",
            imageFile
        )
        photoUri = uri
        takePicture.launch(uri)
    }

    /** 캐시 폴더에 임시 파일 생성 */
    private fun createImageFile(): File {
        return File.createTempFile(
            "pill_${System.currentTimeMillis()}",
            ".jpg",
            cacheDir
        )
    }
}

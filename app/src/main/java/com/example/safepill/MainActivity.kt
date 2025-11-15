package com.example.safepill

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class MainActivity : AppCompatActivity() {

    private lateinit var btnCamera: View

    private val takePreview =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp ->
            if (bmp == null) {
                Toast.makeText(this, "촬영 취소됨", Toast.LENGTH_SHORT).show()
                return@registerForActivityResult
            }

            val label = classify(bmp)

            startActivity(
                Intent(this, ResultActivity::class.java)
                    .putExtra("pred_label", label ?: "분류 실패")
            )
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

        btnCamera = findViewById(R.id.btnCamera)
        btnCamera.setOnClickListener { ensureCameraThenShoot() }

        printModelInfo()
    }

    private fun ensureCameraThenShoot() {
        val granted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            takePreview.launch(null)
        } else {
            requestCamPermission.launch(Manifest.permission.CAMERA)
        }
    }

    private val requestCamPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) takePreview.launch(null)
            else Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }

    // ==========================
    // 🔥 classify() — threshold 기반 분류 적용
    // ==========================
    private fun classify(bitmap: Bitmap): String? {
        return try {
            val labels = assets.open("labels.txt").bufferedReader().readLines()
            val interpreter = Interpreter(loadModelFile("model.tflite"))

            val resized = Bitmap.createScaledBitmap(bitmap, 224, 224, true)

            val input = Array(1) { Array(224) { Array(224) { FloatArray(3) } } }

            for (y in 0 until 224) {
                for (x in 0 until 224) {
                    val p = resized.getPixel(x, y)

                    val r = ((p shr 16) and 0xFF) / 255.0f
                    val g = ((p shr 8) and 0xFF) / 255.0f
                    val b = (p and 0xFF) / 255.0f

                    input[0][y][x][0] = r
                    input[0][y][x][1] = g
                    input[0][y][x][2] = b
                }
            }

            val output = Array(1) { FloatArray(labels.size) }

            interpreter.run(input, output)

            println("OUTPUT VALS = ${output[0].joinToString()}")

            interpreter.close()

            // ==========================
            // 🔥 threshold로 분류
            // 0번 출력값 기준
            // ==========================
            val score = output[0][0]
            val label = if (score > 0.731f) "탁센" else "타이레놀"

            return label

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "AI 오류: ${e.message}", Toast.LENGTH_LONG).show()
            null
        }
    }

    private fun loadModelFile(name: String): MappedByteBuffer {
        val fd = assets.openFd(name)
        FileInputStream(fd.fileDescriptor).use { fis ->
            return fis.channel.map(
                FileChannel.MapMode.READ_ONLY,
                fd.startOffset,
                fd.declaredLength
            )
        }
    }

    private fun printModelInfo() {
        try {
            val interpreter = Interpreter(loadModelFile("model.tflite"))
            val inTensor = interpreter.getInputTensor(0)
            val outTensor = interpreter.getOutputTensor(0)

            val inShape = inTensor.shape()
            val outShape = outTensor.shape()

            Toast.makeText(
                this,
                "INPUT: ${inShape.joinToString()}  OUTPUT: ${outShape.joinToString()}",
                Toast.LENGTH_LONG
            ).show()

            interpreter.close()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Err: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}

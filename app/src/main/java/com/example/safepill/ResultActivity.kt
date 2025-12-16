package com.example.safepill

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ResultActivity : AppCompatActivity() {

    private lateinit var tvInfo: TextView
    private lateinit var tvDesc: TextView
    private lateinit var ivPill: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        tvInfo = findViewById(R.id.tvInfo)
        tvDesc = findViewById(R.id.tvDesc)
        ivPill = findViewById(R.id.ivPill)

        val uriStr = intent.getStringExtra("image_uri")
        if (uriStr.isNullOrEmpty()) {
            Toast.makeText(this, "이미지 경로가 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val uri = Uri.parse(uriStr)
        ivPill.setImageURI(uri)
        tvInfo.text = "AI 분석 중..."

        analyzePill(uri)
    }

    private fun analyzePill(uri: Uri) {
        lifecycleScope.launch {
            try {
                val file = createTempFileFromUri(uri)

                val body = file
                    .asRequestBody("image/*".toMediaType())
                    .let { req ->
                        MultipartBody.Part.createFormData(
                            name = "image",
                            filename = file.name,
                            body = req
                        )
                    }

                val response = RetrofitClient.api.analyzePill(body)

                if (!response.isSuccessful) {
                    tvInfo.text = "분석 실패 (${response.code()})"
                    return@launch
                }

                val result = response.body()
                if (result == null) {
                    tvInfo.text = "분석 실패 (빈 응답)"
                    return@launch
                }

                val name = result.name // ex) "타이레놀 500mg ..."
                tvInfo.text = "AI 분석 결과: $name"

                // ✅ 약 기본 설명 + 개인화 추가 주의사항을 합쳐서 출력
                when {
                    name.contains("타이레놀") -> {
                        ivPill.setImageResource(R.drawable.pill_tylenol)
                        val base = """
                            타이레놀은 아세트아미노펜 성분의 해열·진통제입니다.
                            · 주 성분: 아세트아미노펜
                            · 용도: 발열, 두통, 감기몸살 통증 완화
                            · 주의: 과다 복용 시 간 손상 위험, 음주와 함께 복용 금지
                        """.trimIndent()
                        tvDesc.text = base + buildExtraWarning(name)
                    }

                    name.contains("탁센") -> {
                        ivPill.setImageResource(R.drawable.pill_taksen)
                        val base = """
                            탁센은 해열·진통 효과가 있는 이부프로펜 계열 약입니다.
                            · 용도: 두통, 치통, 생리통, 근육통 등 완화
                            · 주의: 공복 복용 피하기, 위장 장애 시 의사와 상담
                        """.trimIndent()
                        tvDesc.text = base + buildExtraWarning(name)
                    }

                    name.contains("게보린") -> {
                        ivPill.setImageResource(R.drawable.pill_geborin) // 너 drawable 있으면 사용, 없으면 줄 삭제
                        val base = """
                            게보린은 복합 진통제(여러 성분이 함께 들어간 형태)로 알려져 있습니다.
                            · 용도: 두통, 치통, 근육통 등 통증 완화
                            · 주의: 다른 감기약/진통제와 성분 중복 가능 → 중복 복용 주의
                        """.trimIndent()
                        tvDesc.text = base + buildExtraWarning(name)
                    }

                    name.contains("이지엔6") -> {
                        ivPill.setImageResource(R.drawable.pill_ezn6) // 너 drawable 있으면 사용, 없으면 줄 삭제
                        val base = """
                            이지엔6는 진통·소염 목적의 약으로 사용되는 경우가 많습니다.
                            · 용도: 두통, 생리통, 근육통 등 통증 완화
                            · 주의: 공복 복용은 피하고, 속쓰림/위통이 있으면 복용 중단 후 상담
                        """.trimIndent()
                        tvDesc.text = base + buildExtraWarning(name)
                    }

                    name.contains("펜잘큐") -> {
                        ivPill.setImageResource(R.drawable.pill_penzal_q) // 너 drawable 있으면 사용, 없으면 줄 삭제
                        val base = """
                            펜잘큐는 복합 성분 진통제로 사용되는 경우가 많습니다.
                            · 용도: 두통, 치통, 근육통 등 통증 완화
                            · 주의: 다른 진통제/감기약과 함께 복용 시 성분 중복 가능
                        """.trimIndent()
                        tvDesc.text = base + buildExtraWarning(name)
                    }

                    else -> {
                        tvDesc.text = "등록되지 않은 약이거나 분석에 실패했습니다."
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                tvInfo.text = "오류 발생: ${e.message}"
                tvDesc.text = ""
            }
        }
    }

    /**
     * ✅ 내 정보(연령/임신/알레르기) + 약 이름(5종) 기반으로
     * Result 화면에 붙일 '추가 주의사항'을 만들어줌.
     */
    private fun buildExtraWarning(pillName: String): String {
        val prefs = getSharedPreferences(ProfileActivity.PREFS, MODE_PRIVATE)
        val ageGroup = prefs.getString(ProfileActivity.KEY_AGE_GROUP, "adult") ?: "adult"
        val pregnant = prefs.getBoolean(ProfileActivity.KEY_PREGNANT, false)
        val allergy = prefs.getBoolean(ProfileActivity.KEY_ALLERGY, false)

        val lines = mutableListOf<String>()

        // 1) 연령대 기반 공통 주의
        when (ageGroup) {
            "child" -> lines.add("소아는 성인 기준으로 복용하면 위험할 수 있어요. 제품 라벨의 ‘소아 용법/용량’을 우선 확인하세요.")
            "senior" -> lines.add("고령은 부작용에 더 민감할 수 있어요. 어지럼/졸림/위장불편이 있으면 복용을 중단하고 상담하세요.")
        }

        // 2) 임신 기반 공통 주의
        if (pregnant) {
            lines.add("임신 중에는 일부 진통제/소염제 성분이 주의 대상일 수 있어요. 복용 전 전문가 상담을 권장해요.")
        }

        // 3) 알레르기 기반 공통 주의
        if (allergy) {
            lines.add("알레르기 이력이 있으면 성분/첨가제를 꼭 확인하세요. 발진·가려움·호흡곤란 등 이상 반응 시 즉시 중단하세요.")
        }

        // 4) 약(5종)별 추가 주의 (너가 말한 “약마다 조금 다르게”를 최소한으로 반영)
        when {
            pillName.contains("타이레놀") -> {
                lines.add("같은 성분(아세트아미노펜)이 들어간 감기약과 중복 복용에 주의하세요.")
                if (pregnant) lines.add("임신 중이라면 복용 전 전문가 확인이 더 안전해요.")
                if (ageGroup == "child") lines.add("소아는 체중/연령별 용량이 달라질 수 있어요. 성인 기준 복용은 피하세요.")
            }

            pillName.contains("탁센") || pillName.contains("이지엔6") -> {
                lines.add("공복 복용은 피하고, 속쓰림/위통이 나타나면 복용을 중단하세요.")
                if (pregnant) lines.add("임신 중에는 일부 소염진통제 계열이 특히 주의 대상일 수 있어요.")
                if (ageGroup == "senior") lines.add("고령은 위장 출혈/신장 부담 위험이 커질 수 있어요. 증상이 있으면 상담하세요.")
            }

            pillName.contains("게보린") || pillName.contains("펜잘큐") -> {
                lines.add("복합 성분 약은 다른 진통제/감기약과 성분이 겹칠 수 있어요. ‘중복 복용’만 특히 조심하세요.")
                if (ageGroup == "child") lines.add("소아는 복합 성분 약 복용 전 성분 확인이 더 중요해요.")
                if (allergy) lines.add("복합 성분은 첨가제도 다양할 수 있어요. 알레르기 반응에 특히 주의하세요.")
            }
        }

        // 아무것도 없으면 추가 문구 자체를 안 붙임
        if (lines.isEmpty()) return ""

        return buildString {
            append("\n\n[추가 주의사항]\n")
            lines.distinct().forEach { append("· ").append(it).append("\n") }
        }.trimEnd()
    }

    private fun createTempFileFromUri(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("이미지 스트림을 열 수 없습니다.")

        val tempFile = File(cacheDir, "pill_${System.currentTimeMillis()}.jpg")
        tempFile.outputStream().use { out ->
            inputStream.use { input ->
                input.copyTo(out)
            }
        }
        return tempFile
    }
}

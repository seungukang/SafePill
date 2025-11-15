package com.example.safepill

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val tvInfo = findViewById<TextView>(R.id.tvInfo)
        val tvDesc = findViewById<TextView>(R.id.tvDesc)
        val ivPill = findViewById<ImageView>(R.id.ivPill)

        // ✅ MainActivity에서 보낸 pred_label 그대로 받기
        val label = intent.getStringExtra("pred_label") ?: "unknown"

        tvInfo.text = "AI 분석 결과: $label"

        when (label) {
            "탁센" -> {
                ivPill.setImageResource(R.drawable.pill_taksen)
                tvDesc.text = """
                    탁센은 해열·진통 효과가 있는 약입니다.
                    · 주 성분: 이부프로펜 계열
                    · 용도: 두통, 치통, 생리통, 근육통 등 완화
                    · 주의: 공복 복용 피하기, 위장 장애 시 의사와 상담
                """.trimIndent()
            }

            "타이레놀" -> {
                ivPill.setImageResource(R.drawable.pill_tylenol)
                tvDesc.text = """
                    타이레놀은 아세트아미노펜 성분의 해열·진통제입니다.
                    · 주 성분: 아세트아미노펜
                    · 용도: 발열, 두통, 감기몸살 통증 완화
                    · 주의: 과다 복용 시 간 손상 위험, 음주와 함께 복용 금지
                """.trimIndent()
            }

            else -> {
                tvDesc.text = "등록되지 않은 약이거나 분석에 실패했습니다.\n약 포장지의 이름을 다시 확인해주세요."
            }
        }
    }
}

package com.example.safepill

import android.os.Bundle
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial

class ProfileActivity : AppCompatActivity() {

    companion object {
        const val PREFS = "safepill_prefs"
        const val KEY_AGE_GROUP = "age_group"   // child/adult/senior
        const val KEY_PREGNANT = "pregnant"     // boolean
        const val KEY_ALLERGY = "allergy"       // boolean
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val rgAge: RadioGroup = findViewById(R.id.rgAge)
        val swPregnant: SwitchMaterial = findViewById(R.id.swPregnant)
        val swAllergy: SwitchMaterial = findViewById(R.id.swAllergy)
        val btnSave: MaterialButton = findViewById(R.id.btnSave)

        // 저장값 불러와서 UI 반영
        val prefs = getSharedPreferences(PREFS, MODE_PRIVATE)
        when (prefs.getString(KEY_AGE_GROUP, "adult")) {
            "child" -> rgAge.check(R.id.rbChild)
            "adult" -> rgAge.check(R.id.rbAdult)
            "senior" -> rgAge.check(R.id.rbSenior)
        }
        swPregnant.isChecked = prefs.getBoolean(KEY_PREGNANT, false)
        swAllergy.isChecked = prefs.getBoolean(KEY_ALLERGY, false)

        btnSave.setOnClickListener {
            val ageGroup = when (rgAge.checkedRadioButtonId) {
                R.id.rbChild -> "child"
                R.id.rbAdult -> "adult"
                R.id.rbSenior -> "senior"
                else -> null
            }

            if (ageGroup == null) {
                Toast.makeText(this, "연령대를 선택해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            prefs.edit()
                .putString(KEY_AGE_GROUP, ageGroup)
                .putBoolean(KEY_PREGNANT, swPregnant.isChecked)
                .putBoolean(KEY_ALLERGY, swAllergy.isChecked)
                .apply()

            Toast.makeText(this, "저장 완료!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}

package com.project.farmingapp.view.introscreen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.project.farmingapp.R
import com.project.farmingapp.adapter.IntroAdapter
import com.project.farmingapp.databinding.ActivityIntroBinding
import com.project.farmingapp.model.data.IntroData
import com.project.farmingapp.view.auth.LoginActivity

class IntroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntroBinding

    private val introSliderAdapter = IntroAdapter(
        listOf(
            IntroData(
                "Welcome to the\nFarming App",
                "Best Guide and Helper for any Farmer. Provides various features at one place!",
                R.drawable.intro_first
            ),
            IntroData(
                "Read Articles",
                "Read online articles related to farming concepts, technologies, and other useful knowledge.",
                R.drawable.intro_read
            ),
            IntroData(
                "Share Knowledge",
                "Social Media lets you share knowledge with other farmers!\nCreate your own posts using Image/Video/Text.",
                R.drawable.intro_share
            ),
            IntroData(
                "E-Commerce",
                "Buy / Sell agriculture-related products & manage your cart online.",
                R.drawable.intro_ecomm
            ),
            IntroData(
                "Weather Forecast",
                "Get notified for daily weather conditions. 24x7 data.",
                R.drawable.intro_weather
            ),
            IntroData(
                "APMC Statistics",
                "Get updates on APMC pricing and commodity details every day.",
                R.drawable.intro_statistics
            ),
            IntroData(
                "Let's Grow Together",
                "- Farming App",
                R.drawable.intro_help
            )
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sliderViewPager.adapter = introSliderAdapter
        setupIndicators()
        setCurrentIndicator(0)

        binding.sliderViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })

        binding.nextBtn.setOnClickListener {
            val currentItem = binding.sliderViewPager.currentItem
            if (currentItem + 1 < introSliderAdapter.itemCount) {
                binding.sliderViewPager.currentItem += 1
            } else {
                goToLoginScreen()
            }
        }

        binding.skipIntro.setOnClickListener {
            goToLoginScreen()
        }
    }

    private fun setupIndicators() {
        val indicators = arrayOfNulls<ImageView>(introSliderAdapter.itemCount)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)

        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext).apply {
                setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
                this.layoutParams = layoutParams
            }
            binding.sliderballsContainer.addView(indicators[i])
        }
    }

    private fun setCurrentIndicator(index: Int) {
        val childCount = binding.sliderballsContainer.childCount
        for (i in 0 until childCount) {
            val imageView = binding.sliderballsContainer.getChildAt(i) as ImageView
            imageView.setImageDrawable(
                ContextCompat.getDrawable(
                    applicationContext,
                    if (i == index) R.drawable.indicator_active else R.drawable.indicator_inactive
                )
            )
        }

        binding.nextBtn.text =
            if (index == introSliderAdapter.itemCount - 1) "Get Started" else "Next"
    }

    private fun goToLoginScreen() {
        startActivity(Intent(this, LoginActivity::class.java))
        getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).edit()
            .putBoolean("firstTime", false)
            .apply()
        finish()
    }
}

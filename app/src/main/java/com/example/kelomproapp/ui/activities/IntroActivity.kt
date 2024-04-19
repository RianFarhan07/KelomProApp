package com.example.kelomproapp.ui.activities

import android.content.Intent
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.Typeface
import android.os.Bundle
import android.text.Html
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.example.kelomproapp.R
import com.example.kelomproapp.adapter.ViewPagerAdapter
import com.example.kelomproapp.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {

    private var binding : ActivityIntroBinding? = null
    private lateinit var slideViewPager: ViewPager
    private lateinit var dotIndicator: LinearLayout
    private lateinit var dots: Array<TextView?>
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    private val viewPagerListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {
            setDotIndicator(position)
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityIntroBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // Initialize views
        slideViewPager = findViewById(R.id.view_pager)
        dotIndicator = findViewById(R.id.dot_indicator)

        // Initialize ViewPager adapter
        viewPagerAdapter = ViewPagerAdapter(this)
        slideViewPager.adapter = viewPagerAdapter

        // Call setDotIndicator method
        setDotIndicator(0)

        // Add the ViewPager listener
        slideViewPager.addOnPageChangeListener(viewPagerListener)

        val uniSans: Typeface = Typeface.createFromAsset(assets,"Uni Sans Heavy.otf")
        binding?.tvAppName?.typeface = uniSans

        setGradientText(binding?.tvAppName)

        binding?.btnSignIn?.setOnClickListener{
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        binding?.btnSignUp?.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setGradientText(textView: TextView?) {
        val shader = LinearGradient(
            0f, 0f, 0f, textView?.textSize ?: 0f,
            intArrayOf(
                ContextCompat.getColor(this, R.color.black),
                ContextCompat.getColor(this, R.color.green_gpt)
            ),
            null,
            Shader.TileMode.CLAMP
        )
        textView?.paint?.shader = shader
    }

    private fun setDotIndicator(currentPosition: Int) {
        val numDots = viewPagerAdapter.count
        dots = Array(numDots) { TextView(this) }
        dotIndicator.removeAllViews()

        for (i in 0 until numDots) {
            dots[i] = TextView(this)
            dots[i]?.text = Html.fromHtml("&#8226;", Html.FROM_HTML_MODE_LEGACY)
            dots[i]?.textSize = 35F
            dots[i]?.setTextColor(resources.getColor(R.color.grey_gpt, applicationContext.theme))
            dotIndicator.addView(dots[i])
        }

        dots[currentPosition]?.setTextColor(resources.getColor(R.color.blue_gpt, applicationContext.theme))
    }
}

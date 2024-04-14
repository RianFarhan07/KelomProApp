package com.example.kelomproapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.example.kelomproapp.R

class ViewPagerAdapter(private val context: Context) : PagerAdapter() {

    private val sliderAllImages = intArrayOf(R.drawable.manajemen_tugas, R.drawable.pemantauan_progress, R.drawable.unduh_materi)
    private val sliderAllTitle = intArrayOf(R.string.screen1, R.string.screen2, R.string.screen3)
    private val sliderAllDesc = intArrayOf(R.string.screen1desc, R.string.screen2desc, R.string.screen3desc)

    override fun getCount(): Int {
        return sliderAllTitle.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.slider_screen, container, false)
        val slideImage = view.findViewById<ImageView>(R.id.sliderImage)
        val sliderTitle = view.findViewById<TextView>(R.id.sliderTitle)
        val sliderDesc = view.findViewById<TextView>(R.id.sliderDesc)

        slideImage.setImageResource(sliderAllImages[position])
        sliderTitle.setText(context.getString(sliderAllTitle[position]))
        sliderDesc.setText(context.getString(sliderAllDesc[position]))

        container.addView(view)
        return view
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

}

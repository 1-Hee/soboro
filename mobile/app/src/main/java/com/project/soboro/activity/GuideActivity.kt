package com.project.soboro.activity

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.project.soboro.R
import com.project.soboro.databinding.ActivityGuideBinding
import com.project.soboro.databinding.ActivityMainBinding
import com.project.soboro.util.SoboroConstant

class GuideActivity : AppCompatActivity() {

    lateinit var b:ActivityGuideBinding
    lateinit var guideImage:LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)
        b = ActivityGuideBinding.inflate(layoutInflater)
        setContentView(b.root)
        SoboroConstant.tempContext = this;

        guideImage = findViewById<LinearLayout>(R.id.guideImage)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        var idx = 0
        val guide01: Drawable? = ContextCompat.getDrawable(this, R.drawable.guide_page1)
        val guide02: Drawable? = ContextCompat.getDrawable(this, R.drawable.guide_page2)
        val guide03: Drawable? = ContextCompat.getDrawable(this, R.drawable.guide_page3)
        val guide04: Drawable? = ContextCompat.getDrawable(this, R.drawable.guide_page4)
        val guide05: Drawable? = ContextCompat.getDrawable(this, R.drawable.guide_page5)
        val guide06: Drawable? = ContextCompat.getDrawable(this, R.drawable.guide_page6)
        val guide07: Drawable? = ContextCompat.getDrawable(this, R.drawable.guide_page7)
        val guide08: Drawable? = ContextCompat.getDrawable(this, R.drawable.guide_page8)
        val guide09: Drawable? = ContextCompat.getDrawable(this, R.drawable.guide_page9)
        val guide10: Drawable? = ContextCompat.getDrawable(this, R.drawable.guide_page10)
        val guide11: Drawable? = ContextCompat.getDrawable(this, R.drawable.guide_page11)
        val guide12: Drawable? = ContextCompat.getDrawable(this, R.drawable.guide_page12)
        val guide13: Drawable? = ContextCompat.getDrawable(this, R.drawable.guide_page13)
        val guide14: Drawable? = ContextCompat.getDrawable(this, R.drawable.guide_page14)
        val guide15: Drawable? = ContextCompat.getDrawable(this, R.drawable.guide_page15)
        val guide16: Drawable? = ContextCompat.getDrawable(this, R.drawable.guide_page16)
        val guide17: Drawable? = ContextCompat.getDrawable(this, R.drawable.guide_page17)

        var guideList = mutableListOf<Drawable?>(
            guide01, guide02, guide03, guide04, guide05, guide06,
            guide07, guide08, guide09, guide10, guide11, guide12,
            guide13, guide14, guide15, guide16, guide17
        )

        guideImage.background = guideList[idx]

        guideImage.setOnClickListener {
            if(idx==17){
                onBackPressed()
                finish()
            }

            if(idx < 17){
                guideImage.background = guideList[idx++]
            }
         }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
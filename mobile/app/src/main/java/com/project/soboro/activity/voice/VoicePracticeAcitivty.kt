package com.project.soboro.activity.voice

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.project.soboro.R
import com.project.soboro.databinding.ActivityVoicePracticeAcitivtyBinding
import com.project.soboro.factory.MainViewModelFactory
import com.project.soboro.model.MainViewModel
import com.project.soboro.repository.Repository
import com.project.soboro.util.SoboroConstant
import com.project.soboro.util.SoboroVoice
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.http.Part
import java.io.File
import java.io.FileInputStream


class VoicePracticeAcitivty : AppCompatActivity() {

    lateinit var b:ActivityVoicePracticeAcitivtyBinding;
    lateinit var pastString:String;

    // saveRoot
    private val recordingFilePath: String by lazy {
        "${externalCacheDir?.absolutePath}/voicePractice.webm"
    }

    // layout
    lateinit var voicePracticeText:TextView;
    lateinit var voicePracticeBtn:LinearLayout;
    lateinit var voicePracticeIcon:ImageView;
    lateinit var voicePracticeGuideText:TextView;
    lateinit var practiceButtonHolder:LinearLayout;

    var isChangeModel = false; // true -> naver / false -> soboro

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_practice_acitivty)
        b = ActivityVoicePracticeAcitivtyBinding.inflate(layoutInflater);
        setContentView(b.root);
        SoboroConstant.tempContext = this;

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        pastString = "";
        voicePracticeText = findViewById<TextView>(R.id.voicePracticeText);
        practiceButtonHolder = findViewById<LinearLayout>(R.id.practiceButtonHolder);

        // 애니메이션
        val extentionAnim: Animation = AnimationUtils.loadAnimation(applicationContext,
            R.anim.extention_anim
        )
        val scaleUp = AnimationUtils.loadAnimation(applicationContext,
            R.anim.scale_up
        )

        // 하단 가이드 텍스트
        voicePracticeIcon = findViewById<ImageView>(R.id.voicePracticeIcon);
        voicePracticeGuideText = findViewById<TextView>(R.id.voicePracticeGuideText);
        voicePracticeGuideText.text = getString(R.string.voice_practice_ready);

        var isClicked = false;
        voicePracticeBtn = findViewById<LinearLayout>(R.id.voicePracticeBtn);
        voicePracticeBtn.setOnClickListener {
            if(!SoboroConstant.isVoiceLock){
                voicePracticeBtn.startAnimation(scaleUp);
                isClicked = !isClicked;
                if(isClicked){
                    voicePracticeIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.audio_active_btn));
                    voicePracticeBtn.background =
                        ContextCompat.getDrawable(this, R.drawable.gradiant_list);
                    val animationDrawable: AnimationDrawable = voicePracticeBtn.background as AnimationDrawable;
                    animationDrawable.setEnterFadeDuration(500);
                    animationDrawable.setExitFadeDuration(1000);
                    animationDrawable.start();
                    voicePracticeIcon.startAnimation(extentionAnim);
                    voicePracticeGuideText.text = getString(R.string.voice_listen);

                    // 음성녹음 테스트
                    SoboroVoice.stopPlaying()
                    SoboroVoice.startRecording(recordingFilePath);
                    voicePracticeText.text = getString(R.string.voice_empty_string);

                } else {
                    SoboroVoice.stopRecording();

                    SoboroConstant.isVoiceLock = true;
                    voicePracticeBtn.background = null;
                    voicePracticeIcon.clearAnimation();
                    voicePracticeGuideText.text = getString(R.string.voice_wait);

                    sendRecordFiles(isChangeModel); // 서버로 전송
                }
            }
        }

        // 학습 모델 변경 이벤트...
        practiceButtonHolder.setOnLongClickListener {
            Toast.makeText(this,getString(R.string.voice_change_model_text),Toast.LENGTH_SHORT).show();
            isChangeModel = !isChangeModel;

            setVoiceBtnColor();
            true;
        }
    }

    fun setVoiceBtnColor(){
        if(isChangeModel){
            voicePracticeIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.audio_inactive_naver_btn));
        }else {
            voicePracticeIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.audio_inactive_btn));
        }
    }

    fun sendRecordFiles(isNaver:Boolean){

        val file = File(recordingFilePath)
         val requestFile = RequestBody.create("video/webm".toMediaTypeOrNull(), file);
         val body:MultipartBody.Part = MultipartBody.Part.createFormData("webm_file", file.name, requestFile);
        // webm
        val repository = Repository();
        val viewModelFactory = MainViewModelFactory(repository);
        var viewModel:MainViewModel = ViewModelProvider(this, viewModelFactory).get(
            MainViewModel::class.java
        );

        viewModel.uploadWavFile(body, isNaver);
        viewModel.soboroWavFileResult.observe(this, Observer {
            if(pastString.equals("") || !pastString.equals(it.toString())){
                voicePracticeText.text = it.toString();
                pastString = it.toString();
                SoboroConstant.isVoiceLock = false;
                setVoiceBtnColor();

                voicePracticeGuideText.text = getString(R.string.voice_trans_ready);
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
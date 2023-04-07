package com.project.soboro.activity.voice

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.project.soboro.R
import com.project.soboro.databinding.ActivityVoiceTranslationAcitivtyBinding
import com.project.soboro.factory.MainViewModelFactory
import com.project.soboro.model.MainViewModel
import com.project.soboro.repository.Repository
import com.project.soboro.util.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.w3c.dom.Text
import java.io.File
import java.io.IOException
import java.net.URL
import java.util.*


class VoiceTranslationActivity : AppCompatActivity() {
    lateinit var b: ActivityVoiceTranslationAcitivtyBinding

    // save audio soruce path
    private val recordingFilePath: String by lazy {
        "${externalCacheDir?.absolutePath}/voicePractice.wav"
    }

    // audio play status flag
    var mp:MediaPlayer? = null;
    var pastAddress = "";
    var pastString = "";

    // layout
    lateinit var voiceTransIcon:ImageView;
    lateinit var voiceTransBtn:LinearLayout;
    lateinit var voiceTransText:TextView;
    lateinit var extentionAnim:Animation;
    lateinit var pastVoicetString:String;

    lateinit var voiceTransChatText:EditText;
    lateinit var voiceChatHolder:LinearLayout;

    lateinit var transButtonHolder:LinearLayout;

    lateinit var prefs: PreferenceUtil;

    var isChangeModel = false; // true -> naver / false -> soboro
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_translation_acitivty)
        b = ActivityVoiceTranslationAcitivtyBinding.inflate(layoutInflater);
        setContentView(b.root);
        prefs = PreferenceUtil(this);
        prefs.init(this);

        pastVoicetString = ""

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        // 상단 음성 아이콘 애니메이션 관련 init
        extentionAnim = AnimationUtils.loadAnimation(applicationContext,
            R.anim.extention_anim
        )
        val scaleUp = AnimationUtils.loadAnimation(applicationContext,
            R.anim.scale_up
        )
        voiceTransBtn = findViewById<LinearLayout>(R.id.voiceTransBtn);
        voiceTransIcon = findViewById<ImageView>(R.id.voiceTransIcon);
        voiceTransText = findViewById<TextView>(R.id.voiceTransGuideText);
        transButtonHolder = findViewById<LinearLayout>(R.id.transButtonHolder);

        // 음성 번역기 버튼쪽 스타일
        var isClicked = false;
        voiceTransText.text = getString(R.string.voice_trans_ready);
        voiceTransBtn.setOnClickListener {

            if(!SoboroConstant.isVoiceLock){

                voiceTransBtn.startAnimation(scaleUp);
                isClicked = !isClicked;
                if(isClicked){
                    voiceTransIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.audio_active_btn));
                    voiceTransBtn.background =
                        ContextCompat.getDrawable(this, R.drawable.gradiant_list);
                    val animationDrawable: AnimationDrawable = voiceTransBtn.background as AnimationDrawable;

                    animationDrawable.setEnterFadeDuration(500);
                    animationDrawable.setExitFadeDuration(1000);
                    animationDrawable.start();
                    voiceTransIcon.startAnimation(extentionAnim);
                    voiceTransText.text = "음성을 인식하는 중입니다."

                    // 음성녹음 테스트
                    SoboroVoice.stopPlaying()
                    SoboroVoice.startRecording(recordingFilePath);


                }else {
                    SoboroConstant.isVoiceLock = true;
                    SoboroVoice.stopRecording();
                    voiceTransBtn.background = null;
                    voiceTransIcon.clearAnimation();
                    voiceTransText.text = "잠시만 기다려 주세요";

                    // 음성출력 테스트
//                    SoboroVoice.stopRecording();
//                    SoboroVoice.startPlaying(recordingFilePath);

                    sendRecordFiles(isChangeModel);

                }

            }
        }


        // holder layout
        voiceChatHolder = findViewById<LinearLayout>(R.id.voiceTransChatHolder);
        // val testItem1 = findViewById<TextView>(R.id.testItem1);

        // 채팅 입력 텍스트
        val voiceTransChatText = findViewById<EditText>(R.id.voiceTransChatText);

        // 채팅 전송 버튼
        val voiceTransSendBtn = findViewById<Button>(R.id.voiceTransSendBtn);
        voiceTransSendBtn.setOnClickListener {

            val chatString = voiceTransChatText?.text.toString(); // 입력된 채팅 문자열
            val removeSpaceString = chatString.replace(" ", "");

            if(removeSpaceString.length>2 && removeSpaceString.length <= 30){
                // 채팅 말풍선 생성
                SoboroCreateItem
                    .createChatItem(this, voiceTransChatText,
                        voiceChatHolder, true);

                if(!chatString.equals(pastString)){
                    voiceTransText.text = getString(R.string.voice_wait);
                    getTextTranslation(chatString);
                    pastString = chatString;
                }else {
                    setVoiceBtn(this, voiceTransIcon, voiceTransBtn, voiceTransText,  extentionAnim, pastAddress);
                }
            }else {
                if(removeSpaceString.length<=2){
                    Toast
                        .makeText(
                        this,
                        getString(R.string.voice_length_min),
                        Toast.LENGTH_SHORT)
                        .show();
                }else{
                    Toast
                        .makeText(
                        this,
                        getString(R.string.voice_length_max),
                        Toast.LENGTH_SHORT)
                        .show();
                }
            }
        }

        // 학습 모델 변경 이벤트...
        transButtonHolder.setOnLongClickListener {
            Toast.makeText(this,getString(R.string.voice_change_model_text),Toast.LENGTH_SHORT).show();
            isChangeModel = !isChangeModel;
            setVoiceBtnColor();
            true
        }
    }


    fun setVoiceBtnColor(){
        if(isChangeModel){
            voiceTransIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.audio_inactive_naver_btn));
        }else {
            voiceTransIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.audio_inactive_btn));
        }
    }

    // tts 결과 요청
    fun getTextTranslation(text:String){
        val repository = Repository();
        val viewModelFactory = MainViewModelFactory(repository);
        var viewModel: MainViewModel = ViewModelProvider(this,viewModelFactory).get(MainViewModel::class.java);
        
        viewModel.getTextTranslation(text);
        viewModel.voiceFileName.observe(this, Observer {
            if(!it.toString().equals(pastAddress)){
                setVoiceBtn(this, voiceTransIcon, voiceTransBtn, voiceTransText,  extentionAnim, it.toString());
                pastAddress = it.toString();
            }

        })
    }

    
    // 소리 재생 함수
    fun setVoiceBtn(context:Context, icon:ImageView, btn:LinearLayout, textView:TextView, extentionAnim: Animation, audioAddress:String){
        val timer = Timer();
        val viewList:Array<View> = arrayOf<View>(icon, btn, textView);

        // 애니메이션 init
        icon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.audio_listen_btn));
        btn.background =
            ContextCompat.getDrawable(this, R.drawable.gradiant_list2);
        val animationDrawable: AnimationDrawable = btn.background as AnimationDrawable;
        animationDrawable.setEnterFadeDuration(500);
        animationDrawable.setExitFadeDuration(1000);
        animationDrawable.start();
        icon.startAnimation(extentionAnim);
        textView.text = getString(R.string.voice_activate);

        val audioURL = prefs.getString(R.string.wav_address) + audioAddress;
        val fileUrl = URL(audioURL);

        if(mp != null) {
            mp?.stop();
            mp?.release();
        }
        mp = MediaPlayer();
        mp?.setDataSource(fileUrl.toString());

        mp?.setOnCompletionListener {
            setVoiceBtnColor();
            btn.background = null;
            icon.clearAnimation();
            textView.text = getString(R.string.voice_trans_ready);
        }
        mp?.prepare();
        mp?.start();

    }

    fun sendRecordFiles(isNaver:Boolean){
        val file = File(recordingFilePath)

        val requestFile = RequestBody.create("video/webm".toMediaTypeOrNull(), file);
        val body: MultipartBody.Part = MultipartBody.Part.createFormData("webm_file", file.name, requestFile);

        // webm
        val repository = Repository();
        val viewModelFactory = MainViewModelFactory(repository);
        var viewModel:MainViewModel = ViewModelProvider(this, viewModelFactory).get(
            MainViewModel::class.java
        );

        viewModel.uploadWavFile(body, isNaver);
        viewModel.soboroWavFileResult.observe(this, Observer {
            if(pastVoicetString.equals("") || !pastVoicetString.equals(it.toString())){
                SoboroCreateItem.createOtherChatItem(this, it.toString(),
                    voiceChatHolder, false);
                pastVoicetString = it.toString();
                SoboroConstant.isVoiceLock = false;
                setVoiceBtnColor();
                voiceTransText.text = getString(R.string.voice_trans_ready);
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
package com.project.soboro.activity

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.project.soboro.R
import com.project.soboro.activity.history.HistoryActivity
import com.project.soboro.activity.member.ProfileActivity
import com.project.soboro.activity.voice.VoicePracticeAcitivty
import com.project.soboro.activity.voice.VoiceTranslationActivity
import com.project.soboro.data.SoboroUserInfo
import com.project.soboro.databinding.ActivityMainBinding
import com.project.soboro.factory.MainViewModelFactory
import com.project.soboro.model.MainViewModel
import com.project.soboro.repository.Repository
import com.project.soboro.util.PreferenceUtil
import com.project.soboro.util.SoboroConstant
import org.w3c.dom.Text


class MainActivity : AppCompatActivity() {

    lateinit var b: ActivityMainBinding;
    //  Shared Preference를 위한 Util 객체
    lateinit var prefs: PreferenceUtil;
    lateinit var userGreetText:TextView;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        b = ActivityMainBinding.inflate(layoutInflater);
        setContentView(b.root);
        prefs = PreferenceUtil(applicationContext);
        SoboroConstant.tempContext = this;

        // 사용자 이름 렌더링
        userGreetText = findViewById<TextView>(R.id.userGreetText);
        userGreetText.text = prefs.getString("userName", "no Name");

        // 오디오 권한을 요청한다
        requestAudioPermission();

        // 가이드 버튼
        val moveGuideBtn = findViewById<LinearLayout>(R.id.moveGuideBtn);
        moveGuideBtn.setOnClickListener {
            val intent = Intent(this, GuideActivity::class.java);
            startActivity(intent);
        }

        // 혹시 Programmically하게 바꿀 경우!

        // QR 버튼
        val showQRBtn = findViewById<LinearLayout>(R.id.showQRBtn);
        showQRBtn.setOnClickListener {
            showQRDialog(prefs.getString("userToken", "no Tokens"));
//            Toast.makeText(this, "token : ${SoboroConstant.userToken}", Toast.LENGTH_SHORT).show();
        }
        
        // 발음 연습 버튼
        val movePracticeBtn = findViewById<LinearLayout>(R.id.movePracticeBtn);
        movePracticeBtn.setOnClickListener{
            val intent = Intent(this, VoicePracticeAcitivty::class.java);
            startActivity(intent);
        }

        // 음성 번역기 버튼
        val moveTranslationBtn = findViewById<LinearLayout>(R.id.moveTranslationBtn);
        moveTranslationBtn.setOnClickListener {
            val intent = Intent(this, VoiceTranslationActivity::class.java);
            startActivity(intent);
        }

        // 히스토리 이동 버튼
        val moveRecentLogBtn = findViewById<LinearLayout>(R.id.moveRecentLogBtn);
        moveRecentLogBtn.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java);
            startActivity(intent);
        }

        // 사용자 정보
        val seeUserInfoBtn = findViewById<TextView>(R.id.seeUserInfoBtn);
        seeUserInfoBtn.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java);
            startActivity(intent);
        }
    }

    private fun showQRDialog(text: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.qr_dialog)
        val imgView = dialog.findViewById<ImageView>(R.id.dialogImageView);
        imgView.setImageBitmap(generateQR(text, 1024));
        val dialogCloseBtn = dialog.findViewById<Button>(R.id.dialogCloseBtn);
        dialogCloseBtn.setOnClickListener {
            dialog.cancel();
            dialog.dismiss();
        }
        dialog.show()
    }


    // 사용자 이름 실시간 렌더링
    override fun onRestart() {
        super.onRestart()
        userGreetText.text = prefs.getString("userName", "no Name");
    }

    // 오디오 권한 선언
    private val requiredPermissions = arrayOf(android.Manifest.permission.RECORD_AUDIO);
    // 오디오 권한 요청 함수
    private fun requestAudioPermission() {
        requestPermissions(requiredPermissions, REQUEST_RECORD_AUDIO_PERMISSION);
    }

    // 오디오 권한 요청 실패시 앱 종료
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        //권한이 부여받은게 맞는지 check 권한부여받았으면 true 아니면 false
        val audioRequestPermissionGranted =
            requestCode == REQUEST_RECORD_AUDIO_PERMISSION &&
                    grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED

        //권한이 부여되지않으면 어플 종료
        if (!audioRequestPermissionGranted) {
            finish()
        }
    }

    // QR 생성
    fun generateQR(content: String?, size: Int): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val barcodeEncoder = BarcodeEncoder()
            bitmap = barcodeEncoder.encodeBitmap(
                content,
                BarcodeFormat.QR_CODE, size, size
            )
        } catch (e: WriterException) {
            // Log.e("generateQR()","");
        }
        return bitmap
    }

    // 오디오 권한 요청을 위한 요청 코드 선언
    companion object {
        //permission code 선언
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 201
    }
}
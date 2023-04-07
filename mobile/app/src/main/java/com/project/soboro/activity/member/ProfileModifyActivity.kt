package com.project.soboro.activity.member

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.project.soboro.R
import com.project.soboro.activity.regist.ChangePasswordActivity
import com.project.soboro.data.ModifyUserInfoData
import com.project.soboro.util.SoboroTimer
import com.project.soboro.databinding.ActivityProfileModifyBinding
import com.project.soboro.factory.MainViewModelFactory
import com.project.soboro.model.MainViewModel
import com.project.soboro.repository.Repository
import com.project.soboro.util.PreferenceUtil
import com.project.soboro.util.SoboroConstant
import com.project.soboro.util.SoboroRegex
import java.util.Timer

class ProfileModifyActivity : AppCompatActivity() {

    lateinit var b:ActivityProfileModifyBinding;
    var emailTimer:Timer = Timer();
    var phoneTimer:Timer = Timer();
    var timers = mutableListOf<Timer>(emailTimer, phoneTimer);
    lateinit var prefs:PreferenceUtil;

    // 이름 수정 레이아웃
    lateinit var modifyNameEditText:EditText;
    lateinit var modifyNameGuideText:TextView;
    lateinit var modifyNameBtn:Button;

    // 이메일 수정 레이아웃
    lateinit var modifyEmailGuideText:TextView;
    lateinit var modifyEmailEditText:EditText;
    lateinit var modifyEmailCheckCodeItem:LinearLayout;
    lateinit var modifyEmailSendCodeBtn:Button;
    lateinit var modifyEmailCheckTimerText:TextView;
    lateinit var modifyEmailCheckText:EditText;
    lateinit var modifyEmailCheckBtn:Button;


    // 휴대폰 수정 레이아웃
    lateinit var modifyPhoneBtn:Button;
    lateinit var modifyPhoneEditText:EditText;

    // 회원정보 수정용 객체
    lateinit var modifyUserInfoData: ModifyUserInfoData;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_modify)
        b = ActivityProfileModifyBinding.inflate(layoutInflater);
        setContentView(b.root);
        SoboroConstant.tempContext = this;
        prefs = PreferenceUtil(this);

        modifyUserInfoData =  ModifyUserInfoData(
            userId = prefs.getString("userId", "no Id"),
            userName = prefs.getString("userName", "no Name"),
            userEmail = prefs.getString("userEmail", "no email"),
            userPhone = prefs.getString("userPhone", "no Phone")
        )

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        val wrongColor = ContextCompat.getColor(this, R.color.wrong_color);
        val currectColor = ContextCompat.getColor(this, R.color.currect_color);

        // 이름 수정 기능 ///////////////////
        // 이름 유효성 가이드 텍스트
        modifyNameGuideText = findViewById<TextView>(R.id.modifyNameGuideText);
        // 이름 입력 텍스트
        modifyNameEditText = findViewById<EditText>(R.id.modifyNameEditText);
        modifyNameEditText.addTextChangedListener { s: Editable? ->

            modifyUserInfoData = getModifyUserInfo(userName = s.toString());
            SoboroRegex.setValidText(s, SoboroRegex.namePattern, modifyNameGuideText, SoboroRegex.nameGuideText, wrongColor);
        }

        // 이름 수정 버튼 텍스트
        modifyNameBtn = findViewById<Button>(R.id.modifyNameBtn);
        // var isNameCicked = false;
        modifyNameBtn.setOnClickListener {
           if(SoboroRegex.setValidText(
                   modifyNameEditText.text,
                   SoboroRegex.namePattern,
                   modifyNameGuideText,
                   SoboroRegex.nameGuideText,
                   wrongColor)) {

               modifyUserInfo(modifyUserInfoData,
                   btn = modifyNameBtn,
                   editText = modifyNameEditText
               );

//               disableBtn(modifyNameBtn, modifyNameEditText);

//               modifyNameBtn.background = ContextCompat.getDrawable(this, R.drawable.round_primary_disable_btn);
//               modifyNameBtn.text = "변경완료";
//               modifyNameBtn.isEnabled = false;
//               modifyNameEditText.isEnabled = false;
           }
        }

        // 이메일 수정 기능 /////////////////////////////////////////
        // 이메일 유효성 가이드 텍스트
        modifyEmailGuideText = findViewById<TextView>(R.id.modifyEmailGuideText);
        // 이메일 입력 텍스트
        modifyEmailEditText = findViewById<EditText>(R.id.modifyEmailEditText);

        modifyEmailEditText.addTextChangedListener { s: Editable? ->
            SoboroRegex.setValidText(s, SoboroRegex.emailPattern, modifyEmailGuideText, SoboroRegex.emailGuideText, wrongColor);
            modifyUserInfoData = getModifyUserInfo(userEmail = s.toString());
        }

        // 이메일 코드 아이템
        modifyEmailCheckCodeItem = findViewById<LinearLayout>(R.id.modifyEmailCheckCodeItem);
        // 휴대폰 코드 발송 버튼
        modifyEmailSendCodeBtn = findViewById<Button>(R.id.modifyEmailSendCodeBtn);
        var isEmailClicked = false;
        var isEmailDisabled = false;
        // 인증코드 타이머 텍스트
        modifyEmailCheckTimerText = findViewById<TextView>(R.id.modifyEmailCheckTimerText);

        // 이메일 인증 코드 발송
        modifyEmailSendCodeBtn.setOnClickListener {
            if(SoboroRegex.setValidText(
                    modifyEmailEditText.text,
                    SoboroRegex.emailPattern,
                    modifyEmailGuideText,
                    SoboroRegex.emailGuideText,
                    wrongColor)){
                if(!isEmailDisabled){

//                    SoboroRegex.sendCodeSetActive(isEmailClicked,
//                        modifyEmailCheckCodeItem,
//                        modifyEmailSendCodeBtn,
//                        timers,
//                        0,
//                        modifyEmailCheckTimerText,
//                        "재발송");
//
//                    sendFindPwdCodeByEmail(
//                        modifyEmailEditText?.text.toString()
//                    );

                    modifyUserInfo(modifyUserInfoData,
                        btn = modifyEmailSendCodeBtn,
                        editText = modifyEmailEditText
                    )

                    isEmailClicked = true;
                }
            }
        }

//        modifyEmailCheckText = findViewById<EditText>(R.id.modifyEmailCheckText); // 이메일 코드 입력 텍스트
//        modifyEmailCheckBtn = findViewById<Button>(R.id.modifyEmailCheckBtn); // 이메일 코드 인증 버튼
//        modifyEmailCheckBtn.setOnClickListener {
//            if(!isEmailDisabled){
//                // 더미데이터
//                val code = "abcd";
//
//                certifyFindPwd(
//                    id = modifyUserInfoData.userId,
//                    email = modifyUserInfoData.userEmail,
//                    code = modifyEmailCheckText?.text.toString()
//                )
//
//                modifyUserInfo(
//                    modifyUserInfoData,
//                    btn = modifyEmailSendCodeBtn,
//                    editText = modifyEmailEditText
//                )
//
//                isEmailDisabled = true;
//
//                if(code.equals(modifyEmailCheckText.text.toString())){
//                    SoboroRegex.setInActiveItems(this, modifyEmailSendCodeBtn,
//                        modifyEmailCheckBtn, modifyEmailEditText,
//                        modifyEmailCheckText, timers[0], modifyEmailCheckTimerText);
//
////                    modifyUserInfo(
////                        modifyUserInfoData,
////                        btn = modifyEmailSendCodeBtn,
////
////                    );
//
//                }
//            }
//        }

        /// 휴대폰 번호 수정 기능 ///////////////////////

        // 휴대폰 유효성 가이드 텍스트
        val modifyPhoneGuideText = findViewById<TextView>(R.id.modifyPhoneGuideText);
        // 휴대폰 입력 텍스트
        modifyPhoneEditText = findViewById<EditText>(R.id.modifyPhoneEditText);

        modifyPhoneEditText.addTextChangedListener { s: Editable? ->
            SoboroRegex.setValidText(s, SoboroRegex.phonePattern, modifyPhoneGuideText, SoboroRegex.phoneGuideText, wrongColor);
            modifyUserInfoData = getModifyUserInfo(userPhone = s.toString());

        }


        // 휴대폰  코드 아이템
        // val modifyPhoneCheckCodeItem = findViewById<LinearLayout>(R.id.modifyPhoneCheckCodeItem);
        // 휴대폰 코드 발송 버튼
        modifyPhoneBtn = findViewById<Button>(R.id.modifyPhoneBtn);
        var isPhoneClicked = false;
        var isPhoneDisabled = false;
        // 인증코드 타이머 텍스트
        // val modifyPhoneCheckTimerText = findViewById<TextView>(R.id.modifyPhoneCheckTimerText);

        // 휴대폰 번호 변경 버튼
        modifyPhoneBtn.setOnClickListener {
            if(SoboroRegex.setValidText(
                    modifyPhoneEditText.text,
                    SoboroRegex.phonePattern,
                    modifyPhoneGuideText,
                    SoboroRegex.phoneGuideText,
                    wrongColor)){
                if(!isPhoneDisabled){
//                    SoboroRegex.sendCodeSetActive(isPhoneClicked,
//                        modifyPhoneCheckCodeItem,
//                        modifyPhoneSendCodeBtn,
//                        timers,
//                        1,
//                        modifyPhoneCheckTimerText,
//                        "재발송");

//                    disableBtn(
//                        modifyPhoneBtn,
//                        modifyPhoneEditText
//                    );

                    modifyUserInfo(modifyUserInfoData,
                        btn = modifyPhoneBtn,
                        editText = modifyPhoneEditText
                    );

                    isPhoneClicked = true;
                }
            }
        }

//        val modifyPhoneCheckText = findViewById<EditText>(R.id.modifyPhoneCheckText); // 휴대폰 코드 입력 텍스트
//        val modifyPhoneCheckBtn = findViewById<Button>(R.id.modifyPhoneCheckBtn); // 휴대폰 코드 인증 버튼
//        modifyPhoneCheckBtn.setOnClickListener {
//            if(!isPhoneDisabled){
//                // 더미데이터
//                val code = "abcd";
//                if(code.equals(modifyPhoneCheckText.text.toString())){
//                    SoboroRegex.setInActiveItems(this, modifyPhoneSendCodeBtn,
//                        modifyPhoneCheckBtn, modifyPhoneEditText,
//                        modifyPhoneCheckText, timers[1], modifyPhoneCheckTimerText);
//                    isPhoneDisabled = true;
//                }
//            }
//        }

        // 비밀번호 변경 액티비티로 이동
        val moveChangePasswordBtn = findViewById<TextView>(R.id.moveChangePasswordBtn);
        moveChangePasswordBtn.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java);
            startActivity(intent);
        }

        // 회원정보 렌더링
        modifyNameEditText.setText("${modifyUserInfoData.userName}");
        modifyEmailEditText.setText("${modifyUserInfoData.userEmail}");
        modifyPhoneEditText.setText("${modifyUserInfoData.userPhone}")

    }

    fun getModifyUserInfo(
        userId:String = prefs.getString("userId", "no Id"),
        userName:String = prefs.getString("userName", "no Name"),
        userEmail:String = prefs.getString("userEmail", "no email"),
        userPhone:String = prefs.getString("userPhone", "no Phone")
    ):ModifyUserInfoData{
        val modifyUserInfoData
            = ModifyUserInfoData(
                userId=userId,
                userName=userName,
                userEmail=userEmail,
                userPhone=userPhone
            )
        return modifyUserInfoData;
    }

    fun disableBtn(btn:Button, editText: EditText){
        btn.text = "변경 완료";
        btn.isEnabled = false;
        btn.background = ContextCompat.getDrawable(this, R.drawable.round_primary_disable_btn);
        editText.isEnabled = false;
    }

    fun modifyUserInfo(modifyUserInfoData: ModifyUserInfoData, btn:Button, editText: EditText){
        val repository = Repository();
        val viewModelFactory = MainViewModelFactory(repository);
        var viewModel:MainViewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java);

        viewModel.modifyUserInfo(
            accToken = "${SoboroConstant.HeaderPrefix} ${prefs.getString("userToken", "no Token")}",
            modifyUserInfoData
        );

        viewModel.modifyUserInfoResult.observe(this, Observer {
            Toast.makeText(this, "${it.toString()}", Toast.LENGTH_SHORT).show();
            disableBtn(btn, editText);
        })
        //disableBtn(btn = btn, editText=editText);
    }

    // 이메일로 인증코드 요청
    fun sendFindPwdCodeByEmail(email:String){
        val repository = Repository();
        val viewModelFactory = MainViewModelFactory(repository);
        val viewModel:MainViewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java);

        viewModel.sendFindPwdCodeByEmail(email);
        viewModel.findPwdResult.observe(this, Observer {
            Toast.makeText(this, "${it.toString()}", Toast.LENGTH_SHORT).show();
        })
    }

    // 입력된 결과 값을 검증
    fun certifyFindPwd(id:String, email:String, code:String){
        // SoboroConstant.errorMessage = "인증에 실패하였습니다 \n아이디 혹은 인증코드를 확인해주세요.";
        SoboroConstant.tempContext = this;

        val repository = Repository();
        val viewModelFactory = MainViewModelFactory(repository);
        val viewModel:MainViewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java);

        viewModel.certifyFindPwd(id, email, code);
        viewModel.certifyPwdResult.observe(this, Observer {
            // Toast.makeText(this, "${it.toString()}", Toast.LENGTH_SHORT).show();
            SoboroRegex.setInActiveItems(this,
                sendCodeBtn = modifyEmailSendCodeBtn,
                checkBtn = modifyEmailCheckBtn,
                checkText = modifyEmailCheckText,
                inputText = modifyEmailEditText,
                timer = emailTimer,
                timerText = modifyEmailCheckTimerText
            );
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        phoneTimer.cancel();
        emailTimer.cancel();

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
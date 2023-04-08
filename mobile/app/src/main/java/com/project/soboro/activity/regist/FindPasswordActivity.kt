package com.project.soboro.activity.regist

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.project.soboro.R
import com.project.soboro.activity.LoginActivity
import com.project.soboro.util.SoboroRegex
import com.project.soboro.util.SoboroTimer
import com.project.soboro.databinding.ActivityFindPasswordBinding
import com.project.soboro.factory.MainViewModelFactory
import com.project.soboro.model.MainViewModel
import com.project.soboro.repository.Repository
import androidx.lifecycle.Observer;
import com.project.soboro.util.PreferenceUtil
import com.project.soboro.util.SoboroConstant
import java.util.*

class FindPasswordActivity : AppCompatActivity() {
    lateinit var b:ActivityFindPasswordBinding;
    var mTimer = Timer();
    var timers = mutableListOf<Timer>(mTimer);
    lateinit var prefs:PreferenceUtil;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_password)
        b = ActivityFindPasswordBinding.inflate(layoutInflater);
        setContentView(b.root);
        SoboroConstant.tempContext = this;
        prefs = PreferenceUtil(this);

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        // 색상
        val wrongColor = ContextCompat.getColor(this, R.color.wrong_color);
        val currectColor = ContextCompat.getColor(this, R.color.currect_color);

        // 아이디 가이드 텍스트
        val findPwdIdGuideText = findViewById<TextView>(R.id.findPwdIdGuideText);
        // 아이디 입력 텍스트
        val findPwdIdEditText = findViewById<EditText>(R.id.findPwdIdEditText);
        var isIdValid = false; // 아이디 유효성 상태 관리 용  boolean 변수
        findPwdIdEditText.addTextChangedListener { s:Editable? ->
            isIdValid = SoboroRegex.setValidText(s, SoboroRegex.idPattern, findPwdIdGuideText, SoboroRegex.idGuideText, wrongColor);
        }

        // 이메일 유효성 가이드 텍스트
        val findPwdEmailGuideText = findViewById<TextView>(R.id.findPwdEmailGuideText);
        // 이메일 입력 텍스트
        val findPwdEmailEditText = findViewById<EditText>(R.id.findPwdEmailEditText);
        var isEmailValid = false; // 이메일 유효성 상태 관리 용  boolean 변수
        findPwdEmailEditText.addTextChangedListener { s:Editable? ->
            isEmailValid = SoboroRegex.setValidText(s, SoboroRegex.emailPattern,
                findPwdEmailGuideText, SoboroRegex.emailGuideText, wrongColor);
        }

        // 이메일 입력 부분
        // 이메일 코드 아이템
        val findPwdEmailCheckCodeItem = findViewById<LinearLayout>(R.id.findPwdEmailCheckCodeItem);
        // 이메일 발송 버튼
        val findPwdEmailSendCodeBtn = findViewById<Button>(R.id.findPwdEmailSendCodeBtn);
        var isEmailClicked = false;
        var isEmailDisabled = false; // 인증완료시 터치 막는 변수
        // 인증코드 타이머 텍스트
        val findPwdEmailCheckTimerText = findViewById<TextView>(R.id.findPwdEmailCheckTimerText);
        findPwdEmailSendCodeBtn.setOnClickListener {
            if(isIdValid && isEmailValid){
                if(!isEmailDisabled){
                    sendFindPwdCodeByEmail(findPwdEmailEditText?.text.toString());

                    SoboroRegex.sendCodeSetActive(isEmailClicked,
                        findPwdEmailCheckCodeItem,
                        findPwdEmailSendCodeBtn,
                        timers,
                        0,
                        findPwdEmailCheckTimerText,
                        "재발송");
                    isEmailClicked = true;
                }
            }
        }
        val findPwdEmailCheckText = findViewById<EditText>(R.id.findPwdEmailCheckText); // 이메일 코드 입력 텍스트
        val findPwdBtn = findViewById<Button>(R.id.findPwdBtn); // 이메일 코드 인증 버튼
        findPwdBtn.setOnClickListener {

            if(isIdValid && isEmailValid){
                if(!isEmailClicked) sendFindPwdCodeByEmail(findPwdEmailEditText?.text.toString()); // 이메일 발송

                if(!isEmailClicked){
                    if(isIdValid && isEmailValid){
                        if(!isEmailDisabled){
                            SoboroRegex.sendCodeSetActive(isEmailClicked,
                                findPwdEmailCheckCodeItem,
                                findPwdEmailSendCodeBtn,
                                timers,
                                0,
                                findPwdEmailCheckTimerText,
                                "재발송");
                            isEmailClicked = true;
                        }
                        findPwdBtn.text="인증하기"
                    }
                }else {
                    if(!isEmailDisabled){
                        // 더미데이터

                        certifyFindPwd(
                            findPwdIdEditText?.text.toString(),
                            findPwdEmailEditText?.text.toString(),
                            findPwdEmailCheckText?.text.toString()
                        )

                        // Log.d("CHANGE_PWD", "${findPwdIdEditText?.text.toString()}")
                        prefs.setString("userId", "${findPwdIdEditText?.text.toString()}");

//                    if(code.equals(findPwdEmailCheckText.text.toString())){
//                        SoboroRegex.setInActiveItems(this, findPwdEmailSendCodeBtn,
//                            findPwdBtn, findPwdEmailEditText,
//                            findPwdEmailCheckText, timers[0], findPwdEmailCheckTimerText);
//                        isEmailDisabled = true;
//                        // 아이디 부분 추가 disabled 처리
//                        findPwdIdEditText.isEnabled = false;
//
//                        /* 변수들 초기화 , 하드코딩.. 현재까진 리팩토링 방법 안떠오름 (3/15) */
//                        // 불리언
//                        isEmailClicked = false;
//                        isEmailDisabled = false;
//                        isEmailValid = false;
//                        isIdValid = false;
//
//                        // 에딧텍스트들 값 초기화
//                        findPwdIdEditText.text.clear();
//                        findPwdIdEditText.isEnabled = true;
//
//                        findPwdEmailEditText.text.clear();
//                        findPwdEmailEditText.isEnabled = true;
//
//                        findPwdEmailCheckText.text.clear();
//                        findPwdEmailCheckText.isEnabled = true;
//
//                        // 버튼 초기화
//                        val params = findPwdEmailCheckCodeItem.layoutParams;
//                        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
//                        params.height = 0;
//                        findPwdEmailCheckCodeItem.layoutParams = params;
//
//                        findPwdBtn.background = ContextCompat.getDrawable(this, R.drawable.round_primary_btn);
//                        findPwdBtn.text = "비밀번호 변경";
//
//                        // 이동 함수 ////
//                        moveToChangePwd()
//                    }

                    }
                }

            } else {
              Toast.makeText(this, "아이디 혹은 이메일을 확인해주세요", Toast.LENGTH_SHORT).show();
            }



        }

        // 취소 버튼
        val findPwdCancelBtn = findViewById<Button>(R.id.findPwdCancelBtn);
        findPwdCancelBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java);
            startActivity(intent);
            finish();
        }
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
            Toast.makeText(this, "${it.toString()}", Toast.LENGTH_SHORT).show();
            moveToChangePwd();
        })
    }

    fun moveToChangePwd(){
        val intent = Intent(this, ChangePasswordActivity::class.java);
        startActivity(intent);
        finish();
    }

    override fun onDestroy() {
        super.onDestroy()
        for(i in timers) i.cancel();
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
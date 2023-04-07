package com.project.soboro.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.soboro.R
import com.project.soboro.activity.regist.FindIdActivity
import com.project.soboro.activity.regist.FindPasswordActivity
import com.project.soboro.activity.regist.RegisterActivity
import com.project.soboro.data.LoginInfo
import com.project.soboro.databinding.ActivityLoginBinding
import com.project.soboro.util.SoboroRegex
import com.project.soboro.factory.MainViewModelFactory
import com.project.soboro.model.MainViewModel
import com.project.soboro.repository.Repository
import com.project.soboro.util.PreferenceUtil
import com.project.soboro.util.SoboroConstant


class LoginActivity : AppCompatActivity() {

    lateinit var b: ActivityLoginBinding;

    lateinit var id:String ;
    lateinit var pwd:String ;

    // editText 류...
    lateinit var loginIdInputText:EditText;
    lateinit var loginPwdInputText:EditText;

    lateinit var prefs: PreferenceUtil;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        b = ActivityLoginBinding.inflate(layoutInflater);
        setContentView(b.root);
        prefs = PreferenceUtil(this);
        prefs.init(this);
        //URL INIT
        SoboroConstant.GET_WAV_URL = prefs.getString(R.string.wav_address);
        SoboroConstant.JSON_BASE_URL = prefs.getString(R.string.json_address);
        SoboroConstant.SOBORO_BASE_URL = prefs.getString(R.string.base_address);
        SoboroConstant.SOBORO_VOICE_STT_URL = prefs.getString(R.string.stt_address);
        SoboroConstant.SOBORO_VOICE_TTS_URL = prefs.getString(R.string.tts_address);

        SoboroConstant.tempContext = this;

        // id, pwd 유효성 결과
        var isIdValid:Boolean = false;
        var isPwdValid:Boolean = false;

        // id, pwd init
        id = "";
        pwd = "";

        // 색상
        val wrongColor = ContextCompat.getColor(this, R.color.wrong_color);
        val currectColor = ContextCompat.getColor(this, R.color.currect_color);

        // 아이디 가이드 문구 text
        val loginIdGuideText = findViewById<TextView>(R.id.loginIdGuideText);
        // 로그인 텍스트 입력 창
        loginIdInputText = findViewById<EditText>(R.id.loginIdInputText);
        loginIdInputText.addTextChangedListener{ s: Editable? ->
            id = s.toString();
            isIdValid = SoboroRegex.setValidText(s, SoboroRegex.idPattern, loginIdGuideText, SoboroRegex.idGuideText, wrongColor);
        }

        // 비밀번호 가이드 문구
        val loginPwdGuideText = findViewById<TextView>(R.id.loginPwdGuideText);
        // 비밀번호 텍스트 입력창
        loginPwdInputText = findViewById<EditText>(R.id.loginPwdInputText);
        loginPwdInputText.addTextChangedListener{ s: Editable? ->
            pwd = s.toString();
            isPwdValid = SoboroRegex.setValidText(s, SoboroRegex.pwdPattern, loginPwdGuideText, SoboroRegex.pwdGuideText, wrongColor);
        }

        // 화면 이동용 버튼들
        // 로그인 버튼
        val loginBtn = findViewById<Button>(R.id.loginButton);
        loginBtn.setOnClickListener {
//            val isValid = idValid && pwdValid;
            val isValid = isIdValid && isPwdValid;
            SoboroConstant.tempContext = this;
            // SoboroConstant.errorMessage = "로그인에 실패하였습니다."

            if(isValid){
                doLogin(id, pwd);
            }else {
                 showLoginAlert();
            }
//            Log.d("TEST:::::::", "버튼 누름");
        }

        // 회원가입 버튼
        val registBtn = findViewById<TextView>(R.id.registerText);
        registBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java);
            startActivity(intent);
        }

        // 아이디 찾기 버튼
        val findIdBtn = findViewById<TextView>(R.id.findId);
        findIdBtn.setOnClickListener {
            val intent = Intent(this, FindIdActivity::class.java);
            startActivity(intent);
        }

        // 비밀번호 찾기 버튼
        val findPasswordBtn = findViewById<TextView>(R.id.findPassword);
        findPasswordBtn.setOnClickListener {
            // Toast.makeText(this, "현재 비밀번호 찾기를 이용할 수 없습니다.", Toast.LENGTH_SHORT).show();
            val intent = Intent(this, FindPasswordActivity::class.java);
            startActivity(intent);
        }
    }

    // 뷰모델 가져오는 메서드
    private fun getViewModel(): ViewModel {
        val repository = Repository();
        val viewModelFactory = MainViewModelFactory(repository);
        var viewModel: MainViewModel = ViewModelProvider(this,viewModelFactory).get(MainViewModel::class.java);
        return viewModel;
    }

    // 메인 액티비티 이동 함수
    fun moveMainActivity(){
        val intent = Intent(this, MainActivity::class.java);
        startActivity(intent);
        finish();
    }


    // 토스트 메세지
    fun showLoginAlert(){
        Toast.makeText(this, "아이디 또는 비밀번호를 다시 확인해주세요", Toast.LENGTH_SHORT).show();
    }

    fun doLogin(vararg info: String){
        val repository = Repository();
        val viewModelFactory = MainViewModelFactory(repository);
        var viewModel: MainViewModel = ViewModelProvider(this,viewModelFactory).get(MainViewModel::class.java);

        viewModel.doLogin(LoginInfo("${info[0]}", "${info[1]}")); // 각각 아이디, 비밀번호
        viewModel.userLoginToken.observe(this, Observer {
            SoboroConstant.userToken = it;
            prefs.setString("userToken", it.toString());
            // Log.d("LOGIN:::::", "${it.toString()}");
            if(it.length > 0){
                getUserInfo("${SoboroConstant.HeaderPrefix} ${it.toString()}");
            }else {
                showLoginAlert();
            }
        })
    }

    fun getUserInfo(accToken:String){
        val repository = Repository();
        val viewModelFactory = MainViewModelFactory(repository);
        var viewModel: MainViewModel = ViewModelProvider(this,viewModelFactory).get(MainViewModel::class.java);

        viewModel.getUserInfo(accToken);
        viewModel.soboroUserInfo.observe(this, Observer {
            // Log.d("GET_USER_INFO::::", "${it.toString()}");
            prefs.setString("userId", it.userId.toString());
            prefs.setString("userName", it.userName.toString());
            prefs.setString("userEmail", it.userEmail.toString());
            prefs.setString("userPhone", it.userPhone.toString());
            prefs.setString("userAuthType", it.userAuthType.toString());
            prefs.setString("userCreateTime", it.userCreateTime.toString());

            moveMainActivity();

            SoboroConstant.showToast(this, "로그인 성공");
        })
    }






}
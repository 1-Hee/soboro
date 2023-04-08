package com.project.soboro.activity.regist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.project.soboro.R
import com.project.soboro.activity.LoginActivity
import com.project.soboro.data.RegistUserInfo
import com.project.soboro.factory.MainViewModelFactory
import com.project.soboro.model.MainViewModel
import com.project.soboro.repository.Repository
import com.project.soboro.util.SoboroRegex
import androidx.lifecycle.Observer
import com.project.soboro.data.CheckId
import com.project.soboro.data.DuplicateUserId
import com.project.soboro.databinding.ActivityRegisterBinding
import com.project.soboro.util.PreferenceUtil
import com.project.soboro.util.SoboroConstant
import java.util.*

class RegisterActivity : AppCompatActivity() {

    lateinit var b: ActivityRegisterBinding;
    var emailTimer:Timer = Timer();
    var phoneTimer:Timer = Timer();
    var timers = mutableListOf<Timer>(emailTimer, phoneTimer);

    object RegistObject {
        lateinit var userId : String;
        lateinit var userPassword: String;
        lateinit var userName:String;
        lateinit var userEmail:String;
        lateinit var userPhone:String;
        lateinit var userGender:String;
        var userTerm:Boolean = false;
    }

    private val rejectString= "중복된 아이디 입니다";
    private val okString = "사용할 수 있는 아이디 입니다";

    lateinit var prefs:PreferenceUtil;

    // layout
    // ID
    lateinit var registIdGuideText:TextView // 아이디 텍스트
    lateinit var registIdEditText:EditText; // 아이디 입력 텍스트
    lateinit var registIdCheckOverlapBtn:Button; // 중복확인 버튼
    // PWD
    lateinit var registPwdGuideText:TextView;
    lateinit var registPwdEditText:EditText;
    lateinit var registPwdCheckText:TextView;
    lateinit var registPwdCheckEditText:EditText;

    // NAME
    lateinit var registNameGuideText:TextView;
    lateinit var registNameEditText:EditText;

    // EMAIL
    lateinit var registEmailGuideText:TextView;
    lateinit var registEmailSendCodeBtn:Button;
    lateinit var registEmailCheckBtn:Button;
    lateinit var registEmailEditText:EditText;
    lateinit var registEmailCheckText:EditText;
    lateinit var registEmailCheckTimerText:TextView;
    lateinit var registEmailCheckCodeItem:LinearLayout;

    // Phone
    lateinit var registPhoneGuideText:TextView;
    lateinit var registPhoneEditText:EditText;

    // SEX
    lateinit var manRadioBtn:RadioButton;
    lateinit var femaleRadioBtn:RadioButton;
    lateinit var registSexRadioGroup:RadioGroup;

    // 약관
    lateinit var registSeeTermsOfUse:TextView;
    lateinit var registSeePrivacyPolicy:TextView;

    // checkBox
    lateinit var registAgreeTermsOfUse:CheckBox;
    lateinit var registAgreePrivacyStatement:CheckBox;
    lateinit var registAgreeAllCheck:CheckBox;

    // 회원가입 버튼
    lateinit var registBtn:Button;
    lateinit var registCancelBtn:Button;

    var isPwdValid = false;
    var isPwdSame = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        b = ActivityRegisterBinding.inflate(layoutInflater);
        setContentView(b.root);
        prefs = PreferenceUtil(this);
        SoboroConstant.tempContext = this;

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        // 색상 init
        val wrongColor = ContextCompat.getColor(this, R.color.wrong_color);
        val currectColor = ContextCompat.getColor(this, R.color.currect_color);


        // 아이디 유효성 가이드 텍스트
        registIdGuideText = findViewById<TextView>(R.id.registIdGuideText);
        // 아이디 입력 텍스트
        registIdEditText = findViewById<EditText>(R.id.registIdEditText);
        var isIdValid = false;
        registIdEditText.addTextChangedListener { s:Editable? ->
            isIdValid =
                SoboroRegex.setValidText(s, SoboroRegex.idPattern,
                    registIdGuideText, SoboroRegex.idGuideText, wrongColor);

            // 아이디 객체 저장
            RegistObject.userId = s.toString();
        }
        // 아이디 중복확인 버튼
        registIdCheckOverlapBtn = findViewById<Button>(R.id.registIdCheckOverlapBtn);
        registIdCheckOverlapBtn.setOnClickListener {
            if(isIdValid){
                // 아이디 중복 확인 메서드
                searchId(
                    DuplicateUserId(registIdEditText.text.toString()),
                registIdGuideText, wrongColor, currectColor
                );
            };
        }


        // 회원가입 유효성 체크
        var inputPassword = ""; // 입력 비밀번호
        // 비밀번호 입력 가이드 텍스트
        registPwdGuideText = findViewById<TextView>(R.id.registPwdGuideText);
        // 비밀번호 입력 텍스트
        registPwdEditText = findViewById<EditText>(R.id.registPwdEditText);
        registPwdEditText.addTextChangedListener { s:Editable? ->
            inputPassword = s.toString();
            isPwdValid =
                SoboroRegex.setValidText(s, SoboroRegex.pwdPattern,
                    registPwdGuideText, SoboroRegex.pwdGuideText, wrongColor);
            // 비밀번호 객체 저장
            RegistObject.userPassword = registPwdEditText.text.toString();

            if(registPwdCheckEditText.text.length>0){
                // 비밀번호 감시
                isPwdSame = SoboroRegex.setIsSameText(registPwdCheckEditText.text, inputPassword,
                    registPwdCheckText, SoboroRegex.pwdDiffText, currectColor, wrongColor);
            }

        }

        // 비밀번호 일치 가이드 텍스트
        registPwdCheckText = findViewById<TextView>(R.id.registPwdCheckText);
        // 비밀번호 일치 확인 텍스트
        registPwdCheckEditText = findViewById<EditText>(R.id.registPwdCheckEditText);
        registPwdCheckEditText.addTextChangedListener { s:Editable? ->
            isPwdSame = SoboroRegex.setIsSameText(s, inputPassword,
                registPwdCheckText, SoboroRegex.pwdDiffText, currectColor, wrongColor);
        }

        // 이름 유효성 가이드 텍스트
        registNameGuideText = findViewById<TextView>(R.id.registNameGuideText);
        // 이름 입력 텍스트
        registNameEditText = findViewById<EditText>(R.id.registNameEditText);
        var isNameValid = false;
        registNameEditText.addTextChangedListener { s:Editable? ->
            isNameValid =
                SoboroRegex.setValidText(s, SoboroRegex.namePattern,
                    registNameGuideText, SoboroRegex.nameGuideText, wrongColor);
            // 비밀번호 객체 저장
            RegistObject.userName = s.toString();
        }

        // 이메일 유효성 가이드 텍스트
        registEmailGuideText = findViewById<TextView>(R.id.registEmailGuideText);
        // 이메일 입력 텍스트
        registEmailEditText = findViewById<EditText>(R.id.registEmailEditText);
        var isEmailValid = false;
        registEmailEditText.addTextChangedListener { s:Editable? ->
            isEmailValid =
                SoboroRegex.setValidText(s, SoboroRegex.emailPattern,
                    registEmailGuideText, SoboroRegex.emailGuideText, wrongColor);
            // 이메일 객체 저장
            RegistObject.userEmail = s.toString();
        }


        // 이메일 입력 부분
        // 이메일 코드 아이템
        registEmailCheckCodeItem = findViewById<LinearLayout>(R.id.registEmailCheckCodeItem);
        // 이메일 발송 버튼
        registEmailSendCodeBtn = findViewById<Button>(R.id.registEmailSendCodeBtn);
        var isEmailClicked = false;
        var isEmailDisabled = false; // 인증완료시 터치 막는 변수
        // 인증코드 타이머 텍스트
        registEmailCheckTimerText = findViewById<TextView>(R.id.registEmailCheckTimerText);
        registEmailSendCodeBtn.setOnClickListener {
            if(isEmailValid){
                if(!isEmailDisabled){

                    // SoboroConstant.errorMessage="인증 도중 오류가 발생했습니다. \n이메일 혹은 인증 코드를 확인해주세요";

                    // 매 요청마다 타이머 init
                    SoboroRegex.sendCodeSetActive(isEmailClicked,
                        registEmailCheckCodeItem,
                        registEmailSendCodeBtn,
                        timers,
                        0,
                        registEmailCheckTimerText,
                    "재발송");

                    // 이메일 발송
                    sendCodeToEmail(registEmailEditText?.text.toString());

                    isEmailClicked = true;
                }
            }
        }
        
        // 이메일 인증
        registEmailCheckText = findViewById<EditText>(R.id.registEmailCheckText); // 이메일 코드 입력 텍스트
        registEmailCheckBtn = findViewById<Button>(R.id.registEmailCheckBtn); // 이메일 코드 인증 버튼
        registEmailCheckBtn.setOnClickListener {
            if(!isEmailDisabled){
                if(!SoboroConstant.isTimerEnd){ // 타이머 작동중일 시

                    certifyEmail(
                        registEmailEditText?.text.toString(),
                        registEmailCheckText?.text.toString()
                    );

                    if(prefs.getString("data", "no data").equals("${registEmailCheckText?.text.toString()}")){
                        SoboroRegex.setInActiveItems(this, registEmailSendCodeBtn,
                            registEmailCheckBtn, registEmailEditText,
                            registEmailCheckText, timers[0], registEmailCheckTimerText);
                    }
                    isEmailDisabled = true;

                } else {
                    Toast.makeText(this, "유효 시간 만료", Toast.LENGTH_SHORT).show();
                }
            }
        }

        // 휴대폰 유효성 가이드 텍스트
        registPhoneGuideText = findViewById<TextView>(R.id.registPhoneGuideText);
        // 전화 번호 입력 텍스트
        registPhoneEditText = findViewById<EditText>(R.id.registPhoneEditText);
        var isPhoneValid = false;
        registPhoneEditText.addTextChangedListener { s:Editable? ->
            isPhoneValid =
                SoboroRegex.setValidText(s, SoboroRegex.phonePattern,
                    registPhoneGuideText, SoboroRegex.phoneGuideText, wrongColor);
            // 휴대폰 번호 객체 저장
            RegistObject.userPhone = s.toString();
        }

//        // 휴대폰 코드 아이템
//        val registPhoneCheckCodeItem = findViewById<LinearLayout>(R.id.registPhoneCheckCodeItem);
//        // 휴대폰 코드 발송 버튼
//        val registPhoneSendCodeBtn = findViewById<Button>(R.id.registPhoneSendCodeBtn);
//        var isPhoneClicked = false;
//        var isPhoneDisabled = false;
//        // 인증코드 타이머 텍스트
//        val registPhoneCheckTimerText = findViewById<TextView>(R.id.registPhoneCheckTimerText);
//        registPhoneSendCodeBtn.setOnClickListener {
//            if(isPhoneValid){
//                if(!isPhoneDisabled){
//                    SoboroRegex.sendCodeSetActive(isPhoneClicked,
//                        registPhoneCheckCodeItem,
//                        registPhoneSendCodeBtn,
//                        timers,
//                        1,
//                        registPhoneCheckTimerText,
//                        "재발송");
//                    isPhoneClicked = true;
//                }
//            }
//        }
//
//        val registPhoneCheckText = findViewById<EditText>(R.id.registPhoneCheckText); // 휴대폰 코드 입력 텍스트
//        val registPhoneCheckBtn = findViewById<Button>(R.id.registPhoneCheckBtn); // 휴대폰 코드 인증 버튼
//        registPhoneCheckBtn.setOnClickListener {
//            if(!isPhoneDisabled){
//                // 더미데이터
//                val code = "abcd";
//                if(code.equals(registPhoneCheckText.text.toString())){
//                    SoboroRegex.setInActiveItems(this, registPhoneSendCodeBtn,
//                        registPhoneCheckBtn, registPhoneEditText,
//                        registPhoneCheckText, timers[1], registPhoneCheckTimerText);
//                    isPhoneDisabled = true;
//                }
//            }
//        }


        // 라디오 버튼 init
        // 남성 성별 라디오 버튼
        manRadioBtn = findViewById<RadioButton>(R.id.maleRadioBtn);
        // 여성 성별 라디오 버튼
        femaleRadioBtn = findViewById<RadioButton>(R.id.femaleRadioBtn);
        manRadioBtn.isChecked = true;
        RegistObject.userGender = "M"

        // 성별 radioGroup
        registSexRadioGroup = findViewById<RadioGroup>(R.id.registSexRadioGroup);
        registSexRadioGroup.setOnCheckedChangeListener { radioGroup, i ->
            if(i == R.id.maleRadioBtn){
                RegistObject.userGender = "M"
            }else {
                RegistObject.userGender = "F"
            }
        }

        // 이용약관과 개인정보처리방침 전문 이동 버튼
        // 이용약관 이동
        registSeeTermsOfUse = findViewById<TextView>(R.id.registSeeTermsOfUse);

        registSeeTermsOfUse.setOnClickListener {
            val intent = Intent(this, SoboroUseOfTermActivity::class.java);
            startActivity(intent);
        }

        // 개인정보처리방침 전문 이동
        registSeePrivacyPolicy = findViewById<TextView>(R.id.registSeePrivacyPolicy);

        registSeePrivacyPolicy.setOnClickListener {
            val intent = Intent(this, SoboroPrivacyStatementActivity::class.java);
            startActivity(intent);
        }

        // 이용약관 동의 체크박스
        registAgreeTermsOfUse = findViewById<CheckBox>(R.id.registAgreeTermsOfUse);
        // 개인정보 취급방침 동의 체크박스
        registAgreePrivacyStatement = findViewById<CheckBox>(R.id.registAgreePrivacyStatement);
        // 전체동의 체크박스
        registAgreeAllCheck = findViewById<CheckBox>(R.id.registAgreeAllCheck); // 모두 동의

        // 체크박스 이벤트 처리
        registAgreeTermsOfUse.setOnClickListener {
            watchAllCheck(registAgreeTermsOfUse,
                registAgreePrivacyStatement,
                registAgreeAllCheck);
        }
        registAgreePrivacyStatement.setOnClickListener {
            watchAllCheck(registAgreeTermsOfUse,
                registAgreePrivacyStatement,
                registAgreeAllCheck);
        }

        registAgreeAllCheck.setOnClickListener {
            if(registAgreeAllCheck.isChecked){
                registAgreeTermsOfUse.isChecked = true;
                registAgreePrivacyStatement.isChecked = true;
            }else {
                registAgreeTermsOfUse.isChecked = false;
                registAgreePrivacyStatement.isChecked = false;
            }
        }
        
        // 회원가입 버튼
        registBtn = findViewById<Button>(R.id.registBtn);
        registBtn.setOnClickListener {
            RegistObject.userTerm = registAgreeTermsOfUse.isChecked && registAgreePrivacyStatement.isChecked;
            val isCheckValid = registAgreeTermsOfUse.isChecked && registAgreePrivacyStatement.isChecked;
            val checkList =
                arrayOf(isIdValid, isNameValid, isPwdValid, isPwdSame, isEmailDisabled, isPhoneValid, isCheckValid);

            if(checkAllValid(checkList)){
                doRegist(registUserInfo = RegistUserInfo(
                    userId = RegistObject.userId, userPassword = RegistObject.userPassword,
                    userName = RegistObject.userName, userEmail = RegistObject.userEmail,
                    userGender = RegistObject.userGender, userPhone = RegistObject.userPhone,
                    userTerm = RegistObject.userTerm
                ));
            } else {
                Toast.makeText(this, "회원가입 양식을 다시 확인해주세요", Toast.LENGTH_SHORT).show();
            }
        }
        
        // 취소 버튼
        registCancelBtn = findViewById<Button>(R.id.registCancelBtn);
        registCancelBtn.setOnClickListener {
            moveLogin(false);
        }
    }

    private fun moveLogin(isRegist:Boolean){
        if(isRegist) Toast.makeText(this, "회원가입이 완료되었습니다", Toast.LENGTH_SHORT).show();
        val intent = Intent(this, LoginActivity::class.java);
        startActivity(intent);
        finish();
    }
    private fun checkAllValid(checkList: Array<Boolean>): Boolean {
        for(i in checkList) if(!i) return false;
        return true;
    }



    // 윻성 검사 로직
    fun watchAllCheck(ck1:CheckBox, ck2:CheckBox, ck3:CheckBox){
        if(ck1.isChecked
            && ck2.isChecked)
            ck3.isChecked = true;
        else {
            ck3.isChecked = false
        };
    }

    // 회원가입
    fun doRegist(registUserInfo: RegistUserInfo){
        val repository = Repository();
        val viewModelFactory = MainViewModelFactory(repository);
        var viewModel: MainViewModel = ViewModelProvider(this,viewModelFactory).get(MainViewModel::class.java);
        viewModel.doRegist(registUserInfo);
        viewModel.registResult.observe(this, Observer{
            moveLogin(it);
        })
    }

    // 아이디 중복 검사
    fun searchId(dupUser: DuplicateUserId, rsTxtv:TextView, wColor:Int, cColor:Int){
        val repository = Repository();
        val viewModelFactory = MainViewModelFactory(repository);
        var viewModel: MainViewModel = ViewModelProvider(this,viewModelFactory).get(MainViewModel::class.java);

        viewModel.searchId(dupUser);
        viewModel.searchResult.observe(this, Observer {
            setOverlapIdText(it, rsTxtv, wColor, cColor);
        });

//        Toast.makeText(this, "${SoboroRegex.hasId}", Toast.LENGTH_SHORT).show();
    }

    // 회원가입 시 이메일 인증 요청
    fun sendCodeToEmail(email:String){
        val repository = Repository();
        val viewModelFactory = MainViewModelFactory(repository);
        var viewModel: MainViewModel = ViewModelProvider(this,viewModelFactory).get(MainViewModel::class.java);

        viewModel.sendCodeToEmail(email);
        viewModel.emailCertResult.observe(this, Observer{
            if(it != null)
                Toast.makeText(this, it?.message, Toast.LENGTH_SHORT).show();
            prefs.setString("data", "${it?.data}");

        })
    }

    // 회원 가입시 인증 코드 서버 검증
    fun certifyEmail(email: String, code:String){
        val repository = Repository();
        val viewModelFactory = MainViewModelFactory(repository);
        val viewModel: MainViewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java);

        viewModel.certifyEmail(email, code);
        viewModel.emailCertServerResult.observe(this, Observer {
            Toast.makeText(this, "${it.toString()}", Toast.LENGTH_SHORT).show();

        });
    }

    override fun onDestroy() {
        super.onDestroy()
        emailTimer.cancel();
        phoneTimer.cancel();
    }

    // 아이디 중복용 regex 테스트
    fun setOverlapIdText(isRight:Boolean, rsGuideTv:TextView, wrongColor:Int, currectColor:Int){
        SoboroRegex.showCheckText(
             isRight = isRight,
            okText = okString, rejectText = rejectString,
            textView = rsGuideTv,
            wrongColor = wrongColor,
            currectColor =  currectColor
        );
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
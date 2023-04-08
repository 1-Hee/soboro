package com.project.soboro.activity.regist

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.project.soboro.R
import com.project.soboro.activity.LoginActivity
import com.project.soboro.data.ChangePwdInfo
import com.project.soboro.databinding.ActivityChangePasswordBinding
import com.project.soboro.factory.MainViewModelFactory
import com.project.soboro.model.MainViewModel
import com.project.soboro.repository.Repository
import com.project.soboro.util.PreferenceUtil
import com.project.soboro.util.SoboroConstant
import com.project.soboro.util.SoboroRegex

class ChangePasswordActivity : AppCompatActivity() {

    lateinit var b:ActivityChangePasswordBinding;
    lateinit var prefs:PreferenceUtil;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        b = ActivityChangePasswordBinding.inflate(layoutInflater);
        setContentView(b.root);
        SoboroConstant.tempContext = this;
        prefs = PreferenceUtil(this);

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        // 색상
        val wrongColor = ContextCompat.getColor(this, R.color.wrong_color);
        val currectColor = ContextCompat.getColor(this, R.color.currect_color);

        // 비밀번호
        var newPassword = ""
        // 비밀번호 가이드 텍스트
        val changePwdGuideText = findViewById<TextView>(R.id.changePwdGuideText);
        // 비밀번호 텍스트
        val changePwdEditText = findViewById<EditText>(R.id.changePwdEditText);
        changePwdEditText.addTextChangedListener { s:Editable? ->
            newPassword = s.toString();
            SoboroRegex.setValidText(s, SoboroRegex.pwdPattern, changePwdGuideText, SoboroRegex.pwdGuideText, wrongColor);
        }

        // 비밀번호 체크 가이드 텍스트
        val changePwdCheckGuideText = findViewById<TextView>(R.id.changePwdCheckGuideText);
        // 비밀번호 체크 텍스트
        val changePwdCheckEditText = findViewById<EditText>(R.id.changePwdCheckEditText);
        changePwdCheckEditText.addTextChangedListener { s:Editable? ->
            SoboroRegex.setIsSameText(s, newPassword,
                changePwdCheckGuideText, SoboroRegex.pwdDiffText, currectColor, wrongColor);
        }

        // 변경사항 저장 버튼
        val changePwdBtn = findViewById<Button>(R.id.changePwdBtn);
        changePwdBtn.setOnClickListener {



            val changePwdInfo = ChangePwdInfo(
                id = prefs.getString("userId", "no Id"),
                password = changePwdEditText?.text.toString()
            );
            changePassword(changePwdInfo);
        }

        // 취소 버튼
        val changePwdCancelBtn = findViewById<Button>(R.id.changePwdCancelBtn);
        changePwdCancelBtn.setOnClickListener {
//            val intent = Intent(this, LoginActivity::class.java);
//            startActivity(intent);
            onBackPressed();
            finish();
        }
    }

    fun changePassword(changePwdInfo: ChangePwdInfo){
        val repository = Repository();
        val viewModelFactory = MainViewModelFactory(repository);
        var viewModel: MainViewModel = ViewModelProvider(this,viewModelFactory).get(MainViewModel::class.java);

        viewModel.changePassword(changePwdInfo);
        viewModel.chagePwdResult.observe(this, Observer {
            Toast.makeText(this, "${it.toString()}", Toast.LENGTH_SHORT).show();

            // Toast.makeText(this, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show();
            val intent = Intent(this, LoginActivity::class.java);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
package com.project.soboro.activity.regist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.soboro.R
import com.project.soboro.activity.LoginActivity
import com.project.soboro.util.SoboroRegex
import com.project.soboro.databinding.ActivityFindIdBinding
import com.project.soboro.factory.MainViewModelFactory
import com.project.soboro.model.MainViewModel
import com.project.soboro.repository.Repository
import com.project.soboro.util.SoboroConstant

class FindIdActivity : AppCompatActivity() {

    lateinit var b:ActivityFindIdBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityFindIdBinding.inflate(layoutInflater);
        setContentView(b.root);
        SoboroConstant.tempContext = this;

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        // 색상
        val wrongColor = ContextCompat.getColor(this, R.color.wrong_color);
        val currectColor = ContextCompat.getColor(this, R.color.currect_color);

        // 이메일 입력 가이드 텍스트
        val findIdGuideText = findViewById<TextView>(R.id.findIdGuideText);
        var isEmailValid = false;
        // 이메일 입력 텍스트
        val findIdEmailEditText = findViewById<EditText>(R.id.findIdEmailEditText);
        findIdEmailEditText.addTextChangedListener { s:Editable? ->
            isEmailValid =
                SoboroRegex.setValidText(s, SoboroRegex.emailPattern,
                    findIdGuideText, SoboroRegex.emailGuideText, wrongColor);
        }
        
        // 아이디 찾기 버튼
        val findIdBtn = findViewById<Button>(R.id.findIdBtn);
        findIdBtn.setOnClickListener {
            if(isEmailValid){
                sendFindIdEmail(findIdEmailEditText?.text.toString());
                Toast.makeText(this, "입력하신 이메일로 아이디가 발송되었습니다", Toast.LENGTH_SHORT).show();
                moveLoginActivity();
            }else {
                Toast.makeText(this, "이메일을 올바르게 입력해주세요", Toast.LENGTH_SHORT).show();
            }
        }

        //  취소 버튼
        val findCancelBtn = findViewById<Button>(R.id.findIdCancelBtn);
        findCancelBtn.setOnClickListener {
            moveLoginActivity();
        }
    }

    fun sendFindIdEmail(email: String){
        val repository = Repository();
        val viewModelFactory = MainViewModelFactory(repository);
        var viewModel:MainViewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java);

        viewModel.sendFindIdEmail(email);
        viewModel.findIdResult.observe(this, Observer {

        })
    }

    fun moveLoginActivity(){
        val intent = Intent(this, LoginActivity::class.java);
        startActivity(intent);
        finish();
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
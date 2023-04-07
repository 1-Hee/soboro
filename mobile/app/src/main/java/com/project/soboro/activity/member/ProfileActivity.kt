package com.project.soboro.activity.member

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.project.soboro.R
import com.project.soboro.activity.LoginActivity
import com.project.soboro.databinding.ActivityProfileBinding
import com.project.soboro.factory.MainViewModelFactory
import com.project.soboro.model.MainViewModel
import com.project.soboro.repository.Repository
import com.project.soboro.util.PreferenceUtil
import com.project.soboro.util.SoboroConstant

class ProfileActivity : AppCompatActivity() {
    lateinit var  b:ActivityProfileBinding;
    lateinit var prefs:PreferenceUtil;

    // 레이아웃
    lateinit var profileUserName:TextView;
    lateinit var profileUserNumber:TextView;
    lateinit var profileUserEmail:TextView;
    lateinit var userAuthTypeText:TextView;
    lateinit var userCreateTimeText:TextView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        b = ActivityProfileBinding.inflate(layoutInflater);
        setContentView(b.root);
        SoboroConstant.tempContext = this;
        prefs = PreferenceUtil(this);

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        // 사용자 정보 init
        profileUserName = findViewById<TextView>(R.id.profileUserName);
        profileUserNumber = findViewById<TextView>(R.id.profileUserNumber);
        profileUserEmail = findViewById<TextView>(R.id.profileUserEmail);
        userAuthTypeText = findViewById<TextView>(R.id.userAuthTypeText);
        userCreateTimeText = findViewById<TextView>(R.id.userCreateTimeText);

        getUserInfo(
            accToken = "${SoboroConstant.HeaderPrefix} ${prefs.getString("userToken", "no Token")}"
        );

        val moveProfileEditBtn = findViewById<TextView>(R.id.moveProfileEditBtn);
        moveProfileEditBtn.setOnClickListener {
            val intent = Intent(this, ProfileModifyActivity::class.java);
            startActivity(intent);
        }

        val profileLogoutBtn = findViewById<Button>(R.id.profileLogoutBtn);
        profileLogoutBtn.setOnClickListener {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.logout_modal)

            // 모달 버튼 이벤트 리스너 등록
            val logoutCancelBtn = dialog.findViewById<Button>(R.id.logoutCancelBtn);
            logoutCancelBtn.setOnClickListener {
                dialog.dismiss();
            }

            val logoutBtn = dialog.findViewById<Button>(R.id.logoutBtn);
            logoutBtn.setOnClickListener {
                dialog.dismiss();
                Log.d("LogOut >>> ", "${SoboroConstant.HeaderPrefix} ${prefs.getString("userToken", "no Token")}");
                doLogout("${SoboroConstant.HeaderPrefix} ${prefs.getString("userToken", "no Token")}");
                val intent = Intent(this, LoginActivity::class.java);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();

                startActivity(intent);
                finish();
            }

            dialog.show()
        }


        // 회원탈퇴 버튼
        val profileWithdrawalBtn = findViewById<Button>(R.id.profileWithdrawalBtn);
        profileWithdrawalBtn.setOnClickListener {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.withdrawal_modal)

            // 회원탈퇴 모달의 이벤트 리스너 등록
            val withdrawalCancelBtn = dialog.findViewById<Button>(R.id.withdrawalCancelBtn);
            withdrawalCancelBtn.setOnClickListener {
                dialog.dismiss();
            }

            val withdrawalBtn = dialog.findViewById<Button>(R.id.withdrawalBtn);
            withdrawalBtn.setOnClickListener {
                dialog.dismiss();
                doWithdrawal("${SoboroConstant.HeaderPrefix} ${prefs.getString("userToken", "no Token")}");
                val intent = Intent(this, LoginActivity::class.java);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK )
                Toast.makeText(this, "회원 탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show();

                startActivity(intent);
                finish();
            }
            dialog.show()
        }
    }


    // 로그아웃
    fun doLogout(accToken: String){
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository);
        val viewModel:MainViewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java);

        viewModel.doLogout(accToken);
        viewModel.userLogoutResult.observe(this, Observer {

        })
    }

    // 회원탈퇴
    fun doWithdrawal(accToken: String){
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository);
        val viewModel:MainViewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java);

        viewModel.doWithdrawal(accToken);
        viewModel.userWithdrawalResult.observe(this, Observer{

        })
    }

    // 사용자 정보 로딩
    fun getUserInfo(accToken:String){
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository);
        val viewModel:MainViewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java);

        viewModel.getUserInfo(accToken);
        viewModel.soboroUserInfo.observe(this, Observer {

            prefs.setString("userId", it.userId.toString());
            prefs.setString("userName", it.userName.toString());
            prefs.setString("userEmail", it.userEmail.toString());
            prefs.setString("userPhone", it.userPhone.toString());
            prefs.setString("userAuthType", it.userAuthType.toString());
            prefs.setString("userCreateTime", it.userCreateTime.toString());

            profileUserName.text = it.userName
            profileUserNumber.text = it.userPhone
            profileUserEmail.text = it.userEmail;
            userAuthTypeText.text = it.userAuthType;
            userCreateTimeText.text = it.userCreateTime;

        })
    }

    override fun onRestart() {
        super.onRestart()
        getUserInfo(
            accToken = "${SoboroConstant.HeaderPrefix} ${prefs.getString("userToken", "no Token")}"
        );
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
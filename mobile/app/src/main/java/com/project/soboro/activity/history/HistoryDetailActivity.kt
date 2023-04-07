package com.project.soboro.activity.history

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.ScrollView
import android.widget.TextClock
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.project.soboro.R
import com.project.soboro.data.ConsultDetailInfo
import com.project.soboro.databinding.ActivityHistoryDetailBinding
import com.project.soboro.factory.MainViewModelFactory
import com.project.soboro.model.MainViewModel
import com.project.soboro.repository.Repository
import com.project.soboro.util.PreferenceUtil
import com.project.soboro.util.SoboroConstant
import com.project.soboro.util.SoboroCreateItem


class HistoryDetailActivity : AppCompatActivity() {
    lateinit var b:ActivityHistoryDetailBinding;
    lateinit var prefs:PreferenceUtil;
    lateinit var consultDetailDate:TextView;
    lateinit var consultDetailPlace:TextView;
    lateinit var consultDetailContainer:LinearLayout;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_detail)
        b = ActivityHistoryDetailBinding.inflate(layoutInflater);
        setContentView(b.root);
        SoboroConstant.tempContext = this;
        prefs = PreferenceUtil(this);
        SoboroConstant.logListIdx = 0;

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        val token = "${SoboroConstant.HeaderPrefix} ${prefs.getString("userToken", "no Token")}";

        consultDetailDate = findViewById<TextView>(R.id.consultDetailDate);
        consultDetailPlace = findViewById<TextView>(R.id.consultDetailPlace);
        consultDetailContainer = findViewById<LinearLayout>(R.id.consultDetailContainer);

        // 상담 상세 정보로 init
        val curNo = prefs.getString("detailNo", "-1").toInt();
        getConsultDetailLog(
            "${SoboroConstant.HeaderPrefix} ${prefs.getString("userToken", "no Token")}",
            curNo
        );
        getConsultConversation(curNo, 0);


        val historyDetailScroll:ScrollView = findViewById<ScrollView>(R.id.historyDetailScroll);
        historyDetailScroll.setOnScrollChangeListener { view, i, i2, i3, i4 ->
            val view = historyDetailScroll.getChildAt(historyDetailScroll.childCount - 1) as View
            val diff = view.bottom - (historyDetailScroll.height + historyDetailScroll.scrollY)

            // if diff is zero, then the bottom has been reached
            if (diff == 0) {
                // do stuff
                getConsultConversation(curNo, page=++SoboroConstant.logListIdx);
                // Log.d("CONSULT_CHAT", "${SoboroConstant.logListIdx}")
            }
        }

        // 정보 초기화


    }

    fun getConsultDetailLog(accToken: String, consultingNo:Int = 0){
        val repository = Repository();
        val viewModelFactory = MainViewModelFactory(repository);
        var viewModel: MainViewModel = ViewModelProvider(this,viewModelFactory).get(MainViewModel::class.java);

        viewModel.getConsultDetail(accToken, consultingNo);
        viewModel.userLogDetailInfo.observe(this, Observer{
            val ConsultDetailInfo = it;
            prefs.setString("videoURL", "${it.videoLocation}");
            prefs.setString("detailPlaceName", "${it.consultingVisitPlace}");
            prefs.setString("datailDate","${it.consultingVisitDate}");

            consultDetailDate.text = it.consultingVisitDate.toString();
            consultDetailPlace.text = it.consultingVisitPlace.toString();

        })
    }

    fun getConsultConversation(consultingNo:Int, page:Int = 0) {
        val repository = Repository();
        val viewModelFactory = MainViewModelFactory(repository);
        var viewModel: MainViewModel = ViewModelProvider(this,viewModelFactory).get(MainViewModel::class.java);

        viewModel.getConsultConversation(consultingNo, page);
        viewModel.userChatList.observe(this, Observer {

            for(i in it){
                val item = SoboroCreateItem.createLogChatItem(this, i.contentSpeaker, i.contentText);
                consultDetailContainer.addView(item);
            }

        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
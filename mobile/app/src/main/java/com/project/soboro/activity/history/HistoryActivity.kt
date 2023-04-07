package com.project.soboro.activity.history

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.project.soboro.R
import com.project.soboro.databinding.ActivityHistoryBinding
import com.project.soboro.factory.MainViewModelFactory
import com.project.soboro.model.MainViewModel
import com.project.soboro.repository.Repository
import com.project.soboro.util.PreferenceUtil
import com.project.soboro.util.SoboroConstant
import com.project.soboro.util.SoboroCreateItem
import org.w3c.dom.Text

class HistoryActivity : AppCompatActivity() {
    lateinit var b:ActivityHistoryBinding;
    // 이미지에 배열 만들기
    lateinit var imgList:Array<Drawable?>;
    lateinit var prefs: PreferenceUtil;
    lateinit var historyContainer:LinearLayout;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        b = ActivityHistoryBinding.inflate(layoutInflater);
        setContentView(b.root);
        prefs = PreferenceUtil(this);
        SoboroConstant.tempContext = this;
        SoboroConstant.logListIdx = 0;


        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        historyContainer = findViewById<LinearLayout>(R.id.historyContainer);
        val token = "${SoboroConstant.HeaderPrefix} ${prefs.getString("userToken", "no Token")}";

        // temp
        val postImg:Drawable? = ContextCompat.getDrawable(this, R.drawable.icon_post_office);
        val bankImg:Drawable? = ContextCompat.getDrawable(this, R.drawable.icon_bank);
        val hospitalImg:Drawable? = ContextCompat.getDrawable(this, R.drawable.icon_hospital);
        val libraryImg:Drawable? = ContextCompat.getDrawable(this, R.drawable.icon_library);
        val officerImg:Drawable? = ContextCompat.getDrawable(this, R.drawable.icon_officer);

        imgList = arrayOf<Drawable?>(postImg, postImg, bankImg, hospitalImg, libraryImg, officerImg);
        
        historyContainer.removeAllViews();
        getUserConsultLogList(token, SoboroConstant.logListIdx);

        val scrollView:ScrollView = findViewById<ScrollView>(R.id.historyScroll);
        scrollView.setOnScrollChangeListener { view, i, i2, i3, i4 ->
            val view = scrollView.getChildAt(scrollView.childCount - 1) as View
            val diff = view.bottom - (scrollView.height + scrollView.scrollY)

            // if diff is zero, then the bottom has been reached
            if (diff == 0) {
                // do stuff
                getUserConsultLogList(token, ++SoboroConstant.logListIdx);
            }
        }

        val searchKeywordBox:EditText = findViewById<EditText>(R.id.searchKeywordBox);
        searchKeywordBox.addTextChangedListener {

            if(it.toString().length==0){
                historyContainer.removeAllViews();
                getUserConsultLogList(token, 0);
                SoboroConstant.logListIdx = 0;
            }else {
                historyContainer.removeAllViews();
                getSearchKeywordList(
                    accessToken = "${SoboroConstant.HeaderPrefix} ${prefs.getString("userToken", "no Token")}",
                    consultingVisitClass = "${it?.toString()}"
                );
            }
        }

    }

    fun getSearchKeywordList(accessToken: String,
                             consultingVisitClass:String,
                             page:Int = 0){

        val repository = Repository();
        val viewModelFactory = MainViewModelFactory(repository);
        var viewModel: MainViewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java);

        viewModel.getSearchKeywordList(accessToken, consultingVisitClass, page);
        viewModel.soboroConsultSearchLogList.observe(this, Observer{

            for (i in it){
                val item = SoboroCreateItem.createHistoryItem(this,
                    i.consultingVisitPlace, i.consultingVisitDate, imgList[getIndex(i.consultingVisitClass)]);
                historyContainer.addView(item);
                item.setId(i.consultingNo);
                item.setOnClickListener {
                    moveConsultDetal();
                    prefs.setString("detailNo", "${item.id}");
                }
                // Log.d("HISTORY>>>>","${item.id}");
            }

        })
    }

    // 컨설팅 리스트
    fun getUserConsultLogList(accToken:String, page:Int = 0){
        val repository = Repository();
        val viewModelFactory = MainViewModelFactory(repository);
        var viewModel: MainViewModel = ViewModelProvider(this,viewModelFactory).get(MainViewModel::class.java);

        // item Linear Layout

        viewModel.getCosultLogList(accToken, page);
        viewModel.soboroConsultLogList.observe(this, Observer{

            for( i in it ){
               val item = SoboroCreateItem.createHistoryItem(this,
                   i.consultingVisitPlace, i.consultingVisitDate, imgList[getIndex(i.consultingVisitClass)]);
               historyContainer.addView(item);
               item.setId(i.consultingNo);
               item.setOnClickListener {
                   moveConsultDetal();
                   prefs.setString("detailNo", "${item.id}");
               }
                // Log.d("HISTORY>>>>","${item.id}");
           }

            if(historyContainer.childCount==0){
                historyContainer.addView(SoboroCreateItem.getNoHistoryItem(this));
            }
        })
    }

    // 장소 종류에 따라 이미지 소스 가져오기
    fun getIndex(className:String):Int{
        // 0 기본, 1 우채국, 2 은행, 3 병원, 4. 도서관, 5. 관공서
        when(className){
            "우체국" -> {return 1}
            "은행" -> {return 2}
            "병원" -> {return 3}
            "도서관" -> {return 4}
            "동사무소" -> {return 5}
            else -> {return  5}
        }
    }

    fun moveConsultDetal(){
        val intent = Intent(this, HistoryDetailActivity::class.java);
        startActivity(intent);
    }

    override fun onDestroy() {
        super.onDestroy()
        SoboroConstant.logListIdx = 0;
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
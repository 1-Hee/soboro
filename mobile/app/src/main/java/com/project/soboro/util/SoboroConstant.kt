package com.project.soboro.util

import android.content.Context
import android.content.ContextParams
import android.provider.Settings.Global.getString
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.soboro.R
import com.project.soboro.factory.MainViewModelFactory
import com.project.soboro.model.MainViewModel
import com.project.soboro.repository.Repository
import com.project.soboro.service.JNextworkService
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.util.concurrent.TimeUnit


class SoboroConstant: AppCompatActivity() {

    object RetrofitInstance {
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(JSON_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        val api : JNextworkService by lazy {
            retrofit.create(JNextworkService::class.java)
        }
    }

    object SoboroInstance {

        var builder = OkHttpClient().newBuilder()
        var okHttpClient = builder
            .cookieJar(JavaNetCookieJar(CookieManager()))
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100,TimeUnit.SECONDS)
            .writeTimeout(100,TimeUnit.SECONDS)
            .build();

        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(SOBORO_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        val api : JNextworkService by lazy {
            retrofit.create(JNextworkService::class.java)
        }
    }

    object SoboroTTSVoiceInstance{
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(SOBORO_VOICE_TTS_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        }
        val api : JNextworkService by lazy {
           retrofit.create(JNextworkService::class.java)
        }
    }


    object SoboroSTTVoiceInstance{
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(SOBORO_VOICE_STT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        }
        val api : JNextworkService by lazy {
            retrofit.create(JNextworkService::class.java)
        }
    }

    companion object{
        var tempContext:Context? = null; // 토스트 메세지 출력을 위한 context
        var errorMessage:String = ""; // 에러 메세지

        var JSON_BASE_URL = ""
        var GET_WAV_URL = ""
        var SOBORO_BASE_URL =  "";
        var SOBORO_VOICE_TTS_URL = ""
        var SOBORO_VOICE_STT_URL = ""

        var userToken = ""; // 사용자 토큰, will be depreacted...
        var isVerified:Boolean = false; // 사용자 유효성

        val HeaderPrefix:String = "Bearer"
        var logListIdx:Int = 0;

        var isTimerEnd:Boolean = false;

        // method
        fun showToast(context:Context, message:String){
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }

        var isVoiceLock:Boolean = false;

    }
}



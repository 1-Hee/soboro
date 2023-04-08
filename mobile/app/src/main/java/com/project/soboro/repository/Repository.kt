package com.project.soboro.repository

import android.util.Log
import com.project.soboro.data.*
import com.project.soboro.util.SoboroConstant
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.File
import java.util.Objects

class Repository {
    suspend fun getPost(number:Int) : Post {
        return SoboroConstant.RetrofitInstance.api.getPost(number)
    }

    suspend fun getPostList() : MutableList<Post> {
        return SoboroConstant.RetrofitInstance.api.getPostList();
    }

    // 회원 관련 기능
    // 로그인
    suspend fun doLogin(loginInfo: LoginInfo): Object {
        return SoboroConstant.SoboroInstance.api.doLogin(loginInfo);
    }

    // 회원가입
    suspend fun doRegist(registUserInfo: RegistUserInfo):Object {
        return SoboroConstant.SoboroInstance.api.doRegister(registUserInfo);
    }

    // 아이디 중복 확인
    suspend fun searchId(dupUser:DuplicateUserId):Object{
        return SoboroConstant.SoboroInstance.api.searchId(dupUser.userId);
    }

    // 사용자 정보 조회
    suspend fun getUserInfo(accToken:String):Object{
        return SoboroConstant.SoboroInstance.api.getUserInfo(accToken);
    }

    // 회원가입 시 이메일 인증 요청
    suspend fun sendCodeToEmail(email:String):Object{
         return SoboroConstant.SoboroInstance.api.sendCodeToEmail(email);
    }

    // 회원 정보 수정
    suspend fun modifyUserInfo(accToken: String, modifyUserInfoData:ModifyUserInfoData):Object{
        return SoboroConstant.SoboroInstance.api.modifyUserInfo(accToken,modifyUserInfoData);
    }

    // 회원 가입시 인증 코드 서버 검증
    suspend fun certifyEmail(email: String, code:String):Object{
        return SoboroConstant.SoboroInstance.api.certifyEmail(email, code);
    }

    // 아이디 찾기 이메일 발송 요청
    suspend fun sendFindIdEmail(email: String):Object{
        return SoboroConstant.SoboroInstance.api.sendFindIdEmail(email);
    }

    // 비밀번호 찾기 시 이메일 발송 요청
    suspend fun sendFindPwdCodeByEmail(email: String):Object{
        return SoboroConstant.SoboroInstance.api.sendFindPwdCodeByEmail(email);
    }

    // 아이디, 이메일, 인증 코드 검증 메서드
    suspend fun certifyFindPwd(id:String, email:String, code:String):Object{
        return SoboroConstant.SoboroInstance.api.certifyFindPwd(id, email, code);
    }

    // 비밀번호 변경
    suspend fun changePassword(changePwdInfo: ChangePwdInfo):Object{
        return SoboroConstant.SoboroInstance.api.changePassword(changePwdInfo);
    }

    // 로그아웃
    suspend fun doLogout(accToken: String):Object{
        return SoboroConstant.SoboroInstance.api.doLogout(accToken);
    }

    // 회원탍퇴
    suspend fun doWithdrawal(accToken: String):Object{
        return SoboroConstant.SoboroInstance.api.doWithdrawal(accToken);
    }

    // 상담 관련 기능
    // 사용자 상담 기록 조회
    suspend fun getConsultList(accToken: String, page:Int=0):Object{
        return SoboroConstant.SoboroInstance.api.getConsultList(accToken, page);
    }

    suspend fun getSearchKeywordList(
        accessToken: String,
        consultingVisitClass:String,
        page:Int = 0
    ):Object{
        return SoboroConstant.SoboroInstance.api.getSearchKeywordList(accessToken, consultingVisitClass, page);
    }

    // 사용자 상담 기록 디테일 조회
    suspend fun getConsultDetail(accToken: String, consultingNo:Int = 0):Object{
        return SoboroConstant.SoboroInstance.api.getCosultDetail(accToken, consultingNo);
    }

    // 상담 대화 내용 요청
    // consultingNo
    suspend fun getConsultConversation(consultingNo:Int, page:Int = 0):Object{
        return SoboroConstant.SoboroInstance.api.getConsultConversation(consultingNo);
    }

    // VOICE
    // TTS 번역 요청 메서드 , only File Name
    suspend fun getTextTranslation(text:String):Object{
        return SoboroConstant.SoboroTTSVoiceInstance.api.getTextTranslation(text);
    }

    // TTS 번역 요청 응답 파일명으로 파일 저장 주소 요청
    suspend fun getWavFileURL(address: String):Object {
        return SoboroConstant.SoboroInstance.api.getWavFileURL(address);
    }

    // STT 번역 요청
    suspend fun uploadWavFile(body : MultipartBody.Part, isNaver:Boolean):Object{
        return SoboroConstant.SoboroSTTVoiceInstance.api.uploadFile(body, isNaver);
    }


}

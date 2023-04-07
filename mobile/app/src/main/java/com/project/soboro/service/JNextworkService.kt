package com.project.soboro.service

import com.project.soboro.data.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import java.util.Objects


interface JNextworkService {
    @GET("posts/{number}")
    suspend fun getPost(@Path("number") number:Int) : Post;
    @GET("posts")
    suspend fun getPostList() : MutableList<Post>;

    // 회원 로그인 관련 메서드
    // 로그인
    @POST("/api/auth/login")
    suspend fun doLogin(@Body loginInfo: LoginInfo): Object

    // 사용자 정보 로드
    @POST("/api/user")
    suspend fun doRegister(@Body registUserInfo: RegistUserInfo):Object

    // 아이디 중복 검사
    @POST("/api/user/duplicate/id")
    suspend fun searchId(@Query("idCheck") idCheck:String):Object

    // 사용자 정보 조회
    @GET("/api/user")
    suspend fun getUserInfo(@Header("authorization") accessToken: String):Object;

    // 회원가입 시 이메일 인증 요청
    @POST("/api/user/sendnumber/save")
    suspend fun sendCodeToEmail(@Query("email") email:String):Object;

    // 회원가입 시 인증 요청한 것 서버 검증
    @POST("/api/user/certification")
    suspend fun certifyEmail(@Query("email") email: String, @Query("code") code:String):Object;

    // 회원 정보 수정
    @PATCH("/api/user")
    suspend fun modifyUserInfo(@Header("authorization") accessToken: String, @Body modifyUserInfoData:ModifyUserInfoData):Object;

    // 아이디 찾기 이메일 발송 요청
    @POST("/api/user/sendnumber/findid")
    suspend fun sendFindIdEmail(@Query("email") email: String):Object;


    // 비밀번호 찾기 시 이메일 발송 요청
    @POST("/api/user/sendnumber/findpass")
    suspend fun sendFindPwdCodeByEmail(@Query("email") email: String):Object

    // 비밀번호 찾기 입력 정보 확인
    @POST("/api/user/certification/findpass")
    suspend fun certifyFindPwd(@Query("id") id:String, @Query("email") email: String, @Query("code") code:String):Object;

    // 비밀번호 변경 요청
    @PATCH("/api/user/change/pass")
    suspend fun changePassword(@Body changePwdInfo:ChangePwdInfo):Object

    // 로그아웃
    @POST("/api/auth/logout")
    suspend fun doLogout(@Header("authorization") accessToken: String):Object;

    // 회원탈퇴
    @DELETE("/api/user")
    suspend fun doWithdrawal(@Header("authorization") accessToken: String):Object;

    // 상담 기록 관련 메서드
    // 상담 기록 리스트 조회
    @GET("/api/consult/list")
    suspend fun getConsultList(@Header("authorization") accessToken: String, @Query("page") page:Int=0):Object;

    // 상담 리스트 검색
    @GET("/api/consult/list/search/{consultingVisitClass}")
    suspend fun getSearchKeywordList(
        @Header("authorization") accessToken: String,
        @Path("consultingVisitClass") consultingVisitClass:String,
        @Query("page") page:Int = 0):Object;

    // 상담 디테일 리스트 기본 정보
    @GET("/api/consult/detail")
    suspend fun getCosultDetail(@Header("authorization") accessToken: String, @Query("consultingNo") consultingNo:Int = 0):Object;

    // 상담 대화 내용 요청
    @GET("/api/consult/content/detail")
    suspend fun getConsultConversation(@Query("consultingNo") consultingNo:Int, @Query("page") page:Int = 0):Object;

    // VOICE
    // TTS 번역 요청 메서드 , only File Name
    @GET("/tts/{text}")
    suspend fun getTextTranslation(@Path("text") text:String):Object;

    // TTS 번역 요청 응답 파일명으로 파일 저장 주소 요청
    @GET("/api/ai/getWavFileTest")
    suspend fun getWavFileURL(@Query("address") address:String):Object;

    // STT 요청
    @Multipart
    @POST("/stt")
    suspend fun uploadFile(
        @Part webm_file: MultipartBody.Part,
        @Part("naver") naver: Boolean
    ): Object;


}
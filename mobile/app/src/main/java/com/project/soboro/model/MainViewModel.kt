package com.project.soboro.model

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.project.soboro.data.*
import com.project.soboro.repository.Repository
import com.project.soboro.util.PreferenceUtil
import com.project.soboro.util.SoboroConstant
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.http.Part

class MainViewModel(private val repository : Repository) : ViewModel(){
    val myResponse : MutableLiveData<Post> = MutableLiveData() // post >> json place holder
    val viewResponseList : MutableLiveData<MutableList<Post>> = MutableLiveData() // post List  >> json place holder

    val emailCertResult: MutableLiveData<EmailCertificateInfo?> = MutableLiveData(); // 이메일 인증 응답 결과
    val emailCertServerResult : MutableLiveData<String> = MutableLiveData(); // 비밀번호 변경 인증 유효성 검사 결과

    val registResult : MutableLiveData<Boolean> = MutableLiveData(); // 회원가입
    val modifyUserInfoResult : MutableLiveData<String> = MutableLiveData(); // 회원정보 수정
    val userLogoutResult:MutableLiveData<Boolean> = MutableLiveData(); // 로그아웃
    val userWithdrawalResult:MutableLiveData<Boolean> = MutableLiveData(); // 회원탈퇴

    val findIdResult : MutableLiveData<String> = MutableLiveData(); // 아이디 찾기 결과
    val findPwdResult : MutableLiveData<String> = MutableLiveData(); // 비밀번호 찾기 이메일 발송 결과
    val certifyPwdResult : MutableLiveData<String> = MutableLiveData(); // 비밀번호 변경 인증 유효성 검사 결과
    val chagePwdResult : MutableLiveData<String> = MutableLiveData(); // 비밀번호 변경 요청 결과

    val userLoginToken : MutableLiveData<String> = MutableLiveData<String>(); // 로그인 토큰 저장 객체
    val searchResult:MutableLiveData<Boolean> = MutableLiveData(); // 중복체크 LiveData
    val soboroUserInfo:MutableLiveData<SoboroUserInfo> = MutableLiveData<SoboroUserInfo>(); // 사용자 정보 LiveData
    val soboroConsultLogList:MutableLiveData<MutableList<ConsultLog>> = MutableLiveData(); // 사용자 로그 정보 LiveData
    val soboroConsultSearchLogList:MutableLiveData<MutableList<ConsultLog>> = MutableLiveData(); // 사용자 검색 도구

    val userLogDetailInfo:MutableLiveData<ConsultDetailInfo> = MutableLiveData(); // 사용자 상담 정보
    val userChatList:MutableLiveData<MutableList<UserChatInfo>> = MutableLiveData(); // 사용자 대화 정보
    val voiceFileName:MutableLiveData<String> = MutableLiveData(); // 파일 이름
    val soboroWavFileResult:MutableLiveData<String> = MutableLiveData(); // 음성번역 결과

    fun getPost(number:Int) {
        viewModelScope.launch(exceptionHandler) {
            val response = repository.getPost(number);
            myResponse.value = response
        }
    }

    fun getPostList() {
        viewModelScope.launch(exceptionHandler) {
            val response = repository.getPostList();
            viewResponseList.value = response
        }
    }

    // 회원 관련 메서드
    // 로그인...
    fun doLogin(loginInfo: LoginInfo){
        SoboroConstant.errorMessage = "로그인 중 오류 발생"

        viewModelScope.launch(exceptionHandler) {
            val response: Object = repository.doLogin(loginInfo);

            val gson = Gson();
            val jsonString = gson.toJson(response)
            val jsonObject = JSONObject(jsonString)

            SoboroConstant.userToken = jsonObject.get("data").toString(); // 저장되는 곳 바꿀 것!
            SoboroConstant.isVerified = true;

            userLoginToken.value = jsonObject.get("data").toString();

        }
    }

    // 회원가입...
    fun doRegist(registUserInfo: RegistUserInfo){
        SoboroConstant.errorMessage = "회원가입 중 오류 발생"

        viewModelScope.launch(exceptionHandler) {
            val response: Object = repository.doRegist(registUserInfo);

            val gson = Gson();
            val jsonString = gson.toJson(response)
            val jsonObject = JSONObject(jsonString)

            registResult.value = jsonObject.has("message");
        }
    }

    // 아이디 중복 체크...
    fun searchId(dupUser: DuplicateUserId){
        SoboroConstant.errorMessage = "아이디 중복 조회 중 오류 발생"

        viewModelScope.launch(exceptionHandler) {
            val response : Object = repository.searchId(dupUser);

            val gson = Gson();
            val jsonString = gson.toJson(response)
            val jsonObject = JSONObject(jsonString)
            val message:String = jsonObject.get("message").toString();

            searchResult.value = message.equals("아이디 사용 가능");
        }
    }

    // 사용자 정보 가져오기
    fun getUserInfo(accToken:String){
        SoboroConstant.errorMessage = "회원 정보 로딩 중 오류 발생"

        viewModelScope.launch(exceptionHandler){
            val response : Object = repository.getUserInfo(accToken);
            val gson = Gson();
            val jsonString = gson.toJson(response)
            val jsonObject = JSONObject(jsonString)

            val dataObject = JSONObject(jsonObject.get("data").toString());

            val serverSoboroUserInfo = SoboroUserInfo(
               userId = dataObject.get("userId").toString(),
                userName = dataObject.get("userName").toString(),
                userEmail = dataObject.get("userEmail").toString(),
                userPhone =  dataObject.get("userPhone").toString(),
                userAuthType = dataObject.get("userAuthType").toString(),
                userCreateTime = dataObject.get("userCreateTime").toString()
            );

            soboroUserInfo.value = serverSoboroUserInfo;
        }
    }

    // 회원가입 시 이메일 인증 요청
    fun sendCodeToEmail(email:String){
        SoboroConstant.errorMessage = "이메일 발송 중 오류 발생"

        viewModelScope.launch(exceptionHandler) {
            val response = repository.sendCodeToEmail(email);

            val gson = Gson();
            val jsonString = gson.toJson(response)
            val jsonObject = JSONObject(jsonString)

            val isAccept = jsonObject.get("message") != null;

            if(isAccept){
                val emailCertificateInfo = EmailCertificateInfo(
                    message = jsonObject?.get("message").toString(),
                    data = jsonObject?.get("data").toString()
                )
                emailCertResult.value = emailCertificateInfo;
            } else {
                emailCertResult.value = null;
            }
        }
    }

    // 회원 가입시 인증 코드 서버 검증
    fun certifyEmail(email: String, code:String){
        SoboroConstant.errorMessage = "이메일 인증 중 오류 발생"

        viewModelScope.launch(exceptionHandler) {
            val response = repository.certifyEmail(email, code);

            val gson = Gson();
            val jsonString = gson.toJson(response)
            val jsonObject = JSONObject(jsonString)

            if(jsonObject.has("error")) throw Exception();

            emailCertServerResult.value = "인증 완료되었습니다";
        }
    }

    // 회원 정보 수정
    fun modifyUserInfo(accToken: String,modifyUserInfoData:ModifyUserInfoData){
        SoboroConstant.errorMessage = "회원정보 수정 중 오류 발생"

        viewModelScope.launch {
            val response = repository.modifyUserInfo(accToken, modifyUserInfoData);

            val gson = Gson();
            val jsonString = gson.toJson(response)
            val jsonObject = JSONObject(jsonString)

            modifyUserInfoResult.value = jsonObject.get("message").toString()

        }
    }

    // 아이디 찾기 이메일 발송 메서드
    fun sendFindIdEmail(email: String){
        SoboroConstant.errorMessage = "아이디 찾기 이메일 전송 중 오류 발생"

        viewModelScope.launch(exceptionHandler){
            val response = repository.sendFindIdEmail(email);

            val gson = Gson();
            val jsonString = gson.toJson(response)
            val jsonObject = JSONObject(jsonString)

            findIdResult.value = jsonObject.get("message").toString();
        }
    }

    // 비밀번호 찾기 시 이메일 발송 요청
    fun sendFindPwdCodeByEmail(email: String){
        SoboroConstant.errorMessage = "비밀번호 찾기 인증 코드 발송 중 오류 발생"

        viewModelScope.launch(exceptionHandler) {
            val response = repository.sendFindPwdCodeByEmail(email);

            val gson = Gson();
            val jsonString = gson.toJson(response)
            val jsonObject = JSONObject(jsonString)

            findPwdResult.value = jsonObject.get("message").toString();

        }
    }

    // 아이디, 이메일, 인증 코드 검증 메서드
    fun certifyFindPwd(id:String, email:String, code:String){
        SoboroConstant.errorMessage = "비밀번호 찾기 인증 중 오류 발생"

        viewModelScope.launch(exceptionHandler) {
            val response = repository.certifyFindPwd(id, email, code);

            val gson = Gson();
            val jsonString = gson.toJson(response)
            val jsonObject = JSONObject(jsonString)

            if(jsonObject.has("error")) throw Exception();

            certifyPwdResult.value = "인증되었습니다"
        }
    }

    // 비밀번호 변경
    fun changePassword(changePwdInfo: ChangePwdInfo){
        SoboroConstant.errorMessage = "비밀번호 변경 중 오류 발생"

        viewModelScope.launch {
            val response = repository.changePassword(changePwdInfo);

            val gson = Gson();
            val jsonString = gson.toJson(response)
            val jsonObject = JSONObject(jsonString)

            if(jsonObject.has("error")) throw Exception();

            chagePwdResult.value = jsonObject.get("message").toString();

        }
    }

    // 로그아웃
    fun doLogout(accToken: String){
        SoboroConstant.errorMessage = "로그아웃 중 오류 발생"

        viewModelScope.launch {
            val response = repository.doLogout(accToken);

            val gson = Gson();
            val jsonString = gson.toJson(response)
            val jsonObject = JSONObject(jsonString)

            if(jsonObject.has("error")) throw Exception();

            userLogoutResult.value = true;
        }

    }

    // 회원탈퇴
    fun doWithdrawal(accToken: String){
        SoboroConstant.errorMessage = "회원 탈퇴 중 오류 발생"

        viewModelScope.launch {
            val response = repository.doWithdrawal(accToken);

            val gson = Gson();
            val jsonString = gson.toJson(response)
            val jsonObject = JSONObject(jsonString)

            if(jsonObject.has("error")) throw Exception();

            userWithdrawalResult.value = true;
        }
    }


    // 상담 기록 관련 메서드
    // 상담 리스트 불러오기
    fun getCosultLogList(accToken: String, page:Int = 0){
        SoboroConstant.errorMessage = "상담 리스트 불러오던 중 오류 발생"
        viewModelScope.launch(exceptionHandler) {
            val response = repository.getConsultList(accToken, page);

            val gson = Gson();
            val jsonString = gson.toJson(response)
            val jsonObject = JSONObject(jsonString)
            val dataObject = JSONObject(jsonObject.get("data").toString());
            val contentObject = dataObject.get("content");
            val contentArray = JSONArray(contentObject.toString());

            var mutableList:MutableList<ConsultLog> = mutableListOf();

            for(item in 0 .. contentArray.length()-1){
                val jsonObj:JSONObject = JSONObject(contentArray[item].toString());

                val consultingNo = jsonObj.get("consultingNo").toString().toInt();
                val consultingVisitPlace = jsonObj.get("consultingVisitPlace").toString();
                val consultingVisitDate = jsonObj.get("consultingVisitDate").toString();
                val consultingVisitClass = jsonObj.get("consultingVisitClass").toString();

                mutableList.add(ConsultLog(consultingNo, consultingVisitPlace, consultingVisitDate, consultingVisitClass));
            }
            soboroConsultLogList.value = mutableList;
        }
    }

    // 상담 리스트 검사
    fun getSearchKeywordList(
        accessToken: String,
        consultingVisitClass:String,
        page:Int = 0
    ){
        SoboroConstant.errorMessage = "상담 기록 검색 중 오류 발생"
        viewModelScope.launch(exceptionHandler){

            val response = repository.getSearchKeywordList(accessToken, consultingVisitClass, page);

            val gson = Gson();
            val jsonString = gson.toJson(response)
            val jsonObject = JSONObject(jsonString)
            val dataObject = JSONObject(jsonObject.get("data").toString());
            val contentObject = dataObject.get("content");
            val contentArray:JSONArray = JSONArray(contentObject.toString());

            var mutableList:MutableList<ConsultLog> = mutableListOf();

            for(item in 0 .. contentArray.length()-1){
                val jsonObj:JSONObject = JSONObject(contentArray[item].toString());

                val consultingNo = jsonObj.get("consultingNo").toString().toInt();
                val consultingVisitPlace = jsonObj.get("consultingVisitPlace").toString();
                val consultingVisitDate = jsonObj.get("consultingVisitDate").toString();
                val consultingVisitClass = jsonObj.get("consultingVisitClass").toString();

                mutableList.add(ConsultLog(consultingNo, consultingVisitPlace, consultingVisitDate, consultingVisitClass));
            }

            soboroConsultSearchLogList.value = mutableList;

        }
    }

    fun getConsultDetail(accToken: String, consultingNo:Int = 0){
        SoboroConstant.errorMessage = "상담 기록 불러오던 중 오류 발생"

        viewModelScope.launch(exceptionHandler) {
            val response = repository.getConsultDetail(accToken, consultingNo);

            val gson = Gson();
            val jsonString = gson.toJson(response)
            val jsonObject = JSONObject(jsonString)

            val dataArray = JSONArray(jsonObject.get("data").toString());
            val resultObject = JSONObject(dataArray[0].toString());

            val consultDetailInfo:ConsultDetailInfo = ConsultDetailInfo(
                consultingNo = resultObject.get("consultingNo").toString().toInt(),
                consultingVisitPlace = resultObject.get("consultingVisitPlace").toString(),
                consultingVisitDate = resultObject.get("consultingVisitDate").toString(),
                consultingVisitClass = resultObject.get("consultingVisitClass").toString(),
                videoLocation = resultObject.get("videoLocation").toString()
            )
            userLogDetailInfo.value = consultDetailInfo;
        }
    }

    fun getConsultConversation(consultingNo:Int, page:Int = 0){
        SoboroConstant.errorMessage = "상담 기록을 로딩합니다"

        viewModelScope.launch(exceptionHandler) {
            val response = repository.getConsultConversation(consultingNo, page);

            val gson = Gson();
            val jsonString = gson.toJson(response)
            val jsonObject = JSONObject(jsonString)
            val dataObject = JSONObject(jsonObject.get("data").toString());
            val contentArr = JSONArray(dataObject.get("content").toString());

            var mutableList:MutableList<UserChatInfo> = mutableListOf();

            for(i in 0..contentArr.length()-1){
                val jsonObject = JSONObject(contentArr[i].toString());
                val userChatInfo:UserChatInfo = UserChatInfo(
                    consultingNo = jsonObject.get("consultingNo").toString().toInt(),
                    contentText = jsonObject.get("contentText").toString(),
                    contentSpeaker = jsonObject.get("contentSpeaker").toString().toBoolean()
                )
                // Log.d("CONSULT_CHAT", "${userChatInfo.toString()}");
                mutableList.add(userChatInfo);
            }
            // Log.d("CONSULT_CHAT", "${response.toString()}");
            userChatList.value = mutableList;
        }
    }

    // VOICE 관련  기능
    // TTS 결과 요청
    fun getTextTranslation(text:String){
        SoboroConstant.errorMessage = "음성 번역 중 오류 발생"

        viewModelScope.launch(exceptionHandler) {
            val response = repository.getTextTranslation(text);

            val gson = Gson();
            val jsonString = gson.toJson(response)
            val jsonObject = JSONObject(jsonString)

            Log.d("TRANS:::::", "${jsonObject.toString()}");

            if(jsonObject.has("status_code")) throw Exception();

            voiceFileName.value = jsonObject.get("filename").toString();
        }
    }

    // TTS 번역 요청 응답 파일명으로 파일 저장 주소 요청
    fun getWavFileURL(address: String){
        SoboroConstant.errorMessage = "파일 불러오는 중 오류 발생"

        viewModelScope.launch {
            val response = repository.getWavFileURL(address);
        }
    }

    // STT
    fun uploadWavFile(body:MultipartBody.Part, isNaver:Boolean){
        SoboroConstant.errorMessage = "음성 번역 중 오류 발생"

        viewModelScope.launch(exceptionHandler) {
            val response = repository.uploadWavFile(body, isNaver);

            val gson = Gson();
            val jsonString = gson.toJson(response)
            val jsonObject = JSONObject(jsonString)

            soboroWavFileResult.value = jsonObject.get("file").toString()
        }
    }


    // 코루틴 에러 핸들러 >> coroutine exception Handelr
    protected val exceptionHandler = CoroutineExceptionHandler{ i, exception ->
        Log.d("ERR ::::", "에러 발생.... ${exception.message}");
        Log.d("ERR ::::", "에러 발생.... ${exception.toString()}");

        val context = SoboroConstant.tempContext;
        Toast.makeText(context, "${SoboroConstant.errorMessage}", Toast.LENGTH_SHORT).show();
        SoboroConstant.errorMessage = "";
        SoboroConstant.isVoiceLock = false;
    }

}
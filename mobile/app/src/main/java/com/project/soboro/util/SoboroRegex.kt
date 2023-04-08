package com.project.soboro.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.project.soboro.R
import java.util.*
import java.util.regex.Pattern

class SoboroRegex {
    companion object{
        // 아이디 패턴 Regex
        val idPattern = Pattern.compile("^(?=.*[a-zA-Z0-9])(?=.*[a-zA-Z!@#\$%^&*])(?=.*[0-9!@#\$%^&*]).{6,15}\$")
        val idGuideText = "아이디는 영문, 숫자를 포함하여 6-15자 이내로 입력해 주세요";
        // 비밀번호 패턴 Regex
        val pwdPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,20}$");
        val pwdGuideText = "비밀번호는 문자 숫자 특수 문자의 조합으로 8글자 이상 20자 이하로 입력해주세요"

        // 이름 패턴 Regex
        val namePattern = Pattern.compile("^[ㄱ-ㅣ가-힣]+$");
        val nameGuideText = "이름은 한글만 입력 가능합니다.";

        // 이메일 패턴 Regex
        val emailPattern = Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+");
        val emailGuideText = "이메일 형식이 맞지 않습니다.";
        // 휴대폰 번호 Regex
        val phonePattern = Pattern.compile("^[0-9]{11}")
        val phoneGuideText = "전화번호를 올바르게 입력해주세요"
        // 비밀번호 문자열 일치 여부
        val pwdDiffText = "비밀번호가 같지 않습니다"
        val pwdSameText = "비밀번호가 동일합니다"
        var hasId:Boolean = false; // 중복 확인 flag
        // 중복확인 메서드
        fun showCheckText(isRight:Boolean, okText:String, rejectText:String, textView: TextView, currectColor:Int, wrongColor: Int){
            if(isRight){
                textView.text = okText;
                textView.visibility = View.VISIBLE;
                textView.textSize = 16f;
                textView.setTextColor(currectColor);
            } else {
                textView.text = rejectText;
                textView.visibility = View.VISIBLE;
                textView.textSize = 16f;
                textView.setTextColor(wrongColor);

            }
        }

        // 문자열의 유효성
        fun setValidText(s: Editable?, regex:Pattern, textView: TextView, guideText:String, currectColor:Int):Boolean{
            if(s.toString()?.length > 0){
                if(!regex.matcher(s).matches())                {
                    textView.text = guideText;
                    textView.visibility = View.VISIBLE;
                    textView.textSize = 16f;
                    textView.setTextColor(currectColor);
                    return false;
                }else {
                    hideTextView(textView, currectColor);
                    return true;
                }
            }else {
                hideTextView(textView, currectColor);
                return false;
            }
        }

        // 가이트 텍스트 숨김 메서드
        fun hideTextView(textView: TextView, currectColor: Int){
            textView.text = ""
            textView.visibility = View.INVISIBLE;
            textView.textSize = 0f;
            textView.setTextColor(currectColor);
        }

        //비밀 번호 일치 체크 메서드
        fun setIsSameText(s:Editable?, s1:String, textView: TextView, guideText: String, currectColor:Int, wrongColor:Int):Boolean{
            if(s.toString()?.length > 0){
                textView.text = guideText;
                textView.visibility = View.VISIBLE;
                textView.textSize = 16f;
                textView.setTextColor(wrongColor);
                if(s.toString()?.equals(s1)){
                    textView.text = "비밀번호가 일치합니다"
                    textView.setTextColor(currectColor);
                    return true;
                }
                return false;
            } else {
                hideTextView(textView, wrongColor);
                return false;
            }
        }

        // 인증코드 활성화 기능 함수
        fun sendCodeSetActive(isClicked:Boolean, item: LinearLayout, senCodeBtn: Button, timers:MutableList<Timer>, timerIdx:Int, timerText:TextView, btnText:String){
            val sec = 180;
            if(!isClicked){
                val params = item.layoutParams;
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                item.layoutParams = params;
                senCodeBtn.text= btnText;

                // 반복적으로 사용할 TimerTask
                val timeTask = SoboroTimer.getTimerTask(timers[timerIdx], timerText, sec);
                timers[timerIdx].schedule(timeTask, 0, 1000);
            }else{
                timers[timerIdx].cancel();
                timers[timerIdx] = Timer();
                val timeTask = SoboroTimer.getTimerTask(timers[timerIdx], timerText, sec);
                timers[timerIdx].schedule(timeTask, 0, 1000);
            }
        }

        fun setInActiveItems(context: Context, sendCodeBtn:Button,
                             checkBtn:Button, inputText: EditText,
                             checkText: EditText,
                             timer:Timer,
                             timerText:TextView) {
            val disable = ContextCompat.getDrawable(context, R.drawable.round_primary_disable_btn);
            sendCodeBtn.background = disable;
            sendCodeBtn.setTextColor(ContextCompat.getColor(context, R.color.white));
            checkBtn.background = disable;
            checkBtn.setTextColor(ContextCompat.getColor(context, R.color.white));
            inputText.isEnabled = false;
            checkText.isEnabled = false;
            Toast.makeText(context, "인증되었습니다", Toast.LENGTH_SHORT).show();
            timer.cancel();
            timerText.textSize = 0f;
        }
    }
}
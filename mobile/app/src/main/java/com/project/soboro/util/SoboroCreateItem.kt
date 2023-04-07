package com.project.soboro.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.media.Image
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.setMargins
import com.project.soboro.R
import com.project.soboro.activity.MainActivity

class SoboroCreateItem {
    companion object{

        // 상담 채팅 버블 생성
        fun createLogChatItem(context:Context, isUser: Boolean, chatText:String):LinearLayout{

            val ratio = 3;

            val outerLinear = LinearLayout(context);
            val outerParam = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            );
            outerParam.setMargins(0,0,0,16*ratio)

            outerLinear.layoutParams = outerParam;
            outerLinear.orientation = LinearLayout.HORIZONTAL;

            val userImgView:ImageView = ImageView(context);
            val imgParams = LinearLayout.LayoutParams(
                48*ratio, 48*ratio
            );

            if(isUser) {
                userImgView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_user))
                imgParams.setMargins(16*ratio,0,0,0); // Boolean
                imgParams.gravity = Gravity.RIGHT; // Boolean;
            } else {
                userImgView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_consultant))
                imgParams.setMargins(0,0,16*ratio,0); // Boolean
                imgParams.gravity = Gravity.LEFT; // Boolean;
            };

            userImgView.layoutParams = imgParams;

            val chatLinearLayout = LinearLayout(context);
            val chatLinParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, /// 혹시 이상하면 이부분 점검
                ViewGroup.LayoutParams.WRAP_CONTENT,
            );
            chatLinearLayout.layoutParams = chatLinParams;
            chatLinearLayout.orientation = LinearLayout.HORIZONTAL; ////////////

            if(isUser){
                chatLinearLayout.gravity = Gravity.RIGHT;
            }else {
                chatLinearLayout.gravity = Gravity.LEFT;
            }

            val chatTextView:TextView = TextView(context);
            val chatParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );

            chatTextView.layoutParams = chatParams;
            chatTextView.text = chatText;
            chatTextView.textSize = 16f;

            if(isUser){
                chatTextView.background = ContextCompat.getDrawable(context, R.drawable.chat_bubble_right); // boolean 처리 필요 부분
                chatTextView.setPadding(16*ratio,8*ratio,16*ratio,8*ratio);
            }else {
                chatTextView.background = ContextCompat.getDrawable(context, R.drawable.chat_bubble_left); // boolean 처리 필요 부분
                chatTextView.setPadding(16*ratio,8*ratio,16*ratio,8*ratio);
            }

            chatLinearLayout.addView(chatTextView);

            if(isUser){
                outerLinear.addView(chatLinearLayout);
                chatLinearLayout.addView(userImgView);

            }else {
                outerLinear.addView(userImgView) // Boolean;
                outerLinear.addView(chatLinearLayout);
            }

            return outerLinear;
        }

        // 채팅 버블 생성
        fun createChatItem(context:Context,editText: EditText, chatHolder: LinearLayout, isUser:Boolean){
            val newTextView = TextView(context)
            val param = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1.0f
            );
            param.setMargins(16);

            if(isUser) param.gravity = Gravity.RIGHT;
            else param.gravity = Gravity.LEFT;

            newTextView.layoutParams = param

            newTextView.setPadding(32, 16, 32, 16);

            // 텍스트와 사이즈
            newTextView.textSize = 16f;
            newTextView.text = editText?.text.toString();

            if(isUser){
                // newTextView.setTextColor(ContextCompat.getColor(context, R.color.main_navy_700_p));
                newTextView.background =
                    ContextCompat.getDrawable(context, R.drawable.chat_bubble_right);
            }else {
                newTextView.setTextColor(ContextCompat.getColor(context, R.color.white));
                newTextView.background =
                    ContextCompat.getDrawable(context, R.drawable.chat_bubble_left);
            }
            chatHolder.addView(newTextView);
            editText.text.clear();
            editText.clearFocus();
        }

        // 상담 내역 로그 렌더링
        fun createHistoryItem(context:Context, addressString:String, dateString:String, iconImg:Drawable? ):LinearLayout{
            val ratio = 3;

            val topLayout:LinearLayout = LinearLayout(context);
            val paramTop = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                96*ratio
            );
            paramTop.setMargins(8*ratio,8*ratio,8*ratio,8*ratio);

            topLayout.layoutParams = paramTop;
            topLayout.orientation = LinearLayout.HORIZONTAL;
            topLayout.background = ContextCompat.getDrawable(context, R.drawable.item_history);
            topLayout.elevation = 6f*ratio;


            val imgView = ImageView(context);
            val paramImg = LinearLayout.LayoutParams(
                64*ratio,
                64*ratio
            );
            paramImg.setMargins(16*ratio, 0, 16*ratio, 0);
            paramImg.gravity = Gravity.CENTER;

            imgView.layoutParams = paramImg;
            imgView.setImageDrawable(iconImg); // temp

            val rightSection:LinearLayout = LinearLayout(context);
            val paramRight = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            );
            paramRight.setMargins(15);

            rightSection.layoutParams = paramRight;
            rightSection.orientation = LinearLayout.VERTICAL;

            //////

            // top text >> 주소지
            val topTextLinear:LinearLayout = LinearLayout(context);
            val paramTopTextLin = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                1.0f
            );
            topTextLinear.layoutParams = paramTopTextLin;

            // top text View
            val topTextView = TextView(context);
            val paramTopTextView = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
            paramTopTextView.gravity = Gravity.BOTTOM;
            topTextView.layoutParams = paramTopTextView;

            topTextView.setPadding(0,0,0,8);
            topTextView.textSize = 16f;
            topTextView.text = addressString;

            topTextLinear.addView(topTextView);

            // bottom text >> 일자
            val bottomTextLinear:LinearLayout = LinearLayout(context);
            val paramBottomTextLin = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                1.0f
            );
            bottomTextLinear.layoutParams = paramBottomTextLin;


            // bottom text View
            val bottomTextView = TextView(context);
            val paramBottomTextView = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
            paramBottomTextView.gravity = Gravity.TOP;
            bottomTextView.layoutParams = paramBottomTextView;

            bottomTextView.setPadding(0,8,0,0);
            bottomTextView.textSize = 16f;
            bottomTextView.text = dateString;

            bottomTextLinear.addView(bottomTextView);
            // 합치기
            rightSection.addView(topTextLinear);
            rightSection.addView(bottomTextLinear);

            // 이미지뷰와 Linear Layout 추가
            topLayout.addView(imgView);
            topLayout.addView(rightSection);

            return topLayout;
        }

        fun getNoHistoryItem(context:Context):LinearLayout{

            val linearLayout = LinearLayout(context);
            val linParam = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            );

            linearLayout.layoutParams = linParam;
            linearLayout.orientation = LinearLayout.VERTICAL;

            val imgView = ImageView(context);
            val paramImg = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );

            imgView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_exclamation_mark));

            paramImg.gravity = Gravity.CENTER;
            paramImg.setMargins(16, 16, 16, 16);
            imgView.layoutParams = paramImg;

            val textView = TextView(context);
            val textParam = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER;
            textView.textSize = 20f;
            textParam.setMargins(16, 16,16,16);
            textView.layoutParams = textParam;
            textView.text = "사용 내역이 없습니다";

            linearLayout.addView(imgView);
            linearLayout.addView(textView);

            return linearLayout;
        }


        fun createOtherChatItem(context:Context, text:String, chatHolder: LinearLayout, isUser:Boolean){
            val newTextView = TextView(context)
            val param = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1.0f
            );
            param.setMargins(16);

            if(isUser) param.gravity = Gravity.RIGHT;
            else param.gravity = Gravity.LEFT;

            newTextView.layoutParams = param

            newTextView.setPadding(32, 16, 32, 16);

            // 텍스트와 사이즈
            newTextView.textSize = 16f;
            newTextView.text = text;

            if(isUser){
                // newTextView.setTextColor(ContextCompat.getColor(context, R.color.main_navy_700_p));
                newTextView.background =
                    ContextCompat.getDrawable(context, R.drawable.chat_bubble_right);
            }else {
                newTextView.setTextColor(ContextCompat.getColor(context, R.color.white));
                newTextView.background =
                    ContextCompat.getDrawable(context, R.drawable.chat_bubble_left);
            }
            chatHolder.addView(newTextView);

        }

    }
}
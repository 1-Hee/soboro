package com.catchtwobirds.soboro.user.service;

import java.util.Optional;
import java.util.Random;

import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.text.html.Option;

import com.catchtwobirds.soboro.common.error.errorcode.UserErrorCode;
import com.catchtwobirds.soboro.common.error.exception.RestApiException;
import com.catchtwobirds.soboro.user.entity.User;
import com.catchtwobirds.soboro.user.repository.UserRepository;
import com.catchtwobirds.soboro.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;
    private final RedisUtil redisUtil;
    private final UserRepository userRepository;
//    public static final String ePw = createKey();

    private MimeMessage createMessage(String to, String ePw)throws Exception{
        System.out.println("보내는 대상 : "+ to);
        System.out.println("인증 번호 : "+ePw);
        MimeMessage  message = emailSender.createMimeMessage();

        message.addRecipients(RecipientType.TO, to);//보내는 대상
        message.setSubject("[소보로] 이메일 인증 메일 입니다.");//제목

        String msgg="";
        msgg+= "<div style='margin:20px;'>";
        msgg+= "<h1> 안녕하세요 소보로 입니다.</h1>";
        msgg+= "<br>";
        msgg+= "<p>아래 인증 코드를 복사해 인증코드 칸에 입력해주세요<p>";
        msgg+= "<br>";
        msgg+= "<p>감사합니다.<p>";
        msgg+= "<br>";
        msgg+= "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgg+= "<h3 style='color:blue;'>인증 코드입니다.</h3>";
        msgg+= "<div style='font-size:130%'>";
        msgg+= "CODE : <strong>";
        msgg+= ePw+"</strong><div><br/> ";
        msgg+= "</div>";
        message.setText(msgg, "utf-8", "html");//내용
        message.setFrom(new InternetAddress("soboroservice@gmail.com","소보로"));//보내는 사람

        return message;
    }

    private MimeMessage createMessageUserId(String to, String id, String ePw)throws Exception{
        System.out.println("보내는 대상 : "+ to);
        System.out.println("인증 번호 : "+ePw);
        MimeMessage  message = emailSender.createMimeMessage();

        message.addRecipients(RecipientType.TO, to);//보내는 대상
        message.setSubject("[소보로] 아이디 찾기 메일 입니다.");//제목

        String msgg="";
        msgg+= "<div style='margin:20px;'>";
        msgg+= "<h1> 안녕하세요 소보로 입니다.</h1>";
        msgg+= "<br>";
        msgg+= "<p> 아이디 찾기 메일 입니다. <p>";
        msgg+= "<br>";
        msgg+= "<p>감사합니다.<p>";
        msgg+= "<br>";
        msgg+= "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgg+= "<h3 style='color:blue;'>회원님의 아이디는 다음과 같습니다. </h3>";
        msgg+= "<div style='font-size:130%'>";
        msgg+= "ID  : <strong>";
        msgg+= id +"</strong><div><br/> ";
        msgg+= "</div>";
        message.setText(msgg, "utf-8", "html");//내용
        message.setFrom(new InternetAddress("soboroservice@gmail.com","소보로"));//보내는 사람

        return message;
    }

    public static String createKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();
        rnd.setSeed(System.currentTimeMillis());

        for (int i = 0; i < 8; i++) { // 인증코드 8자리
            int index = rnd.nextInt(3); // 0~2 까지 랜덤

            switch (index) {
                case 0:
                    key.append((char) ((int) (rnd.nextInt(26)) + 97));
                    //  a~z  (ex. 1+97=98 => (char)98 = 'b')
                    break;
                case 1:
                    key.append((char) ((int) (rnd.nextInt(26)) + 65));
                    //  A~Z
                    break;
                case 2:
                    key.append((rnd.nextInt(10)));
                    // 0~9
                    break;
            }
        }
        return key.toString();
    }

    @Transactional
    public String sendSimpleMessage(String to)throws Exception {
        String ePw = createKey();
        MimeMessage message = createMessage(to, ePw);
        try {
            emailSender.send(message);
            redisUtil.setDataExpire(to, ePw, 1000L * 60L * 5L);
        } catch (MailException es){
            es.printStackTrace();
            throw new IllegalArgumentException();
        } catch (RuntimeException e) {
            throw new RestApiException(UserErrorCode.USER_500);
        }
        return ePw;
    }

    public void checkCode(String email, String code) throws Exception{
        String getValue = redisUtil.getData(email);
        if (getValue == null) {
            throw new RestApiException(UserErrorCode.USER_411);
        } else if (!getValue.equals(code)) {
            throw new RestApiException(UserErrorCode.USER_411);
        }
    }

    public void checkCodeFindPass(String id, String email, String code) throws Exception{
        Optional<User> result = userRepository.findByUserEmailAndUserId(id, email);
        if (result.isEmpty()) {
            throw new RestApiException(UserErrorCode.USER_411);
        }
        String getValue = redisUtil.getData(email);
        if (getValue == null) {
            throw new RestApiException(UserErrorCode.USER_411);
        } else if (!getValue.equals(code)) {
            throw new RestApiException(UserErrorCode.USER_411);
        }
    }

    @Transactional
    public void sendUserId(String to)throws Exception {
        String ePw = createKey();
        Optional<User> result = userRepository.findByUserEmail(to);
        log.info("result : {}", result);
        if(result.isPresent()) {
            MimeMessage message = createMessageUserId(to, result.get().getUserId(), ePw);
            try {
                emailSender.send(message);
            } catch (MailException es){
                es.printStackTrace();
                throw new IllegalArgumentException();
            }
        }

    }

}
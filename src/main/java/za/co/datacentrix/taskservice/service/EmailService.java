package za.co.datacentrix.taskservice.service;


import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import za.co.datacentrix.taskservice.payload.request.MailBody;


@Service
@RequiredArgsConstructor
public class EmailService {


    private final JavaMailSender javaMailSender;

    public void sendSimpleMessage(MailBody mailBody){

        SimpleMailMessage message =  new SimpleMailMessage();
        message.setTo(mailBody.to());
        message.setFrom("magodiedwin@gmail.com");
//        message.setFrom("edwmagodi@brainstake.tech");
        message.setSubject(mailBody.subject());
        message.setText(mailBody.text());

        javaMailSender.send(message);

    }

}

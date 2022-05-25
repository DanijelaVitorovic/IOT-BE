package com.dex.coreserver.util;
import com.dex.coreserver.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

@Service
public class EmailUtils {

    @Autowired
    private ApplicationUtils applicationUtils;

    private Properties getEmailProperties(){
        Properties emailProps = new Properties();
        emailProps.put("mail.smtp.auth", applicationUtils.getApplicationPropsByKey("email.smtp.auth"));
        emailProps.put("mail.smtp.starttls.enable", applicationUtils.getApplicationPropsByKey("email.smtp.starttls.enable"));
        emailProps.put("mail.smtp.ssl.enable", applicationUtils.getApplicationPropsByKey("email.smtp.ssl.enable"));
        emailProps.put("mail.smtp.host", applicationUtils.getApplicationPropsByKey("email.smtp.host"));
        emailProps.put("mail.smtp.port", applicationUtils.getApplicationPropsByKey("email.smtp.port"));
        return emailProps;
    }

    private Authenticator emailAuthentication(){
        return new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(applicationUtils.getApplicationPropsByKey("email.address"),
                        applicationUtils.getApplicationPropsByKey("email.password"));
            }
        };
    }

    @Async
    public void sendEmail(String recipientEmails, String mailSubject, String messageText){
        try {
            Session session = Session.getInstance(getEmailProperties(), emailAuthentication());
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(applicationUtils.getApplicationPropsByKey("email.address")));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmails, true));
            String message = "<p>"+messageText+"</p>";
            msg.setSubject(mailSubject);
            msg.setContent(message, "text/html; charset=UTF-8");
            Transport.send(msg);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Async
    public void sendEmail(String recipientEmail, String mailSubject, String messageText, String fileName){
        try {
            Session session = Session.getInstance(getEmailProperties(), emailAuthentication());
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(applicationUtils.getApplicationPropsByKey("email.address")));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail, false));
            String message = "<p>"+messageText+"</p>";
            msg.setSubject(mailSubject);

            MimeBodyPart messageBodyPart = new MimeBodyPart();

            Multipart multipart = new MimeMultipart();
            MimeBodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setText(message);

            Path path = Paths.get(applicationUtils.getApplicationPropsByKey("file.path")+fileName);
            try {
                Files.write(path, new byte[1024]);
            }catch (IOException e){
                e.printStackTrace();
            }

            DataSource source = new FileDataSource(path.toString());
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(fileName);
            multipart.addBodyPart(textBodyPart);
            multipart.addBodyPart(messageBodyPart);

            msg.setContent(multipart);
            Transport.send(msg);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Async
    public void sendEmail(String recipientEmail, String mailSubject, String messageText, byte[] fileInByteArray, String fileName){
        try {
            Session session = Session.getInstance(getEmailProperties(), emailAuthentication());
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(applicationUtils.getApplicationPropsByKey("email.address")));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail, false));
            String message = "<p>"+messageText+"</p>";
            msg.setSubject(mailSubject);

            MimeBodyPart messageBodyPart = new MimeBodyPart();

            Multipart multipart = new MimeMultipart();
            MimeBodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setText(message);

            Path path = Paths.get(applicationUtils.getFilePath());
            try {
                Files.write(path,fileInByteArray);
            }catch (IOException e){
                e.printStackTrace();
            }

            DataSource source = new FileDataSource(path.toString());
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(fileName);
            multipart.addBodyPart(textBodyPart);
            multipart.addBodyPart(messageBodyPart);

            msg.setContent(multipart);
            Transport.send(msg);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public String extractEmailFromUsers(List<User> users){
        StringBuilder stringBuilder = new StringBuilder();
        if(!users.isEmpty()) {
            User lastUserInList = users.get( users.size() - 1 );
            for (User user : users
                    ) {
                String userEmail = user.equals(lastUserInList) ? user.getEmail() : user.getEmail() + ",";
                stringBuilder.append( userEmail );
            }
        }
        return stringBuilder.toString();
    }

}

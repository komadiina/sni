package org.unibl.etf.sni.mail;

import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import java.io.ByteArrayOutputStream;
import java.util.Properties;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class GmailHTMLSender {
    public static Message sendHtmlEmail(Gmail service, String userId, String recipientEmail,
                                        String fromEmail, String subject, String htmlBody) throws MessagingException {
        // Setup Mail Session
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        // Create a MimeMessage using the session created above
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(fromEmail));
        email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(recipientEmail));
        email.setSubject(subject);
        email.setContent(htmlBody, "text/html; charset=utf-8");

        // Encode and wrap the MimeMessage into a Gmail Message
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            email.writeTo(buffer);
            byte[] bytes = buffer.toByteArray();
            String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
            Message message = new Message();
            message.setRaw(encodedEmail);

            // Send the email
            message = service.users().messages().send(userId, message).execute();

            System.out.println("HTML email sent successfully.");
            return message;
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }
}

package org.unibl.etf.sni.mail;

import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GmailIntegration {

    private final Gmail service;

    public GmailIntegration(Gmail service) {
        this.service = service;
    }

    // Method to create and send an email directly without using JavaMail api
    public Message sendEmail(String userId, String to, String from, String subject, String bodyText) throws IOException {
        String emailBody = createRawEmailString(to, from, subject, bodyText);
        byte[] emailBytes = emailBody.getBytes("UTF-8");
        String encodedEmail = Base64.encodeBase64URLSafeString(emailBytes);

        Message message = new Message();
        message.setRaw(encodedEmail);
        message = service.users().messages().send(userId, message).execute();

        System.out.println("Sent email with ID: " + message.getId());
        return message;
    }

    // Helper method to create a raw MIME string for an email
    private String createRawEmailString(String to, String from, String subject, String bodyText) {
        List<String> headers = new ArrayList<>();
        headers.add("To: " + to);
        headers.add("From: " + from);
        headers.add("Subject: " + subject);
        headers.add("Content-Type: text/plain; charset=\"UTF-8\"");
        headers.add("MIME-Version: 1.0");

        String headerString = String.join("\r\n", headers);
        return headerString + "\r\n\r\n" + bodyText;
    }
}

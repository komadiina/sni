package org.unibl.etf.sni.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.gmail.Gmail;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.unibl.etf.sni.config.GmailConfig;
import org.unibl.etf.sni.db.OtpRepository;
import org.unibl.etf.sni.mail.GmailHTMLSender;
import org.unibl.etf.sni.mail.GmailIntegration;
import org.unibl.etf.sni.model.Otp;
import org.unibl.etf.sni.model.User;

import java.time.LocalDateTime;
import java.util.*;

import static org.unibl.etf.sni.mail.GmailAPIClient.getCredentials;

@Service
@RequiredArgsConstructor
@NoArgsConstructor
public class OtpService {
    @Autowired
    private OtpRepository otpRepository;

    public Otp generateOtp(String username, String password) {
        return new Otp(
                String.format("%06d", (int) (100_000 + new Random().nextInt(899_999))),
                password,
                LocalDateTime.now().plusMinutes(5),
                false,
                username
        );
    }

    public Otp storeOtp(Otp otp) {
        // work-around, couldn't figure out how to identify correct otp (on multiple) while storing in db
        if (otpRepository.findByUsername(otp.getUsername()).isPresent()) {
            updateOtp(otp.getUsername(), otp);
            return otp;
        }

        return otpRepository.save(otp);
    }

    public Otp readOtp(String username) {
        return otpRepository.findByUsername(username).orElse(null);
    }

    public Otp updateOtp(String username, Otp newValue) {
        Optional<Otp> result = otpRepository.findByUsername(username);
        if (result.isPresent()) {
            Otp otp = result.get();
            otp.setOtpValue(newValue.getOtpValue());
            otp.setExpiryTime(newValue.getExpiryTime());
            otp.setUsed(newValue.isUsed());
            return otpRepository.save(otp);
        } else {
            throw new IllegalStateException("OTP not found: " + username);
        }
    }

    public String deleteOtp(String username) {
        Optional<Otp> result = otpRepository.findByUsername(username);
        if (result.isPresent()) {
            otpRepository.delete(result.get());
            return result.get().getPassword();
        } else {
            throw new IllegalStateException("OTP not found: " + username);
        }
    }

    public void sendOtp(User user, String otp, HttpServletRequest request) {
        // da ne moram gledati na telefon svaki put
        System.out.println("Sending OTP: " + otp);

        String htmlBody = String.format(
                "<html>" +
                    "<body>" +
                        "<p>Dear <b>%s</b>,</p><br/>" +
                        "We have received a request to access your account from a new location.<br/>" +
                        "The following IP Address is making the request: %s.<br/><br/>" +
                        "To complete this process, please enter the following code into the authorization box: <br/><br/>" +
                        "<b>%s</b><br/>" +
                        "If you did not make this request, please ignore this email. Thank you for your cooperation.<br/><br/>" +
                        "<i>Please do not reply to this message. Replies to this message are routed to an unmonitored mailbox. <br/>" +
                        "If you have any questions, please contact support.</i>" +
                    "</body>" +
                "</html>",
                    user.getUsername(),
                    request.getRemoteAddr(),
                otp);

        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Gmail service = new Gmail.Builder(HTTP_TRANSPORT, GmailConfig.JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(GmailConfig.APPLICATION_NAME)
                    .build();

            GmailHTMLSender.sendHtmlEmail(
                    service,
                    "me",
                    user.getEmail() ,
                    "bigblue9992@gmail.com",
                    "New login request - your authentication code",
                    htmlBody);
        } catch (Exception ex) {
            System.err.println("Unable to send email to " + user.getEmail() + ", reason: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public boolean validateOtp(String username, String otp) {
        Optional<Otp> result = otpRepository.findByUsername(username);
        if (result.isPresent()) {
            Otp resultOtp = result.get();
            return !resultOtp.isUsed() && resultOtp.getOtpValue().equals(otp) && resultOtp.getExpiryTime().isAfter(LocalDateTime.now());
        } else {
            throw new IllegalStateException("OTP not found: " + username);
        }
    }
}

package org.unibl.etf.sni.security;

import org.unibl.etf.sni.service.OtpService;

public class OtpDbManagerThread extends Thread {
    private final String username;
    private final OtpService otpService;

    public OtpDbManagerThread(String username, OtpService otpService) {
        this.username = username;
        this.otpService = otpService;
    }

    @Override
    public void run() {
        try {
            // 5 minutes
            Thread.sleep(5 * 60 * 1000);

            // remove otp from database
            otpService.deleteOtp(username);

            System.out.println("Deleted OTP for: " + username);
        } catch (InterruptedException ex) {
            System.err.println("Unable to remove OTP for: " + username);
        } catch (IllegalStateException ex) {
            System.err.println("Unable to remove OTP for: " + username + ", OTP does not exist.");
        } catch (Exception ex) {
            System.err.println("Unknown error occurred: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}

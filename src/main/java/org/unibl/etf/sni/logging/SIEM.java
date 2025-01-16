package org.unibl.etf.sni.logging;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.nio.file.Paths;

@Component
public class SIEM {
    private static final Logger logger = Logger.getLogger(SIEM.class.getName());

    public SIEM() {
        try {
            java.nio.file.Files.createDirectories(Paths.get("logs"));
            String filename = String.format("logs/siem-%s.log", LocalDateTime.now().toString().replace(":", "-"));

            FileHandler fileHandler = new FileHandler(filename, true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logRequest(String ipAddress, String userAgent, String endpoint, String reason) {
        String logMessage = String.format("New request:\n" +
                        "IP Address: %s\nUser Agent: %s\nEndpoint: %s\nReason: %s\nTimestamp: %s\n",
                ipAddress, userAgent, endpoint, reason, LocalDateTime.now());

        logger.info(logMessage);
    }

    public void logMaliciousRequest(String ipAddress, String userAgent, String endpoint, String reason) {
        String logMessage = String.format("New request:\n" +
                        "IP Address: %s\nUser Agent: %s\nEndpoint: %s\nReason: %s\nTimestamp: %s\n",
                ipAddress, userAgent, endpoint, reason, LocalDateTime.now());

        logger.warning(logMessage);
    }
}

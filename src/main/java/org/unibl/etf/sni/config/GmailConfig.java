package org.unibl.etf.sni.config;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.GmailScopes;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

@Configuration
public class GmailConfig {
    public static final String APPLICATION_NAME = "SNI";
    public static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    public static final String TOKENS_DIRECTORY_PATH = "tokens";
    public static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_LABELS);
    public static final String CREDENTIALS_FILE_PATH = "/credentials.json";
}

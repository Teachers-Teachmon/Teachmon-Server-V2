package solvit.teachmon.global.configuration;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import solvit.teachmon.global.properties.GoogleSpreadSheetProperties;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Configuration
@ConditionalOnProperty(name = "google-spreadsheet.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class GoogleSpreadSheetConfiguration {
    private final GoogleSpreadSheetProperties googleSpreadSheetProperties;
    private final ResourceLoader resourceLoader;

    @Bean
    public Credential googleCredential() throws IOException {
        String location = googleSpreadSheetProperties.getCredentialPath();
        Resource resource = resourceLoader.getResource(location);

        if (!resource.exists()) {
            throw new IOException("Google 서비스 계정 키 설정을 읽을 수 없습니다. path=" + location);
        }

        try (InputStream is = resource.getInputStream()) {
            return GoogleCredential.fromStream(is)
                    .createScoped(Collections.singletonList(SheetsScopes.SPREADSHEETS));
        }
    }


    @Bean
    public Sheets googleSheetsService(Credential credential) throws GeneralSecurityException, IOException {
        JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, credential)
                .setApplicationName(googleSpreadSheetProperties.getApplicationName())
                .build();
    }
}

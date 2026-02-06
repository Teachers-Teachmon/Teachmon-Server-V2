package solvit.teachmon.global.configuration;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
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
@ConditionalOnProperty(
        name = "google-spreadsheet.enabled",
        havingValue = "true",
        matchIfMissing = false
)
@RequiredArgsConstructor
public class GoogleSpreadSheetConfiguration {

    private final GoogleSpreadSheetProperties googleSpreadSheetProperties;
    private final ResourceLoader resourceLoader;

    @Bean
    public GoogleCredentials googleCredentials() throws IOException {
        String location = googleSpreadSheetProperties.getCredentialPath();
        Resource resource = resourceLoader.getResource(location);

        if (!resource.exists()) {
            throw new IOException(
                    "Google 서비스 계정 키 파일을 찾을 수 없습니다. location=" + location
            );
        }

        try (InputStream is = resource.getInputStream()) {
            return ServiceAccountCredentials.fromStream(is)
                    .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
        }
    }

    @Bean
    public Sheets googleSheetsService(GoogleCredentials credentials)
            throws GeneralSecurityException, IOException {

        return new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials)
        )
                .setApplicationName(googleSpreadSheetProperties.getApplicationName())
                .build();
    }
}

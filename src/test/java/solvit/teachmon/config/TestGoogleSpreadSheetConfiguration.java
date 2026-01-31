package solvit.teachmon.config;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.sheets.v4.Sheets;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestGoogleSpreadSheetConfiguration {

    @Bean
    @Primary
    public Credential googleCredential() {
        return mock(Credential.class);
    }

    @Bean
    @Primary
    public Sheets googleSheetsService() {
        return mock(Sheets.class);
    }
}

package solvit.teachmon.global.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "google-spreadsheet")
public class GoogleSpreadSheetProperties {
    private final String credentials;
    private final String applicationName;
    private final String credentialsScope;
    private final String page;
}

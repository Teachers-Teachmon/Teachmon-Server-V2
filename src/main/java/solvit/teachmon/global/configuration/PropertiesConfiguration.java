package solvit.teachmon.global.configuration;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationPropertiesScan(basePackages = "solvit.teachmon.global.properties")
public class PropertiesConfiguration {
}
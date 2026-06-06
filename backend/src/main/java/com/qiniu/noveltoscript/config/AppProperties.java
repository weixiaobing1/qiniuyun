package com.qiniu.noveltoscript.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "novel-to-script")
public class AppProperties {

    private Ai ai = new Ai();
    private Upload upload = new Upload();

    public Ai getAi() { return ai; }
    public void setAi(Ai ai) { this.ai = ai; }
    public Upload getUpload() { return upload; }
    public void setUpload(Upload upload) { this.upload = upload; }

    public static class Ai {
        private int timeoutSeconds = 120;
        private int maxRetries = 2;
        private long cacheTtlSeconds = 86400L;

        public int getTimeoutSeconds() { return timeoutSeconds; }
        public void setTimeoutSeconds(int timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; }
        public int getMaxRetries() { return maxRetries; }
        public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }
        public long getCacheTtlSeconds() { return cacheTtlSeconds; }
        public void setCacheTtlSeconds(long cacheTtlSeconds) { this.cacheTtlSeconds = cacheTtlSeconds; }
    }

    public static class Upload {
        private String allowedExtensions = "txt,md,html,htm";
        private int maxChars = 500_000;

        public String getAllowedExtensions() { return allowedExtensions; }
        public void setAllowedExtensions(String allowedExtensions) { this.allowedExtensions = allowedExtensions; }
        public int getMaxChars() { return maxChars; }
        public void setMaxChars(int maxChars) { this.maxChars = maxChars; }
    }
}

package com.accesshr.emsbackend.Service;

import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SecretManagerCredentialProvider {

    @Value("${gcp.secret.db-username}")
    private String dbUsernameSecretId;

    @Value("${gcp.secret.db-password}")
    private String dbPasswordSecretId;

    @Value("${gcp.project-id}")
    private String projectId;

    private String dbUsername;
    private String dbPassword;

    @PostConstruct
    public void init() {
        try (SecretManagerServiceClient client = SecretManagerServiceClient.create()) {
            dbUsername = accessSecret(client, dbUsernameSecretId);
            dbPassword = accessSecret(client, dbPasswordSecretId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access secrets from GCP Secret Manager", e);
        }
    }

    private String accessSecret(SecretManagerServiceClient client, String secretId) {
        SecretVersionName secretVersionName = SecretVersionName.of(projectId, secretId, "latest");
        AccessSecretVersionResponse response = client.accessSecretVersion(secretVersionName);
        return response.getPayload().getData().toStringUtf8();
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }
}

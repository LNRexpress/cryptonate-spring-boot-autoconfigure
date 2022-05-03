package com.nightsky.cryptonate.spring.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author Chris
 */
@ConfigurationProperties(prefix = "cryptonate")
public class CryptonateProperties {

    private String encryptionKeyName;

    private String rngAdditionalData;

    public CryptonateProperties() {
    }

    /**
     * @return the encryptionKeyName
     */
    public String getEncryptionKeyName() {
        return encryptionKeyName;
    }

    /**
     * @param encryptionKeyName the encryptionKeyName to set
     */
    public void setEncryptionKeyName(String encryptionKeyName) {
        this.encryptionKeyName = encryptionKeyName;
    }

    /**
     * @return the rngAdditionalData
     */
    public String getRngAdditionalData() {
        return rngAdditionalData;
    }

    /**
     * @param rngAdditionalData the rngAdditionalData to set
     */
    public void setRngAdditionalData(String rngAdditionalData) {
        this.rngAdditionalData = rngAdditionalData;
    }

}

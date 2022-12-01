package com.nightsky.cryptonate.spring.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author Chris
 */
@ConfigurationProperties(prefix = "crypto")
public class CryptoProperties {

    private CryptoKeyDictionary keyDictionary;

    public CryptoProperties() {
    }

    /**
     * @return the keyDictionary
     */
    public CryptoKeyDictionary getKeyDictionary() {
        return keyDictionary;
    }

    /**
     * @param keyDictionary the keyDictionary to set
     */
    public void setKeyDictionary(CryptoKeyDictionary keyDictionary) {
        this.keyDictionary = keyDictionary;
    }

}

package com.nightsky.cryptonate.spring.boot.autoconfigure;

import java.util.Map;

/**
 *
 * @author Chris
 */
public class CryptoKeyDictionary {

    private Map<String, Integer> keyCodes;

    private Map<String, String> keyNames;

    public CryptoKeyDictionary() {
    }

    /**
     * @return the keyCodes
     */
    public Map<String, Integer> getKeyCodes() {
        return keyCodes;
    }

    /**
     * @param keyCodes the keyCodes to set
     */
    public void setKeyCodes(Map<String, Integer> keyCodes) {
        this.keyCodes = keyCodes;
    }

    /**
     * @return the keyNames
     */
    public Map<String, String> getKeyNames() {
        return keyNames;
    }

    /**
     * @param keyNames the keyNames to set
     */
    public void setKeyNames(Map<String, String> keyNames) {
        this.keyNames = keyNames;
    }

}

package com.nightsky.cryptonate.spring.boot.autoconfigure;

import com.nightsky.cryptonate.event.listener.CryptoEventListener;
import com.nightsky.keycache.VersionedSecretKeyCache;
import com.nightsky.keycache.spring.boot.autoconfigure.KeyCacheAutoConfiguration;
import com.nightsky.keycache.spring.boot.autoconfigure.KeyCacheProperties;
import java.nio.charset.StandardCharsets;
import java.rmi.dgc.VMID;
import java.security.SecureRandom;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.fips.FipsDRBG;
import org.bouncycastle.crypto.fips.FipsSecureRandom;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Chris
 */
@Configuration
@ConditionalOnClass({ CryptoEventListener.class })
@ConditionalOnBean({ VersionedSecretKeyCache.class })
@AutoConfigureAfter({ HibernateJpaAutoConfiguration.class, KeyCacheAutoConfiguration.class })
@EnableConfigurationProperties(CryptonateProperties.class)
public class CryptonateAutoConfiguration {

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "cryptonate", name = "rng-additional-data")
    public FipsSecureRandom cryptonateFipsSecureRandom(
        @Value("${cryptonate.rng-additional-data}") String additionalData)
    {
        SecureRandom entropySource = new SecureRandom();
        byte[] personalizationString = new VMID().toString().getBytes(StandardCharsets.UTF_8);

        FipsDRBG.Builder drgbBuilder = FipsDRBG.SHA512_HMAC
            .fromEntropySource(entropySource, true)
            .setEntropyBitsRequired(256)
            .setSecurityStrength(256)
            .setPersonalizationString(personalizationString);

        // TODO: use a TSA to get a timestamp for the nonce. See section 8.6.7 of NIST.SP.800-90Ar1.pdf

        FipsSecureRandom fipsSecureRandom = drgbBuilder.build(
            entropySource.generateSeed((256 / (2 * 8))),
            true,
            additionalData.getBytes(StandardCharsets.UTF_8));

        CryptoServicesRegistrar.setSecureRandom(fipsSecureRandom);
        return fipsSecureRandom;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({ KeyCacheProperties.class, FipsSecureRandom.class })
    public CryptoEventListener cryptoEventListener(
        @Autowired CryptonateProperties properties,
        @Autowired KeyCacheProperties keyCacheProperties,
        @Autowired VersionedSecretKeyCache keyCache,
        @Autowired FipsSecureRandom cryptonateFipsSecureRandom)
    {
        CryptoEventListener listener = CryptoEventListener.builder()
            .withEncryptionKeyName(properties.getEncryptionKeyName())
            .withKeyCodes(keyCacheProperties.getKeyDictionary().getKeyCodes())
            .withKeyNames(keyCacheProperties.getKeyDictionary().getKeyNames())
            .withRNG(cryptonateFipsSecureRandom)
            .withVersionedSecretKeyCache(keyCache)
                .build();

        registerLisener(listener);

        return listener;
    }

    private void registerLisener(CryptoEventListener cryptoEventListener) {
        SessionFactoryImplementor sessionFactory = entityManagerFactory.unwrap(SessionFactoryImplementor.class);
        EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);

        registry.getEventListenerGroup(EventType.PRE_INSERT).appendListener(cryptoEventListener);
        registry.getEventListenerGroup(EventType.PRE_UPDATE).appendListener(cryptoEventListener);
        registry.getEventListenerGroup(EventType.PRE_LOAD).appendListener(cryptoEventListener);
    }

}

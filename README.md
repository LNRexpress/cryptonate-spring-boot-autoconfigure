# cryptonate-spring-boot-autoconfigure

## Description

`cryptonate-spring-boot-autoconfigure` provides Spring Boot auto-configuration for the [cryptonate](https://github.com/LNRexpress/cryptonate) `CryptoEventListener` class.

## Requirements

* Java 8 or higher
* Apache Maven 3.6 or higher
* Spring Boot 2.6.6 or higher
* [keycache](https://github.com/LNRexpress/keycache) 1.2.1 or higher
* [cryptonate](https://github.com/LNRexpress/cryptonate) 1.2.0 or higher

## Compilation

```
mvn clean package
```

## Installation

```
mvn clean install
```

## Usage

### Dependency Declaration (Apache Maven)

```
<dependency>
    <groupId>com.nightsky</groupId>
    <artifactId>cryptonate</artifactId>
    <version>1.2.0</version>
</dependency>
<dependency>
    <groupId>com.nightsky</groupId>
    <artifactId>cryptonate-spring-boot-autoconfigure</artifactId>
    <version>1.2.0.1</version>
</dependency>
```

### Configuration

To enable auto-configuration of a [cryptonate](https://github.com/LNRexpress/cryptonate) `CryptoEventListener` instance, you need to add a few entries to your Spring Boot application configuration. The first two entries are the `cryptonate.encryption-key-name` and `cryptonate.rng-additional-data` properties.

**encryption-key-name**
: Defines the name of the symmetric key used to encrypt and decrypt domain model field data.

**rng-additional-data**
: Defines quasi-random data used to initialize a FIPS-compliant random number generator.

One additional entry is needed for cryptonate to work properly. You must define a key dictionary that maps encryption key names to unique, numerical identifiers. This dictionary is defined under the `crypto.key-dictionary` hierarchy, and it should be named `key-codes`.

**key-codes**
: Defines a map from encryption key names (or alias) to their unique, numerical identifier. The IDs are used during decryption to identify which encryption key was used to encrypt the data.

Here is an example of what a complete configuration will look like in your Spring Boot `application.yml`:

```
crypto:
    key-dictionary:
        key-codes:
            sample-key: 1
cryptonate:
    encryption-key-name: sample-key
    rng-additional-data: Cryptonate Spring Boot Example
spring:
    jpa:
        properties:
            hibernate:
                validator:
                    apply_to_ddl: false

```

The `spring.jpa.properties.hibernate.validator.apply_to_ddl` property has been included in the above sample configuration file. This configuration property is required as it disables translation of Hibernate's validation constraints into the database schema. [See here for additional information.](https://github.com/LNRexpress/cryptonate#disable-translation-of-hibernate-validation-constraints-into-the-database-schema)

### Database Writes During Application Start-up

If you do any database writes during application start-up, such as using an `InitializingBean` to seed your database, you must add the following annotation to the appropriate class(es) or bean factory method(s):  `@DependsOn({"cryptoEventListener"})`. *This will ensure that the database writes do not occur until the `CryptoEventListener` has been registered with Hibernate*.

Here is an example of what a database seeding class might look like:

```
@Component
@DependsOn({"cryptoEventListener"})
public class Seed implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        ...
    }

}
```

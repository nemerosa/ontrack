ontrack deployment documentation
================================

## Running in production mode with a local H2 database

In a working directory:

    java -jar ontrack-ui-<version>.jar --spring.profiles.active=prod --logging.path=log/
    
The `database` directory will be created in the same directory.

## Certificate management

Some resources (Jenkins servers, ticketing systems, SCM...) will be configured and accessed in _ontrack_ using the
  `https` protocol, possibly with certificates that are not accepted by default.
  
_ontrack_ does not offer any mechanism to accept such invalid certificates.

The running JDK has to be configured in order to accept those certificates.

### Registering a certificate in the JDK

To register the certificate in your JDK:

    sudo keytool -importcert \
        -keystore ${JAVA_HOME}/jre/lib/security/cacerts -storepass changeit \
        -alias ${CER_ALIAS} \
        -file ${CER_FILE}
        
To display its content:

    keytool -list \
        -keystore ${JAVA_HOME}/jre/lib/security/cacerts \
        -storepass changeit \
        -alias ${CER_ALIAS} \
        -v

See the complete documentation at http://docs.oracle.com/javase/8/docs/technotes/tools/unix/keytool.html.

### Saving the certificate on MacOS

1. Open the Keychain Access utility (Applications -> Utilities)
1. Select your certificate or key from the Certificates or Keys category
1. Choose File -> Export items ...
1. In the Save As field, enter a ".cer" name for the exported item, and click Save.

You will be prompted to enter a new export password for the item.
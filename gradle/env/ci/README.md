CI environment
==============

Generation of the keystore:

```bash
keytool -genkey -alias ontrack -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore keystore.p12 -validity 3650 \
    -dname "CN=Damien Coraboeuf, OU=Ontrack, O=Nemerosa, L=Brussels, ST=Unknown, C=BE" \
    -keypass ontrack \
    -storepass ontrack
```

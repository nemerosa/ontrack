CI environment
==============

Generation of the keystore:

```bash
keytool -genkeypair -alias ontrack -keystore `pwd`/acceptance.jks \
    -dname "CN=Damien Coraboeuf, OU=Ontrack, O=Nemerosa, L=Brussels, ST=Unknown, C=BE" \
    -keypass ontrack \
    -storepass ontrack
```

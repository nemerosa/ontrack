Ontrack DSL in Maven Central
============================

## Environment set-up

1. Install GPG
1. Install Maven (3.2+)
1. [Create and register a key](http://central.sonatype.org/pages/working-with-pgp-signatures.html)
1. Edit the _~/.m2/settings.xml_ file used by Maven and add the following sections to define the GPG passphrase and the access to [OSSRH](http://central.sonatype.org/pages/ossrh-guide.html):

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
   <servers>
      <server>
         <id>ossrh</id>
         <username>ossrh_user</username>
         <password>ossrh_password</password>
      </server>
   </servers>
   <profiles>
      <profile>
         <id>ossrh</id>
         <activation>
            <activeByDefault>true</activeByDefault>
         </activation>
         <properties>
            <gpg.executable>gpg</gpg.executable>
            <gpg.passphrase>gpg_passphrase</gpg.passphrase>
         </properties>
      </profile>
   </profiles>
</settings>
```

## Resources

* See the [Nexus Staging Maven Plugin](http://books.sonatype.com/nexus-book/reference/staging-deployment.html)

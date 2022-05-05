## Testing the plugin locally

Install in the Maven local repository:

```bash
./gradlew clean install
```

Declare the plugin and the dependencies using the locally installed version, not omitting to declare
the Maven local repository:

```groovy
buildscript {
   repositories {
      mavenLocal()
      mavenCentral()
   }
   dependencies {
      classpath 'net.nemerosa.ontrack:ontrack-extension-plugin:<version>'
   }
}

repositories {
   mavenLocal()
   mavenCentral()
}

apply plugin: 'ontrack'

dependencies {
   compile 'net.nemerosa.ontrack:ontrack-extension-support:<version>'
}
```

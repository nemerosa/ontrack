Extension test module
=====================

This module is a valid Ontrack extension, used to test the extension
mechanism itself.

### Local test

While this extension is tested by Ontrack pipeline at build time, it is 
also important to be able to test this extension locally.

#### From the IDE

#### Integration tests

In order to reproduce what happens in the Ontrack pipeline, follow this procedure.

```bash
# Version to test
export ONTRACK_VERSION=3.33.1
# Building and testing the extension locally
cd ontrack-extension-test
./gradlew \
    -PontrackVersion=${ONTRACK_VERSION} \
    clean \
    build
```

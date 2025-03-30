The Docker image is published in Docker Hub with name `nemerosa/ontrack-build:5.0.1`.

To build this image on MacOS (ARM) for `linux/amd64`:

Check Buildx Support:

```bash
docker buildx version
```

You should see the version of Buildx, confirming it is available.

Create a Multi-Platform Builder (if not already present):

```bash
docker buildx create --name ontrack-jenkins --use
docker buildx inspect --bootstrap
```

This creates and switches to a new builder instance.

Build the Image for AMD: Use the --platform flag to specify the target architecture:

```bash
docker buildx build --platform linux/amd64 -t nemerosa/ontrack-build:5.0.1 --load .
```

Push the image to Docker Hub:

```bash
docker image push nemerosa/ontrack-build:5.0.1
```

This image is used in the [`Jenkinsfile`](../Jenkinsfile).

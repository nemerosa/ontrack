## Development

### Testing against a local server

> We cannot run integration tests against Bitbucket Server using
> a Docker image since a license would be required.
> For now, the Bitbucket Server integration tests are disabled unless
> enabled explicitly, typically running against a local installation
> of Bitbucket Server using an evaluation license.

Start a server locally:

```bash
docker run -v bitbucketVolume:/var/atlassian/application-data/bitbucket \
  -d \
  -p 7990:7990 -p 7999:7999 \
  atlassian/bitbucket
```

Go to http://localhost:7990 and follow the setup instructions. In particular, 
you need to request an evaluation license for 90 days using your
Atlassian account.

Create a `TEST` project and a `test` repository with some content.

Create a user called `auto_merge` and create an HTTP access 
token for it. Add this user as a write user in the test repository.

When running the tests, set the following environment variables:

* `ONTRACK_TEST_EXTENSION_STASH_ENABLED`: `true`
* `ONTRACK_TEST_EXTENSION_STASH_AUTOMERGETOKEN`: HTTP access token created for the `auto_merge` user

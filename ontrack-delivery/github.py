import base64
import hashlib
import json
import urllib2


def callGithub(options, url, form, type='application/json'):
    req = urllib2.Request(url)
    req.add_header('Content-Type', type)
    req.add_header('Accept', 'application/vnd.github.manifold-preview')
    base64string = base64.encodestring("%s:%s" % (options.github_user, options.github_token)).replace('\n', '')
    req.add_header("Authorization", "Basic %s" % base64string)
    try:
        if type == 'application/json':
            data = json.dumps(form)
        else:
            data = form
        return urllib2.urlopen(req, data)
    except urllib2.HTTPError as e:
        raise ("GitHub error:\n%s\n" % e)


def uploadGithubArtifact(options, releaseId, name, type, path):
    # Opens the artifact
    data = open(path, 'rb').read()
    # Computes the SHA1 for this file
    h = hashlib.sha1(data).hexdigest()
    # Uploads the SHA1 file
    response = callGithub(
        options,
        "https://uploads.github.com/repos/%s/ontrack/releases/%s/assets?name=%s.sha1" % (
            options.github_user, releaseId, name),
        h,
        "text/plain"
    )
    # Uploads the artifact
    callGithub(
        options,
        "https://uploads.github.com/repos/%s/ontrack/releases/%s/assets?name=%s" % (
            options.github_user, releaseId, name),
        data,
        type
    )


def setReleaseDescription(options, releaseId, description):
    callGithub(
        options,
        "https://api.github.com/repos/%s/releases/%d" % (options.github_repository, releaseId),
        {
            'body': description
        }
    )


def createRelease(options, release):
    response = callGithub(
        options,
        "https://api.github.com/repos/%s/releases" % options.github_repository,
        {
            'tag_name': release,
            'name': release
        }
    )
    return json.load(response)['id']

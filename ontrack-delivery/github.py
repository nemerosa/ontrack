import base64
import json
import urllib2


def call_github(options, url, form, type='application/json'):
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
        raise Exception("GitHub error:\n%s\n" % e)


def upload_github_artifact(options, releaseId, name, type, path):
    # Opens the artifact
    data = open(path, 'rb').read()
    # Uploads the artifact
    call_github(
        options,
        "https://uploads.github.com/repos/%s/releases/%s/assets?name=%s" % (
            options.github_repository, releaseId, name),
        data,
        type
    )


def set_release_description(options, releaseId, description):
    call_github(
        options,
        "https://api.github.com/repos/%s/releases/%d" % (options.github_repository, releaseId),
        {
            'body': description
        }
    )


def create_release(options, commit, release):
    response = call_github(
        options,
        "https://api.github.com/repos/%s/releases" % options.github_repository,
        {
            'target_commitish': commit,
            'tag_name': release,
            'name': release
        }
    )
    return json.load(response)['id']

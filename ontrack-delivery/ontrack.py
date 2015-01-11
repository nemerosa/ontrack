import json
import urllib2


def ontrack_get(url, accept='application/json'):
    req = urllib2.Request(url)
    req.add_header('Accept', accept)
    try:
        return urllib2.urlopen(req)
    except urllib2.HTTPError as e:
        raise Exception("Ontrack error:\n%s\n" % e)


def get_project_id(base_url):
    return json.load(ontrack_get("%s/structure/entity/project/ontrack" % base_url))['id']


def search_build(base_url, project_id, criteria):
    resources = json.load(ontrack_get(
        "%s/structure/project/%s/builds/search?maximumCount=1&%s" % (base_url, project_id, criteria)
    ))['resources']
    if len(resources) > 0:
        return resources[0]['build']['id']
    else:
        raise Exception("No build was returned.")


def get_change_log(base_url, branch, promotion_level):
    # Gets the project ID
    project_id = get_project_id(base_url)
    # Gets the last build on the branch to release
    last_build_id = search_build(base_url, project_id, 'branchName=%s' % branch)
    # Gets the last release
    last_release_id = search_build(base_url, project_id, 'promotionName=%s' % promotion_level)
    # Gets the change log
    change_log = ontrack_get(
        "%s/extension/git/changelog/export?from=%d&to=%d&format=text&grouping=Features%%3Dfeature%%7CEnhancements%%3Denhancement%%7CBugs%%3Dbug&exclude=delivery,design" % (
            base_url, last_release_id, last_build_id),
        'text/plain'
    ).read()
    return change_log


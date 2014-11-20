import urllib2


def ontrackGet(url):
    req = urllib2.Request(url)
    req.add_header('Accept', 'application/json')
    try:
        return urllib2.urlopen(req)
    except urllib2.HTTPError as e:
        raise ("Ontrack error:\n%s\n" % e)


def getBranchId(baseUrl, branch):
    return ontrackGet("%s/structure/entity/branch/ontrack/%s" % (baseUrl, branch))


def getBranchBuildView(baseurl, branchId, promotionLevel):
    return ontrackGet(
        "%s/structure/branches/%d/view/net.nemerosa.ontrack.service.StandardBuildFilterProvider?afterDate=&beforeDate=&count=500&sincePromotionLevel=%s" % (
        baseurl, branchId, promotionLevel))


def getChangeLog(baseurl, branch, promotionLevel):
    # Getting the branch ID
    branchId = getBranchId(baseurl, branch)

    # Gets the branch view
    branchBuildView = getBranchBuildView(baseurl, branchId, promotionLevel)

    # Boundaries
    lastBuildId = branchBuildView.buildViews[0].build.id
    lastReleaseId = branchBuildView.buildViews[branchBuildView.buildViews.size() - 1].build.id

    # Gets the change log
    url = "%s/extension/git/changelog/export?branch=%d&from=%d&to=%d&format=text&grouping=Features%%3Dfeature%%7CEnhancements%%3Denhancement%%7CBugs%%3Dbug&exclude=delivery,design" % (baseurl, branchId, lastReleaseId, lastBuildId)
    req = urllib2.Request(url)
    req.add_header('Accept', 'text/plain')
    try:
        return urllib2.urlopen(req)
    except urllib2.HTTPError as e:
        raise ("Ontrack error:\n%s\n" % e)

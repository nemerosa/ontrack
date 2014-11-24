#!/usr/bin/python

import urllib2


def do_get(url, token):
    req = urllib2.Request(url)
    req.add_header('Content-Type', 'application/json')
    req.add_header('Accept', 'application/json')
    req.add_header("Authorization", "Bearer %s" % token)
    return urllib2.urlopen(req)

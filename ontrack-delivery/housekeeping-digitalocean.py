#!/usr/bin/python

import argparse
import datetime
import json
import re
import urllib2


def do_get(url, token):
    req = urllib2.Request(url)
    req.add_header('Content-Type', 'application/json')
    req.add_header('Accept', 'application/json')
    req.add_header("Authorization", "Bearer %s" % token)
    return urllib2.urlopen(req)


def housekeeping(options):
    # Gets the list of droplets
    response = json.load(do_get("https://api.digitalocean.com/v2/droplets?page=1&per_page=100", options.token))
    for droplet in response['droplets']:
        id = droplet['id']
        name = droplet['name']
        print "[%s] %s" % (id, name)
        if re.match(options.pattern, name):
            print "  Pattern match."
            creation = datetime.datetime.strptime(droplet['created_at'], '%Y-%m-%dT%H:%M:%SZ')
            now = datetime.datetime.today()
            days = (now - creation).days
            print "  Creation time: %s" % creation
            print "  Since %d days" % days
            # TODO to_delete = (days > 2)
            to_delete = (days >= 0)
            if to_delete:
                print "  Deleting droplet [%s] %s" % (id, name)
                # delete_droplet(id)

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Ontrack Digital Ocean housekeeping')
    parser.add_argument('--token', required=True, help='Digital Ocean token')
    parser.add_argument('--pattern', required=False, help='Droplet name pattern', default='ontrack-acceptance.*')
    _options = parser.parse_args()
    housekeeping(_options)

import argparse
import json

import digitalocean


def get_ip(name, token):
    # Gets the list of droplets
    response = json.load(digitalocean.do_get("https://api.digitalocean.com/v2/droplets?page=1&per_page=100", token))
    for droplet in response['droplets']:
        if droplet['name'] == name:
            return droplet['networks']['v4'][0]['ip_address']
    raise Exception('Could not find droplet with name %s' % name)


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Ontrack Digital Ocean IP')
    parser.add_argument('--name', required=True, help='Digital Ocean droplet name to get the IP for')
    parser.add_argument('--token', required=True, help='Digital Ocean API token')
    _options = parser.parse_args()
    ip = get_ip(_options.name, _options.token)
    print ip

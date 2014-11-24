Operations
==========

### Preparing Digital Ocean environment

Details of using Digital Ocean as a Vagrant provided are given in the
[vagrant-digitalocean](see https://github.com/smdahlen/vagrant-digitalocean) plug-in documentation.

* install the Digital Ocean plug-in for Vagrant:

    vagrant plugin install vagrant-digitalocean

* create a Personal Access Token in Digital Ocean


### Connecting to a droplet at Digital Ocean

When the machine has been created in DO, you can connect to it as `root` using:

    ssh root@<ip>

where IP is the assigned one to the droplet. The connection uses the SSH key that was used to create the droplet and
this can lead to problems.

For example, if the droplet was created on Jenkins using its key, you cannot connect to this machine from, let's say,
your laptop, because you are using another key.

When the droplet is created using the `vagrant-install.sh` script, add the `--authorized-key=<path>` option to
indicate the path of a key to be copied over into the guest's _authorized_keys_ file.

### Docker management

Once on the Digital Ocean droplet, list the containers:

    docker ps -a

Two _ontrack_ containers must be there:

* `ontrack` - the main Ontrack container, which runs the application.
* `ontrack-data` - the data Ontrack container, which contains the working files for Ontrack (database, key, working
files). This container is stopped - it is normal, it hosts only the volume.

#### Connecting to the Ontrack container

To connect _inside_ the Ontrack manager:

    docker exec -it ontrack /bin/bash

This will open a _bash_ session inside the _ontrack_ container.

You can leave it using the _exit_ command.

#### Back up of Ontrack data

     ssh root@<ip> "docker run --volumes-from ontrack-data --volume /root:/backup ubuntu tar czvf /backup/backup.tgz /opt/ontrack/mount"
     scp root@<ip>:backup.tgz .

where IP is the assigned one to the droplet.

The backup file, _backup.tgz_, contains:

* the database files
* the logs
* the key file
* the working files

#### Looking at the logs live

    ssh root@<ip> "docker exec -it ontrack tail -f /opt/ontrack/mount/log/spring.log"

where IP is the assigned one to the droplet.

### Production setup

#### Upgrade (automated)

An upgrade is done by replacing the existing ontrack container by a new one from a more recent image.

Through SSH on the production machine, run the `production-update.sh` script with the `--version` parameter.

#### Installation (manual)

##### Preparing Nginx

In order to set-up a production like machine for Ontrack, you'll have to generate or produce some SSH keys. Store
them in a _./conf/certs_ directory.

Generate the Nginx configuration files using:

    ./nginx.sh --host=ontrack --proxy-name=\$host

if the Nginx host is not bound to a known DNS entry or, for example:

    ./nginx.sh --host=ontrack --proxy-name=ontrack.nemerosa.net

By default, SSH keys are generated and auto-signed, but in case of a real production like server, use additionally:

    ... --cert-pem=<path to .pem file> --cert-key=<path to .key file>

to prepare the keys.

In all cases, you'll have the following structure:

    ./build/
       |-- certs/
       |     |-- server.pem (or server.crt)
       |     |-- server.key
       |-- sites-enabled/
             |-- nginx.conf

##### Installation on local virtual box

    ./vagrant-install.sh \
        --vagrant-host=ontrack-production \
        --authorized-key=~/.ssh/id_rss.pub \
        --nginx-certs=build/certs \
        --nginx-sites-enabled=build/sites-enabled \
        --image=nemerosa/ontrack:<tag>

##### Installation on Digital Ocean

    ./vagrant-install.sh \
        --vagrant-provider=digital_ocean \
        --do-token=<DOToken> \
        --do-region=ams2 \
        --do-size=1024mb \
        --do-key=<DOKey> \
        --vagrant-host=ontrack-production \
        --authorized-key=~/.ssh/id_rsa.pub \
        --nginx-certs=build/certs \
        --nginx-sites-enabled=build/sites-enabled \
        --image=nemerosa/ontrack:<tag>

#### Migration (manual)

##### Nginx setup migration

1. Connect to the Docker host (see above).
2. Copy the SSH keys from another location:

    rm -f /root/nginx/certs/*
    scp user@host:/etc/nginx/ssl/* /root/nginx/certs

3. You now have a _.pem_ and a _.key_  file in _/root/nginx/certs_.
4. Update the _/root/nginx/sites-enabled/nginx.conf_  file with the new key location and the new host file (if needed):

    ...
    server_name ontrack.nemerosa.net;
    ...
    ssl_certificate /etc/nginx/certs/my.pem;
    ssl_certificate_key /etc/nginx/certs/my.key;
    ...
    proxy_set_header X-Forwarded-Host ontrack.nemerosa.net;

5. Restart the `nginx` container:

    docker restart nginx

##### Ontrack data migration (manual)

1. Connect to the Docker host (see above).
2. Stop and remove the `ontrack` container and `nginx`

    docker stop ontrack
    docker rm ontrack
    docker stop nginx
    docker rm nginx

3. Prepare the data:

    rm -rf migration
    mkdir -p migration/work/files/security
    mkdir -p migration/database
    scp -r install@ontrack.nemerosa.net:/opt/ontrack/work/files/security/* migration/work/files/security
    scp -r install@ontrack.nemerosa.net:/opt/ontrack/database/data.mv.db migration/database
    cd migration
    tar zcvf ../migration.tgz .

4. Start and connect to the `ontrack-data` volumes:

    docker run -it --volumes-from ontrack-data ubuntu:14.04 /bin/bash

5. And execute the following commands:

    cd /opt/ontrack/mount
    rm -rf *
    tar xzvf /migration/migration.tgz
    exit

6. Clean the container list

    docker rm $(docker ps -l | grep ubuntu  | awk "{print \$1}")

7. Remove the `ontrack` old container:

    docker rm ontrack

8. Recreate the `ontrack` container from your favourite version:

    docker run -d --name ontrack-<version> --volumes-from ontrack-data nemerosa/ontrack:<version>

9. Recreate the Nginx container:

    docker run -d \
        --publish 443:443 \
        --link ontrack-<version>:ontrack \
        --volume /root/nginx/certs:/etc/nginx/certs \
        --volume /root/nginx/sites-enabled:/etc/nginx/sites-enabled \
        dockerfile/nginx

##### DNS switch from old machine

1. Perform the migration
1. Shut down the old machine
1. Adjust DNS settings

#### Connectivity

The production machine is accessible as `root` using the SSH key(s) that was(were) setup at creation time. In this
SSH key is lost, you have a serious problem because you cannot connect any longer to the machine.

In parallel of this SSH key connection, you can setup a user/password connection:

    adduser ontrack
    visudo
    # ontrack ALL=(ALL:ALL) ALL

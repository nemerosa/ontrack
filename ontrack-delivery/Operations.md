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

Create an Ontrack Docker host as indicated above.

* Migration of Nginx keys
  * Connect to the Docker host (see above)
  * Copy the SSH keys to ...  (TODO)
  * Adjust the Nginx configuration

* Migration of Ontrack data
  * Connect to the Docker host (see above)
  * Stop the `ontrack`container: `docker stop ontrack`
  * Migrate database and security file (TODO)
  * Restart the `ontrack`container: `docker start ontrack`

#### Upgrade (automated)

TODO
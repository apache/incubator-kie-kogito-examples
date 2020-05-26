# Openshift (CRC) Kogito ansible automation

### Prerequisite
Tested on Fedora 31/32 and Ubuntu 19/20
Default values for the installation are 13GB of ram and 22GB of diskspace.

Ansible scripts to automate creation of CRC cluster and the deploy of one of the Kogito examples with just one command line.
[Ansible must be installed](https://docs.ansible.com/ansible/latest/installation_guide/index.html)
.


### Create an account on cloud.redhat.com
https://cloud.redhat.com/openshift/install and download or copy your Pull secret from the laptop installation https://cloud.redhat.com/openshift/install/crc/installer-provisioned


## Fedora

### Install LibVirt libs
```sh
sudo ansible-playbook ./playbook_libs.yaml
```

### Install CRC
(If you haven't already installed)

Download and copy CRC in the user's path (2GB),
change the app_name (my-kafka-project) in the file if you want different name
```sh
ansible-playbook ./playbook_crc.yaml
```

### Install Kogito on CRC
The create playbook will create from scratch the crc setup, the project namespace, then download  and install Kogito Operator and Kogito CLI.
The name of the Project to create and the Pull secret will be asked in the beginning of the run.
To use the default project name (my-kogito-project) just press enter.
```sh
ansible-playbook ./playbook_create.yaml
```

### Deploy Kogito Examples
To deploy one of the kogito examples, enter the name of one of the example,
at the end the browser show you the login page, the output of the deploy with the instructions to see
the logs from cli will be on the ansible output.
```sh
ansible-playbook ./playbook_deploy.yaml
```



## Ubuntu

### Install LibVirt libs
```sh
sudo ansible-playbook ./playbook_libs.yaml
```

### Install CRC
(If you haven't already installed)
Download and copy CRC in the user's path (2GB),
change the app_name (my-kafka-project) in the file if you want different name
```sh
ansible-playbook ./playbook_crc.yaml
```

### Install Kogito on CRC

The create playbook will create from scratch the crc setup, the project namespace, then download  and install Kogito Operator and Kogito CLI.
The name of the Project to create and the Pull secret will be asked in the beginning of the run.
To use the default project name (my-kogito-project) just press enter.
```sh
ansible-playbook ./playbook_create.yaml
```
### Deploy Kogito Examples
To deploy one of the kogito examples, enter the name of one of the example,
at the end the browser show you the login page, the output of the deploy with the instructions to see
the logs from cli will be on the ansible output.
```sh
ansible-playbook ./playbook_deploy.yaml
```



## Videos
Note: The CRC start spent 10 minute on a laptop, cut to few seconds in the video recording

### Setup CRC
Video Duration: 1:39 min.
[![asciicast](https://asciinema.org/a/313700.png)](https://asciinema.org/a/313700)

### Deploy Kpgotp Examples
Video Duration 27:sec
[![asciicast](https://asciinema.org/a/313703.png)](https://asciinema.org/a/313703)

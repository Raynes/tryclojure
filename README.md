# tryclojure

TryClojure is a online Clojure REPL written using Noir and Chris Done's jquery console (you're awesome, Chris).

[<img src="https://secure.travis-ci.org/Raynes/tryclojure.png"/>](http://travis-ci.org/Raynes/tryclojure)

## Usage

http://tryclj.com

To run it locally, use `lein ring server`.

## Online

We are running an instance of the tutorial on EC2

http://ec2-54-77-13-3.eu-west-1.compute.amazonaws.com:8801/

## Amazon EC2 setup notes

Installed Ubuntu 14.04 LTS 64-bit AMI, login and:

    sudo apt-get install openjdk-7-jdk
    cd $(mktemp -d)
    wget https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein
    chmod 0755 lein
    sudo cp lein /usr/local/bin
    lein version
    sudo apt-get install git
    sudo adduser --system tryclj --group --disabled-login --home /opt/tryclojure
    cd /opt/tryclojure
    sudo -u tryclj git clone https://github.com/ray1729/tryclojure.git
    sudo -u tryclj mkdir tryclojure/logs
    sudo sh -c 'cat > /etc/init/tryclj.conf' <<EOT
    # tryclj - Try Clojure Ring Server
    #

    description     "TryClojure Ring Server"

    start on filesystem or runlevel [2345]
    stop on runlevel [!2345]

    respawn
    respawn limit 10 5
    umask 022

    #console none

    exec su tryclj -s /bin/bash -c "cd /opt/tryclojure/tryclojure && lein ring server-headless"
    EOT
    sudo service tryclj start

## Credits

apgwoz: Design

## License

Licensed under the same thing Clojure is licensed under: the EPL, of which you can find a copy at the root of this directory.

Curator Service Discovery Plugin for PlayFramework 2
=============

To run it on a different port than 9000
	play -Dhttp.port=9123

Current plugin versions:

* 1.0-SNAPSHOT Play 2.3

What is Service Discovery
-------------
In SOA/distributed systems, services need to find each other. i.e. a web service might need to find a caching service, etc. DNS can be used for this but it is nowhere near flexible enough for services that are constantly changing. A Service Discovery system provides a mechanism for:

Services to register their availability
Locating a single instance of a particular service
Notifying when the instances of a service change


What is the Curator Service Discover Plugin
-------------

The [Apache Curator](http://curator.apache.org/) project is a set of libraries to simplify working with [Apache ZooKeeper](http://zookeeper.apache.org).
Curator has a [Service Discovery](http://curator.apache.org/curator-x-discovery/) extension to help simplify using service discovery.

How to use it in your Play 2 app?
==============

To enable the CSDP plugin in your Play 2 application, you have to add it as a dependency. Since it's hosted on my custom repository, you also have to add the url to this repository. Your **build.sbt**-file should look something like this:

    libraryDependencies ++= Seq(
        "com.schwartech" %% "play2-curator-service-discovery" % "1.0-SNAPSHOT"
    )

    resolvers += (
        "SchwarTech GitHub Repository" at "http://schwartech.github.com/m2repo/releases/"
    )

Properties Available in application.conf
-----------

    #
    # Setup a mock zookeeper server on port 2181
    # curator.service.discovery.zooServers=Mock
    #
    curator.service.discovery.zooServers="localhost:2181"

    #
    # Register this service when the application starts
    #
    curator.service.discovery.autoregister=true

    #
    # Name the service.  This is what the clients will use to search
    # Any string is fine, but by including environment and version
    # there are some nice benefits
    #
    curator.service.discovery.name="DEV:V1:ApacheCuratorXDiscovery"

    #
    # A description of the service
    #
    curator.service.discovery.description="ApacheCuratorXDiscovery discovery"

    #
    # The path used for searching within zookeeper
    #
    curator.service.discovery.path="/discovery/example"


When your application is automatically broadcasted as online, its url is generated like this:

	http://<the-ip-of-your-server>:<the-port-play-is-listening-on>/

Example

	http://10.0.0.1:9000/

If you are using a different port than 9000, you have to use the -Dhttp.port property.

To use a custom HTTP port, start you application like this:

	play -Dhttp.port=9011 run


Sample - Curator Service
------------

Demonstrates how to setup and register an application using the Play 2 - Curator Service Discovery Plugin.  To run it:

    <new terminal>
    $ cd <to-directory>/samples/CuratorService
    $ ./run 9001

    <new terminal>
    $ cd <to-directory>/samples/CuratorService
    $ ./run 9002

    <new terminal>
    $ cd <to-directory>/samples/CuratorService
    $ ./run 9003

This starts three copies.  Browse to each one to initialize (http://localhost:9001)


Sample - Curator Client
------------

Demonstrates how to discover a service.  To run:

    <new terminal>
    $ cd <to-directory>/samples/CuratorClient
    $ ./run 9000

Browse to http://localhost:9000

Keep hitting refresh on your browser and the response will keep changing depending on which of the three services you started responds.

Kill some of the CuratorService apps and see that they automatically get removed.

Good luck :)
# Overview
This is client program for JAX-WS MTOM file transfer.

# Tested configuration
- CentOS release 6.6 (Final)
- jdk1.7.0_80 (from Oracle, 64bit)
- Apache Maven 3.3.9

# Setup
1. Setup the server program.

    see [jaxws-mtom-sample-server](https://github.com/ooura/jaxws-mtom-sample-server/)

2. Edit pom.xml
Edit host and port in pom.xml in order to get WSDL.
    ```
    <properties>
        <server.host>"your http server's address"</server.host>
        <server.port>"your http server's port"</server.port>
        <!-- do not edit -->
    </properties>
    ```

3. Build

    ```
    $ mvn compile
    ```

4. Run
    ```
    $ mvn exec:java
    ```

# Customize
There is some parameters in "conf/client.properties". By default,
- Number of concurrent clients is set to 1.
- Number of iterations of each client is set to 1.

For more information, see "conf/client.properties".

# Disclaimer
This program was created in order to reproduce and debug some of the problems of my application.  
So this is not intended to be used in a production environment.  
For any damage due to the use of this program , I will not be responsible.  

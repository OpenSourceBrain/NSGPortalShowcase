
*******************************************
This is code originally developed for CIPRES: https://www.phylo.org/restusers/docs/guide.html#SampleCode
custimised to work with the NSG REST API
*******************************************

## Quick start

    cd ..
    svn export https://svn.sdsc.edu/repo/scigap/trunk/rest/datatypes datatypes
    cd datatypes
    mvn clean install 
    # Problems? See: http://stackoverflow.com/questions/16723533/how-do-you-specify-the-java-compiler-version-in-a-pom-xml-file
    
    cd ../Java
    mvn clean install
    # Create an NSG REST account and update the configuration file as outlined [here](https://github.com/OpenSourceBrain/NSGPortalShowcase/blob/master/NSG-R/README.md)
    mvn exec:java
    


*******************************************

**Original Readme.txt follows**



==================================================================================================================
cipres_rest_client.jar
==================================================================================================================
This is a java client library for the REST API.  


To build cipres_rest_client.jar: 

1. You must have maven and a java sdk installed.  The build was tested with maven 3.2.3
and java 1.7.   

2. Before building cipres_rest_client.jar, build restdatatypes.jar.  Do this in a directory
next to the java_direct directory:

    $ cd ..
    $ svn export https://svn.sdsc.edu/repo/scigap/trunk/rest/datatypes datatypes
    $ cd datatypes
    $ mvn clean install 

Get the datatypes source code from the same release that you get the java_direct
code from.  For example, if you get java_direct from the trunk, then the url shown above
would be fine, but if you get java_direct from a branch, such as 
https://svn.sdsc.edu/repo/scigap/branches/rest-R4/rest_client_examples/examples/java_direct
then get datatypes from the same branch, i.e. https://svn.sdsc.edu/repo/scigap/branches/rest-R4/rest/datatypes.

3. Now return to the java_direct and build cipres_rest_client.jar:

    $ mvn clean install


==================================================================================================================
Documentation
==================================================================================================================
Javadoc documentation for cipres_rest_client.jar is generated at 
    java_direct/target/site/apidocs/index.html

==================================================================================================================
Example Code
==================================================================================================================
The java_direct directory includes an example application that uses the cipres_rest_client.jar.
The source code is in java_direct/src/main/org/ngbw/directclient/example/Example.java.  To run
the example you'll need to:

1) Register for a CIPRES Rest account at http://www.phylo.org/restusers then 
login and go to the "Developers" menu and choose "Application Management".   Press 
"Create New Application" and fill in the form to register a new application that uses 
Direct Authentication.   

2) Copy java_direct/pycipres.conf to your home directory and edit it, filling in your username, 
password, application name and application ID.  Or put pycipres.conf anywhere on disk and set
an environment variable named PYCIPRES to the full pathname of the file; on Windows this is preferable
to using the home directory.

3) From the java_direct directory, run:

    mvn exec:java

You should see some logging output and a prompt that looks like:

    Enter s(submit), v(validate), l(list), d(delete), j(job status), r(retrieve results) or q(quit):

Enter 's' to submit a hard coded job.  You'll be prompted to enter a name for the job, and
once it's submitted, the program will show you the job's url.  After that you can use the 'j' command
repeatedly to check the job's updated status.  



==================================================================================================================
Umbrella Authentication Example
==================================================================================================================
If you're more interested in UMBRELLA authentication than in DIRECT authentication (i.e. you are
planning to call the REST API from a web application that handles its own user registration and login)
then in step 1, under "Example Code" above, you'll want to register an application to use UMBRELLA
authentication.

In step 2, you'll put your username and password in pycipres.conf.  You're considered the application's
developer or administrator, and your credentials will be used to authenticate all requests between
your web application and the REST service.   As explained in the User's Guide, special request
headers will be used on each request to tell CIPRES which of your users (i.e. which "end user")
the request is for.

Invoke the example with:

	mvn exec:java -Dexec.args="-u"

When the program starts, it will prompt you to enter information about the "end user" before you
can choose to run a job or retrieve results, etc.  This is to simulate a web application that can
have several different users logged in.  Here you're telling the example code which user to make
the requests on behalf of.  You can use the "c(change user)" prompt to switch users.

Note that you can enter any username, email address, etc, at the prompt.   If the username
isn't found in CIPRES's database, CIPRES creates an account, on the fly, for the end user.



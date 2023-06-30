==================
python_cipres
==================

The CIPRES REST API (CRA) provides a way to run parallel versions of phylogenetic alignment and tree 
inference software, such as RaXML, MrBayes, Garli, Beast, etc. on high performance supercomputers.  
The python_cipres package is a client library that makes it easy to use the CRA.  The package includes 
several scripts that can be run, as is, to submit jobs, check their status and download results.  Or, 
the package can imported and used to develop custom software that uses the CRA.

python_cipres has been tested on python 2.6, python 2.7 and python 3.4.3 on linux, mac OS and windows.


Installation
------------
To install python_cipres, simply::

    $ pip install python_cipres

Or, if you've downloaded the source from svn, or from a source archive, go to the top
level directory, which contains "setup.py", and run::
    
    $ python setup.py install


Register to use the CIPRES REST API
-----------------------------------
Register at https://www.phylo.org/restusers, then log in, and from the "Developer" menu,
choose "Application Management" and create a new application.  Make note of the application ID that 
is assigned.

General documentation about using the CIPRES REST API is available at 
https://www.phylo.org/restusers/documentation.  However, the documentation doesn't specifically 
address the python_cipres package.


Configuration File (~/pycipres.conf)
-------------------------------------
In your home directory create a file named "pycipres.conf" with the following settings::

    URL=https://cipresrest.sdsc.edu/cipresrest/v1
    APPNAME=the_application_you_just_registered
    APPID=application_ID
    USERNAME=the_username_you_just_registered
    PASSWORD=the_password
    VERBOSE=


Test
----
Run a quick test of the installation and configuration::

    $ cipresjob.py -l

This should return the url of the CRA and a list of jobs that you've submitted.  Without any 
submitted jobs it looks like this::

    URL=https://cipresrest.sdsc.edu/cipresrest/v1

Documentation
-------------
There are two scripts, tooltest.py and cipresjob.py that can be run from the command line to submit jobs
and monitor them.   The scripts are explained in examples/Readme.txt.   

    *You will find the examples directory where you installed the python_cipres package (the path
    will typically be similar to site-packages/python_cipres-XX-py2.7.egg/python_cipres/examples) OR you can
    download a source archive from the pypi page.  Within the expanded source archive, the path is
    python_cipres/examples.   Each subdirectory under examples is a "job template" that can be 
    submitted to the CRA using tooltest.py.*

To use the python_cipres library in your own code::

    import python_cipres.client as CipresClient

The API provided by the python_cipres library is not documented yet, however it is quite small and 
simple.  It is implemented entirely in client.py.  Commands.py uses the API to implement the 
functionality provided by tooltest.py and cipresjob.py, so looking at commands.py is a good way 
to see how to use the API.

    client.py
        provides the basic job management API

    commands.py
        uses the methods in client.py to implement what tooltest.py and cipresjob.py do

    bin/tooltest.py and bin/cipresjob.py
        are executable scripts that call the corresponding methods in commands.py.
        Tooltest.py submits a job to CIPRES based on the specification in a job template directory.
        Cipresjob.py lists jobs, checks a job's status and downloads results.

Please subscribe to the cipres-rest-users mailing list at https://groups.google.com/forum/#!forum/cipres-rest-users. 
Send a message to the list if you have any questions about how to use the API or how to configure a specific tool.  


Distributing your code
----------------------
If you create scripts or an application that uses the CIPRES REST API and you share your code with others, 
keep in mind that the users of your code should use the same APPNAME and APPID that you registered, 
to identify your code, but must use their own USERNAME and PASSWORD to submit jobs.

Web Applications 
----------------------
If you are submitting jobs from a web application that has multiple users, then as a matter of policy, 
you must register your application as an umbrella application, as explained in the "Register" section
of the User Guide (https://www.phylo.org/restusers/docs/guide.html).  Web applications can't use
tooltest.py and cipresjob.py, as is, because they don't support umbrella authentication; however
you can use the code that implements tooltest.py and cipresjob.py (see commands.py) as a template for
your own code. The only change you would need to make is that you must pass an additional dictionary 
argument, endUserHeaders, to the CipresClient.Client() ctor, as shown in a comment in commands.py.   
The endUserHeaders identifies the end user, of your application, for whom the cipres request 
is being made. 


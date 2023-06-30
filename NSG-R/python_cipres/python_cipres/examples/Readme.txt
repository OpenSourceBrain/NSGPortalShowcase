OVERVIEW
----------

This directory has a number of example "job template" directories that you can submit to 
the CIPRES REST API using tooltest.py.   Here are some examples of how to run tooltest.py
and cipresjob.py.

tooltest.py clustalw run ./myresults
	Submits the job specified in the "clustalw" subdirectory.  Waits for
	the job to finish and downloads the results to a subdirectory named "myresults".

tooltest.py clustalw validate 
	Submits the job specified in the "clustalw" subdirectory but just
	asks the REST API to validate the submission without actually running the job.

cipresjob.py -j NGBW-JOB-RAXMLHPC2_TGB-00580C183D2D44C99F5FECCD6ADA2A08
	Returns the status of the job with the specified jobhandle.  Tooltest.py prints
	out the jobhandle after submitting a job.

cipresjob.py -l 
	Lists jobs.

cipresjob.py -j NGBW-JOB-RAXMLHPC2_TGB-00580C183D2D44C99F5FECCD6ADA2A08 -d ./myresults
	Downloads the results of the specified job to a directory named "myresults"
	If the job has not finished running yet this will download the contents of the
	job's working directory.

cipresjob.py -j NGBW-JOB-RAXMLHPC2_TGB-00580C183D2D44C99F5FECCD6ADA2A08 -r 
	Deletes the specified job.  This will also cancel the job if it is currently
	running or queued to run.


JOB TEMPLATE
-------------

The command line progams tooltest.py and cipresjob.py look for the job's input data and
configuration parameters in a job template directory.  However, the python_cipres library
that is used to implement these programs, and that you can use directly, lets you specify
all the inputs in dictionaries instead of using a template directory.

Job template directories must contain 2 property files: testInput.properties and testParam.properties.
testInput.properties gives the properties that refer to input files for the job and
testParam.properties gives the "visible" parameters.  Please refer to 
https://www.phylo.org/restusers/docs/guide.html (especially "Submit Jobs" and "Tool Specific
Parameters" sections) for an explanation of the types of parameters and https://www.phylo.org/restusers/docs/tools
for a description of the specific parameters that can be used with each tool.

With python_cipres, the "input.", "vparam." and "metadata" prefixes mentioned in the User Guide
can be omitted and the tool can be specified with either "tool" or "toolId" properties.   When you
use the python_cipres library from your own code, you can supply metadata, including a flag
that tells Cipres to send you email when the job is finished.  However, there is no way to
supply metadata when using tooltest.py with a job template directory.


MORE ABOUT HOW TO USE CIPRESJOB.PY
-----------------------------------

Here's an extended example:

If you need to stop tooltest.py before the job you submitted is finished, or if tooltest.py
encounters a transient error (like being unable to connect to CIPRES) before the job is finished,
you can use cipresjob.py to check the job's status and then download the results once the job 
is finshed.   In the example below, we type ^C to stop tooltest.py, then use cipresjob.py to
get the job's results:  

SUBMIT A JOB:
$ tooltest.py raxml_ancestral_states run results2
Job=NGBW-JOB-RAXMLHPC2_TGB-00580C183D2D44C99F5FECCD6ADA2A08, not finished, stage=QUEUE
	2014-12-17T15:39:11-08:00: Added to cipres run queue.
	Waiting for job to complete ...

TYPE ^C TO STOP TOOLTEST.PY:
^CTraceback (most recent call last):
  File "/Users/terri/local/virtualenv/common/bin/tooltest.py", line 8, in <module>
    execfile(__file__)
  File "/Users/terri/Documents/workspace/commoncodebase/trunk/scigap/rest_client_examples/python_cipres/bin/tooltest.py", line 10, in <module>
    sys.exit(main())
  File "/Users/terri/Documents/workspace/commoncodebase/trunk/scigap/rest_client_examples/python_cipres/bin/tooltest.py", line 7, in main
    return CipresCommands.tooltest(sys.argv)
  File "/Users/terri/Documents/workspace/commoncodebase/trunk/scigap/rest_client_examples/python_cipres/python_cipres/commands.py", line 149, in tooltest
    job.waitForCompletion()
  File "/Users/terri/Documents/workspace/commoncodebase/trunk/scigap/rest_client_examples/python_cipres/python_cipres/client.py", line 358, in waitForCompletion
    time.sleep(pollInterval)
KeyboardInterrupt

USE CIPRESJOB.PY TO CHECK THE JOB'S STATUS:
$ cipresjob.py -j NGBW-JOB-RAXMLHPC2_TGB-F56624C0AA0C408681F4805A3CD0F869
Job=NGBW-JOB-RAXMLHPC2_TGB-F56624C0AA0C408681F4805A3CD0F869, not finished, stage=SUBMITTED
	2014-12-17T15:45:43-08:00: Added to cipres run queue.
	2014-12-17T15:45:46-08:00: Command rendered successfully: raxmlHPC-PTHREADS -n infile  -s infile -f A -t tree.tre  -m GTRGAMMA -O
	2014-12-17T15:45:47-08:00: Staging input files to trestles
	2014-12-17T15:45:59-08:00: Input files staged successfully to /projects/ps-ngbt/backend/trestles_test_workspace/NGBW-JOB-RAXMLHPC2_TGB-F56624C0AA0C408681F4805A3CD0F869/
	2014-12-17T15:46:06-08:00: Submitted to trestles as job '2411423'.

CHECK STATUS AGAIN:
$ cipresjob.py -j NGBW-JOB-RAXMLHPC2_TGB-F56624C0AA0C408681F4805A3CD0F869
Job=NGBW-JOB-RAXMLHPC2_TGB-F56624C0AA0C408681F4805A3CD0F869, finished, results are at http://localhost:7070/cipresrest/v1/job/pycipres_eu/NGBW-JOB-RAXMLHPC2_TGB-F56624C0AA0C408681F4805A3CD0F869/output
	2014-12-17T15:45:43-08:00: Added to cipres run queue.
	2014-12-17T15:45:46-08:00: Command rendered successfully: raxmlHPC-PTHREADS -n infile  -s infile -f A -t tree.tre  -m GTRGAMMA -O
	2014-12-17T15:45:47-08:00: Staging input files to trestles
	2014-12-17T15:45:59-08:00: Input files staged successfully to /projects/ps-ngbt/backend/trestles_test_workspace/NGBW-JOB-RAXMLHPC2_TGB-F56624C0AA0C408681F4805A3CD0F869/
	2014-12-17T15:46:06-08:00: Submitted to trestles as job '2411423'.
	2014-12-17T15:47:44-08:00: Trying to transfer results.
	2014-12-17T15:49:04-08:00: Output files retrieved.  Task is finished.
 

DOWNLOAD THE RESULTS:
$ cipresjob.py -j NGBW-JOB-RAXMLHPC2_TGB-F56624C0AA0C408681F4805A3CD0F869 -d results2
Downloading final results to /Users/terri/examples/results2
$ 

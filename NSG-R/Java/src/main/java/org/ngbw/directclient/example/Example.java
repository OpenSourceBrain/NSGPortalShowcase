package org.ngbw.directclient.example; 

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.ngbw.directclient.CiApplication;
import org.ngbw.directclient.CiCipresException;
import org.ngbw.directclient.CiClient;
import org.ngbw.directclient.CiJob;
import org.ngbw.directclient.CiResultFile;
import org.ngbw.restdatatypes.ErrorData;
import org.ngbw.restdatatypes.LimitStatus;
import org.ngbw.restdatatypes.ParamError;
import org.ngbw.restdatatypes.StatusData;
import org.ngbw.restdatatypes.ToolData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
Example command line application that shows how to use the CIPRES REST API Java
Client library, i.e. the classes in the org.ngbw.directclient package.  By 
default the example uses DIRECT authentication, but if invoked with the "-u" command
line argument it switches to UMBRELLA authentication.   Username, password, application 
name and application ID are read from ~/pycipres.conf and the information in pycipres.conf
must correspond to an application registered for UMBRELLA auth or DIRECT auth respectively
depending on whether you run the example with or with "-u".
<p>
This example repeatedly prompts the user to submit a job, list the user's
already submitted jobs, retrieve a job's status, download a job's results, delete a job
or quit.  The example only submits one type of job: clustalw, run on the included
sequence file.  Consult the Users Guide for information about how to configure
different kinds of jobs.
<p>
The gist of using the library is that you instantiate a CIClient object, then use
its methods, and the objects that those methods return.   When using direct auth
a single CIClient can be used, but when using UMBRELLA auth, a CIClient must be
instantiated for each end user.   A CIClient object should not be used from multiple threads 
at the same time.
<p>
 */
public class Example 
{
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(Example.class.getName());
	private static Console console = System.console();
	private static String entry;
	private static CiClient myClient;
	private static String fastaFile;
	private static boolean umbrella = false;

	// For umbrella app's only
	private static CiClient adminClient;

	public static void main(String[] args) throws Exception
	{
		if (console == null)
		{
			System.out.println("No console");
			return;
		}

		// If true, the example will use umbrella authentication
		if (args.length > 0 && args[0].equals("-u"))
		{
			umbrella = true;
		}

		// Copy example fasta file from jar to temp file on disk
		fastaFile = copyResource("ex1.fasta");

		// Get username, password, app name, app id, url from pycipes.conf
		CiApplication app = CiApplication.getInstance();

		if (umbrella)
		{
			myClient = loginUser(app);
			if (myClient == null)
			{
				return;
			}
			adminClient = new CiClient(app.getAppKey(), app.getUsername(), app.getPassword(), app.getRestUrl());
		}
		else 
		{
			myClient = new CiClient(app.getAppKey(), app.getUsername(), app.getPassword(), app.getRestUrl());
		}
		String command = "";
		while (!command.equals("q"))
		{
			if (umbrella)
			{
				System.out.println("\nEnter s(submit), v(validate), l(list), d(delete), j(job status), r(retrieve results), t(show tools), u(change end user) or q(quit):");
			} else
			{
				System.out.println("\nEnter s(submit), v(validate), l(list), d(delete), j(job status), r(retrieve results) or t(show tools) or q(quit):");
			}
			command = console.readLine();
			try
			{
				if (command.equals("q"))
				{
					continue;
				}
				if (command.equals("v"))
				{
					validateJob();
				}
				else if (command.equals("s"))
				{
					submitJob();
				} else if (command.equals("l"))
				{
					listJobs();
				} else if (command.equals("d"))
				{
					deleteJob();
				} else if (command.equals("j"))
				{
					jobStatus();
				} else if (command.equals("r"))
				{
					retrieveResults();
				} else if (umbrella && command.equals("u"))
				{
					myClient = loginUser(app);
					if (myClient == null)
					{
						break;
					}
				}
				else if (command.equals("t"))
				{
					showTools();
				}
				else
				{
					System.out.println("invalid response: " + command);
				}
			}
			catch(CiCipresException ce)
			{
				ErrorData ed = ce.getErrorData();
				System.out.println("Cipres error code=" + ed.code + ", message=" + ed.displayMessage);
				if (ed.code == ErrorData.FORM_VALIDATION)
				{
					for (ParamError pe : ed.paramError)
					{
						System.out.println(pe.param + ": " + pe.error);
					}
				} else if (ed.code == ErrorData.USAGE_LIMIT)
				{
					LimitStatus ls = ed.limitStatus;
					System.out.println("Usage Limit Error, type=" + ls.type + ", ceiling=" + ls.ceiling);
				}
			}
			catch (Exception e)
			{
				System.out.println(e.toString());
			}	
		}
	}

	private static void showTools() throws CiCipresException
	{
		Collection<ToolData> tools = myClient.listTools(); 
		System.out.println("");
		for (ToolData td : tools)
		{
			System.out.println(td.toolId + " : " + td.toolName);
		}
	}

	
	private static void validateJob() throws CiCipresException
	{
		System.out.println("Sending a canned clustalw job for validation ...");
		sendCannedJob("fakeName", true);
	}

	private static void submitJob() throws CiCipresException
	{
		System.out.println("Will submit an example clustalw job.  Enter a name for the job or enter a single 'n' to cancel"); 
		entry = console.readLine();
		if (entry.equals("n"))
		{
			return;
		}
		sendCannedJob(entry, false);
	} 

	private static void sendCannedJob(String jobName, boolean validateOnly) throws CiCipresException
	{
		Map<String, Collection<String>> vParams = new HashMap<String, Collection<String>>();
		HashMap<String, String> inputParams = new HashMap<String, String>();
		HashMap<String, String> metadata = new HashMap<String, String>();

		vParams.put("runtime_", Arrays.asList(".2"));
		vParams.put("hgapresidues_", Arrays.asList("A", "D"));
		inputParams.put("infile_", fastaFile);

		// See https://www.phylo.org/restusers/docs/guide.html#UseOptionalMetadata for list of available
		// metadata keys.   
		metadata.put("statusEmail", "true");
		metadata.put("clientJobName", jobName);

		CiJob jobStatus;
		if (validateOnly)
		{
			jobStatus = myClient.validateJob("CLUSTALW", vParams, inputParams, metadata);
		} else
		{
			jobStatus = myClient.submitJob("CLUSTALW", vParams, inputParams, metadata);
		}
		jobStatus.show(true);
	} 

	private static void listJobs() throws CiCipresException
	{

		int count = 0;
		Collection<CiJob> jobs = myClient.listJobs(); 
		for (CiJob job : jobs)
		{
			count += 1;
			System.out.print("\n" + count + ". ");
			job.show(false);
		}
	}

	private static void deleteJob() throws CiCipresException
	{
		System.out.println("Enter url of job to delete or n(no, cancel)");
		entry = console.readLine();
		if (!entry.startsWith("http"))
		{
			return;
		}
		CiJob job = myClient.getJob(entry);
		job.delete();
	} 

	private static void retrieveResults() throws CiCipresException, IOException
	{
		boolean finalResults = true;
		System.out.println("Enter url of job or n(no, cancel)");
		entry = console.readLine();
		if (!entry.startsWith("http"))
		{
			return;
		}
		CiJob job = myClient.getJob(entry);
		if (job.isDone() == false)
		{
			System.out.println("Job is not finished. Enter y(yes, get working dir contents) or n(no, cancel)"); 
			entry = console.readLine();
			if (entry.equals("n"))
			{
				return;
			} else
			{
				finalResults = false;
			}
		}
		Collection<CiResultFile> files = job.listResults(finalResults);
		int count = 0;
		for (CiResultFile file: files)
		{
			count = count + 1;
			System.out.println(count + ". " + file.getName() + " (" + file.getLength() + " bytes )" );
		}
		System.out.println("Enter name of file to download or a(all) or n(no, cancel)");
		entry = console.readLine();
		boolean all = false;
		if (entry.equals("a"))
		{
			all = true;
		} else if (entry.equals("n"))
		{
			return;
		}
		File directory = new File(new File("."), job.getJobHandle());
		if (!directory.exists())
		{
			directory.mkdir();
		}
		for (CiResultFile file : files)
		{
			if (all == true || file.getName().equals(entry))
			{
				System.out.println("Downloading " + file.getName() + " to " + directory.getAbsolutePath()); 
				file.download(directory);
			}
		}
	}

	private static void jobStatus() throws CiCipresException
	{
		if (!umbrella)
		{
			System.out.println("Enter jobhandle or n(no, cancel)");
			entry = console.readLine();
			if (!entry.startsWith("NGBW"))
			{
				return;
			}
			CiJob job = myClient.getJobFromHandle(entry);
			job.show(true);
		} else
		{
			/*
				This shows how an umbrella app can get status of multiple jobs for multiple users in
				a single call.  This is the only thing an umbrella app can do with a CiClient (see
				adminClient below) that is configured without endUserHeaders.  Everything else must
				be done for a specific end user with a CiClient configured for that user.
				
				An Umbrella app could also use the code above (in the !umbrella case) to get status of a 
				single job, using a CiClient configured for the owner of the job. 

			*/
			System.out.println("Enter comma separated list of jobhandles or n(no, cancel)");
			entry = console.readLine();
			if (!entry.startsWith("NGBW"))
			{
				return;
			}
			String[] handles = entry.split(",");
			ArrayList<String> handleList = new ArrayList<String>(handles.length);
			for (String handle : handles)
			{
				handleList.add(handle.trim());
			}
			//Collection<StatusData> jobList = adminClient.getMultipleJobStatus(handleList);
			Collection<StatusData> jobList = myClient.getMultipleJobStatus(handleList);
			System.out.println("\n");
			for (StatusData jobstatus : jobList)
			{
				String str = jobstatus.jobHandle + " ";
				if (jobstatus.terminalStage == true)
				{
					if (jobstatus.failed == true)
					{
						str += " failed at stage " + jobstatus.jobStage;
					} else
					{
						str += " finished, results at " + jobstatus.resultsUri.url;
					}
				} else
				{
					str += " is not finished, stage = " + jobstatus.jobStage;
				}
				System.out.println(str);
			}
		}
	}

	private static String copyResource(String name) throws IOException
	{
		InputStream is;

		is = Example.class.getResourceAsStream("/" + name);
		File dest = File.createTempFile("Example", ".txt"); 
		dest.deleteOnExit();

		CiResultFile.copyInputStreamToFile(is, dest);
		return dest.getAbsolutePath();
	}

	private static CiClient loginUser(CiApplication app) throws Exception
	{
		Map<String, String> endUserHeaders = new HashMap<String, String>();

		System.out.println("\nEnter information about the current end user of this application.");
		System.out.println("Enter username or q(quit)");
		String username = console.readLine();
		if (username.equals("q"))
		{
			return null;
		}
		System.out.println("Enter user's email address or q(quit)");
		String email = console.readLine();
		if (email.equals("q"))
		{
			return null;
		}
		System.out.println("Enter user's institution or q(quit)");
		String institution = console.readLine();
		if (institution.equals("q"))
		{
			return null;
		}
		System.out.println("Enter user's country code (must be 2 letters, both uppercase) or q(quit)");
		String country = console.readLine();
		if (country.equals("q"))
		{
			return null;
		}
		endUserHeaders.put("cipres-eu", username.trim());
		endUserHeaders.put("cipres-eu-email", email.trim());
		endUserHeaders.put("cipres-eu-institution", institution.trim());
		endUserHeaders.put("cipres-eu-country", country.trim());
		endUserHeaders.put("cipres-eu-email", email.trim());

		log.debug("cipres-eu-email is " + endUserHeaders.get("cipres-eu-email"));
		myClient = new CiClient(	app.getAppKey(), 
									app.getAppname(), 
									app.getUsername(), 
									app.getPassword(),
									app.getRestUrl(),
									endUserHeaders);
		return myClient;
	}

}


package org.ngbw.directclient; 

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.GenericType;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.ngbw.restdatatypes.JobList;
import org.ngbw.restdatatypes.StatusData;
import org.ngbw.restdatatypes.ToolData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Class used to initiate communication with the CIPRES REST API.   (Not thread-safe.)
 */
public class CiClient 
{
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(CiClient.class.getName());
	private String baseUrl;
	private String jobUrl;
	private String genericJobUrl;
	private CiCommunicator comm;


	/**
	 * Instantiates a new CiClient for an application that uses DIRECT authentication.
	 * This method doesn't communicate with the REST API so invalid credentials won't
	 * be detected until the CiClient is used to send a request.
	 *
	 * @param appID
	 *            the application ID of the registered client application
	 * @param username
	 *            the username of the registered user
	 * @param password
	 *            the password of the registered user
	 * @param baseUrl
	 *            the url of the CIPRES REST service 
	 */
	public CiClient(String appID, String username, String password, String baseUrl) 
	{
		this.comm = new CiCommunicator(username, password, appID);
		this.baseUrl = baseUrl;
		this.genericJobUrl = baseUrl + "/job/";
		this.jobUrl = baseUrl + "/job/" + username; 
	}

	/**
	 * Instantiates a new CiClient for a specific end user of an UMBRELLA application.
	 * This method doesn't communicate with the REST API so invalid credentials won't
	 * be detected until the CiClient is used to send a request.
	 *
	 * @param appID
	 *            the application ID of the registered client application
	 * @param appname
	 *            the (short) name of the registered client application
	 * @param username
	 *            the username of the person who registered the application 
	 * @param password
	 *            the password of the person who registered the application 
	 * @param baseUrl
	 *            the url of the CIPRES REST service 
	 * @param endUserHeaders 
	 *            identifies the end user of the application.  The following keys, described
	 * in the User Guide, must be included and have non-null values: cipres-eu, cipres-eu-email,
	 * cipres-eu-institution, cipres-eu-country.  The value of cipres-eu-country must be a 2
	 * letter country code, uppercase.
	 *
	 * @throws Exception if cipres-eu or cipres-eu-email properties are missing  from endUserHeaders.
	 */
	public CiClient(String appID, String appname, String username, String password, String baseUrl, Map<String, String> endUserHeaders) 
		throws Exception
	{
		String endUsername = endUserHeaders.get("cipres-eu");
		if (endUsername == null || (endUserHeaders.get("cipres-eu-email") == null))
		{
			throw new Exception("endUserHeaders must include cipres-eu and cipres-eu-email");
		}
		endUsername = appname + "." + endUsername;

		this.comm = new CiCommunicator(username, password, appID, endUserHeaders);
		this.baseUrl = baseUrl;
		this.genericJobUrl = baseUrl + "/job/";
		this.jobUrl = baseUrl + "/job/" + endUsername; 
	}

	/**
	  Get information about each of the tools.

	  @return list ToolData objects.  ToolData is defined in restdatatypes.jar.
		ToolData public member fields include
		<ul>
			<li>String toolId - to be used with submitJob()
			<li>String toolName - a sentence that describes the tool
			<li>urls that provide various types of documenation for the tool
		</ul>
		
	  @throws CiCipresException
	  	if unable to communicate with the rest api
	*/
	public Collection<ToolData> listTools() throws CiCipresException
	{
		Collection<ToolData> toolList = comm.getFromUrl(baseUrl + "/tool", new GenericType<Collection<ToolData>>() {});

		// sort alphabetically by toolId
		Comparator<ToolData> comparator = new Comparator<ToolData>() 
		{
			public int compare(ToolData t1, ToolData t2) 
			{
				return t1.toolId.compareTo(t2.toolId); 
			}
		};
		List<ToolData> sortedList = new ArrayList<ToolData>();
		sortedList.addAll(toolList);
		Collections.sort(sortedList, comparator); 
		return sortedList;
	}

	/**
	 * Get the list of jobs that the user has submitted via this application.  Does not include jobs
	 * that the user has deleted or that have been automatically deleted after a certain period
	 * of time.
	 *
	 * @return list of the user's jobs.  
	 *             List will be empty if the user has no jobs.  The collection is in no particular order.  
	 *             Includes jobs in any state including submitted, running, finished, etc.
	 * @throws CiCipresException
	 *             if there a problem fulfilling the request (e.g authentication, service down ...)
	 */
	public Collection<CiJob> listJobs() throws CiCipresException
	{
		JobList jl = comm.getFromUrl(jobUrl + "/?expand=true", JobList.class);

		ArrayList<CiJob> jobs = new ArrayList<CiJob>();
		for (StatusData sd : jl.jobstatus)
		{
			jobs.add(new CiJob(comm, sd));
		}
		return jobs;
	}

	/**
	 * Gets information about the job, given it's jobhandle.
	 *
	 * @param jobHandle
	 *            the jobhandle (starts with "NGBW-")
	 * @return the job
	 * @throws CiCipresException
	 *             if jobhandle doesn't exist, authentication error, ...
	 */
	public CiJob getJobFromHandle(String jobHandle) throws CiCipresException
	{
		StatusData sd = comm.getFromUrl(jobUrl + "/" + jobHandle, StatusData.class);
		return new CiJob(comm, sd);
	}

	/**
	 * Umbrella apps can use this to check status of multiple jobs, for multiple users, in a single call.
	 * This may be called using a CiClient that was constructed with the ctor for DIRECT apps, i.e. the
	 * one that doesn't take an appname or endUserHeaders.   This is the only method that umbrella
	 * apps can use without end user headers.  For example, if this method indicates that a particular job
	 * is finished, the umbrella app must construct a CiClient with appropriate end user headers for the owner
	 * of that job, in order to download the results of that job.
	 *
	 * @param jobHandles
	 *            returns status for the jobs indicated by this list of jobhandles 
	 * @return list of StatusData objects, one for each job. 
	 * @throws CiCipresException
	 *             if jobhandle doesn't exist, authentication error, ...
	 */
	public Collection<StatusData> getMultipleJobStatus(List<String> jobHandles) throws CiCipresException
	{
		String queryString = "?";
		for (String handle : jobHandles)
		{
			queryString +="jh=" + handle + "&";
		}
		// get rid of last "&" 
		queryString = queryString.substring(0, queryString.length() - 1);
		JobList joblist = comm.getFromUrl(genericJobUrl + queryString,  JobList.class);

		for (StatusData sd: joblist.jobstatus)
		{
			log.debug("Got status for " + sd.jobHandle);
		}
		return joblist.jobstatus;
	}

	/**
	 * Gets information about a job, given the job's url.
	 *
	 * @param url
	 *            the url of the job.
	 * @return the job
	 * @throws CiCipresException 
	 *             if job doesn't exist, authentication error, ...
	 */
	public CiJob getJob(String url) throws CiCipresException
	{
		StatusData sd = comm.getFromUrl(url, StatusData.class);
		return new CiJob(comm, sd);
	}

	/**
	 * Submit a task to be run on CIPRES.  Consult the 
	 *	<a href="https://www.phylo.org/restusers/docs">REST API Developer documentation</a> for detailed 
	 * information about the tool IDs, parameters and metadata.
	 *
	 * @param tool
	 *		a tool ID string that identifies the tool to be run on CIPRES.  A list of tool IDs is
	 *		available programmatically from the REST API.  You can also find the tools IDs and
	 *		documentation for tool parameters at 
	 *		<a href="https://www.phylo.org/restusers/docs/tools">https://www.phylo.org/restusers/docs/tools</a>.
	 *			
	 * @param vParams
	 *      a map of parameters that are used to set the tool's command line options, and in some cases,
	 *		configure other aspects of the run.  Each entry in the map is a String key and a collection of String
	 *		values.   The value collection usually has only a single item; multiple items are present only for
	 *		parameters of type "List". The permitted parameters vary with the tool.   The parameter keys, their
	 *		types (e.g. Integer, Float, String, Switch, List ...) and default values are documented on the
	 *		tools "REST Tool Info" page at 
	 *		<a href="https://www.phylo.org/restusers/docs/tools">https://www.phylo.org/restusers/docs/tools</a>.
	 *		<p>
	 *		In the documentation, these parameters start with the prefix "vparam.".   When using the 
	 *		org.ngbw.directclient classes you can include the prefix in the parameter keys or omit it. 
	 *		
	 * @param inputParams
	 *		a map of input data parameters (these start with the prefix "input.", in the documentation).  
	 *		The set of parameter keys varies with the tool.   When using org.ngbw.directclient classes you can
	 *		include the prefix in the parameter key or omit it.  The value of each inputParam element is the 
	 *		pathname of a file that the tool will be run on, either the primary input for the tool, or an
	 *      auxillary file (e.g. constraints, starting tree ...)
	 *
	 * @param metadata
	 *		the metadata for the job.  The permitted metadata keys are explained in the
	 *		<a href="https://www.phylo.org/restusers/docs/guide.html#UseOptionalMetadata">Metadata section of the
	 *		User Guide</a>.  The "metadata." prefix can be included in the keys or omitted.
	 *
	 * @return the job
	 * @throws CiCipresException exception
	 *             
	 */
	public CiJob submitJob(
							String tool,
							Map<String, Collection<String>> vParams,
							Map<String, String> inputParams,
							Map<String, String> metadata)
			throws CiCipresException
	{
		return sendJob(tool, vParams, inputParams, metadata, false);
	}


	/**
	 * Validate the job instead of submitting it to run and return the command line that
	 * would be executed, or return a detailed error message in the thrown exception.  This is
	 * a quick, light weight way to verify parameters before submitting the job to run.
	 * Parameters are the same as for {@link #submitJob}.
	 * @param tool			See submitJob
	 * @param vParams		See submitJob
	 * @param inputParams	See submitJob
	 * @param metadata		See submitJob
	 *
	 * @return 
	 *		a special type of job object that lacks most of the usual fields but contains the command
	 *		line that would be run if the job were submitted.
	 * @throws CiCipresException exception.  The exception data gives the specifics of any parameters that
	 * aren't valid, or are missing.
	 *             
	 */
	 
	public CiJob validateJob(
							String tool,
							Map<String, Collection<String>> vParams,
							Map<String, String> inputParams,
							Map<String, String> metadata)
			throws CiCipresException
	{
		return sendJob(tool, vParams, inputParams, metadata, true);
	}

	/*
		Todo: let inputParams be map of String/InputStream and instead of FileDataBodyPart use StreamDataBodyPart
	*/
	private CiJob sendJob(
							String tool,
							Map<String, Collection<String>> vParams,
							Map<String, String> inputParams,
							Map<String, String> metadata,
							boolean validateOnly) 
			throws CiCipresException
	{

		FormDataMultiPart mp = new FormDataMultiPart();
		mp.field("tool", tool);
		for (String input : inputParams.keySet())
		{
			mp.bodyPart(new FileDataBodyPart(prefixProperty(input, "input."), new File(inputParams.get(input))));
		}
		for (String field : vParams.keySet())
		{
			Collection<String> values = vParams.get(field);
			for (String value : values)
			{
				mp.field(prefixProperty(field, "vparam."), value);
			}
		}
		for (String field : metadata.keySet())
		{
			mp.field(prefixProperty(field, "metadata."), metadata.get(field));
		}
		StatusData sd = comm.postToUrl((validateOnly ? jobUrl + "/validate" : jobUrl), mp, StatusData.class);  
		return new CiJob(comm, sd);
	}

	/*
		If property starts with prefix, return it as is; otherwise add prefix
		to property and return that.  For example: 
			"medatadata.statusEmail",   "metadata" ->   "metadata.statusEmail"
			"statusEmail",              "metadata" ->   "metadata.statusEmail"
	*/	
	private String prefixProperty(String property, String prefix)
	{
		if (property.startsWith(prefix))
		{
			return property;
		}
		return prefix + property;
	}


}


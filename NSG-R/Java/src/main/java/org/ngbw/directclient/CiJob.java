
package org.ngbw.directclient; 

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.ngbw.restdatatypes.FileData;
import org.ngbw.restdatatypes.JobMessage;
import org.ngbw.restdatatypes.ResultsData;
import org.ngbw.restdatatypes.StatusData;
import org.ngbw.restdatatypes.WorkingDirData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Represents a submitted CIPRES job.  
 */
public class CiJob 
{
	private static final Logger log = LoggerFactory.getLogger(CiJob.class.getName());

	private CiCommunicator comm;
	private String jobUrl;
	private StatusData sd;

	/* Constructors */


	/**
	 * Instantiates a new ci job.  Not part of the public API.
	 * Clients get CiJob objects from CiClient methods.
	 * This constructor initializes the CiJob by sending a get request to the url
	 * and populating internal fields from the returned StatusData object.
	 *
	 * @param comm
	 *            the comm
	 * @param url
	 *            the url
	 * @throws CiCipresException
	 *             the ci cipres exception
	 */
	CiJob(CiCommunicator comm, String url) throws CiCipresException
	{
		this.comm = comm;
		this.jobUrl = url;
		update();
	}

	/**
	 * Instantiates a new ci job. Not part of the public API.
	 * Clients get CiJob objects from CiClient methods.
	 * This constructor populates internal fields from the StatusData object
	 * it is passed and sets CiJob.jobUrl to StatusData.selfUrl.
	 *
	 * @param comm
	 *            the comm
	 * @param sd
	 *            the sd
	 */
	CiJob(CiCommunicator comm, StatusData sd)
	{
		this.comm = comm;
		this.sd = sd;
		if (this.sd.selfUri != null)
		{
			this.jobUrl = sd.selfUri.url;
		}
	}


	/**
	 * This is a convenience method that prints selected data from the job to stdout.
	 *
	 * @param message
	 *            if message is true, includes all task event messages in the output.  If false,
	 *            task messages are not included in the output.
	 */
	// Debugging method to print the contents of this object to stdout.
	public void show(boolean message)
	{
		if (this.jobUrl == null)
		{
			if (sd.commandline != null)
			{
				System.out.println("Submission validated.  Commandline is: " +  sd.commandline);
			} else
			{
				System.out.println("No jobstatus content retrieved.");
			}
			return;
		} else
		{

			String jobName = null;
			if (sd.metadata != null)
			{
				jobName = sd.metadata.get("clientJobName");
			}

			System.out.println( (jobName == null ? "" : (jobName + ", ")) +
				jobUrl + " , submitted: " + sd.dateSubmitted);
		}
		String str = "";
		if (sd.terminalStage == true)
		{
			if (sd.failed == true)
			{
				str += " Failed at stage " + sd.jobStage;
			} else
			{
				str += " Finished, results at " + sd.resultsUri.url;
			}
		} else
		{
			str += " Not finished, stage = " + sd.jobStage;
		}
		System.out.println(str);
		if (message == true)
		{
			for (JobMessage jm : sd.message)
			{
				System.out.println("\t" + jm.timestamp + ":" + jm.stage + ":" + jm.text);
			}
		}
	}

	/**
	 * Sends a GET request to the job's url to retrieve the current job status.
	 *
	 * @return this object, after its fields have been updated.
	 * @throws CiCipresException exception
	 *            
	 */
	/*
		Get updated jobstatus. Refresh state of this object from cipres service.
	*/
	public CiJob update() throws CiCipresException
	{
		if (this.jobUrl == null)
		{
			return this;
		}
		this.sd = comm.getFromUrl(this.jobUrl, StatusData.class);
		return this;
	}

	/**
	 * Deletes the job.  If the job is scheduled to run, or currently running it will be cancelled 
	 * first.  Then all info and files associated with the job are deleted from CIPRES.
	 *
	 * @throws CiCipresException exception
	 *             
	 */
	public void delete() throws CiCipresException
	{
		comm.deleteUrl(this.jobUrl);
	}


	/**
	 * List the jobs result files.
	 *
	 * @param finalResults
	 *            If true, return result files only if the job is finished and results have been
	 *            copied to the CIPRES database.  If false, return all files in the job's current working
	 *            directory.  The working directory only exists, after the job has been submitted to the 
	 *            execution host and before the results have been copied to the CIPRES database.  Since the 
	 *            working directory can disappear while this method is running, exceptions should be 
	 *            expected when using finalResults=false.
	 * @return a list of the result files
	 * @throws CiCipresException exception
	 *             
	 */
	public Collection<CiResultFile> listResults(boolean finalResults) throws CiCipresException
	{
		String url;
		Collection<FileData> fdlist;
		ArrayList<CiResultFile> files = new ArrayList<CiResultFile>();
		if (finalResults == true)
		{
			url = this.sd.resultsUri.url;
			ResultsData rd  = comm.getFromUrl(url, ResultsData.class);
			fdlist = rd.jobfiles;
		} else
		{
			url = this.sd.workingDirUri.url;
			WorkingDirData wdd  = comm.getFromUrl(url, WorkingDirData.class);
			fdlist = wdd.jobfiles;
		}
		for (FileData fd : fdlist)
		{
			files.add(new CiResultFile(comm, fd));
		}
		return files;
	}


	/**
	 * Download final results or files from the job's working directory while the job is running..
	 *
	 * @param location
	 *            the directory where the results are to be downloaded.  The directory must exist.
	 * @param finalResults
	 *            set finalResults=true if the job is finished, otherwise set finalResults=false
	 *            to download files from the job's working dir, before the job has finished.
	 * @throws CiCipresException exception
	 *             
	 * @throws IOException exception
	 *             
	 */
	public void downloadResults(File location, boolean finalResults) throws CiCipresException, IOException
	{
		Collection<CiResultFile> results = listResults(finalResults);
		for (CiResultFile result : results)
		{
			if (!result.download(location))
			{
				log.error("Unable to download " + result.getName() + " to " + location.getAbsolutePath());
				break;
			}
		}
		
	}

	/**
	 * Checks if job has finished running and results have been transferred to the cipres database.
	 *
	 * @return true, if is done
	 */
	public boolean isDone()
	{
		return sd.terminalStage == true;
	}

	/**
	 * Checks if there was an internal or system error running the job.
	 *
	 * @return true if there was an error (note that false doesn't necessarily mean that the 
	 * job produced valid results, just that CIPRES was able to run the tool on the
	 * specified data without encountering any system or network failures).
	 */
	public boolean isError()
	{
		return sd.failed == true;
	}

	/**
	 * Gets the CIPRES assigned job handle that uniquely identifies the job.
	 *
	 * @return the jobhandle
	 */
	public String getJobHandle()
	{
		return sd.jobHandle;
	}

	/**
		Gets the date the job was submitted to CIPRES.
		@return date submitted.
	*/
	public Date getDateSubmitted() 
	{
		return sd.dateSubmitted;
	}

	/**
		Get the job's processing stage.
		@return stage.
	*/
	public String getJobStage() 
	{
		return sd.jobStage;
	}

	/**
		Get all metadata parameters that were submitted with the job.
		@return metadata or null if no metadata was submitted with the job.
	*/
	public Map<String, String> getMetadata()
	{
		return sd.metadata;
	}

	/**
		Get the client job ID that was submitted via metadata.
		@return client Job ID or null if none was supplied.
	*/
	public String getClientJobID()
	{
		if (sd.metadata != null)
		{
			return sd.metadata.get("clientJobId");
		}
		return null;
	}

	/**
		Get the client job name that was submitted via metadata.
		@return client Job name or null if none was supplied.
	*/
	public String getClientJobName()
	{
		if (sd.metadata != null)
		{
			return sd.metadata.get("clientJobName");
		}
		return null;
	}
}


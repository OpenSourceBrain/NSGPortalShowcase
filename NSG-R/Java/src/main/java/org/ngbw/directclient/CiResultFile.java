
package org.ngbw.directclient; 

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.ngbw.restdatatypes.FileData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Represents a job result file.
 */
public class CiResultFile 
{
	private static final Logger log = LoggerFactory.getLogger(CiResultFile.class.getName());

	private CiCommunicator comm;
	private FileData fd;

	/**
	 * Instantiates a new ci result file.
	 *
	 * @param comm
	 *            the comm
	 * @param fd
	 *            the fd
	 */
	/* Constructors */
	CiResultFile(CiCommunicator comm, FileData fd)
	{
		this.comm = comm;
		this.fd= fd;
	}


	/**
	 * Download the file.
	 *
	 * @param location
	 *            the directory where the file will be written.  The directory must exist before
	 *            this is called.  The file name is supplied by the CIPRES REST API.
	 * @return true, if successful
	 * @throws CiCipresException exception
	 *             
	 * @throws IOException exception
	 *             
	 */
	public boolean download(File location) throws CiCipresException, IOException
	{
		return download(location, null);
	}

	/**
	 * Download and rename the file.
	 *
	 * @param location
	 *            the directory where the file will be written.  The directory must exist before
	 *            this is called.
	 *            
	 * @param targetName
	 *            the file name to create
	 * @return true, if successful
	 * @throws CiCipresException exception
	 *             
	 * @throws IOException exception
	 *             
	 */
	public boolean download(File location, String targetName) throws CiCipresException, IOException
	{
		if (location.isDirectory() == false)
		{
			return false;
		}
		if (targetName == null)
		{
			targetName = fd.filename;
		}
		File target = new File(location, targetName);
		InputStream is = comm.getStreamFromUrl(fd.downloadUri.url);
		return copyInputStreamToFile(is, target);
	}

	/**
	 * Gets the file name.
	 *
	 * @return the name
	 */
	public String getName()
	{
		return fd.filename;
	}

	/**
	 * Gets the file length in bytes.
	 *
	 * @return the length
	 */
	public long getLength()
	{
		return fd.length;
	}

	/**
	 * Returns the file's url.
	 *
	 * @return the url
	 */
	public String getUrl()
	{
		return fd.downloadUri.url;
	}


	/**
	 * Copy input stream to file.  Closes the input stream before returning.
	 *
	 * @param in
	 *            the input stream
	 * @param file
	 *            the file
	 * @return true, if successful
	 */
	public static boolean copyInputStreamToFile(InputStream in, File file) 
	{
		OutputStream out = null;
		try 
		{
			out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while((len=in.read(buf))>0)
			{
				out.write(buf,0,len);
			}
        	out.close();
        	in.close();
			return true;
    	} catch (Exception e) 
		{
			log.error("", e);
			return false;
    	}
		finally
		{
			if (out != null)
			{
        		try{ out.close();} catch (Exception e) {;}
			}
			if (in != null)
			{
        		try{ in.close();} catch (Exception e) {;}
			}
		}
	}
}


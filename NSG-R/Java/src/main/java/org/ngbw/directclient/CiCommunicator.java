package org.ngbw.directclient; 

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.ngbw.restdatatypes.ErrorData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class CiCommunicator has low level methods to get/post/download/delete data
 * from urls in the CIPRES REST API and return objects of types defined in the restdatatypes jar.
 *
 * Many applications will not need to use this class directly and can instead use just the
 * higher level methods in CiClient, CIJob and CIResultFile.
 */
public class CiCommunicator 
{
	private static final Logger log = LoggerFactory.getLogger(CiCommunicator.class.getName());

	Client client;

	/**
	 * Instantiates a new ci communicator for an application that uses DIRECT authentication.
	 * Note that if invalid credentials are provided, this method won't report an error since
	 * this method doesn't communicate with the REST service.  The error will occur when
	 * getFromUrl, deleteUrl, etc are called.
	 *
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @param appkey
	 *            the appkey
	 */
	/* Constructor */
	public CiCommunicator(String username, String password, String appkey)
	{
		this.client = CiClientHelper.getClient(username, password);

		Map<String, String> headers = new HashMap<String, String>(1);
		headers.put("cipres-appkey", appkey);
		client.register(new CiFilter(headers));

	}

	/**
	 * Instantiates a new ci communicator for an application that uses UMBRELLA authentication.
	 * Use a separate CiCommunicator for each end user of the application.
	 * Note that if invalid credentials are provided, this method won't report an error since
	 * this method doesn't communicate with the REST service.  The error will occur when
	 * getFromUrl, deleteUrl, etc are called.
	 *
	 * @param username
	 *            the username of the person who registered the application
	 * @param password
	 *            the password
	 * @param appkey
	 *            the appkey
	 * @param endUserHeaders 
	 *            identify and provide information about the end user of the application. These keys
	 * are required: cipres-eu, cipres-eu-email, cipres-eu-institution, cipres-eu-country.  
	 * See <a href="https://www.phylo.org/restusers/docs/guide.html#UmbrellaApplicationExamples">this section
	 * of the User Guide</a>
	 *				
	 */
	/* Constructor */
	/* Constructor for UMBRELLA apps */
	public CiCommunicator(String username, String password, String appkey, Map<String, String> endUserHeaders)
	{
		this.client = CiClientHelper.getClient(username, password);

		Map<String, String> headers = new HashMap<String, String>(1);
		headers.put("cipres-appkey", appkey);
		for (String key : endUserHeaders.keySet())
		{
			headers.put(key, endUserHeaders.get(key));
		}
		client.register(new CiFilter(headers));
	}


	/**
	 * Delete url.
	 *
	 * @param url
	 *            the url
	 * @throws CiCipresException
	 *             the ci cipres exception
	 */
	public void deleteUrl(String url) throws CiCipresException
	{
		WebTarget target = client.target(url);
		Response response = target.request().delete();

		log.debug("" + response.getStatus());
		if (response.getStatus() != 200 && response.getStatus() != 204)
		{
			throw new CiCipresException(getErrorData(response));
		} 
	}


	/**
	 * Gets a collection of data, wrapped in a GenericType object, the from url.
	 * Use with rest api endpoints that return a collection rather an object.
	 *
	 * @param <T>
	 *            a collection 
	 * @param url
	 *            the url
	 * @param gt
	 *            a GenericType object wrapping the base collection type
	 * @return a collection of objects.
	 * @throws CiCipresException exception
	 *             
	 */
	public <T> T getFromUrl(String url, GenericType<T> gt) throws CiCipresException
	{
		log.debug("getFromUrl " + url);
		WebTarget target = client.target(url);
		Response response = target.request("application/xml").get();

		log.debug("" + response.getStatus());
		if (response.getStatus() != 200)
		{
			throw new CiCipresException(getErrorData(response));
		} else
		{
			return response.readEntity(gt);
		}
	}

	/**
	 * Gets the from url.
	 *
	 * @param <T>
	 *            xml returned by the rest api endpoint is unmarshaled into an object 
	 * @param url
	 *            the url
	 * @param theClass
	 *            the url will return xml that will be unmarshaled to an object of this type.
	 *            The mappings are defined in restdatatypes.jar.
	 * @return the from url
	 * @throws CiCipresException exception
	 *             
	 */
	public <T> T getFromUrl(String url, Class<T> theClass) throws CiCipresException
	{
		log.debug("getFromUrl " + url);
		WebTarget target = client.target(url);
		Response response = target.request("application/xml").get();
		log.debug("" + response.getStatus());
		if (response.getStatus() != 200)
		{
			throw new CiCipresException(getErrorData(response));
		} else
		{
			return response.readEntity(theClass);
		}
	}

	/**
	 * Post to url.
	 *
	 * @param <T>
	 *            xml returned by the rest api endpoint is unmarshaled into an object.
	 * @param url
	 *            the url
	 * @param mp
	 *            form fields, including input file content
	 * @param theClass
	 *            the type of object that the rest api is expected to return
	 * @return an object of type specified by theClass
	 * @throws CiCipresException
	 *             the ci cipres exception
	 */
	public <T> T postToUrl(String url, FormDataMultiPart mp, Class<T> theClass) throws CiCipresException
	{
		WebTarget target = client.target(url);
		Response response = target.request().post(Entity.entity(mp, mp.getMediaType()));

		log.debug("" + response.getStatus());
		if (response.getStatus() != 200)
		{
			throw new CiCipresException(getErrorData(response));
		} else
		{
			return response.readEntity(theClass);
		}
	}

	private int contentLength = 0;
	private String contentDisposition = null;
	private String contentType = null;

	/**
	 * Gets the stream from url.  Used to download files.
	 *
	 * @param url
	 *            the url
	 * @return the stream from url
	 * @throws CiCipresException exception
	 *             
	 */
	public InputStream getStreamFromUrl(String url) throws CiCipresException
	{
		WebTarget target = client.target(url);
		Response response = target.request("application/xml").get();

		log.debug("" + response.getStatus());
		if (response.getStatus() != 200)
		{
			throw new CiCipresException(getErrorData(response));
		} else
		{
			this.contentLength = response.getLength();
			log.debug("Got contentLength = " + this.contentLength);

			this.contentType = response.getMediaType().toString();
			log.debug("Got media type= " + this.contentType);

			this.contentDisposition = response.getHeaderString("content-disposition");
			log.debug("Got content disposition= " + this.contentDisposition); 

			logHeaders(response);

			// Note we call getEntity instead of readEntity to return the InputStream.
			return (InputStream)response.getEntity();
		}
	}


	/* Private methods */

	/*
		This should never return null.
	*/
	private static ErrorData getErrorData(Response response)
	{
		String text = "";
		ErrorData ed = null;
		try
		{
			text = response.readEntity(String.class);
			log.debug("Error Response as text:'" +  text + "'.");
			InputStream is = new ByteArrayInputStream(text.getBytes());

			ClassLoader cl = org.ngbw.restdatatypes.ErrorData.class.getClassLoader();

			JAXBContext ctx = JAXBContext.newInstance("org.ngbw.restdatatypes", cl);
			Object obj = ctx.createUnmarshaller().unmarshal(is);
			if (obj instanceof ErrorData)
			{
				ed = (ErrorData)obj;
			} else
			{
				ed = new ErrorData("Unmarshalled unexpected error response object:" + text, 
					null,	ErrorData.GENERIC_COMM_ERROR);
			}
		}
		catch(javax.xml.bind.UnmarshalException ue)
		{
			//log.debug("Error unmarshalling response body.  Most likely body is not an expected xml type.", ue);
			ed = new ErrorData("Couldn't unmarshal error from server.  Raw error text is: " + text, 
				null, ErrorData.GENERIC_COMM_ERROR);
		}
		catch (Exception e)
		{
			//log.error("", e);
			ed = new ErrorData("Caught exception reading/unmarshalling error response: " + e.toString() +
				". Text received: " + text, null, ErrorData.GENERIC_COMM_ERROR);
		}
		return ed;
	}

	// debugging
	private void logHeaders(Response response)
	{
		for (String key : response.getHeaders().keySet())
		{
			log.debug(key + ":" + response.getHeaderString(key));
		}

	}
}

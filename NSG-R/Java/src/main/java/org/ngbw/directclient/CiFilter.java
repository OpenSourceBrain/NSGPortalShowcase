package org.ngbw.directclient; 

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.DatatypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
	Adds request headers to all requests that are made via the object the filter is registered on.
*/
class CiFilter implements ClientRequestFilter 
{
	private static final Logger log = LoggerFactory.getLogger(CiFilter.class.getName());
    private Map<String, String> extraHeaders;

    public CiFilter(Map<String, String> extraHeaders) 
	{
        this.extraHeaders= extraHeaders;
    }

    public void filter(ClientRequestContext requestContext) throws IOException 
	{
        MultivaluedMap<String, Object> requestHeaders = requestContext.getHeaders();
		for (String key: extraHeaders.keySet())
		{
        	requestHeaders.putSingle(key, extraHeaders.get(key));
		}
	}
	
}	

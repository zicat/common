package org.zicat.common.http.client.test;

import org.apache.http.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class EchoHandler implements HttpRequestHandler{

	@Override
	public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
		
		final String method = request.getRequestLine().getMethod().toUpperCase(Locale.ROOT);
		if (!"GET".equals(method) && !"POST".equals(method) && !"PUT".equals(method)) {
			throw new MethodNotSupportedException(method + " not supported by " + getClass().getName());
		}
		
		Header[] headers = request.getAllHeaders();
		
		StringBuilder sb = new StringBuilder();
		
		for(Header header: headers) {
			sb.append(header.getName() + ":" + header.getValue());
			sb.append("\r\n");
		}
		
		sb.append(request.getRequestLine().getUri());
		
		
		if (request instanceof HttpEntityEnclosingRequest) {
			HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
			sb.append("\r\n");
			sb.append(EntityUtils.toString(entity));
		}
		
		
		sb.append("\r\n");
		sb.append(method);
		
		response.setStatusCode(HttpStatus.SC_OK);
		response.addHeader("application/json;charset", "UTF-8");
		HttpEntity entity = new StringEntity(sb.toString(), StandardCharsets.UTF_8);
		response.setEntity(entity);
	}

}

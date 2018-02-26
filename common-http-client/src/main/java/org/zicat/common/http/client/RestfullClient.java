package org.zicat.common.http.client;

import org.zicat.common.http.client.factory.AbstractClientFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedMap;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by lz31 on 2017/9/21.
 */
public class RestfullClient {

    private AbstractClientFactory factory;

    public RestfullClient(AbstractClientFactory factory) {
    	this.factory = factory;
    }

    
    /**
     *
     * @param requestHandler
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T request(RequestHandler<T> requestHandler) throws Exception {
        return factory.request(requestHandler);
    }

    /**
     *
     * @param client
     * @param target
     * @param path
     * @param headers
     * @param params
     * @return
     */
    public static Invocation.Builder build(Client client, String target, String path, MultivaluedMap<String, Object> headers, MultivaluedMap<String, Object> params) {

        if(target == null)
            throw new NullPointerException("target is null");

        WebTarget webTarget = client.target(target);
        if(path != null)
            webTarget = webTarget.path(path);

        if(params != null) {
            Iterator<Map.Entry<String, List<Object>>> it = params.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, List<Object>> entry = it.next();
                webTarget = webTarget.queryParam(entry.getKey(), entry.getValue().toArray(new Object[entry.getValue().size()]));
            }
        }

        Invocation.Builder builder = webTarget.request();
        if(headers != null)
            builder = builder.headers(headers);
        return builder;
    }

    /**
     *
     * @param target
     * @param path
     * @param headers
     * @param params
     * @param resultClass
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> Future<T> getAsync(String target, String path, MultivaluedMap<String, Object> headers, MultivaluedMap<String, Object> params, Class<T> resultClass) throws Exception {
        return request((Client client) -> {
            Invocation.Builder builder = build(client, target, path, headers, params);
            return builder.async().get(resultClass);
        });
    }
    
    /**
     * 
     * @param target
     * @param path
     * @param headers
     * @param params
     * @param callback
     * @return
     * @throws Exception
     */
    public <T> Future<T> getAsync(String target, String path, MultivaluedMap<String, Object> headers, MultivaluedMap<String, Object> params, InvocationCallback<T> callback) throws Exception {
        return request((Client client) -> {
            Invocation.Builder builder = build(client, target, path, headers, params);
            return builder.async().get(callback);
        });
    }

    /**
     *
     * @param target
     * @param path
     * @param headers
     * @param params
     * @param resultClass
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T get(String target, String path, MultivaluedMap<String, Object> headers, MultivaluedMap<String, Object> params, Class<T> resultClass) throws Exception {

        return request((Client client) -> {
            Invocation.Builder builder = build(client, target, path, headers, params);
            return builder.get(resultClass);
        });
    }

    /**
     *
     * @param target
     * @param path
     * @param headers
     * @param params
     * @return
     * @throws Exception
     */
    public String get(String target, String path, MultivaluedMap<String, Object> headers, MultivaluedMap<String, Object> params) throws Exception {
        return get(target, path, headers, params, String.class);
    }

    /**
     *
     * @param target
     * @param path
     * @param headers
     * @param params
     * @return
     * @throws Exception
     */
    public Future<String> getAsync(String target, String path, MultivaluedMap<String, Object> headers, MultivaluedMap<String, Object> params) throws Exception {
        return getAsync(target, path, headers, params, String.class);
    }
    
    /**
     *
     * @param target
     * @param path
     * @param headers
     * @param params
     * @param entity
     * @param resultClass
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T post(String target, String path, MultivaluedMap<String, Object> headers, MultivaluedMap<String, Object> params, Entity<?> entity, Class<T> resultClass) throws Exception {
        return request(client -> {
            Invocation.Builder builder = build(client, target, path, headers, params);
            return builder.post(entity, resultClass);
        });
    }

    /**
     *
     * @param target
     * @param path
     * @param headers
     * @param params
     * @param entity
     * @param resultClass
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> Future<T> postAsync(String target, String path, MultivaluedMap<String, Object> headers, MultivaluedMap<String, Object> params, Entity<?> entity, Class<T> resultClass) throws Exception {
        return request(client -> {
            Invocation.Builder builder = build(client, target, path, headers, params);
            return builder.async().post(entity, resultClass);
        });
    }
    
    /**
     * 
     * @param target
     * @param path
     * @param headers
     * @param params
     * @param entity
     * @param callback
     * @return
     * @throws Exception
     */
    public <T> Future<T> postAsync(String target, String path, MultivaluedMap<String, Object> headers, MultivaluedMap<String, Object> params, Entity<?> entity, InvocationCallback<T> callback) throws Exception {
        return request(client -> {
            Invocation.Builder builder = build(client, target, path, headers, params);
            return builder.async().post(entity, callback);
        });
    }

    /**
     *
     * @param target
     * @param path
     * @param headers
     * @param params
     * @param entity
     * @return
     */
    public String post(String target, String path, MultivaluedMap<String, Object> headers, MultivaluedMap<String, Object> params, Entity<?> entity) throws Exception {
        return post(target, path, headers, params, entity, String.class);
    }

    /**
     *
     * @param target
     * @param path
     * @param headers
     * @param params
     * @param entity
     * @return
     * @throws Exception
     */
    public Future<String> postAsync(String target, String path, MultivaluedMap<String, Object> headers, MultivaluedMap<String, Object> params, Entity<?> entity) throws Exception {
        return postAsync(target, path, headers, params, entity, String.class);
    }

    /**
     *
     * @param target
     * @param path
     * @param headers
     * @param params
     * @param entity
     * @param resultClass
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T put(String target, String path, MultivaluedMap<String, Object> headers, MultivaluedMap<String, Object> params, Entity<?> entity, Class<T> resultClass) throws Exception {
        return request(client -> {
            Invocation.Builder builder = build(client, target, path, headers, params);
            return builder.put(entity, resultClass);
        });
    }

    /**
     *
     * @param target
     * @param path
     * @param headers
     * @param params
     * @param entity
     * @param resultClass
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> Future<T> putAsync(String target, String path, MultivaluedMap<String, Object> headers, MultivaluedMap<String, Object> params, Entity<?> entity, Class<T> resultClass) throws Exception {
        return request(client -> {
            Invocation.Builder builder = build(client, target, path, headers, params);
            return builder.async().put(entity, resultClass);
        });
    }
    
    /**
     * 
     * @param target
     * @param path
     * @param headers
     * @param params
     * @param entity
     * @param callback
     * @return
     * @throws Exception
     */
    public <T> Future<T> putAsync(String target, String path, MultivaluedMap<String, Object> headers, MultivaluedMap<String, Object> params, Entity<?> entity, InvocationCallback<T> callback) throws Exception {
        return request(client -> {
            Invocation.Builder builder = build(client, target, path, headers, params);
            return builder.async().put(entity, callback);
        });
    }

    /**
     *
     * @param target
     * @param path
     * @param headers
     * @param params
     * @param entity
     * @return
     * @throws Exception
     */
    public String put(String target, String path, MultivaluedMap<String, Object> headers, MultivaluedMap<String, Object> params, Entity<?> entity) throws Exception {
        return put(target, path, headers, params, entity, String.class);
    }

    /**
     *
     * @param target
     * @param path
     * @param headers
     * @param params
     * @param entity
     * @return
     * @throws Exception
     */
    public Future<String> putAsync(String target, String path, MultivaluedMap<String, Object> headers, MultivaluedMap<String, Object> params, Entity<?> entity) throws Exception {
        return putAsync(target, path, headers, params, entity, String.class);
    }
}

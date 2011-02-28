package aharisu.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * 
 * HTTPでGET,POST操作を行うユーティリティクラス
 * 
 * @author aharisu
 *
 */
public final class HttpClient {
	private DefaultHttpClient _client;
	private HttpResponse _res = null;
	
	public HttpClient() {
		_client = new DefaultHttpClient();
	}
	
	public void setBasicAuth(String authscope, String user, String pass) {
		_client.getCredentialsProvider().setCredentials(
				new AuthScope(authscope, 80),
				new UsernamePasswordCredentials(user, pass));
	}
	
	public boolean executeGet(String uri) throws IOException {
		HttpGet get = new HttpGet(uri);
		try {
			_res = _client.execute(get);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		
		return _res.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
	}
	
	public boolean executePost(String uri) throws IOException {
		HttpPost post = new HttpPost(uri);
		_res = _client.execute(post);
		
		return _res.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
	}
	
	public byte[] getContentData() throws IOException{
		if(_res == null) {
			throw new RuntimeException("not setting data yet");
		}
		
		InputStream is = null;
		ByteArrayOutputStream out = null;
		try {
			HttpEntity entity =  _res.getEntity();
			is = entity.getContent();
			
			out = new ByteArrayOutputStream((int)entity.getContentLength());
			byte[] line = new byte[1024];
			int size = 0;
			while(true) {
				size = is.read(line);
				if(size <= 0) {
					break;
				}
				out.write(line, 0, size);
			}
			
			return out.toByteArray();
		}finally {
			if(is != null) {
				try {
					is.close();
				}catch(Exception e) {}
			}
			if(out != null) {
				try {
					out.close();
				}catch(Exception e){}
			}
		}
		
	}
	
	public void reeleaseResponseData() {
		_res = null;
	}
	
	public static byte[] getByteArrayFromURL(String strUrl) throws IOException{
		byte[] result = null;
		HttpURLConnection con = null;
		InputStream in = null;
		ByteArrayOutputStream out = null;
		
		try {
			URL url = new URL(strUrl);
			con = (HttpURLConnection)url.openConnection();
			con.setRequestMethod("GET");
			con.connect();
			in = con.getInputStream();
			
			out = new ByteArrayOutputStream();
			byte[] line = new byte[1024];
			int size = 0;
			while(true) {
				size = in.read(line);
				if(size <= 0) {
					break;
				}
				out.write(line, 0, size);
			}
			result = out.toByteArray();
		} finally {
			if(con != null)
				con.disconnect();
			
			try {
				if(in != null)
					in.close();
			}catch (Exception e) { }
			try {
				if(out != null)
					out.close();
			}catch (Exception e) { }
		}
		
		return result;
	}
}

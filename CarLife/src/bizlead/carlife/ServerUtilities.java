package bizlead.carlife;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.google.android.gcm.GCMRegistrar;

import android.content.Context;
import android.util.Log;
import static bizlead.carlife.CommonUtilities.*;

public final class ServerUtilities {
	private static final int MAX_ATTEMPTS=5;
	private static final int BACKOFF_MILLI_SECONDS=2000;
	private static final Random random=new Random();
	
	
	static boolean register(final Context context, final String regId){
		Log.i(TAG, "端末登録ID="+regId);
		
		String serverUrl=SERVER_URL+DIR_NAME+"/register.php";
		Map<String, String> params=new HashMap<String, String>();
		params.put("regId", regId);
		long backoff=BACKOFF_MILLI_SECONDS+random.nextInt(1000);
		
		for(int i=1; i<=MAX_ATTEMPTS; i++){
			Log.d(TAG, "登録中... #"+i+"to register");
			try
			{
				displayMessage(context, context.getString(R.string.server_registering, i, MAX_ATTEMPTS));
				post(serverUrl, params);
				GCMRegistrar.setRegisteredOnServer(context, true);
				String message=context.getString(R.string.server_registered);
				displayMessage(context, message);
				return true;
				
			} catch(IOException e){
				Log.d(TAG, "Failed to register on attempt "+i, e);
				
				if(i==MAX_ATTEMPTS)
					break;
				
				try {
					Log.d(TAG, "Sleeping for "+backoff+"ms before retry");
					Thread.sleep(backoff);
				} catch (InterruptedException e1) {
					Log.d(TAG, "Thread interrupted: abort remaining retries!");
					Thread.currentThread().interrupt();
					return false;
				}
			backoff *= 2;
			}
		}
		
		
		String message=context.getString(R.string.server_register_error, MAX_ATTEMPTS);
		displayMessage(context, message);
		return false;
	}
	
	static void unregister(final Context context, final String regId){
		Log.i(TAG, "unregistering device(regId="+regId+")");
		
		String serverUrl=SERVER_URL+"/unregister.php";
		Map<String, String>params = new HashMap<String, String>();
		
		params.put("regId", regId);
		try{
			post(serverUrl, params);
			GCMRegistrar.setRegisteredOnServer(context, false);
			String message=context.getString(R.string.server_unregistered);
			displayMessage(context, message);
		} catch (IOException e){
			String message=context.getString(R.string.server_unregister_error, e.getMessage());
			displayMessage(context, message);
		}
	}
	
	
	private static void post(String endpoint, Map<String, String>params)throws IOException{
		URL url;
		try{
			url=new URL(endpoint);
		} catch(MalformedURLException e){
			throw new IllegalArgumentException("invalid url:"+endpoint);
		}
		StringBuilder bodyBuilder=new StringBuilder();
		Iterator<Entry<String, String>>iterator=params.entrySet().iterator();
		
		while(iterator.hasNext()){
			Entry<String, String>param=iterator.next();
			bodyBuilder.append(param.getKey()).append('=').append(param.getValue());
			if(iterator.hasNext())
				bodyBuilder.append('&');
			
		}
		String body=bodyBuilder.toString();
		Log.v(TAG, "Posting'"+body+"'to"+url);
		byte[]bytes=body.getBytes();
		HttpURLConnection conn=null;
		try{
			conn=(HttpURLConnection)url.openConnection();
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setFixedLengthStreamingMode(bytes.length);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			
			OutputStream out=conn.getOutputStream();
			out.write(bytes);
			out.close();
			
			int status=conn.getResponseCode();
			if(status!=200)
				throw new IOException("Post failed with error code "+ status);
		} finally {
			if(conn!= null)	conn.disconnect();
			
		}
	}
}

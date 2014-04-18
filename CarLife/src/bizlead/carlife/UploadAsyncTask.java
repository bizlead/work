package bizlead.carlife;
import java.io.File;
import java.io.IOException;
 

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
 
import org.apache.http.util.EntityUtils;

import static bizlead.carlife.CommonUtilities.*;
import bizlead.carlife.R.string;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.renderscript.ProgramVertexFixedFunction.Constants;
import android.util.Log;
import android.widget.Toast;


@SuppressWarnings( { "deprecation", "unused" } )
public class UploadAsyncTask extends AsyncTask<String , Integer, Integer> {
 
    public Context context;
    private String ReceiveStr;
    
    private HttpClient client = null;
    private HttpPost post = null;
 
    public UploadAsyncTask(Context context)
    {
        this.context = context;
    }
    
 
    @Override
    protected Integer doInBackground(String... params) {
    	
        try {
        	
            String fileName = params[0];
            client = new DefaultHttpClient();
            post = new HttpPost(SERVER_URL + DIR_NAME + UPLOAD_PATH);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            MultipartEntity multipartEntity = new MultipartEntity();
            File file = new File(fileName);
            FileBody filebody=new FileBody(file);
            multipartEntity.addPart("upfile", filebody);
            post.setEntity(multipartEntity);
            ReceiveStr = client.execute(post, responseHandler);
            
        } catch (ClientProtocolException e) {
        	
            e.printStackTrace();
            
        } catch (IOException e) {
        	
            e.printStackTrace();
            
        } finally {
        	
        	client.getConnectionManager().shutdown();
        	
        }
        
        return 0;
    }
 
    
    @Override
    protected void onPostExecute(Integer result)
    {
    	Log.v("ReceiveStr=", ReceiveStr+"です");
        Toast.makeText(context, ReceiveStr+"\n\n=サーバから受け取った文字列\nresult="+result, Toast.LENGTH_LONG).show();
        Log.v("result="+result, "です");
    }
}
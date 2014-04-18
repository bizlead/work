package bizlead.carlife;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import static bizlead.carlife.CommonUtilities.*;

public class History extends Activity implements OnItemClickListener {
	private final static String FILE_NAME="myDir";
	
	private ListView lv_his;
	private int callCount=0;
	private String local_files[], title1[], title2[];
	
	private SharedPreferences sp;
	
	
	// データベース関連
	private SQLiteDatabase db;
	private PersonOpenHelper helper;
	private SimpleCursorAdapter adapter;
	private Cursor c;
	private boolean isEof;
	
	private TextView tv2;
	private Editor editor;

	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		
		helper = new PersonOpenHelper(this);
		db = helper.getWritableDatabase();
		
		c = db.query("PERSON_TB", PER_KEY, null, null, null, null, null);
		isEof = c.moveToFirst();
		
		
		
		
		
		tv2=(TextView)findViewById(R.id.tv_his2);
		
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		
		editor = sp.edit();
		
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		
		callCount = sp.getInt("callCount",0);
		title1 = new String[callCount];
		title2 = new String[callCount];
		
		Log.v("callCount=", callCount+"");
		
		local_files = new String[callCount];
		lv_his = (ListView)findViewById(R.id.lv_his);

		
		for(int i = 0; i < callCount; i++)
		{
			title1[i]=sp.getString("orderList"+i, "該当なし");
			title2[i]=sp.getString("orderList"+i, "なし");
		}
		
		lv_his=EastSys.List2gyou(title1, title2, lv_his, this);
		lv_his.setOnItemClickListener(this);
	}
	
	protected void onPause()
	{
		super.onPause();
	}
	
	
	private void readFile()
	{
		int title=0;
		String str="";

		
		try {
			InputStream in = openFileInput(FILE_NAME);
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

			while((str=br.readLine())!=null)
			{
				Toast.makeText(getApplicationContext(), "str="+str, Toast.LENGTH_SHORT).show();
				if( title == 0 ) title1[title]=str;
				if( title == 2 ) title2[title]=str;
				title++;
			}
			

		} catch(Exception e) {
			
			e.printStackTrace();
			
		}
	}
	

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3)
	{
		startActivity(new Intent(History.this, Hisshot.class));
		editor.putInt("historyPosition", pos);
		editor.commit();
		
	}
}
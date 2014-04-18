package bizlead.carlife;

import static bizlead.carlife.CommonUtilities.*;

import com.google.android.gcm.GCMRegistrar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class Confirmation extends Activity implements OnClickListener, OnItemSelectedListener {

	private String  PER_DATA[]=new String[PER_KEY.length];
	private String cars[], CAR_DATA[]=new String[CAR_KEY.length];

	
	private Button bt_save, bt_back;
	private ListView listView, listView2;
	private Spinner sp_con;
	private CheckBox cb_con_kiyaku;
	ArrayAdapter <String>adapter;
	private SharedPreferences sp;
	
	AsyncTask<Void, Void, Void>asyncTask=null;
	private String regId="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		
		//NULLかチェックする
		checkNotNull("SERVER_URL", SERVER_URL);
		checkNotNull("SENDER_ID", SENDER_ID);
		
		
		//デバイスとマニフェストの確認
		GCMRegistrar.checkDevice(getApplicationContext());
		GCMRegistrar.checkManifest(getApplicationContext());
		
		setContentView(R.layout.activity_confirmation);
		sp=PreferenceManager.getDefaultSharedPreferences(this);
		
		
		//スピナーを生成
		cars=new String[sp.getInt("tabCount", 1)-1];
		for(int i=0; i<cars.length; i++)
			cars[i]=(i+1)+"台目の自動車情報";
		
		sp_con=(Spinner)findViewById(R.id.sp_con);
		sp_con.setOnItemSelectedListener(this);
		adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, cars);
		sp_con.setAdapter(adapter);
		sp_con.setBackgroundColor(Color.CYAN);
	
		
		//リストビューを２つ作成
		listView2=(ListView)findViewById(R.id.listView2);
		
		
		//チェックボックスを生成
		cb_con_kiyaku=(CheckBox)findViewById(R.id.cb_con_kiyaku);
		cb_con_kiyaku.setOnClickListener(this);
		cb_con_kiyaku.setText("以上のご利用規約に同意する");

		
		// ボタンを生成
		bt_save=(Button)findViewById(R.id.bt_save);
		bt_back=(Button)findViewById(R.id.bt_back);
		
		bt_save.setOnClickListener(this);
		bt_back.setOnClickListener(this);
		
		bt_save.setText("保存");
		bt_back.setText("修正");
		
		bt_save.setBackgroundResource(R.drawable.home_setting);
		bt_back.setBackgroundResource(R.drawable.home_setting);
		
		bt_save.setTextColor(Color.WHITE);
		bt_back.setTextColor(Color.WHITE);
		
		bt_save.setTextSize(13);
		bt_back.setTextSize(13);
		
		for(int i=0; i<PER_KEY.length; i++)
			PER_DATA[i]=sp.getString(PER_KEY[i], "");
				
		listView = (ListView)findViewById(R.id.listView1);
        listView = EastSys.List2gyou(PER_KEY, PER_DATA, listView, this);
        listView = EastSys.ListScroll(listView);
        
        getPushId();
        
	}
	
	
	private void getPushId(){
		registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));
		

		//登録済かどうかを判別
		regId=GCMRegistrar.getRegistrationId(getApplicationContext());
		
		if(!regId.equals("")){
			Log.v("登録されていません", "今から登録し直します");
			GCMRegistrar.register(getApplicationContext(), "SENDER_ID");
			Toast.makeText(getApplicationContext(), "regId=="+regId, 0).show();
		} else {
			Log.v("登録されていました", "あとの処理を始めます");
			final Context context=this;
			asyncTask=new AsyncTask<Void, Void, Void>(){
				@Override
				protected Void doInBackground(Void... params){
					boolean registred=ServerUtilities.register(context, regId);
					if( !registred ) GCMRegistrar.unregister(context);
					
					return null;
				}
				@Override
				protected void onPostExecute(Void result){
					asyncTask=null;
				}
			};
			asyncTask.execute(null, null, null);
		}
	}
	

	@Override
	public void onClick(View v)
	{
		if(v==bt_save)
		{
			if(!cb_con_kiyaku.isChecked()) {
				Toast.makeText(getApplicationContext(), "登録するには利用規約の同意が必須です。", Toast.LENGTH_LONG).show();
				return;
			}
			
			startActivity(new Intent(Confirmation.this, Home.class));
			Toast.makeText(getApplicationContext(), "情報を保存しました。", Toast.LENGTH_SHORT).show();
			SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(this);
			Editor editor=sp.edit();
			editor.putBoolean("初回起動", false);
			editor.putBoolean("登録完了", true);
			editor.commit();
			
			exec_post();
		}
		
		
		if(v==bt_back)
		{
			startActivityForResult(new Intent(Confirmation.this, Personal.class), 0);
		}
	}
	
	
	private void exec_post() {
		String urls=SERVER_URL+DIR_NAME+FILE_NAME;
		HttpPostTask task=new HttpPostTask(this, urls,
		
			new HttpPostHandler(){
				@Override
				public void onPostCompleted(String response){
					Toast.makeText(getApplicationContext(), "完了！"+response, Toast.LENGTH_SHORT).show();
				}
				
				@Override
				public void onPostFailed(String response){
					Toast.makeText(getApplicationContext(), "失敗！"+response, Toast.LENGTH_SHORT).show();
				}
			}
		);


		
		for(int i=0; i<PER_KEY.length; i++)
			task.addPostParam(PER_KEY[i], PER_DATA[i]);
		
		for(int i=0; i<CAR_KEY.length; i++)
			task.addPostParam(CAR_KEY[i], CAR_DATA[i]);
		
		
		//レジストリIDも渡す
		task.addPostParam("REG_ID", regId);
		task.execute();
	}


	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		for(int j=0; j<CAR_KEY.length; j++)
			CAR_DATA[j]=sp.getString(CAR_KEY[j]+"_"+arg2, "");
		
		listView2=EastSys.List2gyou(CAR_KEY, CAR_DATA, listView2, this);
		listView2=EastSys.ListScroll(listView2);
		
	}


	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
	}
	
	
	@Override
	protected void onResume(){
		super.onResume();
		Toast.makeText(getApplicationContext(), "regId="+regId, 0).show();
	}
	

	
	@Override
	protected void onDestroy(){
		if(asyncTask!=null) asyncTask.cancel(true);
		
		unregisterReceiver(mHandleMessageReceiver);
		GCMRegistrar.onDestroy(this);
		super.onDestroy();
	}
	
	
	private void checkNotNull(Object reference, String name){
		if(reference==null)
			throw new NullPointerException(getString(R.string.error_config, name));
	}
	
	
	private final BroadcastReceiver mHandleMessageReceiver=new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent){}
	};
}

package bizlead.carlife;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gcm.GCMRegistrar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import static bizlead.carlife.CommonUtilities.*;


@SuppressWarnings( {"deprecation", "unused"} )
public class Transmission extends Activity implements OnClickListener, OnItemSelectedListener {
	
	private final static int BT_COUNT=5, TV_COUNT=6, HYAKU=100, TARGET_WIDTH=300, TARGET_HEIGHT=300;
	private int saveCount, callCount;

	private final static int BT_ID[]=
	{	R.id.bt_trans1,
		R.id.bt_trans2,
		R.id.bt_trans3,
		R.id.bt_trans4,
		R.id.bt_trans6
	};
	
	private final static int TV_ID[]=
	{
		R.id.tv_trans1,
		R.id.tv_trans2,
		R.id.tv_trans3,
		R.id.tv_trans4,
		R.id.tv_trans5,
		R.id.tv_trans6
	};
	
	private final static String BT_NAME[]=
	{
		"場所変更",
		"個人情報変更",
		"車両情報変更",
		"カメラ起動",
		"決定して送信する"
	};
	
	private final static String TV_NAME[]=
	{
		"注文日時",
		"お問い合わせID",
		"レッカーを呼ぶ場所",
		"個人情報を確認",
		"車両情報を確認",
		"事故写真を撮る"
	};
	
	
	private String tabCount[], CAR_KEY_DATA[], PER_KEY_DATA[], times="", local_file="", regId="";
	private SharedPreferences sp;
	private Editor editor;
	
	private ImageView iv_camera;
	private Button bt_trans[]=new Button[BT_COUNT];
	private TextView tv_trans[]=new TextView[TV_COUNT];
	private TextView orderDate, orderId, addressData;
	private ListView lv_per, lv_car;
	private Spinner spinner;
	private Uri mSaveUri, pictureUri;
	private Bitmap picture;

	private boolean imageupsita=false;
	
	ArrayAdapter<String>adapter;
	AsyncTask<Void, Void, Void>asyncTask=null;
	UploadAsyncTask uptask;
	
	private PersonOpenHelper hlpr=null;
	private SQLiteDatabase db=null;
	private Cursor c=null;

	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		
		//写真ディレクトリ用パス設定
		String strDirPath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/download/";
		
		
		//NULLかチェックする
		checkNotNull(SERVER_URL, SERVER_URL);
		checkNotNull(SENDER_ID, SENDER_ID);
		
		
		//デバイスとマニフェストの確認
		GCMRegistrar.checkDevice(getApplicationContext());
		GCMRegistrar.checkManifest(getApplicationContext());
		
		
		//レイアウト配置
		setContentView(R.layout.activity_transmission);
		
		
		//端末レジストリIDを生成
		getPushId();
		
		
		//プリファレンスを生成
		sp=PreferenceManager.getDefaultSharedPreferences(this);
		editor=sp.edit();
		
		
		//スピナーを生成
		spinner=(Spinner)findViewById(R.id.sp_trans11);
		spinner.setOnItemSelectedListener(this);
		tabCount=new String[sp.getInt("tabCount", 2)-1];
		
		
		for(int i=0; i<tabCount.length; i++)
			tabCount[i]=(i+1)+"台目の自動車で呼ぶ";

		
		adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, tabCount);
		spinner.setAdapter(adapter);
		spinner.setBackgroundColor(Color.CYAN);
		
		
		//保存されたデータ数を配列に入れる
		PER_KEY_DATA=new String[7];
		CAR_KEY_DATA=new String[14];

		
		//保存されたデータを配列に入れる
		for(int i=0; i<PER_KEY_DATA.length; i++)
			PER_KEY_DATA[i]=sp.getString(PER_KEY[i], "");
		
		for(int i=0; i<CAR_KEY_DATA.length; i++)
			CAR_KEY_DATA[i]=sp.getString(CAR_KEY[i], "");
	
		
		//個人情報のリストビューを生成
		lv_per=(ListView)findViewById(R.id.lv_per);
		lv_per=EastSys.List2gyou(PER_KEY, PER_KEY_DATA, lv_per, this);
		lv_per=EastSys.ListScroll(lv_per);
		
		
		//車両情報のリストビューを生成
		lv_car=(ListView)findViewById(R.id.lv_car);

		
		//注文日時を取得
		orderDate=(TextView)findViewById(R.id.order_date);
		orderDate.setText(timeData().toString());
		
		
		//注文IDを取得
		orderId=(TextView)findViewById(R.id.order_id);
		orderId.setText(""+(int)((Math.random()*100)));
		
		
		//位置情報を取得
		addressData=(TextView)findViewById(R.id.address1);
		addressData.setText(sp.getString("getAddress", "住所が選択されていません").toString());

		
		//確認ボタン生成
		for(int i=0; i<BT_COUNT; i++)
		{
			bt_trans[i]=(Button)findViewById(BT_ID[i]);
			bt_trans[i].setOnClickListener(this);
			bt_trans[i].setText(BT_NAME[i]);
			bt_trans[i].setBackgroundResource(R.drawable.home_reca);
			bt_trans[i].setTextColor(Color.WHITE);
			bt_trans[i].setTextSize(13);
			bt_trans[i].setWidth(400);
		}
		bt_trans[BT_COUNT-1].setBackgroundResource(R.drawable.home_setting);
		
		
		//テキストビューのタイトルの設定
		for(int i=0; i<TV_COUNT; i++)
		{
			tv_trans[i]=(TextView)findViewById(TV_ID[i]);
			tv_trans[i].setText(TV_NAME[i]);
			tv_trans[i].setTextSize(16);
			tv_trans[i].setTypeface(Typeface.DEFAULT_BOLD);
		}
		
		
		//イメージビューの生成
		iv_camera=(ImageView)findViewById(R.id.iv_camera);
	}

	
	@Override
	public void onClick(View v)
	{
		if(v==bt_trans[0]) //地図確認
			finish();
		
		
		if(v==bt_trans[1]) //個人情報変更
			startActivityForResult(new Intent(Transmission.this, Personal.class), 0);

		
		if(v==bt_trans[2]) //車両情報変更
			startActivityForResult(new Intent(Transmission.this, Car.class), 0);

		
		if(v==bt_trans[3]) //カメラ起動
		{
			imageupsita=false;
			ContentValues values=new ContentValues();
			values.put(MediaStore.Images.Media.TITLE, System.currentTimeMillis()+".jpg");
			values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
			pictureUri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			
			Intent intent=new Intent();
			intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
			startActivityForResult(intent, HYAKU);
			imageupsita = true;
		}
		
		if(v==bt_trans[4]) { //決定送信
			new AlertDialog.Builder(this)
			.setTitle("場所の再確認")
			.setMessage(addressData.getText().toString()+"にレッカーを手配します、よろしいですか？")
			.setPositiveButton("呼ぶ", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					editor.putBoolean("呼び出し中", true);
					editor.commit();
					
					
					// サーバに画像をアップロード
					if( imageupsita ) {
						uptask = new UploadAsyncTask( Transmission.this );
						uptask.context = Transmission.this;
						String filePath = getPathUri(getApplicationContext(), pictureUri);
						uptask.execute(filePath);
						imageupsita = false;
					}
					
					exec_post();
					SaveDatabase();
					
					Toast.makeText(getApplicationContext(), "情報を送信しました、しばらくその場で待機してください。", Toast.LENGTH_SHORT).show();
					c.close();
					db.close();
					
					startActivity(new Intent(Transmission.this, Home.class));
					finish();

				}
				
			}).setNegativeButton("確認し直す", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which){
					return;
				}
			})
			.create()
			.show();
		}
	}
	
	
	//カメラを起動＞撮った写真をImageViewに貼り付け
	protected void onActivityResult(int requestCode,int resultCode,Intent data) {
		if( requestCode == HYAKU && resultCode == Activity.RESULT_OK ) {
			iv_camera.setImageBitmap( ( Bitmap )data.getExtras().get( "data" ) );
		}
	}

	
	//注文日時を取得
	protected String timeData()
	{
		Time time=new Time("Asia/Tokyo");
		time.setToNow();
		times=time.year+"年 "+(time.month+1)+"月 "+time.monthDay+"日 "+time.hour+"時 "+time.minute+"分 ";

		return times;
	}
	

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {		
		for(int i=0; i<CAR_KEY.length; i++)
			CAR_KEY_DATA[i]=sp.getString(CAR_KEY[i]+"_"+arg2, "");
		
		lv_car=EastSys.List2gyou(CAR_KEY, CAR_KEY_DATA, lv_car, this);
		lv_car=EastSys.ListScroll(lv_car);
	}


	@Override
	public void onNothingSelected(AdapterView<?> arg0) {}
	
	
	private void exec_post() {
		String urls=SERVER_URL+DIR_NAME+FILE_NAME;
		HttpPostTask task=new HttpPostTask(this, urls,
			
			new HttpPostHandler(){
				@Override
				public void onPostCompleted(String response){
					Toast.makeText(getApplicationContext(), "完了！", Toast.LENGTH_SHORT).show();
					Log.v("response="+response, "aaa");
				}
				
				@Override
				public void onPostFailed(String response){
					Toast.makeText(getApplicationContext(), "失敗！"+response, Toast.LENGTH_SHORT).show();
				}
			}
		);
		
		
		//データを渡す
		for(int i=0; i<PER_KEY.length; i++)
			task.addPostParam(PER_KEY[i], PER_KEY_DATA[i]);
		for(int i=0; i<CAR_KEY.length; i++)
			task.addPostParam(CAR_KEY[i], CAR_KEY_DATA[i]);
		
		
		//レジストリIDも渡す
		task.addPostParam("REG_ID", regId);
		task.addPostParam("orderId", orderId.getText().toString());
		task.execute();
	}
	
	
	private void getPushId(){
		registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));
		

		//登録済かどうかを判別
		final String regId = GCMRegistrar.getRegistrationId(getApplicationContext());
		Log.v("regId="+regId, "あああ");
		if( regId.equals("") ) {
			GCMRegistrar.register(Transmission.this, SENDER_ID);
		} else {
			final Context context = this;
			asyncTask = new AsyncTask<Void, Void, Void>(){
				@Override
				protected Void doInBackground(Void... params){
					boolean registred = ServerUtilities.register(context, regId);
					if (!registred) GCMRegistrar.unregister(context);
					return null;
				}
				@Override
				protected void onPostExecute( Void result ){
					asyncTask = null;
				}
			};
			asyncTask.execute(null, null, null);
		}
	}
	
	
	@Override
	protected void onDestroy(){
		if(asyncTask!=null) asyncTask.cancel(true);
		unregisterReceiver(mHandleMessageReceiver);
		GCMRegistrar.onDestroy(this);
		super.onDestroy();
	}
	
	
	private void checkNotNull(Object reference, String name){
		if(reference==null){
			throw new NullPointerException(getString(R.string.error_config, name));
		}
	}
	
	
	/**
	 * UriからPathへの変換処理
	 * @param uri
	 * @return String
	 */
	public static String getPathUri(Context context, Uri uri) {
	    ContentResolver contentResolver = context.getContentResolver();
	    String[] columns = { MediaStore.Images.Media.DATA };
	    Cursor cursor = contentResolver.query(uri, columns, null, null, null);
	    cursor.moveToFirst();
	    String path = cursor.getString(0);
	    cursor.close();
	    return path;
	}
	
	
	private void SaveDatabase(){
		hlpr = new PersonOpenHelper(this);
		db = hlpr.getWritableDatabase();
		c = db.rawQuery("SELECT * FROM "+PERSON_TB, null);
		
		ContentValues val = new ContentValues();
		
		for(int i=0; i<PER_KEY.length; i++)
			val.put(PER_KEY_DB[i], PER_KEY_DATA[i]);
		for(int i=0; i<CAR_KEY.length; i++)
			val.put(CAR_KEY_DB[i], CAR_KEY_DATA[i]);
		
		val.put(HISTORY_TITLE[0], addressData.getText().toString());
		val.put(HISTORY_TITLE[1], timeData());
		
		db.insert(PERSON_TB, null, val);
	}
	
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent){
		}
	};
}
package bizlead.carlife;

import java.io.ByteArrayOutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public final class Go extends FragmentActivity implements OnClickListener, OnCameraChangeListener, OnMapLoadedCallback {
	
	
	//GoogleMapsの宣言
	private GoogleMap gm;
	private LocationManager locationManager;
	private Location location;
	private CameraPosition cameraPosition;
	
	
	//装飾品の宣言
	TextView tv_address;
	private Button bt_call, bt_move;
	
	
	//保存用の宣言
	private SharedPreferences sp;
	private Editor editor;
	
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_go);
		
		
		//Google Play Servicesが使えるかどうかのステータス
        if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext())==ConnectionResult.SUCCESS){ 

        	
        	//GoogleMapsの生成
        	gm=((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        	
        	
        	//現在地ボタンを有効化
        	gm.setMyLocationEnabled(true);
        	
        	
        	//建物内の表記を無効化
        	gm.setIndoorEnabled(false);
        	
        	
        	//渋滞状況を無効化
        	gm.setTrafficEnabled(false);
        	
        	
        	//パディング設定
        	gm.setPadding(20,20,20,20);
        	
        	
        	//設定の取得
        	UiSettings settings = gm.getUiSettings();
        	
        	
        	//コンパスの無効化
        	settings.setCompassEnabled(false);

        	
        	//回転ジェスチャーの無効化
        	settings.setRotateGesturesEnabled(false);
        	
        	
        	//立体表示の無効化
        	settings.setTiltGesturesEnabled(false);
        	
        	
        	//現在地ボタンを非表示にする
        	settings.setMyLocationButtonEnabled(false);
        	
        	
        	//拡大縮小ボタンの無効化
        	settings.setZoomControlsEnabled(false);
        	
        	
        	//ピンチアウト・インのズーム有効化
        	settings.setZoomGesturesEnabled(true);
        	
        	
        	//カメラポジションを生成
        	CameraPosition.Builder builder=new CameraPosition.Builder().bearing(0).tilt(0).zoom(17.0f);
        	
        	
        	//他のアプリとかで測位された場合にその位置情報を流用する
        	locationManager=(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
			
        	
        	//ロケーションの追加
			location=locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
			
			
			//復帰すべき初期位置が不明な場合
			if (location == null)
			    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			
			
			//位置調整
			if (location != null)
			    builder.target(new LatLng(location.getLatitude(), location.getLongitude()));
			
        	
        	//GPSがOFFの場合、設定画面を表示させるかアラートを表示させる
			if(!locationManager.getBestProvider(new Criteria(), true).equals("gps")){
				new AlertDialog.Builder(this)
				.setTitle("現在位置機能を改善")
				.setMessage("現在、位地情報は一部有効ではないものがあります")
				.setPositiveButton("設定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
					}
				})
				.setNegativeButton("スキップ", new DialogInterface.OnClickListener(){
					@Override public void onClick(DialogInterface dialog, int which){}
				})
				.create()
				.show();
			}
			
			
			//カメラポジションを移動
			gm.moveCamera(CameraUpdateFactory.newCameraPosition(builder.build()));

			
        	//カメラチェンジリスナーを追加
        	gm.setOnCameraChangeListener(this);
        	
        	
        	//読み込み完了後のリスナーを追加
        	gm.setOnMapLoadedCallback(this);
        }
        
        
        //部品を生成
        tv_address=(TextView)findViewById(R.id.tv_address);
        bt_call=(Button)findViewById(R.id.bt_call);
        bt_move=(Button)findViewById(R.id.bt_move);
        
        tv_address.setAlpha(0.9f);
        bt_call.setAlpha(0.9f);
        bt_move.setAlpha(0.9f);
        
        bt_call.setOnClickListener(this);
        bt_move.setOnClickListener(this);
	}
	
	
	@Override
	protected void onResume(){
		super.onResume();
		
		gm.setOnCameraChangeListener(new OnCameraChangeListener(){
			@Override
			public void onCameraChange(CameraPosition position) {
				LatLng point=position.target;
				reverse_geocode(point.latitude,point.longitude);
			}
		});
	}
	
	
	@Override
	protected void onStop(){
		super.onStop();
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		finish();
	}
	

	@Override
	public void onClick(View v) {
		if(v==bt_call){
			sp=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			editor=sp.edit();
			editor.putString("getAddress", tv_address.getText().toString());
			editor.commit();
			startActivity(new Intent(Go.this, Transmission.class));
		}
		
		if(v==bt_move){
			myLocationButtonClick();
		}
	}

	
	@Override
	public void onCameraChange(CameraPosition position) {
		reverse_geocode(0.5f, 0.5f);
	}
	
	
	public void myLocationButtonClick(){
		location=gm.getMyLocation();
    	cameraPosition=new CameraPosition.Builder().target
    			(new LatLng(location.getLatitude(), location.getLongitude())).bearing(0).tilt(0).zoom(17.0f).build();
		gm.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}

	
	@Override
	public void onMapLoaded(){
		Toast.makeText(getApplicationContext(), "読み込まれました", Toast.LENGTH_LONG).show();
	}


	/**
	 * 
	 * 住所日本語化メソッド 逆ジオコーディング ここはいじれない。
	 * 
	 */
	private void reverse_geocode(double lat,double lon){
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

		String res="";
		HttpResponse httpResponse = null;
		
	    HttpClient httpClient = new DefaultHttpClient();
	    String uri = "http://maps.googleapis.com/maps/api/geocode/json" +
	        "?latlng=" + lat + "," + lon + "&sensor=true&language=ja";
	    HttpGet request = new HttpGet(uri.toString());
	    
	    try {
	        httpResponse = httpClient.execute(request);
	        
	    } catch (Exception e) {
	    	
	        Log.d("HttpSampleActivity", "Execute エラー");
	    }
	    
	    int status = httpResponse.getStatusLine().getStatusCode();
	    if (HttpStatus.SC_OK == status) {
	        try {
	            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	            httpResponse.getEntity().writeTo(outputStream);
	            try {
	                res = "";
	                JSONObject rootObject = new JSONObject(outputStream.toString());
	                JSONArray eventArray = rootObject.getJSONArray("results");
	                for (int i = 0; i < eventArray.length(); i++) {
	                    JSONObject jsonObject = eventArray.getJSONObject(i);
	                    res = jsonObject.getString("formatted_address");
	                    if (!res.equals("")){
	                        Log.d("Address",res);
	                        
	                        	//文字列『日本, 』と『〒###-####』を削除
	                        	res=res.replaceAll("日本, ", "");
	                        	int postnum = res.indexOf('〒');
	                        	if(postnum != -1)
	                        		res=res.substring(9, res.length());
	                            tv_address.setText(res);
	                        break;
	                    }else{
	                    	tv_address.setHint("現在地が取得できません");
	                    }
	                }
	            } catch (JSONException e) {
	                e.printStackTrace();
	            }       
	        } catch (Exception e) {
	            Log.d("HttpSampleActivity", "エラー");
	        }
	    } else {
	        Log.d("HttpSampleActivity", "Status" + status);
	    }
	}	
}
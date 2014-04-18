package bizlead.carlife;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Home extends Activity implements OnClickListener {
	private static final int HOME_COUNT=5;
	
	
	private static final String[]HOME_TITLE=
		{"レッカーを呼ぶ","設定","履歴","電話",""};
	
	private static final int HOME_TITLE_ID[] =
		{R.id.homeBtId1, R.id.homeBtId2, R.id.homeBtId3,
		 R.id.homeBtId4, R.id.homeBtId5};
	
	private static final int HOME_ICON_COLOR[] =
		{R.drawable.home_reca, R.drawable.home_setting, R.drawable.home_time,
		 R.drawable.home_phone, R.drawable.home_resit};
	
	private Button bt[]=new Button[HOME_COUNT];
	
	
	//色をアニメーションするために
	private LinearLayout ll_home;
	private AlphaAnimation anim;
	private Button bt_home_title;
	private TextView tv_home_title;
	private ImageView iv_home_title;
	
	private SharedPreferences sp;
	
	private AlertDialog.Builder alert = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_home);
		
		
		//保存の装飾
		sp=PreferenceManager.getDefaultSharedPreferences(this);
		final Editor editor=sp.edit();
		
		
		//ボタンを生成
		for(int i=0; i<HOME_COUNT; i++)
		{
			bt[i]=(Button)findViewById(HOME_TITLE_ID[i]);
			bt[i].setOnClickListener(this);
			bt[i].setText(HOME_TITLE[i]);
			bt[i].setTextColor(Color.WHITE);
			bt[i].setBackgroundResource(HOME_ICON_COLOR[i]);
		}
		
		
		//透明色アニメーションの生成
		anim=new AlphaAnimation(1.0f,0.5f);
		
		
		//タイトルバーの装飾
		bt_home_title=(Button)findViewById(R.id.bt_home_title);
		ll_home=(LinearLayout)findViewById(R.id.ll_home_title);
		tv_home_title=(TextView)findViewById(R.id.tv_home_title);
		iv_home_title=(ImageView)findViewById(R.id.iv_home_title);
		bt_home_title.setOnClickListener(this);

		
		//呼び出し中なら、タイトルバー点滅
		if(sp.getBoolean("呼び出し中", true))
		{
				
			//アニメーションの速度を指定(ms)
			anim.setDuration(500);
			
			
			//アニメーションのリピート回数を指定
			anim.setRepeatCount(Animation.INFINITE);
			
			
			//アニメーションのリピートモードを指定
			anim.setRepeatMode(Animation.REVERSE);
			
			
			//リニアレイアウトにアニメーションを載せる
			this.ll_home.startAnimation(anim);
			
			
			// アニメーション後は元の状態に戻す
			anim.setFillBefore( true );
			
			
			// アニメーション後も状態を維持する
			anim.setFillAfter( true );
			
			
			// 繰り返し時の初期状態を上記として適用する
			anim.setFillEnabled( true );
			
			
			// アニメーションリスナを生成
			anim.setAnimationListener(new Animation.AnimationListener() {
			
				@Override
				public void onAnimationStart(Animation animation) {}
				
				@Override
				public void onAnimationRepeat(Animation animation) {}
				
				@Override
				public void onAnimationEnd(Animation animation) {}
			});
		
			
			//アニメーションの実行
			tv_home_title.setText("レッカーを現場に手配中です!!");
			bt_home_title.setVisibility(View.VISIBLE);
			iv_home_title.setImageResource(R.drawable.warning);
			iv_home_title.setScaleType(ImageView.ScaleType.FIT_START);
			bt[HOME_COUNT-1].setText("通知を停止");
			
		
		//呼び出し中で無ければ、アニメーションは無効
		} else {
			if(anim!=null) {
				anim.cancel();
				anim=null;
			}
			tv_home_title.setText("CarLife");
			bt_home_title.setVisibility(View.INVISIBLE);
			iv_home_title.setImageResource(R.drawable.carlife5);
			iv_home_title.setScaleType(ImageView.ScaleType.FIT_START);
			bt[HOME_COUNT-1].setText("");
		}
		
		
		//初回起動時に実行
		if(sp.getBoolean("初回起動", true)) {	
			new AlertDialog.Builder(Home.this)
			.setTitle("カーライフアプリケーションへようこそ！")
			.setMessage("現在、お客様の情報が未登録です。")
			.setPositiveButton("登録", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					startActivityForResult(new Intent(Home.this, Personal.class), 0);
				}
			})
			
			.setNegativeButton("スキップ", new DialogInterface.OnClickListener() {
				@Override public void onClick (DialogInterface dialog, int which) {}
			})
			
			.create()
			.show();
			
			editor.putBoolean("初回起動", false);
			editor.commit();
		}
	}
	
	
	@Override
	protected void onResume(){
		super.onResume();
	}


	@SuppressLint("NewApi")
	@Override
	public void onClick(View v)
	{

		if(v==bt[0])//レッカーを呼ぶ
		{
			if(sp.getBoolean("登録完了", false))
			{
				startActivity(new Intent(Home.this, Go.class));
				
			} else {
				
				alert = new AlertDialog.Builder(this);
				alert
				.setTitle("レッカーを呼びますか？")
				.setMessage("現在、お客様の情報が未登録です。\n未登録の状態では、取引がスムーズにいかない場合があります。")
				
				.setPositiveButton("登録", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						startActivityForResult(new Intent(Home.this, Personal.class), 0);
					}
				})
				
				.setNegativeButton("登録しないでレッカーを呼ぶ", new DialogInterface.OnClickListener(){
					@Override public void onClick(DialogInterface dialog, int which){
						startActivityForResult(new Intent(Home.this, Go.class), 0);
					}
				})
				.create()
				.show();
			}
		}
		
		if(v==bt[1]) //設定
		{
			startActivity(new Intent(Home.this, Setting.class));
		}
		
		if(v==bt[2]) //履歴
		{
			startActivity(new Intent(Home.this, History2.class));
		}
		
		if(v==bt[3]) //電話
		{
			startActivity(new Intent(Home.this, Hisshot.class));
		}
		
		if(v==bt[4]) //通知を停止
		{
			if(anim!=null) anim.cancel();
			ll_home.setBackgroundColor(Color.argb(255,255,10,10));
			ll_home.setAlpha(10.0f);
			tv_home_title.setText("手配をキャンセルしました");
			iv_home_title.setImageResource(R.drawable.carlife5);
		}
		
		if(v==bt_home_title)//通知領域
		{
			startActivity(new Intent(Home.this, History.class));
		}
	}
	
	
	@Override
	protected void onPause(){
		super.onPause();
		Log.v("onPause","が読み込まれました");
	}
	
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		Log.v("onDestroy","が読み込まれました");
	}
	
	
	@Override
	protected void onStart(){
		super.onStart();
		Log.v("onStart","が読み込まれました");
	}
	
	
	@Override
	protected void onRestart(){
		super.onRestart();
		Log.v("onRestart","が読み込まれました");
	}
	
	
	@Override
	protected void onStop(){
		super.onStop();		
		Log.v("onStop","が読み込まれました");
		
		sp=PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor=sp.edit();
		editor.putBoolean("呼び出し中", false);
		editor.commit();
	}
}
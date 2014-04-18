package bizlead.carlife;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import static bizlead.carlife.CommonUtilities.*;

public class Car extends Activity implements OnClickListener, ActionBar.TabListener {
	private final static int ET_COUNT=14;
	private final static int TV_COUNT=14;
		
	private final static int ET_ID[]=
	{
		R.id.et_car1, R.id.et_car2, R.id.et_car3, R.id.et_car4, R.id.et_car5,R.id.et_car6, R.id.et_car7,
		 R.id.et_car8, R.id.et_car9, R.id.et_car10, R.id.et_car11, R.id.et_car12, R.id.et_car13, R.id.et_car14
	};
	
	private final static int TV_ID[]=
	{
		R.id.tv_car1, R.id.tv_car2, R.id.tv_car3, R.id.tv_car4, R.id.tv_car5, R.id.tv_car6, R.id.tv_car7,
		R.id.tv_car8, R.id.tv_car9, R.id.tv_car10, R.id.tv_car11, R.id.tv_car12, R.id.tv_car13, R.id.tv_car14
	};
	
	private EditText et_car[]=new EditText[ET_COUNT];
	private TextView tv_car[]=new TextView[TV_COUNT];

	private Button next, clear, delete;
	
	private ActionBar ab=null;
	int tabCount=0, tabPosition=0, tabPrev=0;
	
	SharedPreferences pref;
	Editor editor;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_car);
		
		
		//保存するためのプリファレンスを生成
		pref=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		
		//エディットテキストを生成
		for(int i=0; i<ET_COUNT; i++)
		{
			et_car[i]=(EditText)findViewById(ET_ID[i]);
			et_car[i].setHint(CAR_HINT[i]);
			et_car[i].setBackgroundColor(Color.WHITE);
			et_car[i].setTextSize(15);
			et_car[i].setHintTextColor(Color.rgb(222, 222, 222));
			et_car[i].setInputType(InputType.TYPE_CLASS_TEXT);
		}
		for(int i=0; i<CAR_KEY.length; i++)
			et_car[i].setText(pref.getString(CAR_KEY[i]+"_"+tabPosition, ""));
		
		
		//テキストビューを生成
		for(int i=0; i<TV_COUNT; i++)
		{
			tv_car[i]=(TextView)findViewById(TV_ID[i]);
			tv_car[i].setText(CAR_KEY[i]);
			tv_car[i].setTextSize(13);
			tv_car[i].setTextColor(Color.GRAY);
		}
		
		
		//ActionBarを生成
		ab=getActionBar();
		
		for(int i=0; i<(pref.getInt("tabCount", 1)-1); i++){
			if(pref.getString(CAR_KEY[5]+"_"+i, "").equals("")){
				ab.addTab(ab.newTab().setText((i+1)+"台目の車").setTabListener(this));
			} else {
				ab.addTab(ab.newTab().setText(pref.getString(CAR_KEY[5]+"_"+i, "")).setTabListener(this));
			}
		}
		ab.addTab(ab.newTab().setText("車両を追加する").setTabListener(this));
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		

		//ボタンを生成
		next=(Button)findViewById(R.id.bt_car_next);
		clear=(Button)findViewById(R.id.bt_car_clear);
		delete=(Button)findViewById(R.id.bt_car_delete);
		
		next.setOnClickListener(this);
		clear.setOnClickListener(this);
		delete.setOnClickListener(this);
		
		if(getCallingActivity().getClassName().toString().equals("bizlead.carlife.Transmission")){
			next.setText("決定");
		} else {
			next.setText("次へ");
		}
		
		clear.setText("全て書き直す");
		delete.setText("削除");
		
		next.setBackgroundResource(R.drawable.home_setting);
		clear.setBackgroundResource(R.drawable.home_setting);
		delete.setBackgroundResource(R.drawable.home_setting);
		
		next.setTextColor(Color.WHITE);
		clear.setTextColor(Color.WHITE);
		delete.setTextColor(Color.WHITE);
		
		next.setTextSize(15);
		clear.setTextSize(15);
		delete.setTextSize(15);
	}
	
	@Override
	protected void onResume(){
		super.onResume();
	}
	
	
	private void save()
	{

		
		//プリファレンスを生成
		pref=PreferenceManager.getDefaultSharedPreferences(this);
		editor=pref.edit();

		
		//名前保存
		for(int i=0; i<CAR_KEY.length; i++)
			editor.putString(CAR_KEY[i]+"_"+tabPosition, et_car[i].getText().toString());

		
		//アクションバーのタブの数と、項目数を保存
		editor.putInt("tabCount", tabCount);
		editor.putInt("carCount", 14);
		editor.commit();
	}

	
	@Override
	public void onClick(View v)
	{
		
		//次へのボタン
		if(v==next)
		{
			save();
			if(getCallingActivity().getClassName().toString().equals("bizlead.carlife.Transmission")){
				startActivity(new Intent(Car.this, Transmission.class));
				finish();
			} else {
				startActivity(new Intent(Car.this, Confirmation.class));
			}
		}
		
		
		//全て消去ボタン
		if(v==clear)
			for(int i=0; i<ET_COUNT; i++)
				et_car[i].setText("");
		
		
		//データ削除ボタン
		if(v==delete){
			if(tabPosition!=tabCount-1){
				for(int j=0; j<CAR_KEY.length; j++){
					et_car[j].setText("");
					pref.edit().remove(CAR_KEY[j]+"_"+tabPosition).commit();
				}
				
				ab.removeTabAt(tabPosition);
			}
		}
	}


	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {

		
		//タブ切り替えした時でも保存できるようにする
		pref=PreferenceManager.getDefaultSharedPreferences(this);
		editor=pref.edit();
		
		
		//タブの数と、タブの配置位置を取得
		tabPosition=ab.getSelectedNavigationIndex();
		tabCount=ab.getTabCount();

		
		//名前保存
		for(int i=0; i<CAR_KEY.length; i++)
			editor.putString(CAR_KEY[i]+"_"+tabPrev, et_car[i].getText().toString());
		editor.commit();
		
		
		//車両追加ボタンを押したら追加
		if(tabPosition==(ab.getTabCount()-1)) {
			
			
			//名前保存
			for(int i=0; i<CAR_KEY.length; i++)
				editor.putString(CAR_KEY[i]+"_"+tabPrev, et_car[i].getText().toString());
			editor.commit();
			
			
			//タブ追加
			ab.addTab(ab.newTab().setText((tabPosition+1)+"台目の車").setTabListener(this), tabCount-1, true);
			
			
			//テキスト内容をクリア
			for(int j=0; j<CAR_KEY.length; j++)
				et_car[j].setText("");
			
			
		//それ以外ボタンを押したら
		} else {

			
			//保存された文字列を表示
			for(int j=0; j<CAR_KEY.length; j++)
				et_car[j].setText(pref.getString(CAR_KEY[j]+"_"+tabPosition, ""));
		}
	}


	//他のタブに切り替えた時（離れた時）に実行される
	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {

		
		//前回選択されていたタブポジションを取得
		if(tabPosition!=(ab.getTabCount()-1))
			tabPrev=ab.getSelectedNavigationIndex();
	}

	
	//選択されているタブをタップすると実行される
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		
	}
}
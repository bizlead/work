package bizlead.carlife;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Setting extends Activity implements OnItemClickListener {
	private final static String SETTING_NAME[] =
		{ "個人情報編集","車両情報編集","通知設定","このアプリについて","利用規約" };
	
	private ListView lv_setting;
	ArrayAdapter <String>aa;

	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		aa=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, SETTING_NAME);
		lv_setting=(ListView)findViewById(R.id.lv_setting);
		lv_setting.setAdapter(aa);
		lv_setting.setOnItemClickListener(this);
	}
	
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3)
	{
		switch(pos)
		{
		
		case 0: //個人情報編集
			startActivityForResult(new Intent(Setting.this, Personal.class), 0);
			break;
			
		case 1: //車両情報編集
			startActivityForResult(new Intent(Setting.this, Car.class), 0);
			break;
			
		case 2: //通知設定
			PreferenceManager.getDefaultSharedPreferences(this).edit().clear().commit();
			Toast.makeText(getApplicationContext(), "管理者用：保存データを削除しました", Toast.LENGTH_SHORT).show();
			break;
			
		case 3: //このアプリについて
			startActivity(new Intent(Setting.this, Description.class));
			break;
			
		case 4: //利用規約
			startActivity(new Intent(Setting.this, Agreement.class));
			break;
		}
		
		finish();
	}
}

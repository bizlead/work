package bizlead.carlife;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class Personal extends Activity implements OnClickListener {
	private final static int ET_COUNT=7;
	private final static int TV_COUNT=8;
	private final static int SP_COUNT=4;
	
	private final static String PERSONAL_TEMP[]=
	{
		"名前(漢字)","名前(カナ)","生年月日","郵便番号",
		"住所1","住所2","連絡先", "よく使うメールアドレス"
	};
	
	private final static String PERSONAL_HINT[]=
	{
		"鈴木 吉宗","スズキ ヨシムネ","1105555",
		"練馬区東丘町２丁目１号室","旭コーポ２０１号室",
		"0570444888", "example@gmail.com"
	};
	
	private final static String JUSHO[]=
	{
		"北海道","青森県","岩手県","宮城県","秋田県", "山形県",  "福島県","茨城県",
		"栃木県","群馬県","埼玉県","千葉県","東京都", "神奈川県","新潟県","富山県","石川県","福井県","山梨県",
		"長野県","岐阜県","静岡県","愛知県","三重県", "滋賀県",  "京都府","大阪府","兵庫県","奈良県","和歌山県",
		"鳥取県","島根県","岡山県","広島県","山口県", "徳島県",  "香川県","愛媛県","高知県","福島県","佐賀県",
		"長崎県","熊本県","大分県","宮崎県","鹿児島県","沖縄県"
	};
		
	private final static int ET_ID[]=
	{	R.id.et_personal1, R.id.et_personal2, R.id.et_personal3, R.id.et_personal4,
		R.id.et_personal5, R.id.et_personal6, R.id.et_personal7
	};
	
	private final static int TV_ID[]=
	{
		R.id.tv_personal1, R.id.tv_personal2, R.id.tv_personal3, R.id.tv_personal4,
		R.id.tv_personal5, R.id.tv_personal6, R.id.tv_personal7, R.id.tv_personal8
	};
	
	private final static int SP_ID[]=
	{
		R.id.sp_personal1, R.id.sp_personal2, R.id.sp_personal3, R.id.sp_personal4
	};
	
	private EditText et_personal[]=new EditText[ET_COUNT];
	private TextView tv_personal[]=new TextView[TV_COUNT];
	private Spinner sp[]=new Spinner[SP_COUNT];
	private Button next, clear;
	private SharedPreferences pref;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal);
		
		
		//エディットテキストを生成
		for(int i=0; i<ET_COUNT; i++)
		{
			et_personal[i]=(EditText)findViewById(ET_ID[i]);
			et_personal[i].setHint(PERSONAL_HINT[i]);
			et_personal[i].setBackgroundColor(Color.WHITE);
			et_personal[i].setTextSize(15);
			et_personal[i].setHintTextColor(Color.rgb(222, 222, 222));
			et_personal[i].setInputType(InputType.TYPE_CLASS_TEXT);
		}
		
		
		//エディットテキストの入力制限
		et_personal[0].setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
		et_personal[1].setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
		et_personal[2].setInputType(InputType.TYPE_CLASS_NUMBER);
		et_personal[3].setInputType(InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
		et_personal[4].setInputType(InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
		et_personal[5].setInputType(InputType.TYPE_CLASS_PHONE);
		
		
		//テキストビューを生成
		for(int i=0; i<TV_COUNT; i++)
		{
			tv_personal[i]=(TextView)findViewById(TV_ID[i]);
			tv_personal[i].setText(PERSONAL_TEMP[i]);
			tv_personal[i].setTextSize(13);
			tv_personal[i].setTextColor(Color.GRAY);
		}


		//アレイアダプターを生成
		ArrayAdapter<String>aa_y=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item);
		ArrayAdapter<String>aa_m=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item);
		ArrayAdapter<String>aa_d=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item);
		ArrayAdapter<String>aa_j=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, JUSHO);


		//スピナーに生年月日代入
		for(int i=2008; i>1918; i--) aa_y.add(i+"年");
		for(int i=1; i<13; i++) aa_m.add(i+"月");
		for(int i=1; i<32; i++) aa_d.add(i+"日");
		
		
		//スピナーを生成
		for(int i=0; i<SP_COUNT; i++)
		{
			sp[i]=(Spinner)findViewById(SP_ID[i]);
			sp[i].setBackgroundColor(Color.CYAN);
		}
		
		
		//スピナーに都道府県と生年月日代入
		sp[0].setAdapter(aa_y);
		sp[1].setAdapter(aa_m);
		sp[2].setAdapter(aa_d);
		sp[3].setAdapter(aa_j);

		
		//ボタンを生成
		next=(Button)findViewById(R.id.bt_personal_next);
		clear=(Button)findViewById(R.id.bt_personal_clear);
		
		next.setOnClickListener(this);
		clear.setOnClickListener(this);
		next.setText("次へ");
		
		if(getCallingActivity().getClassName().toString().equals("bizlead.carlife.Transmission"))
			next.setText("決定");
		clear.setText("全て書き直す");
		
		next.setBackgroundResource(R.drawable.home_setting);
		clear.setBackgroundResource(R.drawable.home_setting);
		
		next.setTextColor(Color.WHITE);
		clear.setTextColor(Color.WHITE);
		
		next.setTextSize(15);
		clear.setTextSize(15);
	}
	
	protected void save()
	{
		
		//プリファレンスを生成
		pref=PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor=pref.edit();
		String getYmd="";
		
		
		//メールアドレス保存
		editor.putString("よく使うメールアドレス", et_personal[6].getText().toString());

		
		//名前保存
		editor.putString("名前(漢字)",et_personal[0].getText().toString());
		editor.putString("名前(カナ)",et_personal[1].getText().toString());

		
		//生年月日保存
		for(int i=0; i<sp.length-1; i++)
			getYmd+=sp[i].getSelectedItem().toString();
		editor.putString("生年月日", getYmd);
		
		editor.putInt("YEAR", sp[0].getSelectedItemPosition());
		editor.putInt("MONTH", sp[1].getSelectedItemPosition());
		editor.putInt("DAY", sp[2].getSelectedItemPosition());

		
		//郵便番号保存
		editor.putString("郵便番号", et_personal[2].getText().toString());

		
		//都道府県保存
		editor.putString("都道府県", sp[3].getSelectedItem().toString());
		editor.putInt("TDFK", sp[3].getSelectedItemPosition());
		
		
		//住所保存
		editor.putString("住所1",et_personal[3].getText().toString());
		editor.putString("住所2",et_personal[4].getText().toString());
		
		
		//住所保存
		String jushodata=sp[3].getSelectedItem().toString()+
				et_personal[3].getText().toString()+et_personal[4].getText().toString();
		
		editor.putString("住所", jushodata);

		
		//連絡先保存
		editor.putString("連絡先",et_personal[5].getText().toString());
		
		
		//保存した回数保存
		editor.putInt("perCount", 7);
		editor.commit();
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		
		pref=PreferenceManager.getDefaultSharedPreferences(this);
		et_personal[0].setText(pref.getString(PERSONAL_TEMP[0], "").toString());
		et_personal[1].setText(pref.getString(PERSONAL_TEMP[1], "").toString());
		et_personal[2].setText(pref.getString(PERSONAL_TEMP[3], "").toString());
		et_personal[3].setText(pref.getString(PERSONAL_TEMP[4], "").toString());
		et_personal[4].setText(pref.getString(PERSONAL_TEMP[5], "").toString());
		et_personal[5].setText(pref.getString(PERSONAL_TEMP[6], "").toString());
		et_personal[6].setText(pref.getString(PERSONAL_TEMP[7], "").toString());
		
		sp[0].setSelection(pref.getInt("YEAR", 0));
		sp[1].setSelection(pref.getInt("MONTH", 0));
		sp[2].setSelection(pref.getInt("DAY", 0));
		sp[3].setSelection(pref.getInt("TDFK", 0));
	}

	
	@Override
	public void onClick(View v)
	{
		
		if(v==next) {
			save();
			
			if(getCallingActivity().getClassName().toString().equals("bizlead.carlife.Transmission")) {
				startActivity(new Intent(Personal.this, Transmission.class));
				finish();
			} else
				startActivityForResult(new Intent(Personal.this, Car.class), 0);
		}
	
		
		if(v==clear)
			for(int i=0; i<ET_COUNT; i++)
				et_personal[i].setText("");			
	}
}

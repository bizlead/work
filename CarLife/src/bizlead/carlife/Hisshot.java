package bizlead.carlife;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.Toast;


public class Hisshot extends Activity implements OnClickListener {
	private SharedPreferences sp;
	
	private final static String SET_HIS[]=
	{
		"注文した日付",
		"注文ID",
		"注文した場所",
		"お客様の名前",
		"連絡先",
		"発生した車名"
	};
	
	private final static String SET_HIS2[]=
	{
		"レッカー社名",
		"担当者名",
		"連絡先",
		"担当者のメッセージ",
		"備考"
	};
	
	private String SET_HIS_DATA[]=new String[SET_HIS.length];
	private String SET_HIS_DATA2[]=new String[SET_HIS2.length];
	
	private ListView lv_hisshot, lv_hisshot2;
	

	protected void onCreate(Bundle savedInstance)
	{
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_hisshot);
		sp=PreferenceManager.getDefaultSharedPreferences(this);

		
		for(int i=0; i<SET_HIS.length-3; i++)
			SET_HIS_DATA[i]=sp.getString(SET_HIS[i], "");
		
		for(int i=0; i<SET_HIS2.length; i++)
			SET_HIS_DATA2[i]=sp.getString(SET_HIS2[i], "");
		
		
		lv_hisshot=(ListView)findViewById(R.id.lv_hisshot);
		lv_hisshot2=(ListView)findViewById(R.id.lv_hisshot2);
	
		lv_hisshot=EastSys.List2gyou(SET_HIS, SET_HIS_DATA, lv_hisshot, this);
		lv_hisshot2=EastSys.List2gyou(SET_HIS2, SET_HIS_DATA2, lv_hisshot2, this);
		
		lv_hisshot=EastSys.ListScroll(lv_hisshot);
		lv_hisshot2=EastSys.ListScroll(lv_hisshot2);
		
		/*
		for(int i=0; i<HIS_COUNT; i++)
		{
			tv_hisshot[i]=(TextView)findViewById(tv_hisshot_id[i]);
			tv_hisshot[i].setText(SET_HIS[i]);
			tv_hisshot[i].setBackgroundColor(Color.rgb(33,115,69));
			tv_hisshot[i].setPadding(20, 10, 10, 10);
			tv_hisshot[i].setTextSize(13);
			tv_hisshot[i].setTextColor(Color.WHITE);
			
			
			tv_hisdata[i]=(TextView)findViewById(tv_hisdata_id[i]);
			tv_hisdata[i].setText(sp.getString(SET_HIS[i],"該当するデータがありません"));
			tv_hisdata[i].setPadding(40, 40, 40, 40);
			tv_hisdata[i].setTextSize(16);
		}
		
		
		getLocalFile();

		for(int i=0; i<SET_HIS.length; i++)
		{
			his_data[i]=sp.getString(SET_HIS[i],"該当するデータがありません");
		}
		*/
	}

	
	public void getLocalFile()
	{
		InputStream in=null;
		String str="";
		
		try{
			in=openFileInput(sp.getString("LOCAL_FILE",""));
			BufferedReader br=new BufferedReader(new InputStreamReader(in, "UTF-8"));
			
			while((str=br.readLine())!=null)
			{
				Log.v("あああstr="+str,"kore");
			}
			
		} catch(Exception e)
		{
			Log.v("br="+e, "aaaAA");
		}
	}
	
	@Override
	public void onClick(View arg0){	}
	


}

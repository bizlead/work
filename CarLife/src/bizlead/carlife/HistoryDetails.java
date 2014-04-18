package bizlead.carlife;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import static bizlead.carlife.CommonUtilities.*;

public class HistoryDetails extends Activity {
	
	private PersonOpenHelper hlpr=null;
	private SQLiteDatabase db=null;
	
	private Cursor c = null, c2 = null, c3 = null;
	private int id = 0;
	
	private final static String SET_HIS[]=
	{
		"注文した日付",
		"注文した場所",
		"注文ID",
		"お客様の名前",
		"連絡先",
		"発生した車名",
		"レッカー社名",
		"担当者名",
		"連絡先",
		"担当者のメッセージ",
		"備考"
	};
	
	private final static String SET_HIS_DATA[]=
	{
		"CALL_DATE",
		"CALL_ADDRESS",
		"CALL_ID",
		
		"name1",
		"tel",
		"CAR_NAME",
		
		"TRUCK_COMPANY",
		"TANTOU",
		"TANTOU_TEL",
		"TANTOU_MESSAGE",
		"REMARKS"
	};
	
	private final static int SET_HIS_ID[] = 
	{
		R.id.hisdet1, R.id.hisdet2, R.id.hisdet3, R.id.hisdet4, R.id.hisdet5, 
		R.id.hisdet6, R.id.hisdet7, R.id.hisdet8, R.id.hisdet9, R.id.hisdet10, R.id.hisdet11
	};
	
	private static final int SET_HISS = 11;
	private TextView tv_det[]=new TextView[SET_HISS];

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hisdetails);
		
		Intent intent = getIntent();
		id = (int)intent.getLongExtra("listId", 0);

		read();
		textViews();
	}
	
	
	private void read() {
		hlpr = new PersonOpenHelper(this);
		db = hlpr.getReadableDatabase();
		Log.v("AA", "11");
		
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<SET_HIS.length; i++){
			sb.append(SET_HIS_DATA[i]);
			if(i < SET_HIS.length-1)
				sb.append(", ");
		}
		
		c = db.rawQuery("SELECT rowid as _id, * FROM " + PERSON_TB + " WHERE rowid = "+id, null);
		Log.v("BB", "11");
		c2 = db.query(PERSON_TB, SET_HIS_DATA, null, null, null, null, null);
		c3 = db.rawQuery("SELECT rowid as _id, "+sb.toString()+" FROM "+ PERSON_TB+ "", null);
		Log.v("CC", "11");
		c.moveToFirst();
		c2.moveToFirst();
		c3.moveToFirst();
	}
	
	
	private void textViews() {
		for (int i = 0; i < SET_HISS; i++) {
			tv_det[i]=(TextView)findViewById(SET_HIS_ID[i]);
			Log.v("EE", "11");
			Log.v("c.getCount=", c3.getCount()+"です");
			Log.v("c.getString("+i+")=", c3.getString(i)+"です");
			Log.v("AAAAAA", "qqqqq"+c3.getString(5).toString());
			tv_det[i].setText(SET_HIS[i]+" : "+c3.getString(i));
		}
	}
}
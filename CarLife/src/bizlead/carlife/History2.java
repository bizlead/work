package bizlead.carlife;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import static bizlead.carlife.CommonUtilities.*;


public class History2 extends Activity implements OnItemClickListener, OnItemLongClickListener, OnClickListener {
	private ListView lv;
	private SimpleCursorAdapter sca=null;
	private Button back;
	
	private SQLiteDatabase db=null;
	private PersonOpenHelper hlpr=null;
	private Cursor c=null;
	
	private String listTitle[]={"CALL_DATE", "CALL_ADDRESS"};
	private int listId[]={android.R.id.text1, android.R.id.text2};
	
	private AlertDialog.Builder alert=null;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history2);
		makeItem();
		read();
	}
	
	
	private void makeItem(){
		lv = new ListView(this);
		lv = (ListView)findViewById(R.id.lv_history2);
		lv.setOnItemClickListener(this);
		lv.setOnItemLongClickListener(this);
		back = (Button)findViewById(R.id.bt_history2_back);
		back.setOnClickListener(this);
	}

	
	@SuppressWarnings("deprecation")
	private void read() {
		try {
			hlpr = new PersonOpenHelper(this);
			db = hlpr.getReadableDatabase();
			c = db.rawQuery("SELECT rowid as _id, * FROM " + PERSON_TB, null);
			sca = new SimpleCursorAdapter(this,
					android.R.layout.simple_list_item_2,
					//R.layout.list1,
					c, listTitle, listId);
			lv.setAdapter(sca);
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.v("History2", "e="+e);
		}
	}
	
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
		Toast.makeText(getApplicationContext(), "position="+position+"\nid="+id, Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(History2.this, HistoryDetails.class);
		intent.putExtra("listId", id);
		startActivityForResult(intent, 0);
	}


	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
		
		alert = new AlertDialog.Builder(this);
		alert.setTitle("警告").setMessage("この履歴の注文内容を削除しますか？")		
		.setPositiveButton("削除する", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				String rowDelete=" rowid = "+id;
				db.delete(PERSON_TB, rowDelete, null);
				finish();
				Toast.makeText(getApplicationContext(), "削除しました", Toast.LENGTH_SHORT).show();
			}
		})
		
		.setNegativeButton("キャンセル", new DialogInterface.OnClickListener(){
			@Override public void onClick(DialogInterface dialog, int which){}
		})
		.create()
		.show();
		return false;
	}


	@Override
	public void onClick(View v) {
		finish();
	}
}
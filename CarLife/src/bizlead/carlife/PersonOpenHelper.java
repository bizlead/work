package bizlead.carlife;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import static bizlead.carlife.CommonUtilities.*;


public class PersonOpenHelper extends SQLiteOpenHelper {
	private StringBuilder sb;

	
	public PersonOpenHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	
	@Override
	public void onCreate(android.database.sqlite.SQLiteDatabase db) {
		sb = new StringBuilder();
		
		sb.append(" ( ");
		for (int i = 0; i < DB_KEY.length; i++) {
			sb.append(DB_KEY[i]+" TEXT");
			if (i < DB_KEY.length-1) sb.append(", ");
		}
		sb.append(" );");
		
		db.execSQL("CREATE TABLE " + PERSON_TB + sb.toString());
	}
	
	
	// データベースの変更が生じた場合は、ここに処理を記述する
	@Override
	public void onUpgrade(android.database.sqlite.SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DROP_TABLE);
		db.execSQL("CREATE TABLE " + PERSON_TB + sb.toString());
	}
}
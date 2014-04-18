package bizlead.carlife;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class Description extends Activity implements OnClickListener {
	Button bt_desc;
	ListView lv_desc;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_desc);
		lv_desc=(ListView)findViewById(R.id.lv_desc);
		lv_desc=EastSys.List2gyou(new String[]{"バージョン"}, new String[]{"1.00"}, lv_desc, this);
		
		bt_desc=(Button)findViewById(R.id.bt_desc);
		bt_desc.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		finish();
	}

}

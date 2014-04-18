package bizlead.carlife;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Agreement extends Activity implements OnClickListener{
	private Button bt_agree;
	
	private final static int TV_AGREE_ID[]=
	{
		R.id.tv_agree1,  R.id.tv_agree2, R.id.tv_agree3, R.id.tv_agree4, 
		R.id.tv_agree5,  R.id.tv_agree6, R.id.tv_agree7, R.id.tv_agree8,
		R.id.tv_agree9,  R.id.tv_agree10, R.id.tv_agree11, R.id.tv_agree12,
		R.id.tv_agree13, R.id.tv_agree14, R.id.tv_agree15, R.id.tv_agree16 
	};
	
	private TextView tv_agree[]=new TextView[TV_AGREE_ID.length];

	@Override
	protected void onCreate(Bundle savedInstanceSate){
		super.onCreate(savedInstanceSate);
		setContentView(R.layout.activity_agreement);
		
		
		for( int i=0; i<TV_AGREE_ID.length; i++ )
		{
			tv_agree[i]=(TextView)findViewById(TV_AGREE_ID[i]);
			tv_agree[i].setTextSize(13);
			tv_agree[i].setTextColor(Color.rgb(110, 110, 110));
			tv_agree[i].setPadding(0, 0, 0, 30);
			
			if( i%2 == 0 )
			{
				tv_agree[i].setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
				tv_agree[i].setTextColor(Color.rgb(80, 80, 80));
			}
		}
		
		bt_agree=(Button)findViewById(R.id.bt_agree);
		bt_agree.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View arg0) {
		finish();
	}
	

}

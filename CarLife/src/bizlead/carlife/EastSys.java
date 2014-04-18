package bizlead.carlife;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;



public class EastSys {
	
	
	// スクロールビュー内のリストビューが一行になる問題点を克服
		public static void setListScroll(ListView getListView)
		{
		    ListAdapter la = getListView.getAdapter();
		    if (la == null)	return;
		    int h = 0; // ListView トータルの高さ
		
		    
		    //XXX -変更あり-
		    for (int i = 0; i < la.getCount(); i++)
		    {
		        View item = la.getView(i, null, getListView);
		        item.measure(0, 0);
		        h += item.getMeasuredHeight()+70;
		    }

		    
		    ViewGroup.LayoutParams p = getListView.getLayoutParams();
		    p.height = h + (getListView.getDividerHeight() * la.getCount());
		    getListView.setLayoutParams(p);
		    
		}
	
	
	
    // スクロールビュー内のリストビューが一行になる問題点を克服
	public static ListView ListScroll(ListView getListView)
	{
	    ListAdapter la = getListView.getAdapter();
	    if (la == null)	return null;
	    int h = 0; // ListView トータルの高さ
	
	    
	    //XXX -変更あり-
	    for (int i = 0; i < la.getCount(); i++)
	    {
	        View item = la.getView(i, null, getListView);
	        item.measure(0, 0);
	        h += item.getMeasuredHeight()+72;
	    }

	    ViewGroup.LayoutParams p = getListView.getLayoutParams();
	    p.height = h + (getListView.getDividerHeight() * la.getCount());
	    getListView.setLayoutParams(p);
	    
	    return getListView;
	}
	
	
	//リストビューを2行にする
	public static ListView List2gyou(String[]main, String[]sub, ListView getListView, Context con)
	{

		List<Map<String, String>>list=null;
		list = new ArrayList<Map<String, String>>();

		for(int i=0; i<main.length; i++)
		{
			Map<String, String>map=new HashMap<String, String>();
			map.put("main", main[i]);
			map.put("sub", sub[i]);
			list.add(map);
		}

		
		SimpleAdapter adapter=new SimpleAdapter(con, list, R.layout.list_style,
			new String[]{"main", "sub"},
			new int[]{R.id.domain, R.id.country});

		getListView.setAdapter(adapter);
		
		return getListView;
	}
}
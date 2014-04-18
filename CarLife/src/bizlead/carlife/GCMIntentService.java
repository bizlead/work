package bizlead.carlife;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

import static bizlead.carlife.CommonUtilities.*;

public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG="GCMIntentService";
	
	public GCMIntentService(){
		super(SENDER_ID);
	}
	
	
	@Override
	protected void onError(Context context, String errorId) {
		Log.i(TAG, "Received error: "+errorId);
		displayMessage(context, getString(R.string.gcm_error, errorId));
	}

	
	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.i(TAG, "Received message");
		Bundle bundle=intent.getExtras();
		String message=getString(R.string.gcm_message)+"="+bundle.getString("message");
		displayMessage(context, message);
		generateNotification(context, message);
	}

	
	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i(TAG, "Device registered:regId="+registrationId);
		displayMessage(context, registrationId);
		ServerUtilities.register(context, registrationId);
	}

	
	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "Device unregistered");
		displayMessage(context, getString(R.string.gcm_unregistered));
		if(GCMRegistrar.isRegisteredOnServer(context)){
			ServerUtilities.unregister(context, registrationId);
		} else {
			Log.i(TAG, "Ignoring unregister callback");
		}
	}
	
	
	@Override
	protected void  onDeletedMessages(Context context, int total){
		Log.i(TAG, "Received deleted messages notification");
		String message = getString(R.string.gcm_deleted, total);
		displayMessage(context, message);
		generateNotification(context, message);
	}
	
	
	@Override
	protected boolean onRecoverableError(Context context, String errorId){
		Log.i(TAG, "Received recoverble error: "+errorId);
		displayMessage(context, getString(R.string.gcm_recoverable_error, errorId));
		return super.onRecoverableError(context, errorId);
	}
	
	
	@SuppressWarnings("deprecation")
	private static void generateNotification(Context context, String message){
		int icon = R.drawable.ic_stat_gcm;
		long when=System.currentTimeMillis();
		NotificationManager notificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification=new Notification(icon, message, when);
		String title=context.getString(R.string.app_name);
		Intent notificationIntent=new Intent(context, Home.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent=PendingIntent.getActivity(context, 0, notificationIntent,0);
		notification.setLatestEventInfo(context, title, message, intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(0, notification);
	}
}

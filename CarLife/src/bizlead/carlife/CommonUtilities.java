package bizlead.carlife;

import android.content.Context;
import android.content.Intent;

public final class CommonUtilities {
	
	
	/**
	 * Transmission & Server関係
	 */
	final static String SERVER_URL="http://133.242.86.71";
	final static String DIR_NAME="/test";
	final static String FILE_NAME="/write.php";
	final static String UPLOAD_PATH="/phps/imgup.php";
	
	final static String SENDER_ID="334619060723";
	final static String TAG="Carlife";
	final static String EXTRA_MESSAGE="message";
	final static String DISPLAY_MESSAGE_ACTION="bizlead.carlife.DISPLAY_MESSAGE";
	
	
	/**
	 *  SQLiteDatabase
	 */
	final static int DB_VERSION=1;
	final static String DB_NAME="CARLIFE_DB14";
	final static String PERSON_TB="PERSON_TB";
	final static String DROP_TABLE="DROP TABLE IF EXITS "+PERSON_TB;
	
								
	/**
	 * Transmission
	 */
	final static String PER_KEY[]=
	{
		"名前(漢字)",
		"名前(カナ)",
		"生年月日",
		"郵便番号",
		"住所",
		"連絡先",
		"よく使うメールアドレス"
	};
	
	final static String PER_KEY_DB[]=
	{	
		"name1",
		"name2",
		"birth",
		"post",
		"address",
		"tel",
		"mail"
	};
	
	final static String PER_KEY2[]=
	{
		"名前漢字",
		"名前カナ",
		"生年月日"
	};
	
	final static String HISTORY_TITLE[]=
	{
		"CALL_ADDRESS",
		"CALL_DATE",
		"CALL_ID"
	};
	
	final static String CAR_KEY[]=
	{
		"自動車登録番号又は車両番号",
		"登録年月日",
		"初年度登録年月",
		"自動車の種別",
		"用途",
		"車名",
		"車台番号",
		"型式",
		"原動機の型式",
		"排気量又は燃料出力",
		"燃料の種類",
		"所有者の氏名",
		"所有者の住所",
		"有効期間の満了する日"
	};
	
	final static String[]CAR_KEY_DB=
	{
		"VEHICLE_NAMBER",
		"REGIST_DATE",
		"FIRST_REGIST",
		"KIND_CAR",
		"CAR_USE",
		"CAR_NAME",
		"SERIAL_NUMBER",
		"MODEL",
		"MODEL_PRIME",
		"FUEL",
		"FUEL_TYPE",
		"OWENER_NAME",
		"OWENER_ADDRESS",
		"MANRYOU"
	};
	
	final static String[]CAR_HINT=
	{
		"品川032 に 3334",
		"平成88年9月9日",
		"平成77年7月",
		"普通",
		"乗用",
		"トヨタ プリウス",
		"BNR34-323334",
		"GF-BFF34",
		"RB26",
		"2.56kw/L",
		"ガソリン",
		"鈴木太郎",
		"東京都台東区東上野０００－０００",
		"平成99年9月9日",
	};
	
	final static String DB_KEY[]=
	{
		"name1",
		"name2",
		"birth",
		"post",
		"address",
		"tel",
		"mail",
	//7
		"VEHICLE_NAMBER",
		"REGIST_DATE",
		"FIRST_REGIST",
		"KIND_CAR",
		"CAR_USE",
		"CAR_NAME",
		"SERIAL_NUMBER",
		"MODEL",
		"MODEL_PRIME",
		"FUEL",
		"FUEL_TYPE",
		"OWENER_NAME",
		"OWENER_ADDRESS",
		"MANRYOU",
	//21
		"CALL_ADDRESS",
		"CALL_DATE",
		"CALL_ID",
	//24
		"TRUCK_COMPANY",
		"TANTOU",
		"TANTOU_TEL",
		"TANTOU_MESSAGE",
		"REMARKS"
	//29
	};
	
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
	
	static void displayMessage(Context context, String message){
		Intent intent=new Intent(DISPLAY_MESSAGE_ACTION);
		intent.putExtra(EXTRA_MESSAGE, message);
		context.sendBroadcast(intent);
	}
}

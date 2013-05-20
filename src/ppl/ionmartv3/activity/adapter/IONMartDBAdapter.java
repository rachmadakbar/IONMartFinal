package ppl.ionmartv3.activity.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class IONMartDBAdapter {

	private static final String TAG = "IONMartDBAdapter";
	private static final String DATABASE_NAME = "IONMart";
	private static final String DATABASE_TABLE1 = "AKUN";
	private static final String DATABASE_TABLE2 = "PEMBELIAN";
	private static final String DATABASE_TABLE3 = "PRODUK";
	private static final String DATABASE_TABLE4 = "PRODUK_TERKAIT_PEMBELIAN";
	private static final String DATABASE_TABLE5 = "IS_FAVORITE";
	private static final String DATABASE_TABLE6 = "HISTORY";

	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_CREATE1 = "CREATE TABLE AKUN(username TEXT PRIMARY KEY,"
			+ "password TEXT, alamat TEXT, kota TEXT, jenis_kelamin TEXT,"
			+ "email TEXT, isLogin INTEGER, role TEXT, saldo INTEGER );";

	private static final String DATABASE_CREATE2 =  "CREATE TABLE PEMBELIAN(idPembelian INTEGER PRIMARY KEY autoincrement,"
			+ "jenis TEXT, type TEXT, time TEXT, nama TEXT, username TEXT, " +
			"FOREIGN KEY(username) REFERENCES AKUN(username));";

	private static final String DATABASE_CREATE3 =  "CREATE TABLE PRODUK(idProduk TEXT PRIMARY KEY);";

	private static final String DATABASE_CREATE4 =  "CREATE TABLE PRODUK_TERKAIT_PEMBELIAN( idProduk TEXT,"
			+ "idPembelian INTEGER, kuantitas INTEGER, PRIMARY KEY (idProduk,idPembelian), FOREIGN KEY(idProduk) REFERENCES PRODUK(id),"
			+ "FOREIGN KEY(idPembelian) REFERENCES PEMBELIAN(id) ON DELETE RESTRICT);";

	private static final String DATABASE_CREATE5 =  "CREATE TABLE IS_FAVORITE(username TEXT, idProduk TEXT, PRIMARY KEY (username,idProduk),"
			+ "FOREIGN KEY(username) REFERENCES AKUN(username),"
			+ "FOREIGN KEY(idProduk) REFERENCES PRODUK(idProduk));";
	
	private static final String DATABASE_CREATE6 =  "CREATE TABLE HISTORY (idHistory INTEGER PRIMARY KEY autoincrement, username TEXT, date TEXT, total INTEGER,"
			+ "FOREIGN KEY(username) REFERENCES AKUN(username));";
	
	private final Context context;

	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	public IONMartDBAdapter(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}
	
	public void dropDatabase(){
		DBHelper.onUpgrade(db, DATABASE_VERSION, DATABASE_VERSION);
	}
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE1);
			db.execSQL(DATABASE_CREATE2);
			db.execSQL(DATABASE_CREATE3);
			db.execSQL(DATABASE_CREATE4);
			db.execSQL(DATABASE_CREATE5);
			db.execSQL(DATABASE_CREATE6);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE1);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE2);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE3);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE4);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE5);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE6);
			onCreate(db);
		}
	}

	// ---opens the database---
	public IONMartDBAdapter open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}

	// ---closes the database---
	public void close() {
		DBHelper.close();
	}

	// ---insert a title into the database---
	public long insertAkun(String username, String password, String alamat,
			String kota, String jenis_kelamin, String email, boolean isLogin,
			String role, double saldo) {
		ContentValues initialValues = new ContentValues();
		initialValues.put("username", username);
		initialValues.put("password", password);
		initialValues.put("alamat", alamat);
		initialValues.put("kota", kota);
		initialValues.put("jenis_kelamin", jenis_kelamin);
		initialValues.put("email", email);
		initialValues.put("isLogin", isLogin);
		initialValues.put("role", role);
		initialValues.put("saldo", saldo);
		return db.insert(DATABASE_TABLE1, null, initialValues);
		
	}

	public long insertPembelian(String jenis, String type,	String time, String username, String name) {

		ContentValues initialValues = new ContentValues();
		initialValues.put("jenis", jenis);
		initialValues.put("type", type);
		initialValues.put("time", time.toString());
		initialValues.put("nama", name);
		initialValues.put("username", username);
		return db.insert(DATABASE_TABLE2, null, initialValues);
	}


	public long insertProduk(String id) {
		final String MY_QUERY = "SELECT * FROM PRODUK where idProduk=?";

		Cursor c =  db.rawQuery(MY_QUERY, new String[] { String.valueOf(id) });
		if(c.moveToFirst()){
			ContentValues initialValues = new ContentValues();
			initialValues.put("idProduk", id);
			return db.insert(DATABASE_TABLE3, null, initialValues);
			}
		return 0;
	}

	public long insertProductTerkaitPembelian(String idProduk, int idPembelian,
			int kuantitas) {
		boolean exist = this.isExist(idProduk, idPembelian);
		if(exist){
			int num = this.getNumProduct(idProduk, idPembelian);
			this.editKuantitas(idPembelian, idProduk, kuantitas+num);
			return 0;
		}else{
			ContentValues initialValues = new ContentValues();
			initialValues.put("idProduk", idProduk);
			initialValues.put("idPembelian", idPembelian);
			initialValues.put("kuantitas", kuantitas);
			return db.insert(DATABASE_TABLE4, null, initialValues);
		}
	}

	public long insertFavoriteProduk(String username, String idProduk) {

		ContentValues initialValues = new ContentValues();
		initialValues.put("username", username);
		initialValues.put("idProduk", idProduk);
		return db.insert(DATABASE_TABLE5, null, initialValues);
	}

	public long insertHistory(String username, String date, double total) {

		ContentValues initialValues = new ContentValues();
		initialValues.put("username", username);
		initialValues.put("date", date);
		initialValues.put("total", total);
		return db.insert(DATABASE_TABLE6, null, initialValues);
	}
	
	// ---deletes a particular data---
	public boolean deleteAkun(String username) {
		return db.delete(DATABASE_TABLE1, "username='" +username+"'", null) > 0;
	}
	

	// ---retrieves all data---

	public Cursor getAllSchedule(String username) {
		final String MY_QUERY = "SELECT PEMBELIAN.idPembelian, PEMBELIAN.type, PEMBELIAN.nama, PEMBELIAN.time FROM PEMBELIAN "
				+ "where PEMBELIAN.jenis='schedule' and PEMBELIAN.username=? order by PEMBELIAN.idPembelian ";

		return db.rawQuery(MY_QUERY, new String[] { String.valueOf(username) });
	}
	
	public Cursor getSchedule(int idPembelian) {
		final String MY_QUERY = "SELECT idProduk, kuantitas FROM PRODUK_TERKAIT_PEMBELIAN where "
				+ "PRODUK_TERKAIT_PEMBELIAN.idPembelian=?";

		return db.rawQuery(MY_QUERY, new String[] { String.valueOf(idPembelian)});
	}
	
	public Cursor getScheduleByName(String username, String nama) {
		final String MY_QUERY = "SELECT * FROM PEMBELIAN where "
				+ "username=? and nama=?";
		return db.rawQuery(MY_QUERY, new String[] { String.valueOf(username), String.valueOf(nama)});
	}

	public Cursor getShoppingCart(String username) {
		final String MY_QUERY = "SELECT idPembelian, idProduk, kuantitas FROM PEMBELIAN "
				+ "NATURAL JOIN PRODUK_TERKAIT_PEMBELIAN where "
				+ "PEMBELIAN.jenis='shoppingcart' and PEMBELIAN.username=?";

		return db.rawQuery(MY_QUERY, new String[] { String.valueOf(username) });
	}
	
	public Cursor getHistoryPembelian(String username) {
		final String MY_QUERY = "SELECT date, total FROM HISTORY  where HISTORY.username=?";
		return db.rawQuery(MY_QUERY, new String[] { String.valueOf(username) });
	}
	
	public Cursor getUser(String username) {
		final String MY_QUERY = "SELECT * FROM AKUN where AKUN.username=?";
		return db.rawQuery(MY_QUERY, new String[] { String.valueOf(username) });
	}
	
	public Cursor getActiveUser() {
		final String MY_QUERY = "SELECT * FROM AKUN where AKUN.isLogin=?";
		return db.rawQuery(MY_QUERY, new String[] { String.valueOf("1") });
	}
	
	public int getShoppingCartID(String username) {
		final String MY_QUERY = "SELECT idPembelian FROM PEMBELIAN where PEMBELIAN.jenis='shoppingcart' and PEMBELIAN.username=?";
		Cursor c = db.rawQuery(MY_QUERY, new String[] { String.valueOf(username) });
		if(c.moveToFirst()) return Integer.parseInt(c.getString(0));
		else return -1;
	}
	
	public Cursor getAllPembelian(String username) {
		final String MY_QUERY = "SELECT idPembelian, jenis FROM PEMBELIAN where PEMBELIAN.username=?";
		return db.rawQuery(MY_QUERY, new String[] { String.valueOf(username) });
	}
	
	public Cursor getPembelian(int idPembelian) {
		final String MY_QUERY = "SELECT * FROM PEMBELIAN where PEMBELIAN.idPembelian=?";
		return db.rawQuery(MY_QUERY, new String[] { String.valueOf(idPembelian) });
	}
	

	public boolean isExist(String idProduk, int idPembelian){
		final String MY_QUERY = "SELECT * FROM PRODUK_TERKAIT_PEMBELIAN where "
				+ "idPembelian=? and idProduk=?";

		Cursor c = db.rawQuery(MY_QUERY, new String[] { String.valueOf(idPembelian),String.valueOf(idProduk)});
		if(c.moveToFirst()) return true;
		else return false;
	}
	
	public int getNumProduct(String idProduk, int idPembelian){
		final String MY_QUERY = "SELECT KUANTITAS FROM PRODUK_TERKAIT_PEMBELIAN where "
				+ "idPembelian=? and idProduk=?";

		Cursor c = db.rawQuery(MY_QUERY, new String[] { String.valueOf(idPembelian),String.valueOf(idProduk)});
		c.moveToFirst();
		return Integer.parseInt(c.getString(0));
	}
	
	public int getLastPembelianID(String username) {
		final String MY_QUERY = "SELECT idPembelian FROM PEMBELIAN where PEMBELIAN.username=?";
		Cursor c = db.rawQuery(MY_QUERY, new String[] { String.valueOf(username) });
		if(c.moveToLast()) return Integer.parseInt(c.getString(0));
		else return -1;
	}
	
	// ---updates akun---
	
	public void loggedIn(String username) {
		db.execSQL("update "+DATABASE_TABLE1+" set isLogin = 1 where username ='"+username+"'");
	}
	
	public void loggedOut(String username) {
		db.execSQL("update "+DATABASE_TABLE1+" set isLogin = 0 where username ='"+username+"'");
	}
	
	public void addProductToShoppingCart(String username, String idProduk, int kuantitas){
		Cursor c = this.getShoppingCart(username);
		if(!c.moveToFirst())this.insertPembelian("shoppingcart", "", "", username, "");
		
		this.insertProductTerkaitPembelian(idProduk, this.getShoppingCartID(username), kuantitas);
	}
	
	public boolean deleteProductFromPembelian(String idProduk, int idPembelian ){
		return db.delete(DATABASE_TABLE4, "idProduk='" +idProduk+"' and idPembelian='"+idPembelian+"'", null) > 0;
	}
	
	public void addProductToSchedule(String idProduk, int idPembelian,int kuantitas){
		this.insertProductTerkaitPembelian(idProduk, idPembelian, kuantitas);
	}
	
	public void createSchedule(String username, String type, String name, String date){
		this.insertPembelian("schedule", type, date, username, name);
	}
	
	public void updateSchedule(int idPembelian, String type, String name, String date){
		db.execSQL("update "+DATABASE_TABLE2+" set type='"+type+"' , nama='"+name+"' , time='"+date+"' where idPembelian ='"+idPembelian+"'");
	}
	
	public void updateAkun(String username, String password, double money){
		db.execSQL("update "+DATABASE_TABLE1+" set password='"+password+"' , saldo='"+money+"' where username ='"+username+"'");
	}
	public boolean deleteSchedule(String username, String nama) {
		return db.delete(DATABASE_TABLE2, "username='"+username+"' and nama='"+nama+"'", null) > 0;
	}
	
	public void editKuantitas(int idPembelian, String idProduk, int kuantitas){
		db.execSQL("update "+DATABASE_TABLE4+" set kuantitas ='"+kuantitas+"' where idProduk='"+idProduk+"' and idPembelian='"+idPembelian+"'");
	}
	
	public boolean clearShoppingCart(String username) 
	{
	    return db.delete(DATABASE_TABLE2, "username='" + username+"'", null) > 0;
	}
}
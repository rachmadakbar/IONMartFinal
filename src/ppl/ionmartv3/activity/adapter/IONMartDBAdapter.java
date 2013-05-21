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
	private static final String DATABASE_TABLE2 = "PRODUK";
	private static final String DATABASE_TABLE3 = "SHOPPING_CART";
	private static final String DATABASE_TABLE4 = "ROLE_SESSION";

	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_CREATE1 = "CREATE TABLE AKUN(username TEXT PRIMARY KEY, password TEXT, alamat TEXT, kota TEXT, saldo INTEGER );";

	private static final String DATABASE_CREATE2 =  "CREATE TABLE PRODUK(idProduk TEXT PRIMARY KEY);";

	private static final String DATABASE_CREATE3 =  "CREATE TABLE SHOPPING_CART( idProduk TEXT, username TEXT, kuantitas INTEGER, PRIMARY KEY (idProduk,username), FOREIGN KEY(idProduk) REFERENCES PRODUK(id), FOREIGN KEY(username) REFERENCES AKUN(username) ON DELETE RESTRICT);";

	private static final String DATABASE_CREATE4 =  "CREATE TABLE ROLE_SESSION(username TEXT, role TEXT, isLogin INTEGER, PRIMARY KEY (username,role),	FOREIGN KEY(username) REFERENCES AKUN(username));";
	
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
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE1);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE2);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE3);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE4);
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
	public void insertAkun(String username, String password, String alamat,
			String kota, double saldo) {
		final String MY_QUERY = "SELECT * FROM AKUN where username=?";
		Cursor c =  db.rawQuery(MY_QUERY, new String[] { String.valueOf(username) });
		if(c.moveToFirst()){
			this.updateAkun(username, password, alamat, kota, saldo);
		}else{
			ContentValues initialValues = new ContentValues();
			initialValues.put("username", username);
			initialValues.put("password", password);
			initialValues.put("alamat", alamat);
			initialValues.put("kota", kota);
			initialValues.put("saldo", saldo);
			db.insert(DATABASE_TABLE1, null, initialValues);
		}
	}

	public void insertProduk(String id) {
		final String MY_QUERY = "SELECT * FROM PRODUK where idProduk=?";
		Cursor c =  db.rawQuery(MY_QUERY, new String[] { String.valueOf(id) });
		if(!c.moveToFirst()){
			ContentValues initialValues = new ContentValues();
			initialValues.put("idProduk", id);
			db.insert(DATABASE_TABLE2, null, initialValues);
		}
	}

	public void insertToShoppingCart(String idProduk, String username, int kuantitas) {
		final String MY_QUERY = "SELECT * FROM SHOPPING_CART where idProduk=? and username=?";
		Cursor c = db.rawQuery(MY_QUERY, new String[] { String.valueOf(idProduk),String.valueOf(username)});
		if(c.moveToFirst()){
			db.execSQL("update "+DATABASE_TABLE3+" set kuantitas ='"+kuantitas+"' where idProduk='"+idProduk+"' and username='"+username+"'");
		}else{
			ContentValues initialValues = new ContentValues();
			initialValues.put("idProduk", idProduk);
			initialValues.put("username", username);
			initialValues.put("kuantitas", kuantitas);
			db.insert(DATABASE_TABLE3, null, initialValues);
		}
	}

	public void insertRole(String username, String role) {
		final String MY_QUERY = "SELECT * FROM ROLE_SESSION where username=? and role=?";
		Cursor c =  db.rawQuery(MY_QUERY, new String[] { String.valueOf(username),String.valueOf(role) });
		if(!c.moveToFirst()){
			ContentValues initialValues = new ContentValues();
			initialValues.put("username", username);
			initialValues.put("role", role);
			initialValues.put("isLogin", 0);
			db.insert(DATABASE_TABLE4, null, initialValues);
		}
	}
	
	public void login(String username, String role) {
		db.execSQL("update "+DATABASE_TABLE4+" set isLogin = 1 where username ='"+username+"' and role = '"+role+"'");
	}
	
	public void logout(String username, String role) {
		db.execSQL("update "+DATABASE_TABLE4+" set isLogin = 0 where username ='"+username+"' and role = '"+role+"'");
	}
	
	public Cursor getShoppingCart(String username) {
		final String MY_QUERY = "SELECT idProduk, kuantitas FROM SHOPPING_CART where username=?";
		return db.rawQuery(MY_QUERY, new String[] { String.valueOf(username) });
	}
	
	public Cursor getActiveSession() {
		final String MY_QUERY = "SELECT username, role FROM ROLE_SESSION where isLogin=?";
		return db.rawQuery(MY_QUERY, new String[] { String.valueOf("1") });
	}
	
	public Cursor getUser(String username) {
		final String MY_QUERY = "SELECT * FROM AKUN where username=?";
		return db.rawQuery(MY_QUERY, new String[] { String.valueOf(username) });
	}
	
	public double getSaldo(String username){
		final String MY_QUERY = "SELECT saldo FROM AKUN where username=?";
		Cursor c = db.rawQuery(MY_QUERY, new String[] { String.valueOf(username) });
		c.moveToFirst();
		return Double.parseDouble(c.getString(0));
	}
	
	public int getKuantitasSC(String idProduk, String username){
		final String MY_QUERY = "SELECT kuantitas FROM SHOPPING_CART where idProduk=? and username=?";
		Cursor c = db.rawQuery(MY_QUERY, new String[] { String.valueOf(idProduk), String.valueOf(username) });
		c.moveToFirst();
		return Integer.parseInt(c.getString(0));
	}
	
	public String getAlamat(String username){
		final String MY_QUERY = "SELECT alamat,kota FROM AKUN where username=?";
		Cursor c = db.rawQuery(MY_QUERY, new String[] { String.valueOf(username) });
		c.moveToFirst();
		return c.getString(0)+" , "+c.getString(1);
	}
	
	public boolean deleteProduct(String idProduk, String username){
		return db.delete(DATABASE_TABLE3, "idProduk='" +idProduk+"' and username='"+username+"'", null) > 0;
	}
	
	public void updateAkun(String username, String password, String alamat, String kota, double saldo){
		db.execSQL("update "+DATABASE_TABLE1+" set password='"+password+"' , alamat = '"+alamat+"' , kota = '"+kota+"' , saldo='"+saldo+"' where username ='"+username+"'");
	}
	
	public boolean clearShoppingCart(String username) 
	{
	    return db.delete(DATABASE_TABLE3, "username='" + username+"'", null) > 0;
	}
	
	public int getRoleCount(String username){
		final String MY_QUERY = "SELECT count(*) FROM ROLE_SESSION where username=?";
		Cursor c =  db.rawQuery(MY_QUERY, new String[] { String.valueOf(username) });
		c.moveToFirst();
		return Integer.parseInt(c.getString(0));		
	}
	
	public String getPassword(String username){
		final String MY_QUERY = "SELECT password FROM AKUN where username=?";
		Cursor c =  db.rawQuery(MY_QUERY, new String[] { String.valueOf(username) });
		c.moveToFirst();
		return c.getString(0);
	}
}
package ppl.ionmartv3.activity.session;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import ppl.ionmartv3.activity.BrowseFragment;
import ppl.ionmartv3.activity.CartFragment;
import ppl.ionmartv3.activity.helper.Global;

import android.graphics.Bitmap;
import android.graphics.Picture;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.Toast;

public class Product implements Cloneable {

    public String id = "";
    public String name = "";
    public double price = 0.0;
    public int stock = 0;
    Bitmap image = null;
    String description = "";
    double discount = 0.0;
    String vendor = "";
    public BaseAdapter adapter = null;
    //List<Review> reviews = null;

    
    /**
     * Constructor dari kelas product
     */
    public Product(){}
    public Product(String productId,BaseAdapter adapter)
    {
    	this.adapter = adapter;
    	new InfoDownloader().execute(productId);
    }
    public Product (String productId, String productName, double productPrice, int productStock,BaseAdapter adapter) {
        id = productId;
        name = productName;
        price = productPrice;
        stock = productStock;
        this.adapter = adapter;
        new ImageDownloader().execute(this.id);
    }
    
    public Product (String productId, String productName, double productPrice, int productStock, String vendor, String description,BaseAdapter adapter) {
        id = productId;
        name = productName;
        price = productPrice;
        stock = productStock;
        this.adapter = adapter;
        this.description = description;
        this.vendor = vendor;
        new ImageDownloader().execute(this.id);
    }
    
    /**
     * Digunakan untuk menambahkan data produk ke dalam database.
     * @param productId
     * @param productName
     * @param productPrice
     * @param productStock;
     */
    
    /**
     * Mengeset nama sebuah produk
     * @param "name" : nama dari sebuah produk
     */
    public void setId(String id)
    {
    	this.id = id;
    }
    public void setName(String productName){
        name = productName;
    }
    
    /**
     * Mengeset harga sebuah produk
     * @param "price" : harga dari sebuah produk
     */
    public void setPrice(double productPrice){
        price = productPrice;
    }
    
    /**
     * Mengeset stok sebuah produk
     * @param "stock" : ketersediaan barang dari sebuah produk
     */
    public void setStock(int productStock){
        stock = productStock;
    }
    
    /**
     * Mengeset gambar sebuah produk
     * @param "image" : gambar dari sebuah produk
     */
    public void setImage(Bitmap productImage){
        image = productImage;
    }
    
    /**
     * Mengeset deskripsi sebuah produk
     * @param "description" : deskripsi dari sebuah produk
     */
    public void setDesc(String productDesc){
        description = productDesc;
    }
    
    /**
     * Mengeset diskon sebuah produk
     * @param "discount" : diskon dari sebuah produk
     */
    public void setDiscount(double productDiscount){
        discount = productDiscount;
    }
        
    /**
     * Mengeset vendor sebuah produk
     * @param "vendor" : vendor dari sebuah produk
     */
    public void setVendor(String productVendor){
        vendor = productVendor;
    }
    
    /**
     * Mendapatkan id dari sebuah produk
     * @return "id" : idn dari sebuah produk
     */
    public String getId(){
        return id;
    }
    
    /**
     * Mendapatkan nama dari sebuah produk
     * @return "name" : nama dari sebuah produk
     */
    public String getName(){
        return name;
    }  
    
    /**
     * Mendapatkan harga dari sebuah produk
     * @return "price" : harga dari sebuah produk
     */
    public double getPrice(){
        return price;
    }
    
    /**
     * Mendapatkan ketersediaan barang dari sebuah produk
     * @return "stock" : ketersediaan barang dari sebuah produk
     */
    public int getStock(){
        return stock;
    }
    
    /**
     * Mendapatkan gambar dari sebuah produk
     * @return "image" : gambar dari sebuah produk
     */
    public Bitmap getImage(){
        return image;
    }
    
    public String getDesc(){
        return description;
    }
    
    public double getDiscount(){
        return discount;
    }
    
    public String getVendor(){
        return vendor;
    }
    
    void editProduct(){
        
    }
    
    void createProduct(){
        
    }
    
    public String toString()
    {
    	return this.id + ". "+this.name+" [$"+ this.price+" ]";
    }
    
    private class InfoDownloader extends AsyncTask<String,String,JSONObject> {

    	int hh = 0;
        protected JSONObject doInBackground(String... str) {
        	 try {
 	            JSONObject o = Global.getJSONFromUrl(Global.server+"services.php?ct=select_product&id="+str[0]);
 	            return o;
 	        } catch (Exception e) {
 	            e.printStackTrace();
 	            Log.e("erorr at", str[0]);
 	            return null;
 	        }
        }

        @Override
        protected void onPreExecute() {
            Log.i("Async-Example", "onPreExecute Called");

        }

        protected void onPostExecute(JSONObject ret) {
	    	try{
		        if (ret != null) {
		            	id = ret.getString("idProduk");
		            	name = ret.getString("nama");
		            	price = Double.parseDouble(ret.getString("harga"));
		            	stock = Integer.parseInt(ret.getString("stock"));
		        	    vendor = ret.getString("produsen"); // cost child value
		        	    description = ret.getString("deskripsi");
	
		        	    new ImageDownloader().execute(id);
		        	    //Toast.makeText(ProductDetailActivity.this.getApplicationContext(), id, Toast.LENGTH_SHORT);
				}
	        } catch (JSONException e) {
					e.printStackTrace();
				}
	    }
    }
    private class ImageDownloader extends AsyncTask<String,String,Bitmap> {

    	int hh = 0;
        protected Bitmap doInBackground(String... str) {
            // TODO Auto-generated method stub
        	Bitmap bitmap =  Global.downloadBitmap(Global.server+"product_img/"+str[0]+".PNG");
            return bitmap;
        }

        @Override
        protected void onPreExecute() {
            Log.i("Async-Example", "onPreExecute Called");

        }

        protected void onPostExecute(Bitmap result) {
    			image= result;
    			if(adapter!=null)
                adapter.notifyDataSetChanged();   		
    		
        }
    }
}
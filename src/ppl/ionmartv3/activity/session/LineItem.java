package ppl.ionmartv3.activity.session;


public class LineItem {
    private Product product;
    private int quantity;
    
    public LineItem(Product product, int quantity){
        this.product = product;
        this.quantity = quantity;
    }
    
    public void setQuantity(int number){
        this.quantity = number;
    }
 
    public int getQuantity(){
        return quantity;
    }
    
    public double getPricePerItem(){
    	return product.getPrice();
    }
    
    public String getProductName(){
    	return product.getName();
    }
    
    public String getIdProduct(){
    	return product.getId();
    }
    
    public Product getProduct(){
    	return product;
    }
    
    public double getSubTotal(){
    	return quantity*product.getPrice();
    }
}
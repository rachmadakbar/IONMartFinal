package ppl.ionmartv3.activity.session;

import java.util.ArrayList;


public class Schedule {
    private String dateStart = null;
    private String type = null;
    private ArrayList<LineItem> listOfProduk = null;
    private int idSchedule;
    private String name;
    
    public Schedule (String date, String type, int idSchedule, String name){
        this.dateStart = date;
        this.type = type;
        this.listOfProduk = new ArrayList<LineItem>();
        this.idSchedule = idSchedule;
        this.name = name;
    }
    
    public int getId(){
    	return idSchedule;
    }
    public void addLineItem(LineItem l){
    	listOfProduk.add(l);
    }
    
    public void deleteLineItem(LineItem l){
    	listOfProduk.remove(l);
    }
    
    public void setDate(String sDate){
        dateStart = sDate;
    }
    
    public void setName(String name){
    	this.name = name;
    }
    
    public void setType(String type){
    	this.type = type;
    }
    
    public String getDate(){
        return dateStart;
    }
    
    public String getType(){
        return type;
    }
    
    public String getName(){
    	return name;
    }
    
    public double getTotalPrice(){
    	double totalPrice = 0.0;
    	for(int i = 0; i < listOfProduk.size(); i++){
    		totalPrice += listOfProduk.get(i).getSubTotal();
    	}
    	return totalPrice;
    }
    
    public ArrayList<LineItem> getAllLineItem(){
    	return listOfProduk;
    }
}

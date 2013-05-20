package ppl.ionmartv3.activity.session;
import java.util.Date;

public class Customer extends Account{
	String phone;
	String email;
	boolean is_closed;
	long money;
	//XYCoordinate coordinate ;
	String address;
	Date join_on;
	String gender;
	String name;
	String city;
	//List<Review> l;
	public String status;
	
	public Customer(String username, String password){
		//constructor
		super(username,password);
		status = "ok";
		money = 0;
	}
	
	public String getCity()
	{
		return this.city;
	}
	public void setCity(String city)
	{
		this.city = city;
	}
	
	public String getPhone(){
		return phone;
	}
	
	public String getStatus(){
		return status;
	}
	
	public String getEmail(){
		return email;
	}
	
	public long getMoney(){
		return money;
	}
	
	public String Address(){
		return address;
	}
	/*
	public XYCoordinate getCoordinate(){
		return coordinate;
	}
	*/
	public boolean getStatus2(){
		return is_closed;
	}
	
	public void setPhone(String phone)
	{
		this.phone = phone;
	}
	public void setEmail(String email){
		this.email = email;
	}
	
	public void setGender(String gender){
		this.gender = gender;
	}
	
	public void setMoney(long money){
		this.money = money;
	}
	
	public void setAddress(String address){
		this.address = address;
	}
	public String getPassword()
	{
		return this.password;
	}
	public String getAddress()
	{
		return this.address;
	}
	public String getName()
	{
		return this.name;
	}
	public String getGender()
	{
		return this.gender;
	}
	
	/*
	public void setCoordinate(XYCoordinate coordinate){
		this.coordinate = coordinate;
	}
	*/
	public void setName(String name){
		this.name = name;
	}
	
	public void setStatus(String status)
	{
		this.status = status;
	}
	
	public void closeAccount(boolean close){
		this.is_closed = close;
	}
}

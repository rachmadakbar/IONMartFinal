package ppl.ionmartv3.activity.session;
//import java.util.List;

public class Account {
	String username;
	String password;
	//List<Message> l;

	public Account(String username, String password){
		this.username = username;
		this.password = password;
	}
	
	public void setPassword(String password){
		this.password = password;
	}
	
	public void setUsername(String username){
		this.username = username;
	}
	
	public String getUsername(){
		return username;
	}
}

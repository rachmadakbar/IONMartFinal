package ppl.ionmartv3.activity.session;

import java.io.Serializable;
import java.util.Date;

/**
 * Class that describes a row in a list
 */
public class Log implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String description;
	private Date date;
	
	public Log(String id, String description) {
		this.id = id;
		this.description = description;
		this.date = new Date();
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDescription() {
		return description;
	}
	public String getDate(){
		return date.toString();
	}
	
}

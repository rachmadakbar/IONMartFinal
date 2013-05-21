package ppl.ionmartv3.activity.session;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that describes a row in a list
 */
public class Schedule implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String type;
	private ArrayList<String> date;
	private int repeat;
	private HashMap<String, LineItem> map;
	private boolean paid;

	public Schedule(String name, String type) {
		this.name = name;
		this.type = type;
		this.date = new ArrayList<String>();
		this.map = new HashMap<String, LineItem>();
		paid = true;
	}

	public Schedule(String name, String type, String repeat, int y, int m, int d) {
		this.paid = false;
		this.name = name;
		this.type = type;
		this.date = new ArrayList<String>();
		this.repeat = Integer.parseInt(repeat);
		this.map = new HashMap<String, LineItem>();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.set(y, m, d);
		date.add(dateFormat.format(cal.getTime()));
		if (type.equalsIgnoreCase("Daily")) {
			for (int i = 1; i < this.repeat; i++) {
				cal.add(Calendar.DATE, 1);
				date.add(dateFormat.format(cal.getTime()));
			}
		} else if (type.equalsIgnoreCase("Weekly")) {
			for (int i = 1; i < this.repeat; i++) {
				cal.add(Calendar.DATE, 7);
				date.add(dateFormat.format(cal.getTime()));
			}
		} else if (type.equalsIgnoreCase("Monthly")) {
			for (int i = 1; i < this.repeat; i++) {
				cal.add(Calendar.MONTH, 1);
				date.add(dateFormat.format(cal.getTime()));
			}
		} else {
			for (int i = 1; i < this.repeat; i++) {
				cal.add(Calendar.YEAR, 1);
				date.add(dateFormat.format(cal.getTime()));
			}
		}

	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public void addDate(String date) {
		this.date.add(date);
	}

	public void addLineItem(LineItem l) {
		if (map.containsKey(l.getIdProduct())) {
			map.get(l.getIdProduct()).updateQuantity(l.getQuantity());
		} else {
			map.put(l.getIdProduct(), l);
		}
	}

	public void deleteLineItem(LineItem l) {
		map.remove(l.getIdProduct());
	}

	public String getDate() {
		String result = "";
		for (int i = 0; i < date.size(); i++) {
			if (i % 4 == 0)
				result += "\n";
			result += date.get(i) + " ";
		}
		return result;
	}

	public int getRepeated() {
		return this.repeat;
	}

	public HashMap<String, LineItem> getAllLineItem() {
		return this.map;
	}

	public boolean isAlreadyExist(String id) {
		return map.containsKey(id);
	}

	public double getTotalPrice() {
		double total = 0.0;
		for (Map.Entry<String, LineItem> entry : map.entrySet()) {
			total += entry.getValue().getSubTotal();
		}
		return total;
	}
	
	public double getTotalSchedulePrice(){
		return getTotalPrice()*repeat;
	}
	public void setQuantity(LineItem l, int quantity){
		map.get(l.getIdProduct()).setQuantity(quantity);
	}
	
	public boolean getPaid(){
		return paid;
	}
}

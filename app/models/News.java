package models;

import java.util.Date;

import play.db.jpa.GenericModel;

public class News extends GenericModel {
	public int id;
	public String title;
	public String detail;
	public Date timestamp;
	public String user;

	public News(int id, String title, String detail, Date timestamp, String user) {
		this.id = id;
		this.title = title;
		this.detail = detail;
		this.timestamp = timestamp;
		this.user = user;
	}
}

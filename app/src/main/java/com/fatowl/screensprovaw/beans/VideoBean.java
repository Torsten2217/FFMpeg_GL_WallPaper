package com.fatowl.screensprovaw.beans;

public class VideoBean {

	private int id;
	private String name;
	private String keywords;
	private String datecreated;
	private String thumblow;
	private String thumbmid;
	private String videophonelow;
	private String videophonemid;
	private String videophonehigh;
	private String videotabletmid;
	private String videotablethigh;
	private int isFavourite;

	public int getIsFavourite() {
		return isFavourite;
	}

	public void setIsFavourite(int isFavourite) {
		this.isFavourite = isFavourite;
	}

	public int getid() {
		return id;
	}

	public void setid(int id) {
		this.id = id;
	}

	public String getname() {
		return name;
	}

	public void setname(String name) {
		this.name = name;
	}

	public String getkeywords() {
		return keywords;
	}

	public void setkeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getdateCreated() {
		return datecreated;
	}

	public void setdateCreated(String datecreated) {
		this.datecreated = datecreated;
	}

	public String getthumbLow() {
		return thumblow;
	}

	public void setthumbLow(String thumblow) {
		this.thumblow = thumblow;
	}

	public String getthumbMid() {
		return thumbmid;
	}

	public void setthumbMid(String thumbmid) {
		this.thumbmid = thumbmid;
	}

	public String getvideoPhoneLow() {
		return videophonelow;
	}

	public void setvideoPhoneLow(String videophonelow) {
		this.videophonelow = videophonelow;
	}

	public String getvideoPhoneMid() {
		return videophonemid;
	}

	public void setvideoPhoneMid(String videophonemid) {
		this.videophonemid = videophonemid;
	}

	public String getvideoPhoneHigh() {
		return videophonehigh;
	}

	public void setvideoPhoneHigh(String videophonehigh) {
		this.videophonehigh = videophonehigh;
	}

	public String getvideoTabletMid() {
		return videotabletmid;
	}

	public void setvideoabletMid(String videotabletmid) {
		this.videotabletmid = videotabletmid;
	}

	public String getvideotablethigh() {
		return videotablethigh;
	}

	public void setvideoTabletHigh(String videotablethigh) {
		this.videotablethigh = videotablethigh;
	}

	public VideoBean() {

	}

	public VideoBean(int id, String name, String keywords,
			String datecreated, String thumblow, String thumbmid,
			String videophonelow, String videophonemid, String videophonehigh,
			String videotabletmid, String videotablethigh, int isFavourite) {

		this.id = id;
		this.name = name;
		this.keywords = keywords;
		this.datecreated = datecreated;

		this.thumblow = thumblow;
		this.thumbmid = thumbmid;

		this.videophonelow = videophonelow;
		this.videophonemid = videophonemid;
		this.videophonehigh = videophonehigh;

		this.videotabletmid = videotabletmid;
		this.videotablethigh = videotablethigh;
		this.isFavourite = isFavourite;
	}

	public VideoBean(int id, String name, String keywords,
			String datecreated, String thumblow, String thumbmid,
			String videophonelow, String videophonemid, String videophonehigh,
			String videotabletmid, String videotablethigh) {

		this.id = id;
		this.name = name;
		this.keywords = keywords;
		this.datecreated = datecreated;

		this.thumblow = thumblow;
		this.thumbmid = thumbmid;

		this.videophonelow = videophonelow;
		this.videophonemid = videophonemid;
		this.videophonehigh = videophonehigh;

		this.videotabletmid = videotabletmid;
		this.videotablethigh = videotablethigh;
	}

}

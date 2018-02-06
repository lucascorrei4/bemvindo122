package util;

import java.util.List;

import models.SequenceMail;

public class SequenceMailTO {
	String url;
	List<SequenceMail> listSequenceMail;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<SequenceMail> getListSequenceMail() {
		return listSequenceMail;
	}

	public void setListSequenceMail(List<SequenceMail> listSequenceMail) {
		this.listSequenceMail = listSequenceMail;
	}

}
package util.howtodo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;

import models.howtodo.SequenceMail;
import models.howtodo.SequenceMailQueue;

public class SequenceMailTO {
	String url;
	List<SequenceMail> listSequenceMail;

	Long total;
	Long totalSent;
	Long totalOpened;
	Long totalClicked;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<SequenceMail> getListSequenceMail() {
		if (this.listSequenceMail == null) {
			setListSequenceMail(new ArrayList<SequenceMail>());
		}
		return listSequenceMail;
	}

	public void setListSequenceMail(List<SequenceMail> listSequenceMail) {
		this.listSequenceMail = listSequenceMail;
	}

	public Long getTotalSent() {
		return totalSent;
	}

	public void setTotalSent(Long totalSent) {
		this.totalSent = totalSent;
	}

	public Long getTotalOpened() {
		return totalOpened;
	}

	public void setTotalOpened(Long totalOpened) {
		this.totalOpened = totalOpened;
	}

	public Long getTotalClicked() {
		return totalClicked;
	}

	public void setTotalClicked(Long totalClicked) {
		this.totalClicked = totalClicked;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

}
package models;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import play.db.jpa.Model;

@Entity
public class Country extends Model {
	public String name;
	
	@GenericGenerator(name="generator", strategy="sequency")
	@GeneratedValue(generator="generator")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public static Country verifyById(long id) {
		return find("byId", id).first();
	}

}

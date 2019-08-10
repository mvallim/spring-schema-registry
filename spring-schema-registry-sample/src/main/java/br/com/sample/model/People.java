package br.com.sample.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "id", "name", "height", "mass", "hair_color", "skin_color", "eye_color", "birth_year", "gender",
		"created", "edited" })
public class People {

	private String id;
	private String name;
	private int height;
	private int mass;
	private @JsonProperty("hair_color") String hairColor;
	private @JsonProperty("skin_color") String skinColor;
	private @JsonProperty("eye_color") String eyeColor;
	private @JsonProperty("birth_year") String birthYear;
	private String gender;
	private Date created;
	private Date edited;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getHeight() {
		return this.height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getMass() {
		return this.mass;
	}

	public void setMass(int mass) {
		this.mass = mass;
	}

	public String getHairColor() {
		return this.hairColor;
	}

	public void setHairColor(String hairColor) {
		this.hairColor = hairColor;
	}

	public String getSkinColor() {
		return this.skinColor;
	}

	public void setSkinColor(String skinColor) {
		this.skinColor = skinColor;
	}

	public String getEyeColor() {
		return this.eyeColor;
	}

	public void setEyeColor(String eyeColor) {
		this.eyeColor = eyeColor;
	}

	public String getBirthYear() {
		return this.birthYear;
	}

	public void setBirthYear(String birthYear) {
		this.birthYear = birthYear;
	}

	public String getGender() {
		return this.gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Date getCreated() {
		return this.created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getEdited() {
		return this.edited;
	}

	public void setEdited(Date edited) {
		this.edited = edited;
	}
}

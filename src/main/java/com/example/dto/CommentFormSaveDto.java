package com.example.dto;

public class CommentFormSaveDto {

	private String experience;
	private String comment;
	private String name;
	private String email;

	public String getExperience() {
		return experience;
	}

	public void setExperience(String experience) {
		this.experience = experience;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return String.format("CommentFormSaveDto [experience=%s, comment=%s, name=%s, email=%s]", experience, comment,
				name, email);
	}

}

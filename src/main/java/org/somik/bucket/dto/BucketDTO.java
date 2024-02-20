package org.somik.bucket.dto;

public class BucketDTO {
	public BucketDTO(String name) {
		super();
		this.name = name;
	}

	public BucketDTO() {
		this("");
	}

	public String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

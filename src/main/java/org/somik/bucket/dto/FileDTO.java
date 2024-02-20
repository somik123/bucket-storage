package org.somik.bucket.dto;

public class FileDTO {
	public FileDTO(String name, String message, String url) {
		super();
		this.name = name;
		this.message = message;
		this.url = url;
	}

	public FileDTO(String name, String url) {
		this(name, "", url);
	}

	public FileDTO(String message) {
		this("", message, "");
	}

	public FileDTO() {
		this("", "", "");
	}

	private String name;
	private String message;
	private String url;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}

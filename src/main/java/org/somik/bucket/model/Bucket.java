package org.somik.bucket.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.somik.bucket.util.CommonUtils;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Bucket {

	private static final String disable = "DISABLED";

	@Id
	private String id;

	private String name;
	private String uploadKey;
	private String downloadKey;
	private LocalDateTime created;
	private Boolean active;

	public Bucket(String name) {
		super();
		this.id = UUID.randomUUID().toString();
		this.name = name;
		this.uploadKey = CommonUtils.randString();
		this.downloadKey = CommonUtils.randString();
		this.created = LocalDateTime.now();
		this.active = true;
	}

	public Bucket() {
		this("");
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUploadKey() {
		return uploadKey;
	}

	public void setUploadKey(String uploadKey) {
		this.uploadKey = uploadKey;
	}

	public void disableUploadKey(){
		uploadKey = disable;
	}

	public boolean isUploadKeyDisabled(){
		return (uploadKey.equals(disable)) ? true : false;
	}

	public String getDownloadKey() {
		return downloadKey;
	}

	public void setDownloadKey(String downloadKey) {
		this.downloadKey = downloadKey;
	}

	public void disableDownKey(){
		downloadKey = disable;
	}

	public boolean isDownKeyDisabled(){
		return (downloadKey.equals(disable)) ? true : false;
	}


	public LocalDateTime getCreated() {
		return created;
	}

	public void setCreated(LocalDateTime created) {
		this.created = created;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

}

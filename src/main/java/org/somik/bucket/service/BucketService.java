package org.somik.bucket.service;

import java.util.List;

import org.somik.bucket.model.Bucket;

public interface BucketService {
	public List<Bucket> getAllBuckets();

	public Bucket createBucket(String name);

	public String disableUploadKey(String id);

	public String resetUploadKey(String id);

	public String disableDownloadKey(String id);

	public String resetDownloadKey(String id);

	public Boolean deleteBucket(String id);

	public Bucket getBucketById(String uploadKey);
	// public Bucket getBucketByUploadKey(String uploadKey);
	// public Bucket getBucketByDownloadKey(String downloadKey);
}

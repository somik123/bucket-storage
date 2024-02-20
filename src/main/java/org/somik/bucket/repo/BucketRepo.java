package org.somik.bucket.repo;

import org.somik.bucket.model.Bucket;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface BucketRepo extends CrudRepository<Bucket, String> {

	@Query("SELECT b FROM Bucket b WHERE b.name = ?1")
	public Bucket findByName(String name);

	@Query("SELECT b FROM Bucket b WHERE b.uploadKey = ?1")
	public Bucket findByUploadKey(String uploadKey);

	@Query("SELECT b FROM Bucket b WHERE b.downloadKey = ?1")
	public Bucket findByDownloadKey(String downloadKey);
}

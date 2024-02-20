package org.somik.bucket.service;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.somik.bucket.model.Bucket;
import org.somik.bucket.repo.BucketRepo;
import org.somik.bucket.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BucketServiceImpl implements BucketService {

	@Autowired
	BucketRepo bucketRepo;

	private Logger log = Logger.getLogger(BucketService.class.getName());

	@Override
	public List<Bucket> getAllBuckets() {
		List<Bucket> bucketList = (List<Bucket>) bucketRepo.findAll();
		Collections.sort(bucketList, (o1, o2) -> (o1.getName().compareTo(o2.getName())));
		return bucketList;
	}

	@Override
	public Bucket createBucket(String name) {
		if (name.length() < 3) {
			log.warning("Bucket name ["+name+"] is less then 3 chars long.");
			return null;
		}
		name = CommonUtils.cleanFileName(name);

		Bucket bucket = bucketRepo.findByName(name);
		if (bucket != null)
			return null;
		bucket = new Bucket(name);

		String newKey;
		if (!isUploadKeyUnique(bucket.getUploadKey())) {
			do {
				newKey = CommonUtils.randString();
			} while (!isUploadKeyUnique(newKey));
			bucket.setUploadKey(newKey);
		}
		if (!isDownloadKeyUnique(bucket.getDownloadKey())) {
			do {
				newKey = CommonUtils.randString();
			} while (!isDownloadKeyUnique(newKey));
			bucket.setDownloadKey(newKey);
		}

		bucketRepo.save(bucket);
		return bucket;
	}

	@Override
	public String resetUploadKey(String id) {
		Bucket bucket = getBucketById(id);
		if (bucket == null)
			return "";

		String newKey;
		do {
			newKey = CommonUtils.randString();
		} while (!isUploadKeyUnique(newKey));

		bucket.setUploadKey(newKey);
		bucketRepo.save(bucket);
		return newKey;
	}

	@Override
	public String resetDownloadKey(String id) {
		Bucket bucket = getBucketById(id);
		if (bucket == null)
			return "";

		String newKey;
		do {
			newKey = CommonUtils.randString();
		} while (!isDownloadKeyUnique(newKey));

		bucket.setDownloadKey(newKey);
		bucketRepo.save(bucket);
		return newKey;
	}

	@Override
	public Boolean deleteBucket(String id) {
		Bucket bucket = getBucketById(id);
		if (bucket == null)
			return false;
		else {
			FileStorageService storageService = new FileStorageService(bucket);
			storageService.deleteAll();
			bucketRepo.delete(bucket);
			return true;
		}
	}

	private boolean isUploadKeyUnique(String key) {
		Bucket bucket = bucketRepo.findByUploadKey(key);
		return (bucket != null) ? false : true;
	}

	private boolean isDownloadKeyUnique(String key) {
		Bucket bucket = bucketRepo.findByDownloadKey(key);
		return (bucket != null) ? false : true;
	}

	@Override
	public Bucket getBucketById(String id) {
		if (id == null || id.isEmpty())
			return null;
		try {
			return bucketRepo.findById(id).get();
		} catch (Exception e) {
			log.warning("Unable to get bucket by ID: " + id);
			log.warning(e.getMessage());
			log.warning(e.getStackTrace().toString());
			return null;
		}
	}

	@Override
	public String disableUploadKey(String id) {
		Bucket bucket = getBucketById(id);
		if (bucket == null)
			return "";

		bucket.disableUploadKey();
		bucketRepo.save(bucket);
		return bucket.getUploadKey();
	}

	@Override
	public String disableDownloadKey(String id) {
		Bucket bucket = getBucketById(id);
		if (bucket == null)
			return "";

		bucket.disableDownKey();
		bucketRepo.save(bucket);
		return bucket.getDownloadKey();
	}

}

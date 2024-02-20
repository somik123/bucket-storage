package org.somik.bucket.controller;

import org.somik.bucket.dto.BucketDTO;
import org.somik.bucket.dto.ResponseDTO;
import org.somik.bucket.model.Bucket;
import org.somik.bucket.service.BucketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class ApiController {

	@Autowired
	private BucketService bucketService;

	@PostMapping("/create")
	public ResponseDTO createBucket(@RequestBody BucketDTO bucketDto) {
		Bucket bucket = bucketService.createBucket(bucketDto.getName());
		return (bucket != null) ? new ResponseDTO("OK", bucket)
				: new ResponseDTO("FAIL", "", "Unable to create bucket");
	}

	@GetMapping("/resetUploadKey/{id}")
	public ResponseDTO resetUploadKey(@PathVariable String id) {
		String key = bucketService.resetUploadKey(id);
		return (key.isEmpty()) ? new ResponseDTO("FAIL", "", "Unable to reset upload key") : new ResponseDTO("OK", key);
	}

	@GetMapping("/disableUploadKey/{id}")
	public ResponseDTO disableUploadKey(@PathVariable String id) {
		String key = bucketService.disableUploadKey(id);
		return (key.isEmpty()) ? new ResponseDTO("FAIL", "", "Unable to disable upload key")
				: new ResponseDTO("OK", key);
	}

	@GetMapping("/resetDownloadKey/{id}")
	public ResponseDTO resetDownloadKey(@PathVariable String id) {
		String key = bucketService.resetDownloadKey(id);
		return (key.isEmpty()) ? new ResponseDTO("FAIL", "", "Unable to reset download key")
				: new ResponseDTO("OK", key);
	}

	@GetMapping("/disableDownloadKey/{id}")
	public ResponseDTO disableDownloadKey(@PathVariable String id) {
		String key = bucketService.disableDownloadKey(id);
		return (key.isEmpty()) ? new ResponseDTO("FAIL", "", "Unable to disable download key")
				: new ResponseDTO("OK", key);
	}

	@GetMapping("/delete/{id}")
	public ResponseDTO deleteBucket(@PathVariable String id) {
		return (bucketService.deleteBucket(id)) ? new ResponseDTO("OK") : new ResponseDTO("FAIL");
	}
}

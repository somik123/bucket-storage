package org.somik.bucket.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

import org.somik.bucket.dto.FileDTO;
import org.somik.bucket.dto.FileDetailsDTO;
import org.somik.bucket.dto.ResponseDTO;
import org.somik.bucket.model.Bucket;
import org.somik.bucket.service.BucketService;
import org.somik.bucket.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping
public class FileController {

	@Autowired
	BucketService bucketService;

	private Logger log = Logger.getLogger(FileController.class.getName());

	@GetMapping(path = "/bucket/{id}/{uploadKey}/{downloadKey}/{uploadPath}")
	public String getAllFiles(@PathVariable String id, @PathVariable String uploadKey, @PathVariable String downloadKey,
			@PathVariable String uploadPath, Model model) {
		return getAllFiles(id, uploadKey, downloadKey, model);
	}

	@GetMapping(path = "/bucket/{id}/{uploadKey}/{downloadKey}", produces = MediaType.APPLICATION_JSON_VALUE)
	public String getAllFiles(@PathVariable String id, @PathVariable String uploadKey, @PathVariable String downloadKey,
			Model model) {
		log.info("Accessing bucket via GET:" + id);
		Bucket bucket = bucketService.getBucketById(id);
		if (bucket == null || bucket.isDownKeyDisabled() || bucket.isUploadKeyDisabled()
				|| !bucket.getUploadKey().equals(uploadKey) || !bucket.getDownloadKey().equals(downloadKey)) {
			return "error";
		}
		String uploadUrl = "/upload/" + bucket.getId() + "/" + bucket.getUploadKey();

		FileStorageService storageService = new FileStorageService(bucket);
		List<FileDetailsDTO> fileDetailsDTOs = storageService.loadAll();
		model.addAttribute("files", fileDetailsDTOs);
		model.addAttribute("max", fileDetailsDTOs.size() - 1);
		model.addAttribute("uploadUrl", uploadUrl);

		return "bucket";
	}

	@PostMapping(path = "/upload/{id}/{uploadKey}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseDTO> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable String id,
			@PathVariable String uploadKey) {
		return uploadFile(file, id, uploadKey, "");
	}

	@PostMapping(path = "/upload/{id}/{uploadKey}/{uploadPath}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseDTO> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable String id,
			@PathVariable String uploadKey, @PathVariable String uploadPath) {
		String message = "";
		Bucket bucket = bucketService.getBucketById(id);
		if (bucket == null) {
			message = "Bucket does not exist.";
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO("FAIL", "", message));
		} else if (bucket.isUploadKeyDisabled()) {
			message = "Bucket upload key disabled.";
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO("FAIL", "", message));
		} else if (!bucket.getUploadKey().equals(uploadKey)) {
			message = "Bucket upload key not provided or does not match.";
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO("FAIL", "", message));
		}
		try {
			FileStorageService storageService = new FileStorageService(bucket);

			String fileName = storageService.save(file, uploadPath);
			message = "Upload successful.";
			String url = storageService.getDownloadLink(fileName);
			FileDTO fileDto = new FileDTO(fileName, message, url);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO("OK", fileDto));
		} catch (Exception e) {
			message = "Upload failed on: " + file.getOriginalFilename() + "!";
			log.warning(message);
			log.warning(e.getMessage());
			log.warning(e.getStackTrace().toString());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO("FAIL", "", message));
		}
	}

	@GetMapping(path = "/download/{id}/{downloadKey}/{downloadFile}")
	public ResponseEntity<?> downloadFile(@PathVariable String id, @PathVariable String downloadKey,
			@PathVariable String downloadFile) {
		return downloadFile(id, downloadKey, "", downloadFile);
	}

	@GetMapping(path = "/download/{id}/{downloadKey}/{downloadPath}/{downloadFile}")
	public ResponseEntity<?> downloadFile(@PathVariable String id, @PathVariable String downloadKey,
			@PathVariable String downloadPath, @PathVariable String downloadFile) {
		String message = "";
		Bucket bucket = bucketService.getBucketById(id);
		if (bucket == null) {
			message = "Bucket does not exist.";
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new FileDTO(message));
		} else if (bucket.isDownKeyDisabled()) {
			message = "Bucket download key disabled.";
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO("FAIL", "", message));
		} else if (!bucket.getDownloadKey().equals(downloadKey)) {
			message = "Bucket download key not provided or does not match.";
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new FileDTO(message));
		}
		try {
			FileStorageService storageService = new FileStorageService(bucket);

			Resource file = storageService.load(downloadFile, downloadPath);
			InputStream in = file.getInputStream();
			String mimeType = storageService.getMimeType(downloadFile, downloadPath);
			if (mimeType == null || mimeType.isEmpty()) {
				mimeType = "application/octet-stream";
			}
			MediaType mediaType = MediaType.parseMediaType(mimeType);
			log.info(file.toString());
			log.info(mediaType.toString());

			return ResponseEntity.ok().contentType(mediaType)
					.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
					.body(new InputStreamResource(in));

		} catch (IOException e) {
			message = "Error while processing file.";
			log.warning(message);
			log.warning(e.getMessage());
			log.warning(e.getStackTrace().toString());
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new FileDTO(message));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new FileDTO(e.getMessage()));
		}
	}

	@GetMapping(path = "/delete/{id}/{uploadKey}/{deleteFile}")
	public ResponseEntity<ResponseDTO> deleteFile(@PathVariable String id, @PathVariable String uploadKey,
			@PathVariable String deleteFile) {
		return deleteFile(id, uploadKey, "", deleteFile);
	}

	@GetMapping(path = "/delete/{id}/{uploadKey}/{deletePath}/{deleteFile}")
	public ResponseEntity<ResponseDTO> deleteFile(@PathVariable String id, @PathVariable String uploadKey,
			@PathVariable String deletePath,
			@PathVariable String deleteFile) {
		String message = "";
		Bucket bucket = bucketService.getBucketById(id);
		if (bucket == null) {
			message = "Bucket does not exist.";
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO("FAIL", "", message));
		} else if (bucket.isUploadKeyDisabled()) {
			message = "Bucket upload key disabled.";
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO("FAIL", "", message));
		} else if (!bucket.getUploadKey().equals(uploadKey)) {
			message = "Bucket upload key not provided or does not match.";
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO("FAIL", "", message));
		}
		FileStorageService storageService = new FileStorageService(bucket);
		Boolean status = storageService.deleteFile(deleteFile, deletePath);
		if (status) {
			return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO("OK"));
		} else {
			message = "Failed to delete file.";
			return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO("FAIL", "", message));
		}
	}
}

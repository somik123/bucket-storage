package org.somik.bucket.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.somik.bucket.controller.FileController;
import org.somik.bucket.dto.FileDetailsDTO;
import org.somik.bucket.model.Bucket;
import org.somik.bucket.util.CommonUtils;
import org.springframework.boot.web.server.MimeMappings;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

public class FileStorageService {

	private final String rootName = "storage/";
	private Path bucketPath = null;
	private Bucket bucket = null;

	private Logger log = Logger.getLogger(FileController.class.getName());

	public FileStorageService(Bucket bucket) {
		try {
			bucketPath = Paths.get(rootName + bucket.getName() + "_[" + bucket.getId() + "]");
			this.bucket = bucket;
			Files.createDirectories(bucketPath);
		} catch (IOException e) {
			log.warning("Could not initialize folder: " + bucketPath.toString());
			log.warning(e.getMessage());
			log.warning(e.getStackTrace().toString());
		}
	}

	public String save(MultipartFile file, String uploadPath) {
		try {
			String fileName = file.getOriginalFilename();
			if (fileName == null || fileName.length() < 2){
				log.info("Missing filename. Generating random file name");
				return null;
			}
			if (fileName.length() == 0 || fileName.contains("..") || uploadPath.contains("..")) {
				log.warning("Invalid file name or upload path.");
				return null;
			}
			fileName = CommonUtils.cleanFileName(fileName);
			if (uploadPath.length() > 0) {
				uploadPath = CommonUtils.cleanFileName(uploadPath);
				fileName = uploadPath + "/" + fileName;
				Files.createDirectories(bucketPath.resolve(uploadPath));
			}
			log.info("Uploaded file: " + fileName + " to bucket " + bucket.getId());
			Files.copy(file.getInputStream(), bucketPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
			return fileName;
		} catch (Exception e) {
			log.warning("Could not store the file.");
			log.warning(e.getMessage());
			log.warning(e.getStackTrace().toString());
			return null;
		}
	}

	public Resource load(String fileName, String downloadPath) {
		try {
			if (downloadPath.length() > 0) {
				fileName = downloadPath + "/" + fileName;
			}
			Path file = bucketPath.resolve(fileName);
			URI uri = file.toUri();
			if (uri == null) {
				log.warning("Could not read the file: " + file.toString());
				return null;
			}
			Resource resource = new UrlResource(uri);
			if (resource.exists() && resource.isReadable()) {
				return resource;
			} else {
				log.warning("Could not read the file: " + fileName);
				return null;
			}
		} catch (MalformedURLException e) {
			log.warning("Could not load the file: " + fileName);
			log.warning(e.getMessage());
			log.warning(e.getStackTrace().toString());
			return null;
		}
	}

	public String getMimeType(String fileName, String downloadPath) {
		String mimeType = "";
		try {
			if (downloadPath.length() > 0) {
				fileName = downloadPath + "/" + fileName;
			}
			Path file = bucketPath.resolve(fileName);
			int i = file.toString().lastIndexOf('.');
			String ext = "";
			if (i > 0) {
				ext = file.toString().substring(i + 1);
				mimeType = MimeMappings.DEFAULT.get(ext);
			}
		} catch (Exception e) {
			log.warning("Could not read the mimeType for file: " + fileName);
			log.warning(e.getMessage());
			log.warning(e.getStackTrace().toString());
			return null;
		}
		return mimeType;
	}

	public Boolean deleteFile(String fileName, String deletePath) {
		Boolean status = false;
		try {
			if (deletePath.length() > 0) {
				fileName = deletePath + "/" + fileName;
			}
			Path file = bucketPath.resolve(fileName);
			if (Files.exists(file) && Files.isWritable(file)) {
				Files.delete(file);
				if (!Files.exists(file))
					status = true;
			}
		} catch (IOException e) {
			log.warning("Could not delete file: " + fileName);
			log.warning(e.getMessage());
			log.warning(e.getStackTrace().toString());
			return null;
		}
		return status;
	}

	public void deleteAll() {
		FileSystemUtils.deleteRecursively(bucketPath.toFile());
	}

	public List<FileDetailsDTO> loadAll() {
		try {
			Stream<Path> paths = Files.walk(bucketPath, 3).filter(path -> !path.equals(bucketPath));

			List<FileDetailsDTO> fileDetailsList = new ArrayList<>();
			paths.forEach(path -> {
				try {
					if (Files.isRegularFile(path)) { // Skip directories
						String name = path.toString().replace(bucketPath.toString(), "").substring(1).replace("\\",
								"/");
						String size = CommonUtils.formatSize(Files.size(path));
						String url = this.getDownloadLink(name);

						FileDetailsDTO fileDetails = new FileDetailsDTO();
						fileDetails.setName(name);
						fileDetails.setSize(size);
						fileDetails.setModified(Files.getLastModifiedTime(path).toString());
						fileDetails.setUrl(url);
						fileDetailsList.add(fileDetails);
						log.info(fileDetails.toString());
					}
				} catch (IOException e) {
					log.warning("Could not read all files: ");
					log.warning(e.getMessage());
					log.warning(e.getStackTrace().toString());
				} catch (Exception e) {
					log.warning("Could not read all files: ");
					log.warning(e.getMessage());
					log.warning(e.getStackTrace().toString());
				}
			});
			return fileDetailsList;
		} catch (IOException e) {
			log.warning("Could not load all files on bucket: " + bucket.getId());
			log.warning(e.getMessage());
			log.warning(e.getStackTrace().toString());
			return null;
		}
	}

	public String getDownloadLink(String fileName) {
		String url = "";
		try {
			String urlDecoded = "/download/" + bucket.getId() + "/" + bucket.getDownloadKey() + "/" + fileName;
			url = UriUtils.encodePath(urlDecoded, "UTF-8");
		} catch (Exception e) {
			log.warning("Error encoding parameter ");
			log.warning(e.getMessage());
			log.warning(e.getStackTrace().toString());
			return null;
		}
		return url;
	}

}

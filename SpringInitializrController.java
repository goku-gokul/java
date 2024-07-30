package com.springioapi.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springioapi.service.SpringInitializrService;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class SpringInitializrController {
	private static final Logger logger = LoggerFactory.getLogger(SpringInitializrController.class);

	@Autowired
	private SpringInitializrService springInitializrService;

	@GetMapping(value = "/generate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<FileSystemResource> generateProject(@RequestParam String dependencies,
			@RequestParam String type, @RequestParam String language, @RequestParam String javaVersion) {
		try {
			springInitializrService.generateProject(dependencies, type, language, javaVersion);
			Path zipFilePath = Paths.get(springInitializrService.baseDir, springInitializrService.artifactId + ".zip");
			if (Files.notExists(zipFilePath)) {
				logger.error("Generated file not found: {}", zipFilePath.toAbsolutePath());
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
			FileSystemResource fileSystemResource = new FileSystemResource(zipFilePath);

			HttpHeaders headers = new HttpHeaders();

			headers.add(HttpHeaders.CONTENT_DISPOSITION,
					"attachment; filename=" + springInitializrService.artifactId + ".zip");

			return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM)
					.body(fileSystemResource);

		} catch (Exception e) {
			logger.error("Error generating project", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}

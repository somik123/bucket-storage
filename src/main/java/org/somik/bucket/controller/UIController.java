package org.somik.bucket.controller;

import java.util.List;

import org.somik.bucket.model.Bucket;
import org.somik.bucket.service.BucketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping
public class UIController {
	@Autowired
	BucketService bucketService;

	@GetMapping("/admin")
	public String adminUI(Model model) {
		List<Bucket> bucketList = bucketService.getAllBuckets();
		model.addAttribute("bucketList", bucketList);
		model.addAttribute("max", bucketList.size() - 1);
		return "admin";
	}

	@GetMapping("/login")
	public String loginPage() {
		return "login";
	}

	@GetMapping("/")
	public RedirectView indexPage() {
		return new RedirectView("/admin");
	}

}

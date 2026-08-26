package com.contact.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.contact.dao.UserRepository;
import com.contact.entities.User;
import com.contact.helper.Message;

import jakarta.validation.Valid;

@Controller
public class UserController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Home - Smart Contact Manager");
		return "home";
	}

	@GetMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "About - Smart Contact Manager");
		return "about";
	}

	@GetMapping("/signup")
	public String signup(Model model) {

		model.addAttribute("title", "SignUp - Smart Contact Manager");

		model.addAttribute("user", new User());

		return "signup";
	}

	@PostMapping("/do_register")
	public String register(@Valid @ModelAttribute("user") User user, BindingResult result,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			RedirectAttributes redirectAttribute) {

		try {
		if (result.hasErrors()) {
			return "signup";
		}

		// 2. SECOND CHECK: Terms & Conditions
		if (!agreement) {
			throw new Exception("You have not agreed to the terms and conditions");
		}

		// 3. Set default values
		user.setRole("ROLE_CUSTOMER");
		user.setEnabled(true);
		user.setImageUrl("default.png");
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		// 4. Save user

			userRepository.save(user);
			model.addAttribute("user", new User());

			redirectAttribute.addFlashAttribute("message", new Message("Successfully Registered !!", "alert-success"));

			return "redirect:/signup";

		} catch (Exception e) {

			e.printStackTrace();
			model.addAttribute("user", user);
			redirectAttribute.addFlashAttribute("message",
					new Message("Something went wrong !! " + e.getMessage(), "alert-danger"));

			return "signup";
		}
	}
	
	@GetMapping("/signin")
	public String signin(Model model) {
		model.addAttribute("title", "Login - Smart Contact Manager");	
		return "login";
	}
}
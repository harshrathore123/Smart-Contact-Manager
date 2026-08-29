package com.contact.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.contact.dao.ContactRepository;
import com.contact.dao.UserRepository;
import com.contact.entities.Contact;
import com.contact.entities.User;
import com.contact.helper.Message;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/customer")
public class CustomerController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;

	@GetMapping("/data")
	public String customer_dashboard(Model model, Principal principal) {

		// Getting Username
		String username = principal.getName();

		// Getting UserDetail
		User user = userRepository.getUserByEmail(username);

		model.addAttribute("user", user);
		model.addAttribute("title", "Customer Dashboard - Smart Contact Manager");
		return "customer/customer_dashboard";
	}

	// Open Add Contact Form
	@GetMapping("/addContact")
	public String addContact(Model model, Principal principal) {

		// Getting Username
		String username = principal.getName();
		System.out.println("Username" + username);

		// Getting UserDetail
		User user = userRepository.getUserByEmail(username);
		System.out.println("User: " + user);

		model.addAttribute("user", user);

		model.addAttribute("contact", new Contact());
		model.addAttribute("title", "Customer Add Contact - Smart Contact Manager");
		return "customer/customer_add_contact";
	}

	// Processing Add Contact Form
	@PostMapping("/process-contact")
	public String processContact(
	        @Valid @ModelAttribute Contact contact,
	        BindingResult result,
	        @RequestParam("profileImage") MultipartFile file,
	        Model model,
	        Principal principal,
	        RedirectAttributes redirectAttribute) {

	    try {

	        if (result.hasErrors()) {

	            User user = userRepository.getUserByEmail(principal.getName());

	            model.addAttribute("user", user);
	            model.addAttribute(
	                    "title",
	                    "Customer Add Contact - Smart Contact Manager"
	            );

	            return "customer/customer_add_contact";
	        }

	        // Getting Username
	        String username = principal.getName();

	        System.out.println("Username " + username);

	        User usern = this.userRepository.getUserByEmail(username);

	        contact.setUser(usern);
	        usern.getContact().add(contact);


	        // =========================
	        // IMAGE UPLOADING
	        // =========================
	        System.out.println("AUTO DEPLOY TEST - Render");
	        if (file.isEmpty()) {

	            System.out.println("File is empty");

	            redirectAttribute.addFlashAttribute(
	                    "message",
	                    new Message("Image Not Uploaded or Image Name Already Available !!", "alert-danger")
	            );

	            return "redirect:/customer/addContact";

	        } else {

	            // Set image name
	            contact.setImage(file.getOriginalFilename());


	            // Create upload folder outside JAR
	            String uploadDir =
	                    System.getProperty("user.dir")
	                    + File.separator
	                    + "uploads";


	            File saveFile = new File(uploadDir);


	            // Create folder if it does not exist
	            if (!saveFile.exists()) {
	                saveFile.mkdirs();
	            }


	            // Create complete file path
	            Path path = Paths.get(
	                    saveFile.getAbsolutePath()
	                    + File.separator
	                    + file.getOriginalFilename()
	            );


	            // Save image
	            Files.copy(
	                    file.getInputStream(),
	                    path,
	                    StandardCopyOption.REPLACE_EXISTING
	            );


	            System.out.println("Image Uploaded!");
	        }


	        // Save user
	        this.userRepository.save(usern);


	        // Getting UserDetail
	        model.addAttribute("user", usern);

	        System.out.println(contact);
	        System.out.println(contact.getNickName());


	        redirectAttribute.addFlashAttribute(
	                "message",
	                new Message("Successfully Registered !!", "alert-success")
	        );


	        return "redirect:/customer/addContact";


	    } catch (Exception e) {

	        e.printStackTrace();

	        User user =
	                userRepository.getUserByEmail(principal.getName());

	        model.addAttribute("user", user);

	        model.addAttribute(
	                "title",
	                "Customer Add Contact - Smart Contact Manager"
	        );

	        redirectAttribute.addFlashAttribute(
	                "message",
	                new Message(
	                        "Something went wrong !! " + e.getMessage(),
	                        "alert-danger"
	                )
	        );

	        return "redirect:/customer/addContact";
	    }
	}
	
	@GetMapping("/viewContact")
	public String viewContact(Model model, Principal principal) {

	    String username = principal.getName();

	    System.out.println("Username: " + username);

	    User user = userRepository.getUserByEmail(username);

	    model.addAttribute("user", user);
	    model.addAttribute("title", "Customer View Contact - Smart Contact Manager");

	    // Get all contacts of logged-in user
	    List<Contact> list = contactRepository.getContactById(user.getId());

	    System.out.println("Total Contacts: " + list.size());

	    for (Contact contact : list) {
	        System.out.println("CID: " + contact.getCid());
	        System.out.println("Name: " + contact.getName());
	        System.out.println("Nick Name: " + contact.getNickName());
	        System.out.println("Phone: " + contact.getPhoneNumber());
	        System.out.println("Description: " + contact.getDescription());
	        System.out.println("Image: " + contact.getImage());
	        System.out.println("-------------------------");
	    }

	    // IMPORTANT
	    model.addAttribute("contacts", list);

	    return "customer/customer_view_contact";
	}
}

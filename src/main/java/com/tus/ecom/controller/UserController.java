package com.tus.ecom.controller;

import com.tus.ecom.dto.UserRequest;
import com.tus.ecom.dto.UserResponse;
import com.tus.ecom.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

	public UserController(UserService userService) {
        this.userService = userService;
	}

    @GetMapping
	@ResponseStatus(HttpStatus.OK)
    public List<UserResponse> getUsernames() {
		return this.userService.getUsernames();
    }

	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public Optional<UserResponse> getUsersById(@PathVariable Integer id) {
		return this.userService.getUsersById(id);
	}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@RequestBody UserRequest req) {
        return this.userService.createUser(req);
    }

	@PutMapping
	@ResponseStatus(HttpStatus.OK)
	public UserResponse updateUser(@RequestBody UserRequest req) {
		return this.userService.updateUser(req);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteUser(@PathVariable Integer id) {
		this.userService.deleteUser(id);
	}

	@GetMapping("/me")
	public String getLoggedinUser(Authentication authentication){
		return authentication.getName();
	}
	
}

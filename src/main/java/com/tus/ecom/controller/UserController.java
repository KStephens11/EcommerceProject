package com.tus.ecom.controller;

import com.tus.ecom.dto.user.UserRequest;
import com.tus.ecom.dto.user.UserResponse;
import com.tus.ecom.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse registerUser(@RequestBody UserRequest req) {
        return this.userService.createUser(req);
    }

	@GetMapping("/me")
	public Map<String, Object> getLoggedinUser(Authentication authentication){

		Map<String, Object> response = new HashMap<>();

		response.put("username", authentication.getName());
		response.put("roles",
				authentication.getAuthorities()
						.stream()
						.map(GrantedAuthority::getAuthority)
						.toList());

		return response;
	}
	
}

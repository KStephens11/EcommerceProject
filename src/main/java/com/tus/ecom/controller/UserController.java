package com.tus.ecom.controller;

import com.tus.ecom.dto.ErrorResponse;
import com.tus.ecom.dto.user.UserRequest;
import com.tus.ecom.dto.user.UserResponse;
import com.tus.ecom.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping
	public ResponseEntity<List<UserResponse>> getUsernames() {
		return ResponseEntity.ok(userService.getUsernames());
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserResponse> getUsersById(@PathVariable Integer id) {
		return userService.getUsersById(id)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping("/register")
	public ResponseEntity<UserResponse> registerUser(@RequestBody UserRequest req) {
			UserResponse response = userService.createUser(req);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorResponse(ex.getMessage()));
	}

	@GetMapping("/me")
	public ResponseEntity<Map<String, Object>> getLoggedinUser(Authentication authentication){

		if (authentication == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("error", "Not authenticated"));
		}

		Map<String, Object> response = new HashMap<>();

		response.put("username", authentication.getName());
		response.put("roles",
				authentication.getAuthorities()
						.stream()
						.map(GrantedAuthority::getAuthority)
						.toList());

		return ResponseEntity.ok(response);
	}
}
package com.example.demo;

	import java.time.LocalDate;

import jakarta.persistence.*;

	@Entity
	public class user {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private String username;
	    private String password;
	    private String role;

	    private String email;
private String phno;
	    private LocalDate createdDate;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getRole() {
			return role;
		}

		public void setRole(String role) {
			this.role = role;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public LocalDate getCreatedDate() {
			return createdDate;
		}

		public void setCreatedDate(LocalDate createdDate) {
			this.createdDate = createdDate;
		}

		public user(Long id, String username, String password, String role, String email, LocalDate createdDate) {
			super();
			this.id = id;
			this.username = username;
			this.password = password;
			this.role = role;
			this.email = email;
			this.createdDate = createdDate;
		}

		public user() {
			super();
			// TODO Auto-generated constructor stub
		}

		public String getPhno() {
			return phno;
		}

		public void setPhno(String phno) {
			this.phno = phno;
		}
		

	    // getters and setters
	}
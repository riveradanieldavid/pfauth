package com.bezkoder.springjwt.controllers;

// MY FILES CREATED IMPORTED
import com.bezkoder.springjwt.models.ERole;
import com.bezkoder.springjwt.models.Role;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.payload.request.LoginRequest;
import com.bezkoder.springjwt.payload.request.SignupRequest;
import com.bezkoder.springjwt.payload.response.JwtResponse;
import com.bezkoder.springjwt.payload.response.MessageResponse;
import com.bezkoder.springjwt.repository.RoleRepository;
import com.bezkoder.springjwt.repository.UserRepository;
import com.bezkoder.springjwt.security.jwt.JwtUtils;
import com.bezkoder.springjwt.security.services.UserDetailsImpl;
// UTILITIES
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
// TO VALIDATE
import javax.validation.Valid;
// TOOLS FROM THE "FRAMWORK" IMPORTED TO CREATE THE "API"
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// "AuthController" CLASS
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
// URI API TO GET auth
@RequestMapping("/api/auth")
public class AuthController {

  // AUTOMATIC LINKING
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  // URI API TO LOGIN
  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(
    @Valid @RequestBody LoginRequest loginRequest
  ) {
    Authentication authentication = authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(
        loginRequest.getUsername(),
        loginRequest.getPassword()
      )
    );

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    List<String> roles = userDetails
      .getAuthorities()
      .stream()
      .map(item -> item.getAuthority())
      .collect(Collectors.toList());

    return ResponseEntity.ok(
      new JwtResponse(
        jwt,
        userDetails.getId(),
        userDetails.getUsername(),
        userDetails.getEmail(),
        roles
      )
    );
  }

  // URI API TO REGISTER
  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(
    @Valid @RequestBody SignupRequest signUpRequest
  ) {
    // email or username ALREADY CREATED IS NOT ACCEPTED
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity
        .badRequest()
        .body(new MessageResponse("Error: Username is already taken!"));
    }
    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity
        .badRequest()
        .body(new MessageResponse("Error: Email is already in use!"));
    }
    // IF NEW username AND email: Create new user's account
    User user = new User(
      signUpRequest.getUsername(),
      signUpRequest.getEmail(),
      encoder.encode(signUpRequest.getPassword())
    );

    // IN strRoles WE PUT'S THE NEW user CREATED BEFORE WITH A role BY DEFAULT
    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    // IF NOT SPECIFIED A role IS ASSIGNED THE role BY DEFAULT
    if (strRoles == null) {
      Role userRole = roleRepository
        .findByName(ERole.ROLE_USER)
        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole); // user role BY DEFAULT
    }
    // THIS CODE VERIFY IF WE REGISTERED WITH A SPECIFIC role
    else {
      // IF WE SPECIFIED A role WHEN WE REGISTERED FIND role IN DB AND GRANT role
      strRoles.forEach(
        role -> {
          switch (role) {
            case "admin":
              Role adminRole = roleRepository
                .findByName(ERole.ROLE_ADMIN)
                .orElseThrow(
                  () -> new RuntimeException("Error: Role is not found.")
                );
              roles.add(adminRole);

              break;
            case "mod":
              Role modRole = roleRepository
                .findByName(ERole.ROLE_MODERATOR)
                .orElseThrow(
                  () -> new RuntimeException("Error: Role is not found.")
                );
              roles.add(modRole);

              break;
            default:
              Role userRole = roleRepository
                .findByName(ERole.ROLE_USER)
                .orElseThrow(
                  () -> new RuntimeException("Error: Role is not found.")
                );
              roles.add(userRole);
          }
        }
      );
    }

    // SET Role TO NEW user
    user.setRoles(roles);
    // SAVE NEW user
    userRepository.save(user);

    // IF EVERYTHING WAS OK UP TO HERE RETURN MESSAGE:
    return ResponseEntity.ok(
      new MessageResponse("User registered successfully!")
    );
  }
}

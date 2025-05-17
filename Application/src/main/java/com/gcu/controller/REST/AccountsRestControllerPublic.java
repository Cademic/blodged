package com.gcu.controller.REST;

import java.util.List;

import com.gcu.model.db.metadata.ReplyMetadata;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gcu.data.service.UserBusinessService;
import com.gcu.model.db.UserModel;
import com.gcu.model.db.metadata.UserMetadata;


@RestController
@RequestMapping("/api/accounts")
public class AccountsRestControllerPublic {

    private @Autowired UserBusinessService userBusinessService;

    @Operation(summary = "Retrieve all user metadata", responses = {@ApiResponse(responseCode = "200", description = "returns a list", content = @Content(schema = @Schema(implementation = UserMetadata.class))), @ApiResponse(responseCode = "500", description = "If the server encounters a database error")})
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<UserMetadata> meta = userBusinessService.getAllUsersMeta();
            return new ResponseEntity<>(meta, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Retrieve metadata for a specific user by username", responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = UserMetadata.class))), @ApiResponse(responseCode = "500", description = "If the server encounters a database error")})
    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable(value="username") String username) {
        try {
            UserMetadata meta = userBusinessService.getUserMeta(username);
            return new ResponseEntity<>(meta, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Retrieve a list of usernames that the specified user follows", responses = {@ApiResponse(responseCode = "200", description = "returns a list", content = @Content(schema = @Schema(implementation = String.class))), @ApiResponse(responseCode = "500", description = "If the server encounters a database error")})
    @GetMapping("/follows/{username}")
    public ResponseEntity<?> getWhoUserFollows(@PathVariable(value="username") String username) {
        try {
            List<String> meta = userBusinessService.getUsersFollowing(username);
            return new ResponseEntity<>(meta, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Retrieve a list of usernames following the specified user", responses = {@ApiResponse(responseCode = "200", description = "returns a list", content = @Content(schema = @Schema(implementation = String.class))), @ApiResponse(responseCode = "500", description = "If the server encounters a database error")})
    @GetMapping("/followed-by/{username}")
    public ResponseEntity<?> getWhoIsFollowingUser(@PathVariable(value = "username") String username) {
        try {
            List<String> meta = userBusinessService.getUsersFollowedBy(username);
            return new ResponseEntity<>(meta, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Check if a username is available", responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Boolean.class))), @ApiResponse(responseCode = "500", description = "If the server encounters a database error")})
    @GetMapping("/username-available/{username}")
    public ResponseEntity<?> isUsernameAvailable(@PathVariable(value = "username") String username) {
        try {
            UserModel m = userBusinessService.getFromUsername(username);
            return new ResponseEntity<>(m != null, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
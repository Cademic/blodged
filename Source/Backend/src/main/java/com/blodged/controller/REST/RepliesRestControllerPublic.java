package com.blodged.controller.REST;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blodged.data.service.ReplyBusinessService;
import com.blodged.model.db.metadata.ReplyMetadata;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/replies")
public class RepliesRestControllerPublic {

    private @Autowired ReplyBusinessService replyBusinessService;

    @Operation(summary = "Retrieve replies for a specific post by ID", responses = {@ApiResponse(responseCode = "200", description = "returns a list", content = @Content(schema = @Schema(implementation = ReplyMetadata.class))), @ApiResponse(responseCode = "500", description = "If the server encounters a database error")})
    @GetMapping("/post/{id}")
    public ResponseEntity<?> getRepliesForPost(@PathVariable(value = "id") long id) {
        try {
            List<ReplyMetadata> re = replyBusinessService.getForPost(id);
            return new ResponseEntity<>(re, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Retrieve replies made by a specific user by ID", responses = {@ApiResponse(responseCode = "200", description = "returns a list", content = @Content(schema = @Schema(implementation = ReplyMetadata.class))), @ApiResponse(responseCode = "500", description = "If the server encounters a database error")})
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getRepliesForUser(@PathVariable(value = "id") long id) {
        try {
            List<ReplyMetadata> re = replyBusinessService.getForUser(id);
            return new ResponseEntity<>(re, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

package com.example.msa.controllers;

import com.example.msa.exceptions.InvalidSenderException;
import com.example.msa.exceptions.NotFoundException;
import com.example.msa.models.Alert;
import com.example.msa.services.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/services/alerts")
public class AlertController {
    @Autowired
    private AlertService alertService;

    @PostMapping("/in")
    public ResponseEntity<Object> newAlert(@RequestBody Alert alert) throws NotFoundException, InvalidSenderException {
        alertService.newAlert(alert);
        return new ResponseEntity<>("Alert in", HttpStatus.OK);
    }

}

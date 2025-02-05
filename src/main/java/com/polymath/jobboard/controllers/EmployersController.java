package com.polymath.jobboard.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/employer")
@PreAuthorize("hasRole('EMPLOYER')")
public class EmployersController {




}

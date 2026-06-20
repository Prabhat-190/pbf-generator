package com.pbf.pbf_generator.controller;

import com.pbf.pbf_generator.dto.response.FileStatusResponse;
import com.pbf.pbf_generator.service.PbfGeneratorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;

@RestController
public class PbfController {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    PbfController.class);

    private final PbfGeneratorService service;

    public PbfController(
            PbfGeneratorService service) {

        this.service = service;
    }

    @RequestMapping(
            value = "/generate",
            method = {RequestMethod.GET, RequestMethod.POST})
    public FileStatusResponse generatePbf()
            throws IOException {

        logger.info("/generate API called");

        return service.generatePbf();
    }

    @GetMapping("/status/{id}")
    public FileStatusResponse getStatus(
            @PathVariable Long id) {

        logger.info(
                "/status API called for id {}",
                id);

        return service.getStatus(id);
    }

    @PostMapping("/callback/{id}")
    public String callback(
            @PathVariable Long id) {

        logger.info(
                "/callback API called for id {}",
                id);

        return service.callback(id);
    }

    @GetMapping("/health")
    public String health() {

        logger.info("/health API called");

        return "Application Running";
    }
}

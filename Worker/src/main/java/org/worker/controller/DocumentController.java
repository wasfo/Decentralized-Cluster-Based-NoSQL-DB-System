package org.worker.controller;
import org.worker.services.CollectionService;
import org.worker.services.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/api/documents")
public class DocumentController {

    private DocumentService documentService;
    private CollectionService collectionService;

    @Autowired
    public DocumentController(DocumentService documentService, CollectionService collectionService) {

        this.documentService = documentService;
        this.collectionService = collectionService;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDocument(@PathVariable String id, @RequestParam String userDir,
                                                 @RequestParam String dbName,
                                                 @RequestParam String collectionName) throws IOException {
        return documentService.deleteById(id, userDir, dbName, collectionName);
    }

    @GetMapping("/")
    public String home2() {
        System.out.println("this is invoked");
        return "home";
    }
}

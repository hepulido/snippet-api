package hectorP.API.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import hectorP.API.snippets.Snippet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.web.bind.annotation.*;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping("/snippets")
public class SnippetController {
    @Autowired
    ResourceLoader resourceLoader;

    @Value("${encryption.password}")
    private String encryptionPass;

    @Value("${encryption.salt}")
    private String encryptionSalt;


    @GetMapping
    public ResponseEntity<Snippet[]> getAllSnippets( String language) throws IOException {
        File snippetResource = resourceLoader.getResource("classpath:seedData.json").getFile();
        ObjectMapper mapper = new ObjectMapper();
        Snippet[] snippets = mapper.readValue(snippetResource, Snippet[].class);
        TextEncryptor encryptor = Encryptors.text(encryptionPass, encryptionSalt);
        if(language != null) {
            snippets = Arrays.stream(snippets).filter(c -> Objects.equals(c.getLanguage(), language)).toArray(Snippet[]::new);
        }
        for(int i = 0; i < snippets.length; i++) {
            String decryptedSnippet = encryptor.decrypt(snippets[i].getCode());
            snippets[i].setCode(decryptedSnippet);
        }
        return new ResponseEntity<>(snippets, HttpStatus.OK);
    }


    @GetMapping("{id}")
    public ResponseEntity<Object> getSnippetById(@PathVariable Long id) throws IOException {
        File snippetResource = resourceLoader.getResource("classpath:seedData.json").getFile();
        ObjectMapper mapper = new ObjectMapper();
        Snippet[] snippets =Arrays.stream(mapper.readValue(snippetResource, Snippet[].class)).filter(c -> Objects.equals(c.getId(), id)).toArray(Snippet[]::new);
        TextEncryptor encryptor = Encryptors.text(encryptionPass, encryptionSalt);
        if (snippets.length == 0) {
            return new ResponseEntity<>("Snippet not found", HttpStatus.NOT_FOUND);
        }
        String decryptedSnippet = encryptor.decrypt(snippets[0].getCode());
        snippets[0].setCode(decryptedSnippet);
        return new ResponseEntity<>(snippets, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Snippet> createSnippet(@RequestBody Snippet data) throws IOException {
        File cupcakeResource = resourceLoader.getResource("classpath:seedData.json").getFile();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        Snippet[] snippets = mapper.readValue(cupcakeResource, Snippet[].class);
        TextEncryptor encryptor = Encryptors.text(encryptionPass, encryptionSalt);

        List<Snippet> SnippetsList = new ArrayList<Snippet>(Arrays.asList(snippets));
        Snippet snippetToAdd = new Snippet(snippets[snippets.length - 1].getId() + 1, data.getLanguage(), data.getCode());
        SnippetsList.add(snippetToAdd);

        mapper.writeValue(resourceLoader.getResource("classpath:seedData.json").getFile(), SnippetsList );
        return new ResponseEntity<>(snippetToAdd, HttpStatus.OK);
    }
}


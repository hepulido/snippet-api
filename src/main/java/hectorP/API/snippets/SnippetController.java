package hectorP.API.snippets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private SnippetRepository snippetRepository;


    @GetMapping
    public ResponseEntity<Snippet[]> getAllSnippets( String language) throws IOException {
        File snippetResource = resourceLoader.getResource("classpath:seedData.json").getFile();
        ObjectMapper mapper = new ObjectMapper();
        Snippet[] snippets = mapper.readValue(snippetResource, Snippet[].class);
        if(language != null) {
            return new ResponseEntity<>(Arrays.stream(snippets).filter(c -> Objects.equals(c.getLanguage(), language)).toArray(Snippet[]::new), HttpStatus.OK);
        }
        return new ResponseEntity<>(snippets, HttpStatus.OK);
    }


    @GetMapping("{id}")
    public ResponseEntity<Object> getSnippetById(@PathVariable Long id) throws IOException {
        File snippetResource = resourceLoader.getResource("classpath:seedData.json").getFile();
        ObjectMapper mapper = new ObjectMapper();
        Snippet[] snippets = mapper.readValue(snippetResource, Snippet[].class);
        Object[] snippet = Arrays.stream(snippets).filter(c -> Objects.equals(c.getId(), id)).toArray();
        if (snippet.length == 0) {
            return new ResponseEntity<>("Snippet not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(snippet[0], HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Snippet> createSnippet(@RequestBody Snippet data) throws IOException {
        File cupcakeResource = resourceLoader.getResource("classpath:seedData.json").getFile();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        Snippet[] snippets = mapper.readValue(cupcakeResource, Snippet[].class);

        List<Snippet> SnippetsList = new ArrayList<Snippet>(Arrays.asList(snippets));
        Snippet snippetToAdd = new Snippet(snippets[snippets.length - 1].getId() + 1, data.getLanguage(), data.getCode());
        SnippetsList.add(snippetToAdd);

        mapper.writeValue(cupcakeResource, SnippetsList );
        return new ResponseEntity<>(snippetToAdd, HttpStatus.OK);
    }
}


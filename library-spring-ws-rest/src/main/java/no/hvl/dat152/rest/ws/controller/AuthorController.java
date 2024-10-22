/**
 * 
 */
package no.hvl.dat152.rest.ws.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.hvl.dat152.rest.ws.exceptions.AuthorNotFoundException;
import no.hvl.dat152.rest.ws.model.Author;
import no.hvl.dat152.rest.ws.model.Book;
import no.hvl.dat152.rest.ws.service.AuthorService;

/**
 * 
 */
@RestController
@RequestMapping("/elibrary/api/v1")
public class AuthorController {

    @Autowired
    private AuthorService authorservice;

    @GetMapping("/authors")
    public ResponseEntity<Object> getAllAuthors() {
        List<Author> authors = authorservice.findAll();

        if (authors.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(authors, HttpStatus.OK);

    }

	@GetMapping("/authors/{id}")
    public ResponseEntity<Object> getAuthor(@PathVariable("id") Long id) throws AuthorNotFoundException {
        Author author = authorservice.findById(id);
        if (author == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(author, HttpStatus.OK);

    }

    @GetMapping("/authors/{id}/books")
    public ResponseEntity<Object> getBooksByAuthorId(@PathVariable("id") Long id) throws AuthorNotFoundException {
        Set<Book> books = authorservice.findBooksByAuthorId(id);
        if (books.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @PostMapping("/authors")
    public ResponseEntity<Object> createAuthor(@RequestBody Author author) {
        Author newAuthor = authorservice.saveAuthor(author);
        return new ResponseEntity<>(newAuthor, HttpStatus.CREATED);
    }

    @PutMapping("/authors/{id}")
    public ResponseEntity<Object> updateAuthor(@RequestBody Author author, @PathVariable("id") Long id) {
        Author newAuthor = authorservice.updateAuthor(author, id);
        return new ResponseEntity<>(newAuthor, HttpStatus.OK);
    }


}

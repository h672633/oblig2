/**
 * 
 */
package no.hvl.dat152.rest.ws.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private AuthorService authorService;

    @GetMapping("/authors")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> getAllAuthors() {
        List<Author> authors = authorService.findAll();

        if (authors.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(authors, HttpStatus.OK);

    }

    @GetMapping("/authors/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> getAuthor(@PathVariable("id") Long id) throws AuthorNotFoundException {
        Author author = authorService.findById(id);
        if (author == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(author, HttpStatus.OK);

    }

    @GetMapping("/authors/{id}/books")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> getBooksByAuthorId(@PathVariable("id") Long id) throws AuthorNotFoundException {
        Set<Book> books = authorService.findBooksByAuthorId(id);
        if (books.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @PostMapping("/authors")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Object> createAuthor(@RequestBody Author author) {
        Author newAuthor = authorService.saveAuthor(author);
        return new ResponseEntity<>(newAuthor, HttpStatus.CREATED);
    }

    @PutMapping("/authors/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Object> updateAuthor(@RequestBody Author author, @PathVariable("id") Long id) throws AuthorNotFoundException {

        Author uAuthor = authorService.updateAuthor(author, id);

        return new ResponseEntity<>(uAuthor, HttpStatus.OK);
    }


}
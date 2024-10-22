/**
 * 
 */
package no.hvl.dat152.rest.ws.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UnauthorizedOrderActionException;
import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.service.OrderService;

/**
 * @author tdoy
 */
@RestController
@RequestMapping("/elibrary/api/v1")
public class OrderController {

    @Autowired
    OrderService orderService;

    @GetMapping("/orders")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Object> getAllBorrowOrders
            (@RequestParam(value = "expiry", defaultValue = "9999-12-31") LocalDate expiry,
             @RequestParam(value = "page", defaultValue = "0") int page,
             @RequestParam(value = "size", defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);

        List<Order> orders = orderService.findByExpiryDate(expiry, pageable);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping(value = "/orders/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Order> getBorrowOrder(@PathVariable("id") Long id) throws OrderNotFoundException, UnauthorizedOrderActionException {

        return new ResponseEntity<>(orderService.findOrder(id), HttpStatus.OK);
    }

    @PutMapping(value = "/orders/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Order> updateOrder(@PathVariable("id") Long id, @RequestBody Order order) {

        return new ResponseEntity<>(orderService.updateOrder(order, id), HttpStatus.OK);
    }

    @DeleteMapping(value = "/orders/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<?> deleteBookOrder(@PathVariable("id") Long id) throws OrderNotFoundException {

        orderService.deleteOrder(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
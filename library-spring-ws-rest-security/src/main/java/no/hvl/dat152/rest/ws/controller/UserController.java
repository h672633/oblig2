/**
 * 
 */
package no.hvl.dat152.rest.ws.controller;

import java.util.List;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UnauthorizedOrderActionException;
import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.model.User;
import no.hvl.dat152.rest.ws.service.UserService;

/**
 * @author tdoy
 */
@RestController
@RequestMapping("/elibrary/api/v1")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<Object> getUsers() {

        List<User> users = userService.findAllUsers();

        if (users.isEmpty())

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else
            return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping(value = "/users/{id}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<Object> getUser(@PathVariable("id") Long id) throws UserNotFoundException, OrderNotFoundException {

        User user = userService.findUser(id);

        return new ResponseEntity<>(user, HttpStatus.OK);

    }

    @PostMapping("/users")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> createUser(@RequestBody User user) {
        User createdUser = userService.saveUser(user);

        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> updateUser(@RequestBody User user, @PathVariable("id") Long id) {
        User updatedUser = userService.updateUser(user, id);

        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<Object> deleteUser(@PathVariable("id") Long id) throws UserNotFoundException {
        userService.deleteUser(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/users/{id}/orders")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> getUserOrders(@PathVariable("id") Long id) {
        Set<Order> orders = userService.getUserOrders(id);

        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/users/{uid}/orders/{oid}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> getUserOrder(@PathVariable("uid") Long uid, @PathVariable("oid") Long oid) {
        Order order = userService.getUserOrder(uid, oid);

        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @DeleteMapping("/users/{uid}/orders/{oid}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> deleteUserOrder(@PathVariable("uid") Long uid, @PathVariable("oid") Long oid) {
        userService.deleteOrderForUser(uid, oid);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/users/{uid}/orders")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<Object> createUserOrder(@PathVariable("uid") Long uid, @RequestBody Order order) throws UserNotFoundException, OrderNotFoundException {

        User user = userService.createOrdersForUser(uid, order);
        Set<Order> orders = user.getOrders();

        addLinks(orders, uid);

        return new ResponseEntity<>(orders, HttpStatus.CREATED);
    }

    private void addLinks(Set<Order> orders, Long uid) throws UserNotFoundException, OrderNotFoundException {

        for (Order order : orders) {
            Link deleteLink = linkTo(methodOn(UserController.class).deleteUserOrder(uid, order.getId())).withRel("Delete-order");
            Link updateLink = linkTo(methodOn(OrderController.class).updateOrder(order.getId(), order)).withRel("Update-order-info");
            Link getLink = linkTo(methodOn(UserController.class).getUserOrder(uid, order.getId())).withRel("Get-info-on_an-order");
            Link getAllLink = linkTo(methodOn(UserController.class).getUserOrders(uid)).withRel("get-info-on-all-orders");

            order.add(deleteLink);
            order.add(updateLink);
            order.add(getLink);
            order.add(getAllLink);

        }
    }
}
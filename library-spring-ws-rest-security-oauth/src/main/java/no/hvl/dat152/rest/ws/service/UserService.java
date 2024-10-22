/**
 * 
 */
package no.hvl.dat152.rest.ws.service;


import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.model.User;
import no.hvl.dat152.rest.ws.repository.UserRepository;

/**
 * @author tdoy
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public List<User> findAllUsers(){

        List<User> allUsers = (List<User>) userRepository.findAll();

        return allUsers;
    }

    public User findUser(Long userid) throws UserNotFoundException {

        User user = userRepository.findById(userid)
                .orElseThrow(()-> new UserNotFoundException("User with id: "+userid+" not found"));

        return user;
    }

    public User saveUser(User user){

        return userRepository.save(user);
    }

    public void deleteUser (Long id) throws UserNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(()->new UserNotFoundException("User with id: "+id+" not found"));
        userRepository.delete(user);
    }

    public User updateUser(User user, Long id) {
        User existingUser = userRepository.findById(id).orElse(null);

        if (existingUser != null) {
            existingUser.setFirstname(user.getFirstname());
            existingUser.setLastname(user.getLastname());

            return userRepository.save(existingUser);
        }

        return null;

    }

    public Set<Order> getUserOrders(Long userid) {
        User user = userRepository.findById(userid).orElse(null);

        if (user != null) {
            return user.getOrders();
        }
        return null;
    }

    public Order getUserOrder(Long userid, Long oid){
        return userRepository.findById(userid)
                .map(user -> user.getOrders().stream()
                        .filter(order ->order.getId().equals(oid))
                        .findFirst()
                        .orElse(null))
                .orElse(null);

    }

    public void deleteOrderForUser(Long userid, Long oid){
        userRepository.findById(userid).ifPresent(user -> {
            user.getOrders().removeIf(order -> order.getId().equals(oid));
            userRepository.save(user);
        });
    }

    public User createOrdersForUser (Long userid, Order order) {
        return userRepository.findById(userid)
                .map(user -> {
                    user.getOrders().add(order);
                    return userRepository.save(user);
                })
                .orElse(null);
    }
}
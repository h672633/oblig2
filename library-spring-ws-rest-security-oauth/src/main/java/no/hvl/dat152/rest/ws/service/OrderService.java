/**
 * 
 */
package no.hvl.dat152.rest.ws.service;

import java.util.List;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UnauthorizedOrderActionException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.repository.OrderRepository;
import no.hvl.dat152.rest.ws.security.UserDetailsImpl;
import org.springframework.web.bind.annotation.*;

/**
 * @author tdoy
 */
@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	public Order saveOrder(Order order) {

		order = orderRepository.save(order);

		return order;
	}

	public void deleteOrder(Long id) throws OrderNotFoundException {
		Order order = orderRepository.findById(id)
				.orElseThrow(() -> new OrderNotFoundException("Order with id: "+id+" not found in the order list!"));
		orderRepository.delete(order);
	}

	public List<Order> findAllOrders() {

		return (List<Order>) orderRepository.findAll();
	}

	public List<Order> findByExpiryDate(LocalDate expiry, Pageable page) {

		return orderRepository.findByExpiryBefore(expiry, page).get().toList();
	}

	public Order updateOrder(Order order, Long id) {

		Optional<Order> order1 = orderRepository.findById(id);
		order.setId(order1.get().getId());
		return orderRepository.save(order);
	}

	public Order findOrder(Long id) throws OrderNotFoundException, UnauthorizedOrderActionException {

		verifyPrincipalOfOrder(id);	// verify who is making this request - Only ADMIN or SUPER_ADMIN can access any order
		Order order = orderRepository.findById(id)
				.orElseThrow(()-> new OrderNotFoundException("Order with id: "+id+" not found in the order list!"));

		return order;
	}

	private boolean verifyPrincipalOfOrder(Long id) throws UnauthorizedOrderActionException {

		UserDetailsImpl userPrincipal = (UserDetailsImpl) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		// verify if the user sending request is an ADMIN or SUPER_ADMIN
		for(GrantedAuthority authority : userPrincipal.getAuthorities()){
			if(authority.getAuthority().equals("ADMIN") ||
					authority.getAuthority().equals("SUPER_ADMIN")) {
				return true;
			}
		}

		// otherwise, make sure that the user is the one who initially made the order
		String email = orderRepository.findEmailByOrderId(id);

		if(email.equals(userPrincipal.getEmail()))
			return true;

		throw new UnauthorizedOrderActionException("Unauthorized order action!");

	}
}

package com.onspring.onspring_customer.domain.customer.service;

import com.onspring.onspring_customer.domain.common.entity.CustomerFranchise;
import com.onspring.onspring_customer.domain.common.repository.CustomerFranchiseRepository;
import com.onspring.onspring_customer.domain.customer.dto.CustomerDto;
import com.onspring.onspring_customer.domain.customer.entity.Customer;
import com.onspring.onspring_customer.domain.customer.repository.CustomerRepository;
import com.onspring.onspring_customer.domain.franchise.entity.Franchise;
import com.onspring.onspring_customer.domain.franchise.repository.FranchiseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private FranchiseRepository franchiseRepository;

    @Mock
    private CustomerFranchiseRepository customerFranchiseRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private CustomerDto customerDto;
    private Customer customer;
    private Franchise franchise;
    private CustomerFranchise customerFranchise;

    @BeforeEach
    void setUp() {
        customerDto = CustomerDto.builder()
                .id(1L)
                .name("customer")
                .address("address")
                .phone("0123456789")
                .isActivated(true)
                .build();

        customer = Customer.builder()
                .id(1L)
                .name("customer")
                .address("address")
                .phone("0123456789")
                .isActivated(true)
                .build();

        franchise = new Franchise();
        franchise.setId(1L);
        franchise.setUserName("franchise");
        franchise.setPassword("password");
        franchise.setPhone("0123456789");
        franchise.setActivated(true);

        customerFranchise = new CustomerFranchise();
        customerFranchise.setId(1L);
        customerFranchise.setCustomer(customer);
        customerFranchise.setFranchise(franchise);
    }

    @Test
    void testSaveCustomer() {
        when(modelMapper.map(any(CustomerDto.class), any())).thenReturn(customer);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        customerService.saveCustomer(customerDto);

        verify(modelMapper).map(customerDto, Customer.class);
        verify(customerRepository).save(customer);
    }

    @Test
    void testFindCustomerById() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(modelMapper.map(any(Customer.class), any())).thenReturn(customerDto);

        CustomerDto customerDto1 = customerService.findCustomerById(1L);

        assertEquals(1L, customerDto1.getId());
        assertEquals("customer", customerDto1.getName());
        assertEquals("address", customerDto1.getAddress());
        assertEquals("0123456789", customerDto1.getPhone());
        assertTrue(customerDto1.isActivated());
    }

    @Test
    void testFindCustomerById_NotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> customerService.findCustomerById(1L));
    }

    @Test
    void testUpdateCustomer() {
        CustomerDto customerDto1 = CustomerDto.builder()
                .id(1L)
                .name("customer1")
                .address("address1")
                .phone("9876543210")
                .isActivated(true)
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        doAnswer(invocationOnMock -> {
            CustomerDto source = invocationOnMock.getArgument(0);
            Customer destination = invocationOnMock.getArgument(1);
            destination.setName(source.getName());
            destination.setAddress(source.getAddress());
            destination.setPhone(source.getPhone());
            destination.setActivated(source.isActivated());
            return null;
        }).when(modelMapper)
                .map(eq(customerDto1), any(Customer.class));

        when(customerRepository.save(customer)).thenReturn(customer);

        boolean result = customerService.updateCustomer(customerDto1);

        assertTrue(result);

        assertEquals(1L, customer.getId());
        assertEquals("customer1", customer.getName());
        assertEquals("address1", customer.getAddress());
        assertEquals("9876543210", customer.getPhone());
        assertTrue(customerDto1.isActivated());
    }

    @Test
    void testAddFranchiseToCustomer() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(franchiseRepository.findById(1L)).thenReturn(Optional.of(franchise));
        when(customerFranchiseRepository.save(any(CustomerFranchise.class))).thenReturn(customerFranchise);

        customerService.addFranchiseToCustomer(1L, 1L);

        verify(customerFranchiseRepository).save(any(CustomerFranchise.class));

        assertEquals(1L, customerFranchise.getId());
        assertEquals(customer, customerFranchise.getCustomer());
        assertEquals(franchise, customerFranchise.getFranchise());
    }

    @Test
    void testDeleteFranchiseFromCustomer() {
        when(customerFranchiseRepository.findById(1L)).thenReturn(Optional.of(customerFranchise));

        customerService.deleteFranchiseFromCustomer(1L);

        verify(customerFranchiseRepository).delete(customerFranchise);
    }

    @Test
    void testActivateCustomer() {
        customer.setActivated(false);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        boolean result = customerService.activateCustomerById(1L);

        assertTrue(result);

        verify(customerRepository).save(any(Customer.class));

        assertTrue(customer.isActivated());
    }

    @Test
    void testDeactivateCustomer() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        boolean result = customerService.deactivateCustomerById(1L);

        assertTrue(result);

        verify(customerRepository).save(any(Customer.class));

        assertFalse(customer.isActivated());
    }
}

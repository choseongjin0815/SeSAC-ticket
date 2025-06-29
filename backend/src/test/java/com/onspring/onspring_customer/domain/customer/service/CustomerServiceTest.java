package com.onspring.onspring_customer.domain.customer.service;

import com.onspring.onspring_customer.domain.common.entity.CustomerFranchise;
import com.onspring.onspring_customer.domain.common.repository.CustomerFranchiseRepository;
import com.onspring.onspring_customer.domain.customer.dto.CustomerDto;
import com.onspring.onspring_customer.domain.customer.entity.Customer;
import com.onspring.onspring_customer.domain.customer.entity.Admin;
import com.onspring.onspring_customer.domain.customer.repository.AdminRepository;
import com.onspring.onspring_customer.domain.customer.repository.CustomerRepository;
import com.onspring.onspring_customer.domain.franchise.entity.Franchise;
import com.onspring.onspring_customer.domain.franchise.repository.FranchiseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock private AdminRepository adminRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private FranchiseRepository franchiseRepository;
    @Mock private CustomerFranchiseRepository customerFranchiseRepository;
    @Mock private ModelMapper modelMapper;

    @InjectMocks private CustomerServiceImpl customerService;

    private Customer customer;
    private CustomerDto customerDto;
    private Franchise franchise;
    private CustomerFranchise customerFranchise;
    private Admin admin;

    @BeforeEach
    void setUp() throws Exception {
        customer = Customer.builder()
                .name("고객A")
                .address("서울시")
                .phone("01012345678")
                .isActivated(true)
                .build();
        setField(customer, "id", 1L);

        customerDto = CustomerDto.builder()
                .id(1L)
                .name("고객A")
                .address("서울시")
                .phone("01012345678")
                .isActivated(true)
                .build();

        franchise = new Franchise();
        setField(franchise, "id", 10L);

        admin = new Admin();
        setField(admin, "id", 99L);
        setField(admin, "customer", customer);

        customerFranchise = new CustomerFranchise();
        customerFranchise.changeCustomer(customer);
        customerFranchise.changeFranchise(franchise);
        setField(customerFranchise, "id", 100L);
    }

    @Test
    void testSaveCustomer() {
        when(modelMapper.map(customerDto, Customer.class)).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(customer);

        Long result = customerService.saveCustomer(customerDto);

        assertEquals(1L, result);
        verify(customerRepository).save(customer);
    }

    @Test
    void testFindCustomerById() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);

        CustomerDto result = customerService.findCustomerById(1L);

        assertEquals("고객A", result.getName());
    }

    @Test
    void testFindCustomerById_NotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> customerService.findCustomerById(1L));
    }

    @Test
    void testFindAllCustomer() {
        when(customerRepository.findAll()).thenReturn(List.of(customer));
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);

        List<CustomerDto> list = customerService.findAllCustomer();

        assertEquals(1, list.size());
        assertEquals("고객A", list.get(0).getName());
    }

    @Test
    void testUpdateCustomer() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        boolean result = customerService.updateCustomer(customerDto);

        assertTrue(result);
        verify(customerRepository).save(customer);
        verify(modelMapper).map(customerDto, customer);
    }

    @Test
    void testAddFranchiseToCustomer() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(franchiseRepository.findById(10L)).thenReturn(Optional.of(franchise));
        when(customerFranchiseRepository.save(any())).thenReturn(customerFranchise);

        Long id = customerService.addFranchiseToCustomer(1L, 10L);

        assertEquals(100L, id);
        verify(customerFranchiseRepository).save(any());
    }

    @Test
    void testAddFranchiseToCustomerWithAdminId() {
        when(adminRepository.findById(99L)).thenReturn(Optional.of(admin));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(franchiseRepository.findById(10L)).thenReturn(Optional.of(franchise));
        when(customerFranchiseRepository.save(any())).thenReturn(customerFranchise);

        Long id = customerService.addFranchiseToCustomerWithAdminId(99L, 10L);

        assertEquals(100L, id);
    }

    @Test
    void testDeleteFranchiseFromCustomer() {
        when(customerFranchiseRepository.findById(100L)).thenReturn(Optional.of(customerFranchise));

        boolean result = customerService.deleteFranchiseFromCustomer(100L);

        assertTrue(result);
        verify(customerFranchiseRepository).delete(customerFranchise);
    }

    @Test
    void testDeleteFranchiseFromCustomer_NotFound() {
        when(customerFranchiseRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> customerService.deleteFranchiseFromCustomer(100L));
    }

    @Test
    void testActivateCustomerById() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        boolean result = customerService.activateCustomerById(1L);

        assertTrue(result);
        verify(customerRepository).save(customer);
        assertTrue(customer.isActivated());
    }

    @Test
    void testDeactivateCustomerById() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        boolean result = customerService.deactivateCustomerById(1L);

        assertTrue(result);
        verify(customerRepository).save(customer);
        assertFalse(customer.isActivated());
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
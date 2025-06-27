package com.onspring.onspring_customer.domain.common.service;

import com.onspring.onspring_customer.domain.common.dto.TransactionDto;
import com.onspring.onspring_customer.domain.common.entity.Transaction;
import com.onspring.onspring_customer.domain.common.repository.TransactionRepository;
import com.onspring.onspring_customer.domain.franchise.entity.Franchise;
import com.onspring.onspring_customer.domain.franchise.repository.FranchiseRepository;
import com.onspring.onspring_customer.domain.user.entity.EndUser;
import com.onspring.onspring_customer.domain.user.repository.EndUserRepository;
import com.onspring.onspring_customer.domain.customer.entity.Party;
import com.onspring.onspring_customer.domain.customer.repository.PartyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private FranchiseRepository franchiseRepository;
    @Mock
    private EndUserRepository endUserRepository;
    @Mock
    private PartyRepository partyRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Franchise franchise;
    private EndUser endUser;
    private Party party;
    private Transaction transaction;

    @BeforeEach
    void setup() {
        // mock 객체로 생성
        franchise = mock(Franchise.class);
        endUser = mock(EndUser.class);
        party = mock(Party.class);

        // Transaction은 spy로 생성 (실제 객체 + 일부 동작 모킹)
        transaction = spy(new Transaction(franchise, endUser, BigDecimal.TEN, false, party));

        // lenient로 불필요 스터빙 예외 회피
        lenient().when(franchise.getId()).thenReturn(1L);
        lenient().doReturn(1L).when(transaction).getId();
    }

    @Test
    void testSaveFalseTransaction_success() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        when(transaction.isClosed()).thenReturn(false);
        when(transactionRepository.save(any())).thenReturn(transaction);

        TransactionDto dto = new TransactionDto();
        dto.setId(1L);
        Long id = transactionService.saveFalseTransaction(dto);

        verify(transaction).closeTransaction();
        verify(transactionRepository).save(transaction);
        assertEquals(1L, id);
    }

    @Test
    void testSaveFalseTransaction_alreadyClosed() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        when(transaction.isClosed()).thenReturn(true);

        TransactionDto dto = new TransactionDto();
        dto.setId(1L);

        assertThrows(RuntimeException.class, () -> transactionService.saveFalseTransaction(dto));
    }

    @Test
    void testSaveFalseTransaction_notFound() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        TransactionDto dto = new TransactionDto();
        dto.setId(1L);

        assertThrows(RuntimeException.class, () -> transactionService.saveFalseTransaction(dto));
    }

    @Test
    void testCancelTransaction_success() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        when(transaction.getFranchise()).thenReturn(franchise);
        when(franchise.getId()).thenReturn(1L);

        boolean result = transactionService.cancelTransaction(1L, 1L);

        assertTrue(result);
        verify(transactionRepository).save(transaction);
    }

    @Test
    void testCancelTransaction_alreadyCancelled() {
        transaction.cancelTransaction();
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        when(transaction.getFranchise()).thenReturn(franchise);
        when(franchise.getId()).thenReturn(1L);

        assertThrows(RuntimeException.class, () -> transactionService.cancelTransaction(1L, 1L));
    }

    @Test
    void testCancelTransaction_closedTransaction() {
        transaction.closeTransaction();
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        when(transaction.getFranchise()).thenReturn(franchise);
        when(franchise.getId()).thenReturn(1L);

        assertThrows(RuntimeException.class, () -> transactionService.cancelTransaction(1L, 1L));
    }

    @Test
    void testCancelTransaction_franchiseMismatch() {
        Franchise wrongFranchise = mock(Franchise.class);
        when(wrongFranchise.getId()).thenReturn(2L);

        when(transaction.getFranchise()).thenReturn(wrongFranchise);
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        assertThrows(RuntimeException.class, () -> transactionService.cancelTransaction(1L, 1L));
    }

    @Test
    void testCancelTransaction_notFound() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> transactionService.cancelTransaction(1L, 1L));
    }
}
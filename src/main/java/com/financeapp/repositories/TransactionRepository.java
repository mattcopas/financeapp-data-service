package com.financeapp.repositories;

import com.financeapp.enitities.Transaction;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

/**
 * Created by Matt on 19/05/2017.
 */
@RepositoryRestResource
public interface TransactionRepository extends PagingAndSortingRepository<Transaction, Long> {

    Transaction findOneByName(String name);
    List<Transaction> findByRepeatTransactionIntervalNotNull();

    @Override
    @RestResource(exported = false)
    void delete(Long id);

    @Override
    @RestResource(exported = false)
    void delete(Transaction transaction);
}

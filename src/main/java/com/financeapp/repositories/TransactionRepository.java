package com.financeapp.repositories;

import com.financeapp.enitities.Transaction;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * Created by Matt on 19/05/2017.
 */
@RepositoryRestResource
@CrossOrigin("http://localhost:3000")
public interface TransactionRepository extends PagingAndSortingRepository<Transaction, Long> {
}

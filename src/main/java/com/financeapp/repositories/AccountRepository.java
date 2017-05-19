package com.financeapp.repositories;

import com.financeapp.enitities.Account;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by Matt on 19/05/2017.
 */

/**
 * This defines a rest resource for the Account entity.
 * We could change the path by adding path='xyz' in the annotation
 * We could change the return array's name using collectionResourceRel="Some other name"
 */
@RepositoryRestResource
public interface AccountRepository extends PagingAndSortingRepository<Account, Long> {
}

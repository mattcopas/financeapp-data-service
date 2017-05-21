package com.financeapp.enitities.projections.transaction;

import com.financeapp.enitities.Account;
import com.financeapp.enitities.Transaction;
import com.financeapp.enitities.projections.account.AccountNameProjection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

/**
 * Created by Matt on 21/05/2017.
 */
@Projection(name = "includeAccount", types = {Transaction.class})
public interface TransactionProjection {

    Long getId();
    String getName();
    String getType();
    float getAmount();
    Account getAccount();
}

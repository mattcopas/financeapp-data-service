package com.financeapp.enitities.projections.account;

import com.financeapp.enitities.Account;
import org.springframework.data.rest.core.config.Projection;

/**
 * Created by Matt on 21/05/2017.
 */
@Projection(name = "accountNameOnly", types = {Account.class})
public interface AccountNameProjection {

    String getName();
}

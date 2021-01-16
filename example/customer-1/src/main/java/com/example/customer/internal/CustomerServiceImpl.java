/**
 *  License: Apache License, Version 2.0
 *  See the LICENSE file in the root directory or <https://www.apache.org/licenses/LICENSE-2.0>.
 */
package com.example.customer.internal;

import com.example.customer.CustomerService;

public class CustomerServiceImpl implements CustomerService {

    @Override
    public void incrementLoyaltyPoints(long customerId, long orderValue) {
        System.out.printf("Incrementing loyalty points: %s, %s", customerId, orderValue);
    }
}

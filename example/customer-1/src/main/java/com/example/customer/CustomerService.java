/**
 *  License: Apache License, Version 2.0
 *  See the LICENSE file in the root directory or <https://www.apache.org/licenses/LICENSE-2.0>.
 */
package com.example.customer;

public interface CustomerService {

    void incrementLoyaltyPoints(long customerId, long orderValue);
}

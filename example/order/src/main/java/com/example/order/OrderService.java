/**
 *  License: Apache License, Version 2.0
 *  See the LICENSE file in the root directory or <https://www.apache.org/licenses/LICENSE-2.0>.
 */
package com.example.order;

import java.util.ServiceLoader;

import com.example.customer.CustomerService;

public class OrderService {

    public static void main(String[] args) {
        CustomerService customerService = ServiceLoader.load(CustomerService.class).findFirst().get();
        customerService.incrementLoyaltyPoints(123, 4999);
    }
}

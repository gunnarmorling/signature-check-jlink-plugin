import com.example.customer.CustomerService;

/**
 *  License: Apache License, Version 2.0
 *  See the LICENSE file in the root directory or <https://www.apache.org/licenses/LICENSE-2.0>.
 */
module com.example.order {
    exports com.example.order;
    requires com.example.customer;
    uses CustomerService;
}

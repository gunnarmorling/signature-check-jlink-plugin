/**
 *  License: Apache License, Version 2.0
 *  See the LICENSE file in the root directory or <https://www.apache.org/licenses/LICENSE-2.0>.
 */

import com.example.customer.CustomerService;
import com.example.customer.internal.CustomerServiceImpl;

module com.example.customer {
    exports com.example.customer;
    provides CustomerService with CustomerServiceImpl;
}

import { request } from '@/shared/api/client';
import { type Customer, type NewCustomer } from './types';

export function getCustomers(fullName: string, sort: string): Promise<Customer[]> {
    const params = new URLSearchParams();
    params.append("sort", sort);
    params.append("sort", "id,asc");

    if (fullName && fullName.trim() !== "") {
        params.append("fullName", fullName.trim());
    }

    return request<Customer[]>(`/customers?${params.toString()}`);
}

export function createCustomer(newCustomer: NewCustomer): Promise<Customer> {
    return request<Customer>("/customers", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(newCustomer),
    });
}

export function getCustomerById (customerId: number): Promise<Customer> {
    return request<Customer>(`/customers/${customerId}`);
}

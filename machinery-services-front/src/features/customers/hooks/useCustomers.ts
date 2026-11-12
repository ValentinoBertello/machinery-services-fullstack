import { keepPreviousData, useQuery } from '@tanstack/react-query';
import type { SortValue } from '../pages/CustomersPage';
import { getCustomers } from '../api';

type UseCustomersOptions = {
  search?: string;
  sort?: SortValue;
  enabled?: boolean;
  staleTime?: number;
};

export function useCustomers({
  search = '',
  sort = 'fullName,asc',
  enabled = true,
  staleTime,
}: UseCustomersOptions = {}) {
  return useQuery({
    queryKey: ['customers', { search, sort }],
    queryFn: () => getCustomers(search, sort),
    placeholderData: keepPreviousData,
    enabled,
    staleTime,
  });
}
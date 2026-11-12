import type { Customer, NewCustomer } from '@/features/customers/types';
import type {
    Maintenance,
    NewMaintenance,
    UpdateMaintenance,
} from '@/features/maintenances/types';
import type { DashboardReportResponse } from '@/features/dashboard/types';
import type { PageResponse } from '@/shared/types/pagination.types';
import { demoCustomers } from './customers';
import { demoMaintenances } from './maintenances';

const CUSTOMERS_STORAGE_KEY = 'demo.customers';
const MAINTENANCES_STORAGE_KEY = 'demo.maintenances';

function readCollection<T>(key: string, initialData: T[]): T[] {
    const storedData = localStorage.getItem(key);

    if (storedData) {
        try {
            const parsedData = JSON.parse(storedData);

            if (Array.isArray(parsedData)) {
                return parsedData as T[];
            }
        } catch {
            // Si los datos guardados no son válidos, se restauran los iniciales.
        }
    }

    const data = initialData.map(item => ({ ...item }));
    localStorage.setItem(key, JSON.stringify(data));
    return data;
}

function saveCollection<T>(key: string, data: T[]): void {
    localStorage.setItem(key, JSON.stringify(data));
}

function readCustomers(): Customer[] {
    return readCollection(CUSTOMERS_STORAGE_KEY, demoCustomers);
}

function readMaintenances(): Maintenance[] {
    return readCollection(MAINTENANCES_STORAGE_KEY, demoMaintenances);
}

function getNextId(items: { id: number }[]): number {
    return items.reduce((highestId, item) => Math.max(highestId, item.id), 0) + 1;
}

function getDatePart(value: string): string {
    return value.slice(0, 10);
}

function filterMaintenancesByDates(
    maintenances: Maintenance[],
    since: string,
    until: string,
): Maintenance[] {
    return maintenances.filter(maintenance => {
        const date = getDatePart(maintenance.maintenanceDate);
        return date >= since && date <= until;
    });
}

function sortMaintenances(
    maintenances: Maintenance[],
    sortOrder: string,
): Maintenance[] {
    const direction = sortOrder === 'asc' ? 1 : -1;

    return [...maintenances].sort((first, second) => {
        const dateComparison = first.maintenanceDate.localeCompare(second.maintenanceDate);
        return dateComparison !== 0
            ? dateComparison * direction
            : (first.id - second.id) * direction;
    });
}

function paginate<T>(items: T[], page: number, size: number): PageResponse<T> {
    const safePage = Math.max(0, page);
    const safeSize = Math.max(1, size);
    const totalElements = items.length;
    const totalPages = Math.ceil(totalElements / safeSize);
    const start = safePage * safeSize;
    const content = items.slice(start, start + safeSize);

    return {
        content,
        number: safePage,
        size: safeSize,
        totalElements,
        totalPages,
        first: safePage === 0,
        last: totalPages === 0 || safePage >= totalPages - 1,
        numberOfElements: content.length,
    };
}

function withMaintenanceCounts(
    customers: Customer[],
    maintenances: Maintenance[],
): Customer[] {
    const counts = new Map<number, number>();

    maintenances.forEach(maintenance => {
        counts.set(
            maintenance.customerId,
            (counts.get(maintenance.customerId) ?? 0) + 1,
        );
    });

    return customers.map(customer => ({
        ...customer,
        maintenanceCount: counts.get(customer.id) ?? 0,
    }));
}

export async function getCustomers(
    fullName: string,
    sort: string,
): Promise<Customer[]> {
    const search = fullName.trim().toLocaleLowerCase('es');
    let customers = withMaintenanceCounts(readCustomers(), readMaintenances());

    if (search) {
        customers = customers.filter(customer =>
            customer.fullName.toLocaleLowerCase('es').includes(search),
        );
    }

    return customers.sort((first, second) => {
        if (sort === 'maintenanceCount,desc') {
            return second.maintenanceCount - first.maintenanceCount || first.id - second.id;
        }

        const direction = sort === 'fullName,desc' ? -1 : 1;
        const nameComparison = first.fullName.localeCompare(second.fullName, 'es');
        return nameComparison !== 0
            ? nameComparison * direction
            : first.id - second.id;
    });
}

export async function getCustomerById(customerId: number): Promise<Customer> {
    const customer = withMaintenanceCounts(readCustomers(), readMaintenances())
        .find(item => item.id === customerId);

    if (!customer) {
        throw new Error('Cliente no encontrado');
    }

    return customer;
}

export async function createCustomer(newCustomer: NewCustomer): Promise<Customer> {
    const customers = readCustomers();
    const customer: Customer = {
        id: getNextId(customers),
        fullName: newCustomer.fullName.trim(),
        maintenanceCount: 0,
        createdAt: new Date().toISOString(),
    };

    saveCollection(CUSTOMERS_STORAGE_KEY, [...customers, customer]);
    return customer;
}

export async function getMaintenanceByDates(
    since: string,
    until: string,
    page: number,
    size: number,
    sortOrder = 'desc',
): Promise<PageResponse<Maintenance>> {
    const filteredMaintenances = filterMaintenancesByDates(
        readMaintenances(),
        since,
        until,
    );

    return paginate(sortMaintenances(filteredMaintenances, sortOrder), page, size);
}

export async function getMaintenancesByCustomerAndDates(
    customerId: number,
    since: string,
    until: string,
    page: number,
    size: number,
    sortOrder = 'desc',
): Promise<PageResponse<Maintenance>> {
    const customerMaintenances = filterMaintenancesByDates(
        readMaintenances(),
        since,
        until,
    ).filter(maintenance => maintenance.customerId === customerId);

    return paginate(sortMaintenances(customerMaintenances, sortOrder), page, size);
}

export async function createMaintenance(
    newMaintenance: NewMaintenance,
): Promise<Maintenance> {
    const maintenances = readMaintenances();
    const customer = readCustomers().find(item => item.id === newMaintenance.customerId);

    if (!customer) {
        throw new Error('Cliente no encontrado');
    }

    const maintenance: Maintenance = {
        id: getNextId(maintenances),
        ...newMaintenance,
        customerName: customer.fullName,
        createdAt: new Date().toISOString(),
    };

    saveCollection(MAINTENANCES_STORAGE_KEY, [...maintenances, maintenance]);
    return maintenance;
}

export async function updateMaintenance(
    id: number,
    update: UpdateMaintenance,
): Promise<Maintenance> {
    const maintenances = readMaintenances();
    const index = maintenances.findIndex(maintenance => maintenance.id === id);

    if (index === -1) {
        throw new Error('Servicio no encontrado');
    }

    const updatedMaintenance: Maintenance = {
        ...maintenances[index],
        ...update,
    };

    maintenances[index] = updatedMaintenance;
    saveCollection(MAINTENANCES_STORAGE_KEY, maintenances);
    return updatedMaintenance;
}

function differenceInDays(since: string, until: string): number {
    const start = Date.parse(`${since}T00:00:00Z`);
    const end = Date.parse(`${until}T00:00:00Z`);
    return Math.floor((end - start) / 86_400_000) + 1;
}

function getMonthlySeries(
    maintenances: Maintenance[],
    since: string,
    until: string,
) {
    const counts = new Map<string, number>();

    maintenances.forEach(maintenance => {
        const monthKey = getDatePart(maintenance.maintenanceDate).slice(0, 7);
        counts.set(monthKey, (counts.get(monthKey) ?? 0) + 1);
    });

    const result = [];
    let year = Number(since.slice(0, 4));
    let month = Number(since.slice(5, 7));
    const lastYear = Number(until.slice(0, 4));
    const lastMonth = Number(until.slice(5, 7));

    while (year < lastYear || (year === lastYear && month <= lastMonth)) {
        const monthKey = `${year}-${String(month).padStart(2, '0')}`;
        result.push({ year, month, total: counts.get(monthKey) ?? 0 });

        month += 1;
        if (month === 13) {
            month = 1;
            year += 1;
        }
    }

    return result;
}

export async function getDashboardReport(
    since: string,
    until: string,
    limit = 5,
): Promise<DashboardReportResponse> {
    const periodDays = differenceInDays(since, until);

    if (periodDays < 1) {
        throw new Error("La fecha 'since' no puede ser posterior a 'until'");
    }

    const maintenances = filterMaintenancesByDates(readMaintenances(), since, until);
    const customers = readCustomers();
    const customerNames = new Map(customers.map(customer => [customer.id, customer.fullName]));
    const customerTotals = new Map<
        number,
        { totalMaintenances: number; lastMaintenanceDate: string }
    >();

    maintenances.forEach(maintenance => {
        const current = customerTotals.get(maintenance.customerId);

        customerTotals.set(maintenance.customerId, {
            totalMaintenances: (current?.totalMaintenances ?? 0) + 1,
            lastMaintenanceDate:
                !current || maintenance.maintenanceDate > current.lastMaintenanceDate
                    ? maintenance.maintenanceDate
                    : current.lastMaintenanceDate,
        });
    });

    const topCustomers = [...customerTotals.entries()]
        .map(([customerId, data]) => ({
            customerId,
            fullName: customerNames.get(customerId) ?? 'Cliente desconocido',
            ...data,
        }))
        .sort((first, second) =>
            second.totalMaintenances - first.totalMaintenances ||
            second.lastMaintenanceDate.localeCompare(first.lastMaintenanceDate),
        )
        .slice(0, Math.max(0, limit));

    return {
        summary: {
            totalMaintenances: maintenances.length,
            distinctCustomers: customerTotals.size,
            notInvoicedCount: maintenances.filter(item => item.invoiceDate === null).length,
            inFieldCount: maintenances.filter(item => item.place === 'FIELD').length,
            inWorkshopCount: maintenances.filter(item => item.place === 'WORKSHOP').length,
            periodDays,
            avgMonthlyMaintenances:
                Math.round((maintenances.length * 30 / periodDays) * 100) / 100,
        },
        monthlySeries: getMonthlySeries(maintenances, since, until),
        topCustomers,
    };
}

export function resetDemoData(): void {
    saveCollection(
        CUSTOMERS_STORAGE_KEY,
        demoCustomers.map(customer => ({ ...customer })),
    );
    saveCollection(
        MAINTENANCES_STORAGE_KEY,
        demoMaintenances.map(maintenance => ({ ...maintenance })),
    );
}

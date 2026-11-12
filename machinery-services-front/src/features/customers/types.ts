export type Customer = {
    id: number;
    fullName: string;
    maintenanceCount: number;
    createdAt: string;
};

export type NewCustomer = {
    fullName: string;
};
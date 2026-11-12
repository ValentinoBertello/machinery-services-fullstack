export type Maintenance = {
    id: number;
    maintenanceDate: string;
    invoiceDate: string | null;
    place: string;
    model: string;
    description: string;
    customerId: number;
    customerName: string;
    createdAt: string;
};

export type NewMaintenance = {
    maintenanceDate: string;
    invoiceDate: string | null;
    place: string;
    model: string;
    description: string;
    customerId: number;
};

export type UpdateMaintenance = {
    maintenanceDate: string;
    invoiceDate: string | null;
    place: string;
    model: string;
    description: string;
}
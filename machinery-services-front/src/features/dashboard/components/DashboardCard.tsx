import type { ReactNode } from "react";

type Props = {
    children: ReactNode;
    className?: string;
};

export default function DashboardCard({ children, className = "" }: Props) {
    return (
        <section className={`dashboard-card ${className}`}>
            {children}
        </section>
    );
}
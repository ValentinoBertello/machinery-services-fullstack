import { Bar, BarChart, CartesianGrid, LabelList, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";

const MONTHS_ES = [
    "Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic",
];

type MonthlyItem = {
    month: number;
    total: number;
};

type MaintenancesBarChartProps = {
    series: MonthlyItem[];
};

export default function MaintenancesBarChart({ series }: MaintenancesBarChartProps) {
    const chartData = series.map((m) => ({
        label: MONTHS_ES[m.month - 1],
        total: m.total,
    }));

    return (
        <div>
            <div className="dashboard-card-title">Servicios por mes</div>
            <div className="maintenances-chart-container">
                <ResponsiveContainer width="100%" height="100%">
                    <BarChart
                        data={chartData}
                        margin={{ top: 20, right: 0, left: 0, bottom: 0 }}
                        barCategoryGap="25%"
                    >
                        <CartesianGrid vertical={false} strokeDasharray="3 3" />
                        <XAxis
                            dataKey="label"
                            tickLine={false}
                            axisLine={false}
                            interval="preserveStartEnd" />
                        <YAxis
                            width="auto"
                            allowDecimals={false}
                            tickLine={false}
                            axisLine={false}
                        />
                        <Tooltip
                            cursor={{ fill: "rgba(13,110,253,0.06)" }}
                            content={() => null}
                        />
                        <Bar
                            dataKey="total"
                            fill="#f89422"
                            radius={[4, 4, 0, 0]}
                            maxBarSize={56}
                        >
                            <LabelList
                                dataKey="total"
                                position="top"
                                fill="#4a4f53"
                                fontSize={14}
                                fontWeight={600}
                            />
                        </Bar>
                    </BarChart>
                </ResponsiveContainer>
            </div>
        </div>
    );
}
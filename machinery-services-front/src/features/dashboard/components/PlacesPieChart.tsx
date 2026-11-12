import { Legend, Pie, PieChart, ResponsiveContainer, Tooltip } from "recharts";

type LocationSplitChartProps = {
    inFieldCount?: number;
    inWorkshopCount?: number;
};

export default function PlacesPieChart({
    inFieldCount = 0,
    inWorkshopCount = 0,
}: LocationSplitChartProps) {
    const total = inFieldCount + inWorkshopCount;
    const chartData = [
        { name: "CAMPO", value: inFieldCount, fill: "#198754" },
        { name: "TALLER", value: inWorkshopCount, fill: "#0d6efd" }
    ].filter((d) => d.value > 0);

    const pct = (v: number) => (total > 0 ? Math.round((v / total) * 100) : 0);

    return (
        <div>
            <div className="dashboard-card-title">Lugar de servicio</div>
            <div style={{ width: '100%', height: 260 }}>
                <ResponsiveContainer>
                    <PieChart>
                        <Pie
                            data={chartData}
                            dataKey="value"
                            nameKey="name"
                            outerRadius={100}
                            paddingAngle={2}
                        />
                        <Tooltip
                            formatter={(value, name) => {
                                const v = Number(value);
                                return [`${v} (${pct(v)}%)`, String(name)];
                            }}
                        />
                        <Legend
                            position="top"
                            iconType="circle"
                            formatter={(value: string) => {
                                const item = chartData.find((d) => d.name === value);
                                return `${value}: ${pct(item?.value ?? 0)}%`;
                            }}
                        />
                    </PieChart>
                </ResponsiveContainer>
            </div>
        </div>
    );
}
"use client"

import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend,
} from 'chart.js';
import { Line } from 'react-chartjs-2';

ChartJS.register(
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend
);

export const options = {
    responsive: true,
    plugins: {
        legend: {
            position: 'top' as const,
        },
        title: {
            display: true,
            text: 'Burndown Chart',
        },
    },
    scales:{
        x:{
            grid:{
                display: false
            }
        },
        y:{
            grid:{
                display: false
            }
        }
    }
    
};



export default function BurndownChart(data: any) {
    return (
        <div className="flex-1 p-16 h-fit">
            <Line options={options} data={data.data} redraw={true} />
        </div>
    )
}

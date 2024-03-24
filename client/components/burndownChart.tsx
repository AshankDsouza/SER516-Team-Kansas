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



export default function BurndownChart(data: any) {
    const options = {
        responsive: true,
        plugins: {
            legend: {
                position: 'top' as const,
            },
            title: {
                display: true,
                text: 'Line Chart',
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
    return (
        <div className="flex-1 p-16 h-fit overflow-scroll">
            <Line options={options} data={data.data} redraw={true} />
        </div>
    )
}


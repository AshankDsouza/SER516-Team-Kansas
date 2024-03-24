"use client"

import { getBurndowMetrics, getProjectMilestones } from "@/actions/project"
import {
    Select,
    SelectContent,
    SelectGroup,
    SelectItem,
    SelectLabel,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select"
import { useEffect, useState } from "react"
import BurndownChart from "./burndownChart"
import { useRouter } from "next/navigation"
import Chip from '@mui/material/Chip';
import LineGraph from "./graphs/lineGraph"
import Highcharts from "highcharts"



function Burndown({slug, sprints}:{slug: string, sprints:{id: string, value: string}[]}) {

    const router = useRouter();

    const [selectedSprintID, setselectedSprintID] = useState("")
    const [showChart, setShowChart] = useState(true)
    const [labels, setLabels] = useState<string[]>([])
    const [open_points, setopen_points] = useState<number[]>([])

    let sprintNames = sprints.map((sprint) => sprint.value);

    const [selectedSprints, setSelectedSprints] = useState<string[]>(sprintNames)


    useEffect(() => {
        getBurndowMetrics(slug)
            .then((data: any) => {
                console.log({dataFromMulti: data});

                if(!data){
                    router.refresh();
                    return
                }
                console.log({dataFromMulti: data});
                
                // setShowChart(false);
                // const daysArray = data.map((item: any) => item.day);
                // setLabels(daysArray);
                // const open_points = data.map((item: any) => item.open_points);
                // setopen_points(open_points);
                // setShowChart(true);
            })
    }, [selectedSprintID])


    const data = {
        labels,
        datasets: [
            {
                label: 'Open points',
                data: open_points,
                borderColor: '#000',
                backgroundColor: '#666',
            },
        ],
    };

    const series = [{

        marker: {
            fillColor: 'transparent',
        },
        data: [7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6]
    }, {

        marker: {
            fillColor: 'transparent'
        },
        data: [-0.2, 0.8, 5.7, 11.3, 17.0, 22.0, 24.8, 24.1, 20.1, 14.1, 8.6, 2.5]
    }, {

        data: [-0.9, 0.6, 3.5, 8.4, 13.5, 17.0, 18.6, 17.9, 14.3, 9.0, 3.9, 1.0]
    }, {

        lineColor: 'red',
        data: [3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6, 4.8]
    }]

    return (
        <div className="flex border-2 border-slate-300 rounded-md divide-x-2">
            <div className="filters flex flex-col divide-y-2">
                <div className="p-8 font-bold">Filters</div>
                <div className="p-8">
                    <Select onValueChange={(e) => setselectedSprintID(e)}>
                        <SelectTrigger className="w-[180px]">
                            <SelectValue placeholder="Sprint" />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectGroup>
                                <SelectLabel>Sprints</SelectLabel>
                                {sprints && sprints.map(sprint =>
                                    <>
                                     <SelectItem key={sprint.id} value={sprint.id}>{sprint.value}</SelectItem>
                             
                                    </>
                                    
                                )}
                            </SelectGroup>
                        </SelectContent>
                    </Select>
                </div>

                <div className="p-8">
                    {selectedSprints.length > 0 && (
                        <div>
                            <div className="font-bold">Selected Sprints:</div>
                            <ul>
                                {selectedSprints.map(sprint => (
                                    <>
                                    <li key={sprint}>{sprint}</li>
                                    <Chip
                                    onDelete={() => (console.log("to be implemented later"))}
                                   />
                                   </>
                                ))}
                            </ul>
                        </div>
                    )}
                </div>
            </div>
            {showChart ? <LineGraph name="Sprint Burndown" labels={[]} series={series} /> : <div className="flex-1 p-16 min-h-50">Loading...</div>}
        </div>
    )
}

export default Burndown

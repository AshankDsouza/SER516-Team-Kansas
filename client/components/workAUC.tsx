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
import WorkAUC from "./burndownChart"
import { useRouter } from "next/navigation"
import Chip from '@mui/material/Chip';



function workAUC({slug, sprints}:{slug: string, sprints:{id: string, value: string}[]}) {

    const router = useRouter();

    const [selectedSprintID, setselectedSprintID] = useState("")
    const [showChart, setShowChart] = useState(true)
    const [labels, setLabels] = useState<string[]>([])
    const [open_points, setopen_points] = useState<number[]>([])
    const [optimal_points, setoptimal_points] = useState<number[]>([])
    const [story_points, setstory_points] = useState<number[]>([])

    let sprintNames = sprints.map((sprint) => sprint.value);

    useEffect(() => {
        getBurndowMetrics(selectedSprintID)
            .then((data: any) => {
                if(!data){
                    router.refresh();
                    return
                }
                setShowChart(false);
                const daysArray = data.map((item: any) => item.day);
                setLabels(daysArray);
                const open_points = data.map((item: any) => item.open_points);
                setopen_points(open_points);
                const optimal_points = data.map((item: any) => item.optimal_points);
                setoptimal_points(optimal_points);
                const story_points = data.map((item: any) => item.story_points);
                setstory_points(story_points);
                setShowChart(true);
            })
    }, [selectedSprintID])


    const data = {
        labels,
        datasets: [
            {
                label: 'Open points',
                data: open_points,
                borderColor: '#000',
                backgroundColor: '#000',
            },
            {
                label: 'Optimal points',
                data: optimal_points,
                borderColor: '#333',
                backgroundColor: '#333',
            },
            {
                label: 'Story points',
                data: story_points,
                borderColor: '#666',
                backgroundColor: '#666',
            },
        ],
    };

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
            </div>
            {showChart ? <WorkAUC data={data} /> : <div className="flex-1 p-16 min-h-50">Loading...</div>}
        </div>
    )
}

export default workAUC
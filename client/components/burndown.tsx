"use client"

import { getBurndowMetrics } from "@/actions/project"
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
import { useRouter } from "next/navigation"
import MultiLineGraph from "./graphs/multiLineGraph"



function Burndown({slug, sprints}:{slug: string, sprints:{id: string, value: string}[]}) {

    const router = useRouter();

    const [selectedSprintID, setselectedSprintID] = useState("")
    const [showChart, setShowChart] = useState(true)

    const [dataPoints, setDataPoints] = useState<number[]>([]);

    useEffect(() => {
        getBurndowMetrics(slug)
            .then((data: any) => {

                if(!data){
                    router.refresh();
                    return
                }
                console.log({dataFromMulti: data});
                const result = [];

                // Populate result array and labels list
                Object.entries(data).forEach(([sprintName, sprintData]) => {
                    const formattedSprintData ={
                        name: sprintName,
                        data: sprintData.map(({ day, open_points, optimal_points }) => ([optimal_points, open_points])),
                        
                    }
                    result.push(formattedSprintData);
                });

                console.log({result});
                setDataPoints(result);
            })
    }, [selectedSprintID])


    const series =  dataPoints;
    

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
            {showChart ? <MultiLineGraph name="Sprint Burndown" labels={[]} series={series} /> : <div className="flex-1 p-16 min-h-50">Loading...</div>}
        </div>
    )
}

export default Burndown

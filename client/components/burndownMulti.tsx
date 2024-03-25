"use client"

import { getBurndowMetricsMulti } from "@/actions/project"
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



function BurndownMulti({slug, sprints}:{slug: string, sprints:{id: string, value: string}[]}) {

    const router = useRouter();

    const [selectedSprintID, setselectedSprintID] = useState("")
    const [showChart, setShowChart] = useState(true)

    const [dataPoints, setDataPoints] = useState<number[]>([]);

    useEffect(() => {
        getBurndowMetricsMulti(slug)
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
            {showChart ? <MultiLineGraph name="Sprint Burndown" labels={[]} series={series} /> : <div className="flex-1 p-16 min-h-50">Loading...</div>}
        </div>
    )
}

export default BurndownMulti

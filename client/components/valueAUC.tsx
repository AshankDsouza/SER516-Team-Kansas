"use client"

import { getValueAUC } from "@/actions/project"
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



function ValueAUC({slug, sprints}:{slug: string, sprints:{id: string, value: string}[]}) {

    const router = useRouter();

    const [selectedSprintID, setselectedSprintID] = useState("")
    const [showChart, setShowChart] = useState(true)
    const [labels, setLabels] = useState<string[]>([])
    const [value, setValue] = useState<number[]>([])
    const [bval, set_bv] = useState<number[]>([])

    let sprintNames = sprints.map((sprint) => sprint.value);

    useEffect(() => {
        getValueAUC(slug,selectedSprintID)
            .then((data: any) => {
                if(!data){
                    router.refresh();
                    return
                }
                setShowChart(false);
                const daysArray = data.map((item: any) => item.date);
                setLabels(daysArray);
                const valuePoints = data.map((item: any) => item.value_points);
                setValue(valuePoints);
                const bv = data.map((item: any) => item.BV);
                set_bv(bv);
                setShowChart(true);
            })
    }, [selectedSprintID])


    const data = {
        labels,
        datasets: [

            {
                label: 'value',
                data: value,
                borderColor: '#666',
                backgroundColor: '#666',
            }
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
            {showChart ? <BurndownChart data={data} /> : <div className="flex-1 p-16 min-h-50">Loading...</div>}
        </div>
    )
}

export default ValueAUC
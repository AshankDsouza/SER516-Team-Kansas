"use client"

import { getProjectMilestones } from "@/actions/project"
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


function Burndown() {
    type sprint = {
        id: string,
        value: string
    }
    const [sprints, setsprints] = useState<sprint[]>([{ id: '1', value: "sprint-1" }, { id: '2', value: "sprint-2" }, { id: '3', value: "sprint-3" }])
    const [selectedSprintID, setselectedSprintID] = useState("")
    const [showChart, setShowChart] = useState(false)

    useEffect(() => {
        getProjectMilestones("1")
            .then((data: any) => {
                setsprints(data)
                setShowChart(true)
            })
    }, [])

    const labels = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'august', 'september', 'october'];

    const data = {
        labels,
        datasets: [
            {
                label: 'Optimal points',
                data: [10, 20, 50, 40, 80, 100, 60, 30, 5, -1],
                borderColor: '#000',
                backgroundColor: '#666',
            },
        ],
    };

    return (
        <div className="flex border-2 border-slate-300 rounded-md divide-x-2">
            <div className="filters flex flex-col divide-y-2">
                <div className="p-8">Filters</div>
                <div className="p-8">
                    <Select onValueChange={(e) => setselectedSprintID(e)}>
                        <SelectTrigger className="w-[180px]">
                            <SelectValue placeholder="Sprint" />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectGroup>
                                <SelectLabel>Sprints</SelectLabel>
                                {sprints.map(sprint =>
                                    <SelectItem key={sprint.id} value={sprint.id}>{sprint.value}</SelectItem>
                                )}
                            </SelectGroup>
                        </SelectContent>
                    </Select>
                </div>
            </div>
            {showChart && <BurndownChart data={data} />}
        </div>
    )
}

export default Burndown

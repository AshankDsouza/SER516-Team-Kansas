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
import BarGraph from "./barGraph"


function LeadTime(props: any) {
  const {slug} = props;


  type sprint = {
    id: string,
    value: string
  }
  const [sprints, setsprints] = useState<sprint[]>([{ id: '1', value: "sprint-1" }, { id: '2', value: "sprint-2" }, { id: '3', value: "sprint-3" }])
  const [selectedSprintID, setselectedSprintID] = useState("")
  const [showChart, setShowChart] = useState(false)
  const [labels, setLabels] = useState<string[]>([])
  const [open_points, setopen_points] = useState<number[]>([])

  useEffect(() => {
    getProjectMilestones(slug)
      .then((data: any) => {
        setsprints(data)
        setShowChart(true)
      })
    setopen_points([])
  }, [])

  useEffect(() => {
    getBurndowMetrics(selectedSprintID)
      .then((data: any) => {
        if (data.error)
          return
        const daysArray = data.map((item: any) => item.day);
        setLabels(daysArray)
        const open_points = data.map((item: any) => item.open_points)
        setopen_points(open_points)

      })
  }, [selectedSprintID])


  const data = {
    labels,
    datasets: [
      {
        label: 'Optimal points',
        data: open_points,
        borderColor: '#000',
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
                {sprints.map(sprint =>
                  <SelectItem key={sprint.id} value={sprint.id}>{sprint.value}</SelectItem>
                )}
              </SelectGroup>
            </SelectContent>
          </Select>
        </div>
      </div>
      {showChart ? <BarGraph series={undefined}/> : <div className="flex-1 p-16 min-h-50">Loading...</div>}
    </div>
  )
}

export default LeadTime

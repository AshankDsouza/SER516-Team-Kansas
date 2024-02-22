"use client"

import { getLeadTime } from "@/actions/project"
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
import { useRouter } from "next/navigation"


function LeadTime({ slug, sprints }: { slug: string, sprints: { id: string, value: string }[] }) {

  const router = useRouter()

  const [selectedSprintID, setselectedSprintID] = useState("")
  const [showChart, setShowChart] = useState(true)
  const [labels, setLabels] = useState<string[]>([])
  const [series, setSeries] = useState<any[]>([])

  useEffect(() => {
    getLeadTime(slug, selectedSprintID)
    .then((leadData: any) => {
      if (!leadData){
        router.refresh();
        return
      }
        const cleanData: any[] = [];
        leadData.forEach((dataPoint: any) => {
          if (dataPoint.finish_date !== 'null') {
            const finishDate = new Date(dataPoint.finish_date);
            const createdDate = new Date(dataPoint.created_date);
            // Calculate the difference in milliseconds
            const timeDifference: number = Math.abs(createdDate.getTime() - finishDate.getTime());
            // Convert milliseconds to days
            dataPoint.dayCount = Math.ceil(timeDifference / (1000 * 60 * 60 * 24));
            cleanData.push(dataPoint);
          }
        })
        const dayCountList = cleanData.map((item: any) => item.dayCount);
        const taskNames = cleanData.map((item: any) => item.userStory_Name)
        let series = [
          {
            name: 'Lead Time',
            data: dayCountList
          }
        ]
        setLabels(taskNames)
        setSeries(series)
        setShowChart(true)
      })
  }, [selectedSprintID])

  return (
    <div className="flex border-2 border-slate-300 rounded-md divide-x-2">
      <div className="filters flex flex-col divide-y-2">
        <div className="p-8 font-bold">Filters</div>
        <div className="p-8">
          <Select onValueChange={(e) => {setselectedSprintID(e); setShowChart(false)}}>
            <SelectTrigger className="w-[180px]">
              <SelectValue placeholder="Sprint" />
            </SelectTrigger>
            <SelectContent>
              <SelectGroup>
                <SelectLabel>Sprints</SelectLabel>
                {sprints && sprints.map(sprint =>
                  <SelectItem key={sprint.id} value={sprint.id}>{sprint.value}</SelectItem>
                )}
              </SelectGroup>
            </SelectContent>
          </Select>
        </div>
      </div>
      {showChart ? <BarGraph name="Lead Time" labels={labels} series={series} /> : <div className="flex-1 p-16 min-h-50">Loading...</div>}
    </div>
  )
}

export default LeadTime

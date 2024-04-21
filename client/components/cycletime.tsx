"use client"

import { getCyleTime, getProjectMilestones } from "@/actions/project"
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


function CycleTime({ slug, sprints }: { slug: string, sprints: { id: string, value: string }[] }) {

  const router = useRouter()
  const [selectedSprintID, setselectedSprintID] = useState("")
  const [showChart, setShowChart] = useState(true)
  const [labels, setLabels] = useState<string[]>([])
  const [series, setSeries] = useState<any>([])


  useEffect(() => {
    const labels = localStorage.getItem(`${slug}-${selectedSprintID}-cycleTime-labels`)
    let series = localStorage.getItem(`${slug}-${selectedSprintID}-cycleTime-series`)
    if (labels !== null && series !== null) {
      setLabels(labels.split(','))
      setSeries(JSON.parse(series))
      setShowChart(true)
    } else {
      getCyleTime(slug, selectedSprintID)
        .then((data: any) => {
          if (!data) {
            router.refresh();
            return
          }
          const labels = data.map((item: any) => item.taskName);
          const openPoints = data.map((item: any) => item.cycleTime)
          setLabels(labels)

          let series = [
            {
              name: 'Cycle Time',
              data: openPoints
            }
          ]
          setSeries(series)
          localStorage.setItem(`${slug}-${selectedSprintID}-cycleTime-labels`, labels)
          localStorage.setItem(`${slug}-${selectedSprintID}-cycleTime-series`, JSON.stringify(series))
          setShowChart(true)
        })
    }
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
      {showChart ? <BarGraph name="Cycle Time" labels={labels} series={series} /> : <div className="flex-1 p-16 min-h-50">Loading...</div>}
    </div>
  )
}

export default CycleTime

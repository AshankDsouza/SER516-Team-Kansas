"use client"

import { getBDConsistency } from "@/actions/project"
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



function BDConsistency({slug, sprints}:{slug: string, sprints:{id: string, value: string}[]}) {

  const router = useRouter();

  type sprint = {
    id: string,
    value: string
  }
  const [selectedSprintID, setselectedSprintID] = useState("")
  const [showChart, setShowChart] = useState(true)
  const [labels, setLabels] = useState<string[]>([])
  const [series, setSeries] = useState<any[]>([])

  useEffect(() => {
    getBDConsistency(slug, selectedSprintID).then((data) => {
      if (!data) {
        router.refresh();
        return
      }
      let dataPoints = data.data_to_plot.story_points_projection;
      let labels = data.data_to_plot.x_axis;
      let series = [
        {
          name: 'BDConsistency',
          data: dataPoints
        }
      ]
      setSeries(series)
      setLabels(labels)
      setShowChart(true)
    });


  }, [selectedSprintID])

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
      {showChart ? <BarGraph name="BDConsistency" labels={labels} series={series} /> : <div className="flex-1 p-16 min-h-50">Loading...</div>}
    </div>
  )
}

export default BDConsistency;

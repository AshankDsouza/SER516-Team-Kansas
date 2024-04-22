"use client"

import { getLeadTime, getVelocity, getWorkCapacity } from "@/actions/project"
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
import LineGraph from "./graphs/lineGraph"


function WorkCapacityGraph(props: any) {
  const { slug } = props;

  const [showChart, setShowChart] = useState(true)
  const [labels, setLabels] = useState<string[]>([])
  const [series, setSeries] = useState<any[]>([])

  useEffect(() => {
    const labels = localStorage.getItem(`${slug}-wc-labels`)
    let series = localStorage.getItem(`${slug}-wc-series`)
    if (labels !== null && series !== null) {
      setLabels(labels.split(','))
      setSeries(JSON.parse(series))
      setShowChart(true)
    } else {
      getWorkCapacity(slug).then((data) => {

        let sprintsCompletedData = data.filter((data: any) => data.completedPoints !== 0);
        sprintsCompletedData = sprintsCompletedData.sort(function (a: any, b: any) {
          return ('' + a.sprintName).localeCompare(b.sprintName);
        })

        let dataPoints = sprintsCompletedData.map((data: any) => data.completedPoints);

        let labels = sprintsCompletedData.map((data: any) => data.sprintName);

        let series = [
          {
            name: 'Work Capacity',
            data: dataPoints
          }
        ]
        setSeries(series)
        setLabels(labels);
        localStorage.setItem(`${slug}-wc-labels`, labels)
        localStorage.setItem(`${slug}-wc-series`, JSON.stringify(series))
        setShowChart(true)


      });
    }

  }, [])

  return (
    <div className="flex border-2 border-slate-300 rounded-md divide-x-2">
      {showChart ? <LineGraph name="Work Capacity" labels={labels} series={series} /> : <div className="flex-1 p-16 min-h-50">Loading...</div>}
    </div>
  )
}

export default WorkCapacityGraph;

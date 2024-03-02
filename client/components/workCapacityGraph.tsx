"use client"

import { getLeadTime, getVelocity } from "@/actions/project"
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


function WorkCapacityGraph(props: any) {
  const {slug} = props;


  type sprint = {
    id: string,
    value: string
  }
  const [sprints, setsprints] = useState<sprint[]>([{ id: '1', value: "sprint-1" }, { id: '2', value: "sprint-2" }, { id: '3', value: "sprint-3" }])
  const [selectedSprintID, setselectedSprintID] = useState("")
  const [showChart, setShowChart] = useState(true)
  const [labels, setLabels] = useState<string[]>([])
  const [open_points, setopen_points] = useState<number[]>([])
  const [series, setSeries] = useState<any[]>([])

//   useEffect(()=>{
    
//     getVelocity(slug).then((data)=>{

//       let sprintsCompletedData = data.filter((data: any)=> data.totalPoints !== 0);

//       sprintsCompletedData = sprintsCompletedData.sort(function (a: any, b: any) {
//         return ('' + a.sprintName).localeCompare(b.sprintName);
//     })
      
//       let dataPoints = sprintsCompletedData.map((data: any) =>  data.totalPoints);

//       let labels = sprintsCompletedData.map((data: any) =>  data.sprintName);

//       let series = [
//         {
//             name: 'Velocity',
//             data: dataPoints
//         }
//       ]
//       setSeries(series)
//       setLabels(labels);
//       setShowChart(true)


//     });
    

//   }, [])

  return (
    <div className="flex border-2 border-slate-300 rounded-md divide-x-2">
      <div className="filters flex flex-col divide-y-2">
        <div className="p-8 font-bold">Filters</div>
        <div className="p-8">
        </div>
      </div>
      {showChart ? <BarGraph name="Work Capactiy"  labels={labels} series={series}/> : <div className="flex-1 p-16 min-h-50">Loading...</div>}
    </div>
  )
}

export default WorkCapacityGraph;

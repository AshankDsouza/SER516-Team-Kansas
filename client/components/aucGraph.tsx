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
import BarGraph from "./barGraphWorkAUC"

import { getAUCData } from "@/actions/project";


function AUCGraph({slug, sprints}:{slug: string, sprints:{id: string, value: string}[]}) {
    // bar graph to represent:
    let data = {
      "work_auc_by_sprint_order": [0, 0, 0, 0, 0],
      "x_axis": [
        "Sprint 1",
        "Sprint 2",
        "Sprint 3",
        "Sprint 4",
        "Sprint 5"
      ],
    }
    const [aucData, setAucData] = useState(data)

    let labels = aucData.x_axis;
    let series = [
        {
            name: "Work AUC",
            data: aucData.work_auc_by_sprint_order
        }
    ]

    useEffect(() => {
      getAUCData(slug).then((data) => {
        console.log(data)
        setAucData(data)
      })
      
    }
    , [slug])
    
    return (
        <div className="flex border-2 border-slate-300 rounded-md divide-x-2">
                    <BarGraph name="Work AUC" labels={labels} series={series} />
        </div>
    )
}

export default AUCGraph;

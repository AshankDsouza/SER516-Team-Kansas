"use client"

import { useEffect, useState } from "react"
import { Button } from "./ui/button"
import Burndown from "./burndown"
import LeadTime from "./leadtime"
import CycleTime from "./cycletime"
import { ArrowLeft, RotateCcw } from "lucide-react"
import Link from "next/link"
import { getProjectMilestones } from "@/actions/project"
import { useRouter } from "next/navigation"
import WorkCapacityGraph from "./workCapacityGraph"
import FocusFactor from "./focusFactor"
import VelocityGraph from "./velocityGraph"
import EstimateEffectiveness from "./estimateEffectiveness"


function Dashboard({ slug }: { slug: string }) {

  type sprint = {
    id: string,
    value: string
  }

  const router = useRouter();

  const [sprints, setsprints] = useState<sprint[]>([{ id: '1', value: "sprint-1" }, { id: '2', value: "sprint-2" }, { id: '3', value: "sprint-3" }])
  const [chart, setChart] = useState("Burndown")
  const charts = ["Burndown", "Lead time", "Cycle time", "Velocity", "Focus Factor", "Work Capacity", "Estimate Effectiveness"]

  useEffect(() => {
    getProjectMilestones(slug)
      .then((data: any) => {
        if(!data){
          router.refresh()
        }
        setsprints(data)
      })
  }, [])

  return (
    <div className="flex flex-col gap-4">
      <Link href={"/project"}><div className="flex font-bold gap-2 underline"><ArrowLeft></ArrowLeft>{slug}</div></Link>
      <div className="flex justify-between mb-4">
        <div className="flex gap-4">
        {charts.map(chart =>
          <Button key={chart} onClick={() => setChart(chart)}>{chart}</Button>
        )}
        </div>
      <Button onClick={()=>localStorage.clear()} className="flex gap-2"><RotateCcw size={16}/>Refresh cache</Button>
      </div>
      {chart == "Burndown" ? <Burndown slug={slug} sprints={sprints}/> : <div className=" hidden"></div>}
      {chart == "Lead time" ? <LeadTime slug={slug} sprints={sprints} /> : <div className=" hidden"></div>}
      {chart == "Cycle time" ? <CycleTime slug={slug} sprints={sprints} /> : <div className=" hidden"></div>}
      {chart == "Focus Factor" ? <FocusFactor slug={slug} sprints={sprints} /> : <div className=" hidden"></div>}
      {chart == "Velocity" ? <VelocityGraph slug={slug} sprints={sprints} /> : <div className=" hidden"></div>}
      {chart == "Estimate Effectiveness" ? <EstimateEffectiveness/> : <div className=" hidden"></div>}
      {chart == "Work Capacity" ? <WorkCapacityGraph slug={slug} /> : <div></div>}

    </div>
  )
} 

export default Dashboard
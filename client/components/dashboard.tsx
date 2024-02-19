"use client"

import { useState } from "react"
import { Button } from "./ui/button"
import Burndown from "./burndown"
import LeadTime from "./leadtime"
import CycleTime from "./cycletime"

function Dashboard({slug}:{slug: string}) {
    const [chart, setChart] = useState("Burndown")
    const charts = ["Burndown", "Lead time", "Cycle time"]

  return (
    <div>
        <div className="flex gap-4 mb-4">
            {charts.map(chart => 
                <Button key={chart} onClick={()=>setChart(chart)}>{chart}</Button>
            )}
        </div>
        {chart=="Burndown"? <Burndown slug={slug}/>: <div></div>}
        {chart=="Lead time"? <LeadTime slug={slug} />: <div></div>}
        {chart=="Cycle time"? <CycleTime slug={slug} />: <div></div>}
    </div>
  )
}

export default Dashboard
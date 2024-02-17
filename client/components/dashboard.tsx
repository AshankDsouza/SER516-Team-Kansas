"use client"

import { useState } from "react"
import { Button } from "./ui/button"
import Burndown from "./burndown"
import LeadTime from "./leadtime"
import CycleTime from "./cycletime"

function Dashboard() {
    const [chart, setChart] = useState("Burndown")
    const charts = ["Burndown", "Lead time", "Cycle time"]

  return (
    <div>
        <div className="flex gap-4 mb-4">
            {charts.map(chart => 
                <Button key={chart} onClick={()=>setChart(chart)}>{chart}</Button>
            )}
        </div>
        {chart=="Burndown"? <Burndown/>: <div></div>}
        {chart=="Lead time"? <LeadTime/>: <div></div>}
        {chart=="Cycle time"? <CycleTime/>: <div></div>}
    </div>
  )
}

export default Dashboard
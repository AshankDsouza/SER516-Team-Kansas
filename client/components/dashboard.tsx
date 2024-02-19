"use client"

import { useState } from "react"
import { Button } from "./ui/button"
import Burndown from "./burndown"
import LeadTime from "./leadtime"
import CycleTime from "./cycletime"
import { ArrowLeft } from "lucide-react"
import Link from "next/link"

function Dashboard({ slug }: { slug: string }) {
  const [chart, setChart] = useState("Burndown")
  const charts = ["Burndown", "Lead time", "Cycle time"]

  return (
    <div className="flex flex-col gap-4">
      <Link href={"/project"}><div className="flex font-bold gap-2 underline"><ArrowLeft></ArrowLeft>{slug}</div></Link>
      <div className="flex gap-4 mb-4">
        {charts.map(chart =>
          <Button key={chart} onClick={() => setChart(chart)}>{chart}</Button>
        )}
      </div>
      {chart == "Burndown" ? <Burndown slug={slug} /> : <div></div>}
      {chart == "Lead time" ? <LeadTime slug={slug} /> : <div></div>}
      {chart == "Cycle time" ? <CycleTime slug={slug} /> : <div></div>}
    </div>
  )
}

export default Dashboard
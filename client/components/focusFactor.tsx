"use client"
import { useState, useEffect } from "react"
import BarGraph from "./barGraph"
import { getFocusFactor } from "@/actions/project";
import LineGraph from "./graphs/lineGraph";

function FocusFactor(props: any) {
  const { slug } = props;
  type sprint = {
    id: string,
    value: string
  }
  const [showChart, setShowChart] = useState(false)
  const [labels, setLabels] = useState<string[]>([])
  const [series, setSeries] = useState<any[]>([])
  useEffect(() => {
    getFocusFactor(slug)
      .then((data: any) => {
        setSeries(data);
      })
  }, [])
  

  return (
    <div className="flex border-2 border-slate-300 rounded-md divide-x-2">
      <div className="filters flex flex-col divide-y-2">
        <div className="p-8 font-bold">Focus Factor</div>
      </div>
      {showChart ? <LineGraph series={series} /> : <div className="flex-1 p-16 min-h-50">Loading...</div>}
    </div>
  )
}

export default FocusFactor 

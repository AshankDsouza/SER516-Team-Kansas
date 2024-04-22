"use client"
import { useState, useEffect } from "react"
import BarGraph from "./barGraph"
import { getFocusFactor } from "@/actions/project";
import LineGraph from "./graphs/lineGraph";

function FocusFactor(props: any) {
  const { slug } = props;
  const [showChart, setShowChart] = useState(false)
  const [labels, setLabels] = useState<string[]>([])
  const [series, setSeries] = useState<any[]>([])
  useEffect(() => {
    const labels = localStorage.getItem(`${slug}-ff-labels`)
    let series = localStorage.getItem(`${slug}-ff-series`)
    if (labels !== null && series !== null) {
      setLabels(labels.split(','))
      setSeries(JSON.parse(series))
      setShowChart(true)
    } else {
      getFocusFactor(slug)
        .then((data: any) => {
          data = data.sort(function (a: any, b: any) {
            return ('' + a.label).localeCompare(b.label);
          })
          let labels = data.map((x: any) => x.label)
          let focusFactor = data.map((x: any) => x.focusFactor)
          let series = [
            {
              name: 'Focus Factor',
              data: focusFactor
            }
          ]
          setSeries(series)
          setLabels(labels)
          localStorage.setItem(`${slug}-ff-labels`, labels)
          localStorage.setItem(`${slug}-ff-series`, JSON.stringify(series))
          setShowChart(true)
        })
    }
  }, [])


  return (
    <div className="flex border-2 border-slate-300 rounded-md divide-x-2">
      {showChart ? <LineGraph series={series} labels={labels} name="Focus factor" /> : <div className="flex-1 p-16 min-h-50">Loading...</div>}
    </div>
  )
}

export default FocusFactor 

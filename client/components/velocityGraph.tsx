"use client"

import { getVelocity } from "@/actions/project"
import { useEffect, useState } from "react"
import BarGraph from "./barGraph"


function VelocityGraph(props: any) {
  const { slug } = props;

  const [showChart, setShowChart] = useState(true)
  const [labels, setLabels] = useState<string[]>([])
  const [series, setSeries] = useState<any[]>([])

  useEffect(() => {
    const labels = localStorage.getItem(`${slug}-velocity-labels`)
    let series = localStorage.getItem(`${slug}-velocity-series`)
    if (labels !== null && series !== null) {
      setLabels(labels.split(','))
      setSeries(JSON.parse(series))
      setShowChart(true)
    } else {
      getVelocity(slug).then((data) => {

        let sprintsCompletedData = data.filter((data: any) => data.totalPoints !== 0);

        sprintsCompletedData = sprintsCompletedData.sort(function (a: any, b: any) {
          return ('' + a.sprintName).localeCompare(b.sprintName);
        })

        let dataPoints = sprintsCompletedData.map((data: any) => data.totalPoints);

        let labels = sprintsCompletedData.map((data: any) => data.sprintName);

        let series = [
          {
            name: 'Velocity',
            data: dataPoints
          }
        ]
        setSeries(series)
        setLabels(labels);
        localStorage.setItem(`${slug}-velocity-labels`, labels)
        localStorage.setItem(`${slug}-velocity-series`, JSON.stringify(series))
        setShowChart(true)


      });
    }


  }, [])

  return (
    <div className="flex border-2 border-slate-300 rounded-md divide-x-2">
      {showChart ? <BarGraph name="Velocity" labels={labels} series={series} /> : <div className="flex-1 p-16 min-h-50">Loading...</div>}
    </div>
  )
}

export default VelocityGraph;

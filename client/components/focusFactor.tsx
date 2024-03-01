import { cookies } from "next/headers";
import { useState, useEffect } from "react"
import BarGraph from "./barGraph"
import { getProjectMilestones } from "@/actions/project";


async function FocusFactor(props: any) {
  const { slug } = props;
  var myHeaders = new Headers();
  const auth_token = cookies().get("auth_token")
  myHeaders.append("Authorization", `Bearer ${auth_token?.value}`);

  var requestOptions = {
    method: 'GET',
    headers: myHeaders
  };
  type sprint = {
    id: string,
    value: string
  }
  const [showChart, setShowChart] = useState(false)
  const [sprints, setsprints] = useState<sprint[]>([{ id: '1', value: "sprint-1" }, { id: '2', value: "sprint-2" }, { id: '3', value: "sprint-3" }])
  const [labels, setLabels] = useState<string[]>([])
  const [series, setSeries] = useState<any[]>([])
  useEffect(() => {
    getProjectMilestones(slug)
      .then((data: any) => {
        setsprints(data)
      })
  }, [])
  const response1:any = await fetch(`http://localhost:8080/api/${sprints[0].id}/getTotalPoint`, requestOptions)
  const response2:any = await fetch(`http://localhost:8080/api/${sprints[0].id}/getCompletedPoints`, requestOptions)
  let lab = [];
  let ser = [];
  for (let i = 0; i < response1.length; i++) {
    lab.push(response1[i].sprintName)
    let focusFactor = response2[i].completedPoints == 0 ? 0 : response1[i].totalPoints / response2[i].completedPoints;
    ser.push(focusFactor)
  }
  setLabels(lab);
  setSeries(ser);
  setShowChart(true)

  return (
    <div className="flex border-2 border-slate-300 rounded-md divide-x-2">
      <div className="filters flex flex-col divide-y-2">
        <div className="p-8 font-bold">Focus Factor</div>
      </div>
      {showChart ? <BarGraph name="Focus Factor" labels={labels} series={series} /> : <div className="flex-1 p-16 min-h-50">Loading...</div>}
    </div>
  )
}

export default FocusFactor 

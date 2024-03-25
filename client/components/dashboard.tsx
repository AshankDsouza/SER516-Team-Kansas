"use client"

import { useEffect, useState } from "react";
import { Button } from "./ui/button";
import Burndown from "./burndown";
import LeadTime from "./leadtime";
import CycleTime from "./cycletime";
import { ArrowLeft, RotateCcw } from "lucide-react";
import Link from "next/link";
import { getProjectMilestones } from "@/actions/project";
import { useRouter } from "next/navigation";
import WorkCapacityGraph from "./workCapacityGraph";
import FocusFactor from "./focusFactor";
import VelocityGraph from "./velocityGraph";
import EstimateEffectiveness from "./estimateEffectiveness";
import ArbitaryCycleTimeGraph from "./arbitraryCycleTime";
import LeadTimeArbitaryGraph from "./leadTimeArbitary"
import BurndownMulti from "./burndownMulti"


function Dashboard({ slug }: { slug: string }) {
  type sprint = {
    id: string;
    value: string;
  };

  const router = useRouter();


  const [sprints, setsprints] = useState<sprint[]>([
    { id: "1", value: "sprint-1" },
    { id: "2", value: "sprint-2" },
    { id: "3", value: "sprint-3" },
  ]);
  const [chart, setChart] = useState("Burndown");
  const charts = [
    "Burndown",
    "BurndownMulti",
    "Lead time",
    "Cycle time",
    "Velocity",
    "Focus Factor",
    "Work Capacity",
    "Estimate Effectiveness",
    "Arbitary Cycle Time" ,"LeadTime Arbitary"
  ];

  useEffect(() => {
    getProjectMilestones(slug).then((data: any) => {
      if (!data) {
        router.refresh();
      }
      setsprints(data);
    });
  }, []);

  return (
    <div className="flex flex-col gap-4">
      <Link href={"/project"}><div className="flex font-bold gap-2 underline"><ArrowLeft></ArrowLeft>{slug}</div></Link>
      <div className="flex justify-between mb-4">
        <div className="flex-wrap gap-4">
        {charts.map(chart =>
          <Button key={chart} onClick={() =>  {setChart(chart)}}>{chart}</Button>
        )}
        </div>
      <Button onClick={()=>localStorage.clear()} className="flex gap-2"><RotateCcw size={16}/>Refresh cache</Button>
      </div>

      {chart == "Burndown" ? <Burndown slug={slug} sprints={sprints}/> : <div className=" hidden"></div>}
      {chart == "BurndownMulti" ? <BurndownMulti slug={slug} sprints={sprints}/> : <div className=" hidden"></div>}
      {chart == "Lead time" ? <LeadTime slug={slug} sprints={sprints} /> : <div className=" hidden"></div>}
      {chart == "Cycle time" ? <CycleTime slug={slug} sprints={sprints} /> : <div className=" hidden"></div>}
      {chart == "Focus Factor" ? <FocusFactor slug={slug} sprints={sprints} /> : <div className=" hidden"></div>}
      {chart == "Velocity" ? <VelocityGraph slug={slug} sprints={sprints} /> : <div className=" hidden"></div>}
      {chart == "Estimate Effectiveness" ? <EstimateEffectiveness slug={slug} sprints={sprints}/> : <div className=" hidden"></div>} 
      {chart == "Work Capacity" ? <WorkCapacityGraph slug={slug} /> : <div></div>}
      {chart == "LeadTime Arbitary" ? <LeadTimeArbitaryGraph slug={slug} sprints={sprints}/> : <div className=" hidden"></div>}
      {chart == "Arbitary Cycle Time" ? (<ArbitaryCycleTimeGraph slug={slug} /> ) : ( <div className=" hidden"></div>)}

      
    </div>
  );
}
       

export default Dashboard;

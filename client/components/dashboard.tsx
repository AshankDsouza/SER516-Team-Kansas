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
import ValueInProgress from "./valueInProgress";
import LeadTimeArbitaryGraph from "./leadTimeArbitary"
import BurndownMulti from "./burndownMulti"
import AUCGraph from "./aucGraph";

import BDConsistency from "./bdConsistency";

import ValueAUC from "./valueAUC";
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectLabel,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"



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
  const [chart, setChart] = useState("Estimate Effectiveness");
  const charts = [
    "Burndown",
    "BurndownMulti",
    "Lead time",
    "Cycle time",
    "Velocity",
    "Focus Factor",
    "Work Capacity",
    "AUC",
    "Estimate Effectiveness",
    "Arbitary Cycle Time",
    "LeadTime Arbitary",
    "Value In Progress",
    "BD Consistency",
    "Value AUC"

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
        <div className="flex-wrap gap-6">
          <Select onValueChange={(e) => setChart(e)}>
            <SelectTrigger className="w-[180px]">
              <SelectValue placeholder="Chart" />
            </SelectTrigger>
            <SelectContent>
              <SelectGroup>
                <SelectLabel>Charts</SelectLabel>
                {charts && charts.map(chart =>
                  <>
                    <SelectItem key={chart} value={chart}>{chart}</SelectItem>

                  </>

                )}
              </SelectGroup>
            </SelectContent>
          </Select>
        </div>
        <Button onClick={() => localStorage.clear()} className="flex gap-2"><RotateCcw size={16} />Refresh cache</Button>
      </div>

      {chart == "Burndown" ? <Burndown slug={slug} sprints={sprints} /> : <div key={"1"} className=" hidden"></div>}
      {chart == "BurndownMulti" ? <BurndownMulti slug={slug} sprints={sprints} /> : <div key={"2"} className=" hidden"></div>}
      {chart == "Lead time" ? <LeadTime slug={slug} sprints={sprints} /> : <div key={"3"} className=" hidden"></div>}
      {chart == "Cycle time" ? <CycleTime slug={slug} sprints={sprints} /> : <div key={"4"} className=" hidden"></div>}
      {chart == "Focus Factor" ? <FocusFactor slug={slug} sprints={sprints} /> : <div key={"5"} className=" hidden"></div>}
      {chart == "Velocity" ? <VelocityGraph slug={slug} sprints={sprints} /> : <div key={"6"} className=" hidden"></div>}
      {chart == "Estimate Effectiveness" ? <EstimateEffectiveness slug={slug} sprints={sprints} /> : <div key={"7"} className=" hidden"></div>}
      {chart == "Work Capacity" ? <WorkCapacityGraph slug={slug} /> : <div key={"8"} className="hidden"></div>}
      {chart == "LeadTime Arbitary" ? <LeadTimeArbitaryGraph slug={slug} sprints={sprints}/> : <div key={"9"} className=" hidden"></div>}
      {chart == "Arbitary Cycle Time" ? (<ArbitaryCycleTimeGraph slug={slug} /> ) : ( <div key={"10"} className=" hidden"></div>)}
      {chart == "Value In Progress" ? (<ValueInProgress slug={slug} sprints={sprints}/> ) : ( <div key={"11"} className=" hidden"></div>)}
      {chart == "BD Consistency" ? (<BDConsistency slug={slug} sprints={sprints}/> ) : ( <div key={"12"} className=" hidden"></div>)}
      {chart == "AUC" ?  <AUCGraph  slug={slug} sprints={sprints} ></AUCGraph> : ( <div key={"13"} className=" hidden"></div>)}
      {chart == "Value AUC" ?  <ValueAUC  slug={slug} sprints={sprints} ></ValueAUC> : ( <div key={"14"} className=" hidden"></div>)}
    </div>
  );
}


export default Dashboard;

"use client";


import { getWorkAUC } from "@/actions/project";
import { useEffect, useState } from "react";
import BarGraph from "./barGraph";
import { useRouter } from "next/navigation";

function workAUC({ slug }: { slug: string }) {
  const router = useRouter();
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [showChart, setShowChart] = useState(false);
  const [labels, setLabels] = useState<string[]>([]);
  const [series, setSeries] = useState<any>([]);

  useEffect(() => {
    const fetchData = async () => {
      console.log(startDate, endDate);
      if (startDate && endDate) {
        const data = await getWorkAUC(slug, startDate, endDate);
        if (!data) {
          router.refresh();
          return;
        }
        const labels = data.map((item: any) => item.taskName);
        const openPoints = data.map((item: any) => item.cycleTime);
        setLabels(labels);
        let series = [
          {
            name: "Work AUC",
            data: openPoints,
          },
        ];
        setSeries(series);
        setShowChart(true);
      }
    };
    fetchData();
  }, [startDate, endDate]);

  return (
    <div className="flex border-2 border-slate-300 rounded-md divide-x-2">
      <div className="filters flex flex-col divide-y-2">
        <div className="p-8 font-bold">Select the dates:</div>
        <div className="p-8 flex flex-col">
          <label htmlFor="startDate">Start Date</label>
          <input
            type="date"
            id="startDate"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
          />
        </div>
        <div className="p-8 flex flex-col">
          <label htmlFor="endDate">End Date</label>
          <input
            type="date"
            id="endDate"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
          />
        </div>
      </div>
      {showChart ? (
        <BarGraph name="Work AUC" labels={labels} series={series} />
      ) : (
        <div className="flex-1 p-16 min-h-50">Loading...</div>
      )}
    </div>
  );
}

export default workAUC;

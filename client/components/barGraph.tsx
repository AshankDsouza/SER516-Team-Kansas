"use client"

import React, { useEffect, useState } from "react";
//import { render } from "react-dom";
import Highcharts from "highcharts";
import HighchartsReact from "highcharts-react-official";
import { getCyleTime } from "@/actions/project";

interface IProps {
  series: any;
  labels: string[];
  name: string;
}

function BarGraph(props: IProps) {

  const { series, labels } = props;

  const options = {
    chart: {
      type: 'column'
    },
    title: {
      text: name,
      align: 'left'
    },
    xAxis: {
      categories: labels,
      crosshair: true,
      accessibility: {
        description: 'Countries',
      }
    },
    yAxis: {
      min: 0,
      title: {
        text: 'Days taken to complete task'
      }
    },
    tooltip: {
      valueSuffix: ' (1000 MT)'
    },
    plotOptions: {
      column: {
        pointPadding: 0.2,
        borderWidth: 0
      }
    },
    accessibility:{
      enabled: false
    },
    series: series
  };


  return (
    <div className="flex-1 p-16 h-fit overflow-scroll">
      <HighchartsReact highcharts={Highcharts} options={options} />
    </div>
  );
};

export default BarGraph;


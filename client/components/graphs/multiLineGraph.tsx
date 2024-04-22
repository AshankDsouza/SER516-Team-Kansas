"use client"

import React from "react";
import Highcharts from "highcharts";
import HighchartsReact from "highcharts-react-official";

interface IProps {
  series: any;
  labels: string[];
  name: string;
}

export default function MultiLineGraph(props: IProps) {

  const { series, labels, name } = props;

  const options = {
    xAxis: {
      categories: labels,

    },
    chart: {
      type: "spline"
    },
    title: {
      text: name
    },
    series: []
  };

  options.series = series;

  return (<div  className="flex-1 p-16 h-fit overflow-scroll">
    <HighchartsReact highcharts={Highcharts} options={options} />
  </div>
  );
};


"use client"

import React from "react";
//import { render } from "react-dom";
import Highcharts from "highcharts";
import HighchartsReact from "highcharts-react-official";

interface IProps {
  series: any;
  labels: string[];
  name: string;
}

export default function LineGraph(props: IProps) {

  const { series, labels, name } = props;

  const options = {
    xAxis: {
      type: 'datetime', // specify datetime type for x-axis
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

  return (
  <div className="flex-1 p-16 h-fit overflow-scroll">
    <HighchartsReact highcharts={Highcharts} options={options} />
  </div>
  );
};


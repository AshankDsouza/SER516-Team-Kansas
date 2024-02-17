"use client"

import React from "react";
//import { render } from "react-dom";
import Highcharts from "highcharts";
import HighchartsReact from "highcharts-react-official";

interface IProps{
    series: any;
    name: string;
}

export default function LineGraph(props: IProps) {

    const { series, name } = props;;

  const options = {
        xAxis: {
      type: 'datetime', // specify datetime type for x-axis
    },
    chart: {
      type: "spline"
    },
    title: {
      text: name
    },
    series: [
    ]
  };

    options.series = series;

  return (<div>
    <HighchartsReact highcharts={Highcharts} options={options} />
  </div>
  );
};


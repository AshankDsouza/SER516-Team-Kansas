"use client"

import React from "react";
//import { render } from "react-dom";
import Highcharts from "highcharts";
import HighchartsReact from "highcharts-react-official";

interface IProps{
    series: any;
    labels: string[];
}

export default function LineGraph(props: IProps) {

    const { series, labels } = props;

  const options = {
        xAxis: {
      type: 'datetime', // specify datetime type for x-axis
      categories: labels,

    },
    chart: {
      type: "spline"
    },
    title: {
      text: "My chart"
    },
    series:  [{
      name: 'Installation & Developers',
      data: [43934, 48656, 65165, 81827, 112143, 142383,
          171533, 165174, 155157, 161454, 154610]
  }]
  };

    options.series = series;

  return (<div>
    <HighchartsReact highcharts={Highcharts} options={options} />
  </div>
  );
};


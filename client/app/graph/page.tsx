"use client"

import React from "react";
//import { render } from "react-dom";
import Highcharts from "highcharts";
import HighchartsReact from "highcharts-react-official";




export default function LineGraph() {

  const options = {
        xAxis: {
      type: 'datetime', // specify datetime type for x-axis
    },
    chart: {
      type: "spline"
    },
    title: {
      text: "My chart"
    },
    series: [
      {
        data: [[1,1], [2, 2], [1, 3], [4, 2], [3, 6], [6, 3]]
      }
      // {
      //   data: [[1,1], [2, 2], [1, 3], [4, 2], [3, 6], [6, 3]]
  
      // }
    ]
  };

  const data = [
    {
      "day": "2024-01-29",
      "name": 29,
      "open_points": 25.0,
      "optimal_points": 25.0
    },
    {
      "day": "2024-01-30",
      "name": 30,
      "open_points": 25.0,
      "optimal_points": 23.75
    },
    {
      "day": "2024-01-31",
      "name": 31,
      "open_points": 25.0,
      "optimal_points": 22.5
    },
    {
      "day": "2024-02-01",
      "name": 1,
      "open_points": 25.0,
      "optimal_points": 21.25
    },
    {
      "day": "2024-02-02",
      "name": 2,
      "open_points": 25.0,
      "optimal_points": 20.0
    }];

    const getOpenPointsData = (dataPoints:any) =>{
      let points: any[][] = [];

      data.forEach(function(dataPoint: any){

        let point = [new Date(dataPoint.day).getTime(), dataPoint.open_points]
        points.push(point);

      });

      return points;
    }

    let open_points = getOpenPointsData(data);

    options.series[0].data = open_points;

  return (<div>
    <HighchartsReact highcharts={Highcharts} options={options} />
  </div>
  );
};


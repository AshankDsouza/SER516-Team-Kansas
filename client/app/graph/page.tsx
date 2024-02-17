// "use client"

// import React, { useEffect, useState } from "react";
// import LineGraph from '../../components/graphs/lineGraph';
// import axios from 'axios';



export default function GraphPage() {

//   const [graphData, setGraphData] = useState<any[]>([]);


//   const data = [
//     {
//       "day": "2024-01-29",
//       "name": 29,
//       "open_points": 25.0,
//       "optimal_points": 25.0
//     },
//     {
//       "day": "2024-01-30",
//       "name": 30,
//       "open_points": 25.0,
//       "optimal_points": 23.75
//     },
//     {
//       "day": "2024-01-31",
//       "name": 31,
//       "open_points": 25.0,
//       "optimal_points": 22.5
//     },
//     {
//       "day": "2024-02-01",
//       "name": 1,
//       "open_points": 25.0,
//       "optimal_points": 21.25
//     },
//     {
//       "day": "2024-02-02",
//       "name": 2,
//       "open_points": 25.0,
//       "optimal_points": 20.0
//     }];

//     useEffect(() => {

//       fetchData();

      
//     }, []);

    const fetchData = async () =>{
      try {
        //const data1 = await axios.get("http://localhost:8080/api/tasks/closedByDate?project=1521719&sprint=376621");

        //console.log({data1});
        
        
        setGraphData(data);
      } catch (error) {
        console.error(error);
      }
    }

//     const getOpenPointsData = (dataPoints:any, pointType: string) =>{
//       let points: any[][] = [];

//       dataPoints.forEach(function(dataPoint: any){

//         let point = [new Date(dataPoint.day).getTime(), dataPoint[pointType]]
//         points.push(point);

//       });

//       return points;
//     }

//     let open_points = getOpenPointsData(graphData, "open_points");
//     let opyimals_points = getOpenPointsData(graphData, "optimal_points");


//     let series:any = [];
//     series.push( { "data": open_points } );
//     series.push( { "data": opyimals_points } )


  return (<div>
    <LineGraph series={series} name="Burndown chart" />
  </div>
  );
};


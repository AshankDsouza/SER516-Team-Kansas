"use server"

import { getRequestOptions } from "@/utils/auth";
import { z } from "zod";

export async function getProjectId(projectSlug: string) {
    const ID = z.object({
        id: z.number()
    })
    const response = await fetch(`https://api.taiga.io/api/v1/projects/by_slug?slug=${projectSlug}`, getRequestOptions())
    const projectId = await response.json()
    try {
        ID.parse(projectId)
    } catch (error) {
        return null
    }
    return projectId.id
}

export async function getProjectMilestones(projectSlug: string) {
    const Response = z.array(z.object({
        id: z.number()
    }))
    const response = await fetch(`${process.env.API_URL}/api/getAllSprints?project=${projectSlug}`, getRequestOptions())
    let sprintIDs = await response.json()
    sprintIDs = Object.keys(sprintIDs).map(key => ({ id: sprintIDs[key], value: key }));
    try {
        Response.parse(sprintIDs)
    } catch (error) {
        return null
    }
    return sprintIDs
}

export async function getCyleTime(projectSlug: string, milestoneId: string) {
    const Response = z.array(z.object({
        cycleTime: z.number()
    }))
    const url = `${process.env.API_URL}/api/${projectSlug}/${milestoneId}/getCycleTime`;
    const response = await fetch(url, getRequestOptions())
    let cycleTimeData = await response.json()
    try {
        Response.parse(cycleTimeData)
    } catch (error) {
        return null
    }
    return cycleTimeData;
}

export async function getLeadTime(projectSlug: string, sprintId: string) {
    const LeadTime = z.array(z.object({
        finish_date: z.string()
    }))
    const url = `${process.env.API_URL}/api/getDataForLeadTime?projectSlug=${projectSlug}&sprintId=${sprintId}`;
    const response = await fetch(url, getRequestOptions())
    let leadTimeData = await response.json()
    try {
        LeadTime.parse(leadTimeData)
    } catch (error) {
        return null
    }
    return leadTimeData;
}
export async function getBurndowMetrics(milestoneId: string) {
    const Response = z.array(z.object({
        open_points: z.number(),
        optimal_points: z.number()
    }))
    const response = await fetch(`${process.env.API_URL}/api/${milestoneId}/getBurnDownChart`, getRequestOptions())
    let BurndownData = await response.json()
    try {
        Response.parse(BurndownData)
    } catch (error) {
        return null
    }
    return BurndownData
}

export async function getBurndowMetricsMulti(projectSlug: string) {
    const response = await fetch(`${process.env.API_URL}/api/${projectSlug}/multiSprintBundown`, getRequestOptions())
    let BurndownData = await response.json()

    return BurndownData
}


export async function getFocusFactor(milestoneId: string) {
    const Response = z.array(z.object({
        open_points: z.number()
    }))
    const response1: any = await fetch(`${process.env.API_URL}/api/${milestoneId}/getTotalPoints`, getRequestOptions())
    const response2: any = await fetch(`${process.env.API_URL}/api/${milestoneId}/getCompletedPoints`, getRequestOptions())
    const tP = await response1.json()
    const cP = await response2.json()
    let lab = [];
    let ser = [];
    for (let i = 0; i < tP.length; i++) {
        lab.push(tP[i].sprintName)
        let focusFactor = cP[i].completedPoints == 0 ? 0 : tP[i].totalPoints * 100 / cP[i].completedPoints;
        ser.push({ focusFactor: focusFactor, label: tP[i].sprintName })
    }
    console.log(ser)
    return ser;
}

export async function getVelocity(projectSlug: string) {
    const Response = z.array(z.object({
        sprintName: z.string(),
        totalPoints: z.number()
    }))


    const url = `${process.env.API_URL}/api/${projectSlug}/getTotalPoints`;

    const response = await fetch(url, getRequestOptions())
    const data = await response.json();

    try {
        Response.parse(data)
    } catch (error) {
        return null
    }

    return data;
}

export async function getWorkCapacity(projectSlug: string) {
    const Response = z.array(z.object({
        sprintName: z.string(),
        completedPoints: z.number()
    }))


    const url = `${process.env.API_URL}/api/${projectSlug}/getCompletedPoints`;

    const response = await fetch(url, getRequestOptions())
    const data = await response.json();

    try {
        Response.parse(data)
    } catch (error) {
        return null
    }

    return data;
}


export async function getLeadTimeArbitrary(projectSlug: string, startDate: string, endDate: string) {
    const Response = z.array(z.object({
        finish_date: z.string()
    }));
    const url = `${process.env.API_URL}/api/getLeadTimeForAbitraryTimeframe?projectSlug=${projectSlug}&startDate=${startDate}&endDate=${endDate}`;

    console.log("This code is working");
    console.log(projectSlug);
    const response = await fetch(url, getRequestOptions());
    // console.log(response);

    let leadTimeArbitraryData = await response.json();
    console.log(leadTimeArbitraryData);
    try {
        Response.parse(leadTimeArbitraryData);
    } catch (error) {
        return null;
    }

    return leadTimeArbitraryData;
}
export async function getEstimateEffectiveness(milestoneId: string) {
    const Response = z.array(z.object({
        taskId: z.string(),
        estimateAccuracy: z.number()
    }))
    var data = []
    try {
        const url = `${process.env.API_URL}/api/${milestoneId}/getEstimateEffectiveness`;// need to change the url
        const response = await fetch(url, getRequestOptions())
        data = await response.json();
        Response.parse(data)
    } catch (error) {
        console.log(error)
        return null
    }
    return data;
}


export async function getArbitraryCycleTime(projectSlug: string, startDate: string, endDate: string) {
    const Response = z.array(z.object({
        taskName: z.string(),
        cycleTime: z.number()
    }));
    const url = `${process.env.API_URL}/api/${projectSlug}/getArbitraryCycleTime?startDate=${startDate}&endDate=${endDate}`;
    const requestBody = {
        startDate,
        endDate
    };
    const response = await fetch(url, getRequestOptions());
    let ArbitraryCycleTime = await response.json();
    console.log(ArbitraryCycleTime);
    try {
        Response.parse(ArbitraryCycleTime);
    } catch (error) {
        return null;
    }
    return ArbitraryCycleTime;

}

export async function getValueInProgress(projectSlug: string, milestoneId: string) {
    const Response = z.array(z.object({
            date: z.string(),
            user_story_points: z.number(),
            BV: z.number()
        }))
        const response = await fetch(`${process.env.API_URL}/api/${projectSlug}/${milestoneId}/vipData`, getRequestOptions())
//         let ValueInProgressData = [{'date': '29 Jan 2024', 'user_story_points': 0.0, 'BV': 0.0}, {'date': '30 Jan 2024', 'user_story_points': 0.0, 'BV': 0.0}, {'date': '31 Jan 2024', 'user_story_points': 0.0, 'BV': 0.0}, {'date': '01 Feb 2024', 'user_story_points': 0.0, 'BV': 0.0}, {'date': '02 Feb 2024', 'user_story_points': 0.0, 'BV': 0.0}, {'date': '03 Feb 2024', 'user_story_points': 0.19117647058823528, 'BV': 0.1111111111111111}, {'date': '04 Feb 2024', 'user_story_points': 0.19117647058823528, 'BV': 0.1111111111111111}, {'date': '05 Feb 2024', 'user_story_points': 0.19117647058823528, 'BV': 0.1111111111111111}, {'date': '06 Feb 2024', 'user_story_points': 0.19117647058823528, 'BV': 0.1111111111111111}, {'date': '07 Feb 2024', 'user_story_points': 0.19117647058823528, 'BV': 0.1111111111111111}, {'date': '08 Feb 2024', 'user_story_points': 0.19117647058823528, 'BV': 0.1111111111111111}, {'date': '09 Feb 2024', 'user_story_points': 0.19117647058823528, 'BV': 0.1111111111111111}, {'date': '10 Feb 2024', 'user_story_points': 0.39705882352941174, 'BV': 0.3333333333333333}, {'date': '11 Feb 2024', 'user_story_points': 0.39705882352941174, 'BV': 0.3333333333333333}, {'date': '12 Feb 2024', 'user_story_points': 0.39705882352941174, 'BV': 0.3333333333333333}, {'date': '13 Feb 2024', 'user_story_points': 0.5147058823529411, 'BV': 0.4444444444444444}, {'date': '14 Feb 2024', 'user_story_points': 0.5882352941176471, 'BV': 0.5555555555555556}, {'date': '15 Feb 2024', 'user_story_points': 0.5882352941176471, 'BV': 0.5555555555555556}];
        let ValueInProgressData = await response.json()
        try {
            Response.parse(ValueInProgressData)
        } catch (error) {
            return null
        }
        return ValueInProgressData

}

export async function getValueAUC(projectSlug: string, milestoneId: string) {
    const Response = z.array(z.object({
            sprint: z.string(),
            value: z.number()
        }))
       // const response = await fetch(`${process.env.API_URL}/api/${projectSlug}/${milestoneId}/vipData`, getRequestOptions())
       let ValueAUCData = [{'sprint' : 'Sprint 1', 'value' : 894.9999999999999 }, { 'sprint': 'Sprint 2', 'value' : 496 },{'sprint': 'Sprint 3',  'value' : 0 },{'sprint': 'Sprint 4', 'value': 0}, { 'sprint': 'Sprint 5', 'value' : 0}];
      //  let ValueAUCData = await response.json()
        try {
            Response.parse(ValueAUCData)
        } catch (error) {
            return null
        }
        return ValueAUCData

}
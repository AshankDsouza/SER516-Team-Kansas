"use server"

import { cookies } from "next/headers";
import { getRequestOptions } from "@/utils/auth";

export async function getProjectId(projectSlug: string) {
    const response = await fetch(`https://api.taiga.io/api/v1/projects/by_slug?slug=${projectSlug}`, getRequestOptions())
    const projectId = await response.json()
    return projectId.id
}

export async function getProjectMilestones(projectId: string) {
const response = await fetch(`http://localhost:8080/api/getAllSprints?project=${projectId}`, getRequestOptions())
let sprintIDs = await response.json()
sprintIDs = Object.keys(sprintIDs).map(key => ({ id: sprintIDs[key], value: key }));
return sprintIDs
}

export async function getCyleTime(projectSlug: string, milestoneId: string) {
    var myHeaders = new Headers();
    const auth_token = cookies().get("auth_token")
    myHeaders.append("Authorization", `Bearer ${auth_token?.value}`);

    var requestOptions = {
        method: 'GET',
        headers: myHeaders
    };

    const url = `http://localhost:8080/api/${projectSlug}/${milestoneId}/getCycleTime`;
    console.log({url: url});

    

    const response = await fetch(url, requestOptions)
    let cycleTimeData = await response.json()
    console.log({cycleTimeData});
    return cycleTimeData;   
}

export async function getBurndowMetrics(milestoneId: string) {
    const response = await fetch(`http://localhost:8080/api/${milestoneId}/getBurnDownChart`, getRequestOptions())
    let BurndownData = await response.json()
    return BurndownData
}
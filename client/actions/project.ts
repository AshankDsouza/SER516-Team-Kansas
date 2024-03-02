"use server"

import { getRequestOptions } from "@/utils/auth";
import { z } from "zod";

export async function getProjectId(projectSlug: string) {
    const ID = z.object({
        id:z.number()
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
        open_points: z.number()
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

export async function getVelocity(projectSlug: string) {
    const Response = z.array(z.object({
        sprintName: z.string(),
        totalPoints: z.number()
    }))
    

    const url = `http://localhost:8080/api/${projectSlug}/getTotalPoints`;

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
    

    const url = `http://localhost:8080/api/1521719/getCompletedPoints`;

    const response = await fetch(url, getRequestOptions())
    const data = await response.json();

    try {
        Response.parse(data)
    } catch (error) {
        return null
    }

    return data;   
}

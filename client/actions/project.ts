"use server"

import { cookies } from "next/headers";
import { getRequestOptions } from "./auth";

export async function getProjectId(projectSlug: string) {
    const response = await fetch(`https://api.taiga.io/api/v1/projects/by_slug?slug=${projectSlug}`, await getRequestOptions())
    const projectId = await response.json()
    return projectId.id
}

export async function getProjectMilestones(projectId: string) {
    const response = await fetch("http://localhost:8080/api/milestones/getAllSprints?project=1521719", await getRequestOptions())
    let sprintIDs = await response.json()
    sprintIDs = Object.keys(sprintIDs).map(key => ({ id: sprintIDs[key], value: key }));
    return sprintIDs
}

export async function getBurndowMetrics(milestoneId: string) {
    const response = await fetch(`http://localhost:8080/api/milestones/${milestoneId}/getTotalStoryPoints`, await getRequestOptions())
    let BurndownData = await response.json()
    return BurndownData
}
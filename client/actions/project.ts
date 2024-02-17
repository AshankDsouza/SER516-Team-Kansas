"use server"

import { cookies } from "next/headers";

export async function getProjectId(projectSlug: string) {
    var myHeaders = new Headers();
    const auth_token = cookies().get("auth_token")

    myHeaders.append("Authorization", `Bearer ${auth_token?.value}`);

    var requestOptions = {
        method: 'GET',
        headers: myHeaders
    };

    const response = await fetch(`https://api.taiga.io/api/v1/projects/by_slug?slug=${projectSlug}`, requestOptions)
    const projectId = await response.json()
    return projectId.id
}

export async function getProjectMilestones(projectId: string) {
    var myHeaders = new Headers();
    const auth_token = cookies().get("auth_token")
    myHeaders.append("Authorization", `Bearer ${auth_token?.value}`);

    var requestOptions = {
        method: 'GET',
        headers: myHeaders
    };

    const response = await fetch("http://localhost:8080/api/milestones/getAllSprints?project=1521719", requestOptions)
    let sprintIDs = await response.json()
    sprintIDs = Object.keys(sprintIDs).map(key => ({ id: sprintIDs[key], value: key }));
    console.log(sprintIDs);
    return sprintIDs   
}
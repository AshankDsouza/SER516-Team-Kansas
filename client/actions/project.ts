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
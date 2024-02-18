"use server"

import { cookies } from "next/headers";
import { redirect } from "next/navigation";
import { z } from "zod";

export default async function getUserToken(userName: string, password: string): Promise<string> {

    var myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");

    var raw = JSON.stringify({
        "username": userName,
        "password": password
    });

    const response = await fetch("http://localhost:8080/api/auth", {
        method: 'POST',
        headers: myHeaders,
        body: raw,
    });

    const res = z.object({
        token: z.string()
    });

    try {
        const tokenObj = await response.json();
        res.parse(tokenObj)
        const token = tokenObj.token;
        cookies().set('auth_token', token, {expires: Date.now()+300000});
        return "token";

    } catch (error) {
        return "wrong creds";
    }
}

export async function deleteAuthToken() {
    cookies().delete("auth_token")
    redirect("/")
}
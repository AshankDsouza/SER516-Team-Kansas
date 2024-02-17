"use server"

import { revalidatePath } from "next/cache";
import { cookies } from "next/headers";
import { redirect } from "next/navigation";

export default async function getUserToken(userName: string, password: string) {

    var myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");

    var raw = JSON.stringify({
        "username": userName,
        "password": password
    });



    try {
        const response = await fetch("http://localhost:8080/api/auth", {
            method: 'POST',
            headers: myHeaders,
            body: raw,
        });
        const tokenObj = await response.json();
        const token = tokenObj.token;
        if(token){
            cookies().set('auth_token', token);
        }
        return token;

    } catch (error) {
        console.error("fetch call failed: ", error);
    }
}

export async function deleteAuthToken() {
    cookies().delete("auth_token")
    redirect("/")
}
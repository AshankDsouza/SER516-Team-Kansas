"use server"

import Error from "next/error";
import { redirect } from "next/navigation";

export default async function getUserToken(userName: string, password: string) {

    let response

    try {
         response = await fetch("localhost:8080/test", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ username: { userName }, password: { password } }),
        });
    } catch (error){
        console.error(error);
    }
    // redirect("/")
    return response
}
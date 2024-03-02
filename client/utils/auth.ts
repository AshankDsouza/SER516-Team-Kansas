import { cookies } from "next/headers";

export function getRequestOptions() {
    var myHeaders = new Headers();
    const auth_token = cookies().get("auth_token");

    myHeaders.append("Authorization", `Bearer ${auth_token?.value}`);

    var requestOptions = {
        method: 'GET',
        headers: myHeaders,
    };
    return requestOptions;
}
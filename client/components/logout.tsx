"use client"

import { deleteAuthToken } from "@/actions/userToken"
import { Button } from "./ui/button"

function Logout() {
  return (
    <Button onClick={()=>deleteAuthToken()}>Logout</Button>
  )
}

export default Logout
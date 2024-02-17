"use client"
import { Button } from "./ui/button"
import { Input } from "./ui/input"
import { useState } from "react"
import { useRouter } from "next/navigation"
import { getProjectId } from "@/actions/project"

function Project() {

    const router = useRouter()
    const [project, setProject] = useState("")
    const [wrongSlug, setWrongSlug] = useState(false)

    async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
        e.preventDefault()
        const id = await getProjectId(project);
        if (id) {
            router.push(`/project/${project}`)
        } else {
            setWrongSlug(true)
        }
    }

    return (
        <div className="p-8 flex flex-col gap-4">
            {wrongSlug && <div><p className=" text-xs p-2 bg-red-300 rounded-sm text-red-800 border-red-800 border-2">There is no project with the slug {project}</p></div>}
            <form onSubmit={handleSubmit} className="flex flex-col gap-4 w-full">
                <Input placeholder="project-slug" name="slug" onChange={(e) => { setProject(e.target.value); setWrongSlug(false) }} />
                <Button type="submit">submit</Button>
            </form>
        </div>
    )
}

export default Project
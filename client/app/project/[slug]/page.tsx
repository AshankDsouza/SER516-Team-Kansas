import Dashboard from "@/components/dashboard"
import NavBar from "@/components/navbar"

function page({ params }: { params: { slug: string } }) {
  return (
    <div>
        <NavBar/>
        <div className="p-8 flex flex-col">
        <Dashboard slug={params.slug}/>
        </div>
    </div>
  )
}

export default page
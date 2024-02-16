import Project from "@/components/project";
import NavBar from "@/components/navbar";
import { Button } from "@/components/ui/button";
import Image from "next/image";
import Link from "next/link";

export default function Home() {
  return (
    <div className="flex flex-col h-screen">
      <NavBar />
      <div className="p-8 flex-1 flex flex-col justify-center">
        <Project />
      </div>
    </div>
  );
}

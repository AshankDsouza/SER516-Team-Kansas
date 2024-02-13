import { Button } from "@/components/ui/button";
import Image from "next/image";
import Link from "next/link";

export default function Home() {
  return (
    <div className="p-8 flex justify-between">
      <p className="flex">Team Kansas</p>
      <Link href={"/login"}><Button>Login</Button></Link>
    </div>
  );
}

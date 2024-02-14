import Link from 'next/link'
import React from 'react'
import { Button } from './ui/button'

function NavBar() {
  return (
    <div className='p-8 flex justify-between'>
        <div className=' font-medium'>Team Kansas</div>
        <Link href={"/"}><Button>Login</Button></Link>
    </div>
  )
}

export default NavBar
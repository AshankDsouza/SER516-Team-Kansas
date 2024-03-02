import Link from 'next/link'
import React from 'react'
import { Button } from './ui/button'
import { cookies } from 'next/headers'
import Logout from './logout'

function NavBar() {
  return (
    <div className='p-8 flex justify-between items-center'>
      <div className=' font-medium'>Team Kansas</div>
      {
        cookies().has("auth_token") ? <Logout/> : <Link href={"/"}><Button>Login</Button></Link>
      }
    </div>
  )
}

export default NavBar
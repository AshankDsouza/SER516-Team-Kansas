import { cookies } from 'next/headers'
import { NextResponse, NextRequest } from 'next/server'

export function middleware(request: NextRequest) {
    const auth_token = cookies().has("auth_token")

    if(!auth_token)
    return NextResponse.redirect(new URL('/', request.url))
}

export const config = {
    matcher: '/project/:path*',
}
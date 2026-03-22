// SPDX-License-Identifier: AGPL-3.0
// Copyright (C) 2026 devtank42 GmbH
import { Outlet, useLocation } from 'react-router-dom'
import { PageWrapper } from './components/layout/PageWrapper'
import { TopBar } from './components/layout/TopBar'
import { Footer } from './components/layout/Footer'
import { ToastRegion } from './components/common/Toast'

const AUTH_PATHS = ['/login', '/register', '/forgot-password', '/reset-password']

export function AppShell() {
  const { pathname } = useLocation()
  const isAuthPage = AUTH_PATHS.includes(pathname)

  return (
    <PageWrapper>
      <TopBar showSearch={!isAuthPage} />
      <Outlet />
      <Footer />
      <ToastRegion />
    </PageWrapper>
  )
}

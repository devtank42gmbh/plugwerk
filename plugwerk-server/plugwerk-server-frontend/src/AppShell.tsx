// SPDX-License-Identifier: AGPL-3.0
// Copyright (C) 2026 devtank42 GmbH
import { Outlet } from 'react-router-dom'
import { PageWrapper } from './components/layout/PageWrapper'
import { TopBar } from './components/layout/TopBar'
import { Footer } from './components/layout/Footer'
import { ToastRegion } from './components/common/Toast'

export function AppShell() {
  return (
    <PageWrapper>
      <TopBar />
      <Outlet />
      <Footer />
      <ToastRegion />
    </PageWrapper>
  )
}

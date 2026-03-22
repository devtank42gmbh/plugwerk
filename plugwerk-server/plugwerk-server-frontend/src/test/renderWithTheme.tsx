// SPDX-License-Identifier: AGPL-3.0
// Copyright (C) 2026 devtank42 GmbH
import { render, type RenderOptions } from '@testing-library/react'
import { ThemeProvider, CssBaseline } from '@mui/material'
import { MemoryRouter } from 'react-router-dom'
import { buildTheme } from '../theme/theme'
import type { ReactNode } from 'react'

const theme = buildTheme('light')

function ThemeWrapper({ children }: { children: ReactNode }) {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      {children}
    </ThemeProvider>
  )
}

function RouterWrapper({ children }: { children: ReactNode }) {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <MemoryRouter>{children}</MemoryRouter>
    </ThemeProvider>
  )
}

export function renderWithTheme(ui: ReactNode, options?: Omit<RenderOptions, 'wrapper'>) {
  return render(ui, { wrapper: ThemeWrapper, ...options })
}

export function renderWithRouter(ui: ReactNode, options?: Omit<RenderOptions, 'wrapper'>) {
  return render(ui, { wrapper: RouterWrapper, ...options })
}

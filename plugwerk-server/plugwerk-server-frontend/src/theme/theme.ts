// SPDX-License-Identifier: AGPL-3.0
// Copyright (C) 2026 devtank42 GmbH
import { createTheme, type PaletteMode } from '@mui/material'
import { tokens } from './tokens'

export function buildTheme(mode: PaletteMode) {
  const isDark = mode === 'dark'

  return createTheme({
    palette: {
      mode,
      primary: {
        main: tokens.color.primary,
        dark: tokens.color.primaryDark,
        light: tokens.color.primaryLight,
        contrastText: tokens.color.white,
      },
      secondary: {
        main: tokens.color.secondary,
      },
      success:  { main: tokens.color.success },
      warning:  { main: tokens.color.warning },
      error:    { main: tokens.color.danger },
      background: {
        default: isDark ? '#161616' : tokens.color.gray10,
        paper:   isDark ? '#262626' : tokens.color.white,
      },
      text: {
        primary:   isDark ? '#F4F4F4' : tokens.color.gray100,
        secondary: isDark ? '#C6C6C6' : tokens.color.gray80,
        disabled:  isDark ? '#6F6F6F' : tokens.color.gray40,
      },
      divider: isDark ? '#393939' : tokens.color.gray20,
    },

    typography: {
      fontFamily: 'Inter, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif',
      h1: { fontWeight: 700, fontSize: '2.25rem', lineHeight: '2.75rem' },
      h2: { fontWeight: 600, fontSize: '1.75rem', lineHeight: '2.25rem' },
      h3: { fontWeight: 600, fontSize: '1.25rem', lineHeight: '1.75rem' },
      h4: { fontWeight: 600, fontSize: '1.0625rem', lineHeight: '1.5rem' },
      body1: { fontSize: '0.875rem', lineHeight: '1.5rem' },
      body2: { fontSize: '0.8125rem', lineHeight: '1.25rem' },
      caption: { fontSize: '0.75rem', lineHeight: '1.125rem' },
    },

    shape: {
      borderRadius: 4,
    },

    components: {
      MuiCssBaseline: {
        styleOverrides: {
          '*, *::before, *::after': { boxSizing: 'border-box' },
          body: { minHeight: '100dvh' },
          a: { color: 'inherit', textDecoration: 'none' },
          'ul, ol': { listStyle: 'none', margin: 0, padding: 0 },
        },
      },
      MuiButton: {
        styleOverrides: {
          root: {
            textTransform: 'none',
            fontWeight: 500,
            borderRadius: tokens.radius.btn,
          },
        },
      },
      MuiChip: {
        styleOverrides: {
          root: { borderRadius: tokens.radius.btn, fontWeight: 500 },
        },
      },
      MuiCard: {
        styleOverrides: {
          root: {
            borderRadius: tokens.radius.card,
            border: `1px solid ${isDark ? '#393939' : tokens.color.gray20}`,
            boxShadow: 'none',
          },
        },
      },
      MuiInputBase: {
        styleOverrides: {
          root: { borderRadius: `${tokens.radius.input} !important` },
        },
      },
      MuiOutlinedInput: {
        styleOverrides: {
          root: { borderRadius: tokens.radius.input },
        },
      },
      MuiAppBar: {
        styleOverrides: {
          root: {
            boxShadow: 'none',
            borderBottom: `1px solid ${isDark ? '#393939' : tokens.color.gray20}`,
            backgroundColor: isDark ? '#262626' : tokens.color.white,
            color: isDark ? '#F4F4F4' : tokens.color.gray100,
          },
        },
      },
    },
  })
}

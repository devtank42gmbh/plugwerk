// SPDX-License-Identifier: AGPL-3.0
// Copyright (C) 2026 devtank42 GmbH
import { ApiReferenceReact } from '@scalar/api-reference-react'
import '@scalar/api-reference-react/style.css'
import { useTheme } from '@mui/material'

export function ApiDocsPage() {
  const theme = useTheme()
  const isDark = theme.palette.mode === 'dark'

  return (
    <ApiReferenceReact
      key={isDark ? 'dark' : 'light'}
      configuration={{
        url: '/api-docs/openapi.yaml',
        darkMode: isDark,
        hideDownloadButton: false,
        layout: 'modern',
      }}
    />
  )
}

// SPDX-License-Identifier: AGPL-3.0
// Copyright (C) 2026 devtank42 GmbH
import { ApiReferenceReact } from '@scalar/api-reference-react'
import '@scalar/api-reference-react/style.css'
import { useMemo } from 'react'
import { useUiStore } from '../stores/uiStore'

export function ApiDocsPage() {
  const themeMode = useUiStore((s) => s.themeMode)
  const isDark = themeMode === 'dark'

  const configuration = useMemo(
    () => ({
      url: '/api-docs/openapi.yaml',
      darkMode: isDark,
      hideDownloadButton: false,
      layout: 'modern' as const,
    }),
    [isDark],
  )

  return (
    <ApiReferenceReact
      key={themeMode}
      configuration={configuration}
    />
  )
}

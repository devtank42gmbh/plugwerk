// SPDX-License-Identifier: AGPL-3.0
// Copyright (C) 2026 devtank42 GmbH
import { ErrorPage } from './ErrorPage'

const Illustration = () => (
  <svg viewBox="0 0 160 160" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true" width="160" height="160">
    <rect x="30" y="30" width="60" height="60" rx="6"/>
    <path d="M55 30 C55 22 65 22 65 30"/>
    <path d="M90 55 C98 55 98 65 90 65"/>
    <path d="M55 90 C55 98 65 98 65 90"/>
    <rect x="68" y="68" width="56" height="56" rx="6" strokeDasharray="4 3"/>
    <circle cx="115" cy="120" r="18"/>
    <line x1="128" y1="133" x2="148" y2="153"/>
  </svg>
)

export function Error404Page() {
  return (
    <ErrorPage
      code={404}
      title="Page not found"
      message="The page you're looking for doesn't exist or may have been moved."
      illustration={<Illustration />}
    />
  )
}

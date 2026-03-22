// SPDX-License-Identifier: AGPL-3.0
// Copyright (C) 2026 devtank42 GmbH
import { ErrorPage } from './ErrorPage'

const Illustration = () => (
  <svg viewBox="0 0 160 160" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true" width="160" height="160">
    <rect x="40" y="75" width="80" height="60" rx="8"/>
    <path d="M55 75 V55 a25 25 0 0 1 50 0 v20"/>
    <circle cx="80" cy="105" r="8"/>
    <line x1="80" y1="113" x2="80" y2="122"/>
  </svg>
)

export function Error403Page() {
  return (
    <ErrorPage
      code={403}
      title="Access denied"
      message="You don't have permission to access this page. Please check your API key or contact an administrator."
      illustration={<Illustration />}
    />
  )
}

// SPDX-License-Identifier: AGPL-3.0
// Copyright (C) 2026 devtank42 GmbH
import { ErrorPage } from './ErrorPage'

const Illustration = () => (
  <svg viewBox="0 0 160 160" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true" width="160" height="160">
    <circle cx="80" cy="80" r="50"/>
    <line x1="80" y1="52" x2="80" y2="90"/>
    <circle cx="80" cy="108" r="4" fill="currentColor" stroke="none"/>
  </svg>
)

export function Error500Page() {
  return (
    <ErrorPage
      code={500}
      title="Internal server error"
      message="Something went wrong on our end. Please try again in a moment."
      illustration={<Illustration />}
    />
  )
}

// SPDX-License-Identifier: AGPL-3.0
// Copyright (C) 2026 devtank42 GmbH
import { describe, it, expect, vi } from 'vitest'
import { screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { renderWithTheme } from '../../test/renderWithTheme'
import { EmptyState } from './EmptyState'

describe('EmptyState', () => {
  it('renders the title', () => {
    renderWithTheme(<EmptyState title="No results found" message="Try adjusting your filters." />)
    expect(screen.getByText('No results found')).toBeInTheDocument()
  })

  it('renders the message', () => {
    renderWithTheme(<EmptyState title="No results found" message="Try adjusting your filters." />)
    expect(screen.getByText('Try adjusting your filters.')).toBeInTheDocument()
  })

  it('does not render action button when actionLabel is not provided', () => {
    renderWithTheme(<EmptyState title="Empty" message="Nothing here." />)
    expect(screen.queryByRole('button')).not.toBeInTheDocument()
  })

  it('does not render action button when onAction is not provided', () => {
    renderWithTheme(<EmptyState title="Empty" message="Nothing here." actionLabel="Retry" />)
    expect(screen.queryByRole('button')).not.toBeInTheDocument()
  })

  it('renders action button when both actionLabel and onAction are provided', () => {
    const onAction = vi.fn()
    renderWithTheme(
      <EmptyState title="Empty" message="Nothing here." actionLabel="Clear filters" onAction={onAction} />,
    )
    expect(screen.getByRole('button', { name: /clear filters/i })).toBeInTheDocument()
  })

  it('calls onAction when the action button is clicked', async () => {
    const user = userEvent.setup()
    const onAction = vi.fn()
    renderWithTheme(
      <EmptyState title="Empty" message="Nothing here." actionLabel="Retry" onAction={onAction} />,
    )
    await user.click(screen.getByRole('button', { name: /retry/i }))
    expect(onAction).toHaveBeenCalledOnce()
  })

  it('renders an SVG illustration', () => {
    renderWithTheme(<EmptyState title="No results" message="Try again." />)
    const svg = document.querySelector('svg[aria-hidden="true"]')
    expect(svg).toBeInTheDocument()
  })
})

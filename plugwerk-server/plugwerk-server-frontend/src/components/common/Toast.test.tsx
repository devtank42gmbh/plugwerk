// SPDX-License-Identifier: AGPL-3.0
// Copyright (C) 2026 devtank42 GmbH
import { describe, it, expect, beforeEach } from 'vitest'
import { screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { act } from 'react'
import { renderWithTheme } from '../../test/renderWithTheme'
import { ToastRegion } from './Toast'
import { useUiStore } from '../../stores/uiStore'


describe('ToastRegion', () => {
  beforeEach(() => {
    useUiStore.setState({ toasts: [] })
  })

  it('renders an accessible notification region', () => {
    renderWithTheme(<ToastRegion />)
    expect(screen.getByRole('region', { name: /notifications/i })).toBeInTheDocument()
  })

  it('renders no toasts when store is empty', () => {
    renderWithTheme(<ToastRegion />)
    expect(screen.queryByRole('status')).not.toBeInTheDocument()
  })

  it('renders a toast when one is added to the store', () => {
    act(() => {
      useUiStore.getState().addToast({ type: 'info', message: 'Hello world' })
    })
    renderWithTheme(<ToastRegion />)
    expect(screen.getByText('Hello world')).toBeInTheDocument()
  })

  it('renders toast with title and message', () => {
    act(() => {
      useUiStore.setState({
        toasts: [{ id: '1', type: 'success', title: 'Upload complete', message: 'Plugin was uploaded.' }],
      })
    })
    renderWithTheme(<ToastRegion />)
    expect(screen.getByText('Upload complete')).toBeInTheDocument()
    expect(screen.getByText('Plugin was uploaded.')).toBeInTheDocument()
  })

  it('renders multiple toasts', () => {
    act(() => {
      useUiStore.setState({
        toasts: [
          { id: '1', type: 'info', message: 'First' },
          { id: '2', type: 'error', message: 'Second' },
        ],
      })
    })
    renderWithTheme(<ToastRegion />)
    expect(screen.getByText('First')).toBeInTheDocument()
    expect(screen.getByText('Second')).toBeInTheDocument()
  })

  it('dismisses a toast when the close button is clicked', async () => {
    const user = userEvent.setup()
    act(() => {
      useUiStore.setState({
        toasts: [{ id: 'toast-1', type: 'warning', message: 'Watch out' }],
      })
    })
    renderWithTheme(<ToastRegion />)
    expect(screen.getByText('Watch out')).toBeInTheDocument()

    await user.click(screen.getByRole('button', { name: /dismiss notification/i }))
    expect(useUiStore.getState().toasts).toHaveLength(0)
  })

  it('renders toast with role="status" for accessibility', () => {
    act(() => {
      useUiStore.setState({
        toasts: [{ id: '1', type: 'error', message: 'Error occurred' }],
      })
    })
    renderWithTheme(<ToastRegion />)
    expect(screen.getByRole('status')).toBeInTheDocument()
  })
})

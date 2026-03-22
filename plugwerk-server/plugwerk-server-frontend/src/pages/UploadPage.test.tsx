// SPDX-License-Identifier: AGPL-3.0
// Copyright (C) 2026 devtank42 GmbH
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { renderWithRouter } from '../test/renderWithTheme'
import { UploadPage } from './UploadPage'
import { useAuthStore } from '../stores/authStore'
import axios from 'axios'
import * as apiConfig from '../api/config'

vi.mock('../api/config', () => ({
  axiosInstance: { post: vi.fn() },
}))

async function fillAndSubmit(user: ReturnType<typeof userEvent.setup>) {
  const file = new File(['fake-jar'], 'plugin.jar', { type: 'application/java-archive' })
  await user.upload(screen.getByLabelText(/select plugin jar file/i), file)
  await user.type(screen.getByPlaceholderText('e.g. acme-pdf-export'), 'my-plugin')
  await user.type(screen.getByPlaceholderText('e.g. 1.0.0'), '1.0.0')
  await user.click(screen.getByRole('button', { name: /upload release/i }))
}

describe('UploadPage', () => {
  beforeEach(() => {
    useAuthStore.setState({ accessToken: 'token', username: 'test', isAuthenticated: true })
    vi.mocked(apiConfig.axiosInstance.post).mockReset()
  })

  it('renders the upload form', () => {
    renderWithRouter(<UploadPage />)
    expect(screen.getByText(/upload plugin release/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/select plugin jar file/i)).toBeInTheDocument()
  })

  it('shows the backend error message when upload returns 422', async () => {
    const axiosError = new axios.AxiosError(
      'Request failed with status code 422',
      '422',
      undefined,
      undefined,
      {
        status: 422,
        data: { message: 'No descriptor found in JAR (tried plugwerk.yml, MANIFEST.MF, plugin.properties)' },
        statusText: 'Unprocessable Entity',
        headers: {},
        config: {} as never,
      },
    )
    vi.mocked(apiConfig.axiosInstance.post).mockRejectedValue(axiosError)

    const user = userEvent.setup()
    renderWithRouter(<UploadPage />)
    await fillAndSubmit(user)

    await waitFor(() => {
      expect(screen.getByRole('alert')).toBeInTheDocument()
      expect(screen.getByText(/no descriptor found in jar/i)).toBeInTheDocument()
    }, { timeout: 15000 })
  }, 20000)

  it('falls back to axios message when response body has no message field', async () => {
    const axiosError = new axios.AxiosError('Request failed with status code 500', '500', undefined, undefined, {
      status: 500,
      data: {},
      statusText: 'Internal Server Error',
      headers: {},
      config: {} as never,
    })
    vi.mocked(apiConfig.axiosInstance.post).mockRejectedValue(axiosError)

    const user = userEvent.setup()
    renderWithRouter(<UploadPage />)
    await fillAndSubmit(user)

    await waitFor(() => {
      expect(screen.getByRole('alert')).toBeInTheDocument()
      expect(screen.getByText(/request failed with status code 500/i)).toBeInTheDocument()
    }, { timeout: 15000 })
  }, 20000)
})

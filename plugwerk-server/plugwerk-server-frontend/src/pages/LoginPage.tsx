// SPDX-License-Identifier: AGPL-3.0
// Copyright (C) 2026 devtank42 GmbH
import { useState } from 'react'
import { Box, TextField, Button, Alert, Link as MuiLink, Typography } from '@mui/material'
import { Link } from 'react-router-dom'
import { AuthCard } from '../components/auth/AuthCard'
import { useAuthStore } from '../stores/authStore'

export function LoginPage() {
  const { setApiKey } = useAuthStore()
  const [apiKey, setApiKeyLocal] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    if (!apiKey.trim()) {
      setError('Please enter your API key.')
      return
    }
    setError(null)
    setLoading(true)
    // Phase 1 MVP: API key is stored directly — no session endpoint yet
    try {
      setApiKey(apiKey.trim())
      window.location.href = '/'
    } catch {
      setError('Invalid API key. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <AuthCard title="Welcome back" subtitle="Sign in with your API key">
      {/* Error banner — fixed: only shown when error is set */}
      {error && (
        <Alert severity="error" role="alert" onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      <Box component="form" onSubmit={handleSubmit} noValidate sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
        <TextField
          label="API Key"
          type="password"
          value={apiKey}
          onChange={(e) => setApiKeyLocal(e.target.value)}
          required
          autoComplete="current-password"
          placeholder="pw_…"
          size="small"
          inputProps={{ 'aria-describedby': error ? 'login-error' : undefined }}
        />

        <Button type="submit" variant="contained" size="large" disabled={loading} fullWidth>
          {loading ? 'Signing in…' : 'Sign In'}
        </Button>
      </Box>

      <Typography variant="caption" color="text.disabled" sx={{ textAlign: 'center' }}>
        Don't have an account?{' '}
        <MuiLink component={Link} to="/register" sx={{ color: 'primary.main' }}>
          Register
        </MuiLink>
      </Typography>
    </AuthCard>
  )
}

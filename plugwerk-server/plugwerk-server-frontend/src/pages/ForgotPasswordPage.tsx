// SPDX-License-Identifier: AGPL-3.0
// Copyright (C) 2026 devtank42 GmbH
import { Box, TextField, Button, Typography, Link as MuiLink } from '@mui/material'
import { Link } from 'react-router-dom'
import { AuthCard } from '../components/auth/AuthCard'

export function ForgotPasswordPage() {
  return (
    <AuthCard title="Reset password" subtitle="Enter your email to receive a reset link">
      <Box component="form" noValidate sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
        <TextField label="Email" type="email" required size="small" autoComplete="email" />
        <Button type="submit" variant="contained" size="large" fullWidth>
          Send Reset Link
        </Button>
      </Box>
      <Typography variant="caption" color="text.disabled" sx={{ textAlign: 'center' }}>
        Remember your password?{' '}
        <MuiLink component={Link} to="/login" sx={{ color: 'primary.main' }}>
          Back to login
        </MuiLink>
      </Typography>
    </AuthCard>
  )
}

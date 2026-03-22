// SPDX-License-Identifier: AGPL-3.0
// Copyright (C) 2026 devtank42 GmbH
import { Box, TextField, Button, Typography, Link as MuiLink } from '@mui/material'
import { Link } from 'react-router-dom'
import { AuthCard } from '../components/auth/AuthCard'

export function RegisterPage() {
  return (
    <AuthCard title="Create account" subtitle="Register to publish plugins">
      <Box component="form" noValidate sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
        <TextField label="Username" required size="small" autoComplete="username" />
        <TextField label="Email" type="email" required size="small" autoComplete="email" />
        <TextField label="Password" type="password" required size="small" autoComplete="new-password" />
        <Button type="submit" variant="contained" size="large" fullWidth>
          Create Account
        </Button>
      </Box>
      <Typography variant="caption" color="text.disabled" sx={{ textAlign: 'center' }}>
        Already have an account?{' '}
        <MuiLink component={Link} to="/login" sx={{ color: 'primary.main' }}>
          Log in
        </MuiLink>
      </Typography>
    </AuthCard>
  )
}

// SPDX-License-Identifier: AGPL-3.0
// Copyright (C) 2026 devtank42 GmbH
import { Box, TextField, Button, Typography, Link as MuiLink } from '@mui/material'
import { Link } from 'react-router-dom'
import { AuthCard } from '../components/auth/AuthCard'

export function ResetPasswordPage() {
  return (
    <AuthCard title="Set new password" subtitle="Enter and confirm your new password">
      <Box component="form" noValidate sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
        <TextField label="New Password" type="password" required size="small" autoComplete="new-password" />
        <TextField label="Confirm Password" type="password" required size="small" autoComplete="new-password" />
        <Button type="submit" variant="contained" size="large" fullWidth>
          Set Password
        </Button>
      </Box>
      <Typography variant="caption" color="text.disabled" sx={{ textAlign: 'center' }}>
        <MuiLink component={Link} to="/login" sx={{ color: 'primary.main' }}>
          Back to login
        </MuiLink>
      </Typography>
    </AuthCard>
  )
}

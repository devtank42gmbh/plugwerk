// SPDX-License-Identifier: AGPL-3.0
// Copyright (C) 2026 devtank42 GmbH
import { Box, Container, Typography, Button } from '@mui/material'
import { Link } from 'react-router-dom'
import type { ReactNode } from 'react'

interface ErrorPageProps {
  code: number
  title: string
  message: string
  illustration: ReactNode
}

export function ErrorPage({ code, title, message, illustration }: ErrorPageProps) {
  return (
    <Box
      component="main"
      id="main-content"
      sx={{
        flex: 1,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        py: 8,
      }}
    >
      <Container maxWidth="sm">
        <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 3, textAlign: 'center' }}>
          <Box sx={{ color: 'text.disabled', width: 160, height: 160 }}>
            {illustration}
          </Box>
          <Typography variant="h1" sx={{ fontSize: '4rem', fontWeight: 700, color: 'text.disabled' }}>
            {code}
          </Typography>
          <Typography variant="h2">{title}</Typography>
          <Typography variant="body1" color="text.secondary">
            {message}
          </Typography>
          <Button component={Link} to="/" variant="contained">
            Back to Catalog
          </Button>
        </Box>
      </Container>
    </Box>
  )
}

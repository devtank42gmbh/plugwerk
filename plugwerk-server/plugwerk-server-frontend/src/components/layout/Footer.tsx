// SPDX-License-Identifier: AGPL-3.0
// Copyright (C) 2026 devtank42 GmbH
import { Box, Typography, Link, Divider } from '@mui/material'
import { tokens } from '../../theme/tokens'

export function Footer() {
  return (
    <Box
      component="footer"
      role="contentinfo"
      sx={{
        borderTop: `1px solid`,
        borderColor: 'divider',
        mt: 'auto',
        py: 1.5,
        px: 3,
        background: 'background.paper',
      }}
    >
      <Box
        sx={{
          display: 'flex',
          alignItems: 'center',
          gap: 3,
          flexWrap: 'wrap',
          maxWidth: 1280,
          mx: 'auto',
        }}
      >
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <Typography variant="body2" fontWeight={700}>PlugWerk</Typography>
          <Typography variant="caption" color="text.disabled">v1.0.0</Typography>
        </Box>

        <Link
          href="/api/v1"
          target="_blank"
          rel="noopener"
          sx={{
            fontSize: '0.8125rem',
            color: tokens.color.primary,
            '&:hover': { textDecoration: 'underline' },
          }}
        >
          API Docs
        </Link>

        <Box sx={{ flex: 1 }} />

        {/* Language toggle */}
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
          <Box
            component="button"
            aria-pressed="true"
            lang="en"
            sx={{
              background: 'none',
              border: 'none',
              cursor: 'pointer',
              fontSize: '0.75rem',
              fontWeight: 600,
              color: tokens.color.primary,
              px: 0.5,
            }}
          >
            EN
          </Box>
          <Divider orientation="vertical" flexItem sx={{ mx: 0.5 }} aria-hidden="true" />
          <Box
            component="button"
            aria-pressed="false"
            lang="de"
            sx={{
              background: 'none',
              border: 'none',
              cursor: 'pointer',
              fontSize: '0.75rem',
              color: 'text.disabled',
              px: 0.5,
              '&:hover': { color: 'text.secondary' },
            }}
          >
            DE
          </Box>
        </Box>
      </Box>
    </Box>
  )
}

// SPDX-License-Identifier: AGPL-3.0
// Copyright (C) 2026 devtank42 GmbH
import { Box, Typography } from '@mui/material'
import { Download, Clock, Puzzle } from 'lucide-react'
import { Link } from 'react-router-dom'
import { Badge } from '../common/Badge'
import type { PluginDto } from '../../api/generated/model'
import { tokens } from '../../theme/tokens'

interface PluginListRowProps {
  plugin: PluginDto
  namespace: string
}

function formatCount(n: number | undefined): string {
  if (!n) return '0'
  if (n >= 1000) return `${(n / 1000).toFixed(1)}k`
  return String(n)
}

export function PluginListRow({ plugin, namespace }: PluginListRowProps) {
  return (
    <Box
      component={Link}
      to={`/${namespace}/plugins/${plugin.pluginId}`}
      role="listitem"
      sx={{
        display: 'flex',
        alignItems: 'center',
        gap: 2,
        px: 2,
        py: 1.5,
        border: '1px solid',
        borderColor: 'divider',
        borderRadius: tokens.radius.card,
        textDecoration: 'none',
        color: 'inherit',
        background: 'background.paper',
        transition: 'border-color 0.15s',
        '&:hover': { borderColor: tokens.color.primary },
      }}
    >
      <Box
        sx={{
          width: 36,
          height: 36,
          borderRadius: tokens.radius.btn,
          background: 'background.default',
          border: '1px solid',
          borderColor: 'divider',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          color: 'text.disabled',
          flexShrink: 0,
        }}
        aria-hidden="true"
      >
        <Puzzle size={20} />
      </Box>

      <Box sx={{ flex: 1, minWidth: 0 }}>
        <Typography variant="body2" fontWeight={600} noWrap>{plugin.name}</Typography>
        <Typography variant="caption" color="text.disabled">{plugin.author ?? namespace}</Typography>
      </Box>

      {plugin.latestVersion && (
        <Badge variant="version">v{plugin.latestVersion}</Badge>
      )}

      <Box sx={{ display: 'flex', gap: 2, color: 'text.disabled', flexShrink: 0 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
          <Download size={12} aria-hidden="true" />
          <Typography variant="caption">{formatCount(plugin.downloadCount ?? 0)}</Typography>
        </Box>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
          <Clock size={12} aria-hidden="true" />
          <Typography variant="caption">
            {plugin.updatedAt ? new Date(plugin.updatedAt).toLocaleDateString() : '—'}
          </Typography>
        </Box>
      </Box>
    </Box>
  )
}

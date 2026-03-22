// SPDX-License-Identifier: AGPL-3.0
// Copyright (C) 2026 devtank42 GmbH
import { Box, Card, CardActionArea, Typography } from '@mui/material'
import { Download, Clock, Puzzle } from 'lucide-react'
import { Link } from 'react-router-dom'
import { Badge } from '../common/Badge'
import type { PluginDto } from '../../api/generated/model'
import { tokens } from '../../theme/tokens'

interface PluginCardProps {
  plugin: PluginDto
  namespace: string
}

function formatCount(n: number | undefined): string {
  if (!n) return '0'
  if (n >= 1000) return `${(n / 1000).toFixed(1)}k`
  return String(n)
}

export function PluginCard({ plugin, namespace }: PluginCardProps) {
  const isDeprecated = plugin.status === 'archived'

  return (
    <Card
      component={Link}
      to={`/${namespace}/plugins/${plugin.pluginId}`}
      role="listitem"
      aria-label={`${plugin.name} plugin`}
      sx={{
        display: 'flex',
        flexDirection: 'column',
        height: '100%',
        textDecoration: 'none',
        transition: 'border-color 0.15s, box-shadow 0.15s',
        '&:hover': {
          borderColor: tokens.color.primary,
          boxShadow: `0 0 0 1px ${tokens.color.primary}`,
        },
      }}
    >
      <CardActionArea
        component="div"
        sx={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'flex-start', p: 2.5, gap: 1.5 }}
      >
        {/* Header: icon + meta */}
        <Box sx={{ display: 'flex', gap: 1.5, width: '100%' }}>
          <Box
            sx={{
              width: 48,
              height: 48,
              borderRadius: tokens.radius.card,
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
            <Puzzle size={28} />
          </Box>
          <Box sx={{ flex: 1, minWidth: 0 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', flexWrap: 'wrap', gap: 0.5, mb: 0.5 }}>
              <Typography
                variant="body1"
                fontWeight={600}
                sx={{
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                  whiteSpace: 'nowrap',
                  maxWidth: '100%',
                }}
              >
                {plugin.name}
              </Typography>
              {plugin.latestVersion && (
                <Badge variant="version">v{plugin.latestVersion}</Badge>
              )}
            </Box>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
              <Typography variant="caption" color="text.disabled">
                {plugin.author ?? namespace}
              </Typography>
              {isDeprecated && <Badge variant="deprecated">Deprecated</Badge>}
            </Box>
          </Box>
        </Box>

        {/* Description */}
        {plugin.description && (
          <Typography
            variant="body2"
            color="text.secondary"
            sx={{
              display: '-webkit-box',
              WebkitLineClamp: 2,
              WebkitBoxOrient: 'vertical',
              overflow: 'hidden',
              lineHeight: 1.6,
            }}
          >
            {plugin.description}
          </Typography>
        )}

        {/* Tags */}
        {plugin.tags && plugin.tags.length > 0 && (
          <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }} aria-label="Tags">
            {plugin.tags.slice(0, 4).map((tag) => (
              <Badge key={tag} variant="tag">{tag}</Badge>
            ))}
          </Box>
        )}

        {/* Stats */}
        <Box
          sx={{ display: 'flex', alignItems: 'center', gap: 2, mt: 'auto' }}
          aria-label="Plugin statistics"
        >
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, color: 'text.disabled' }}>
            <Download size={12} aria-hidden="true" />
            <Typography variant="caption">{formatCount(plugin.downloadCount ?? 0)}</Typography>
          </Box>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, color: 'text.disabled' }}>
            <Clock size={12} aria-hidden="true" />
            <Typography variant="caption">
              {plugin.updatedAt ? new Date(plugin.updatedAt).toLocaleDateString() : '—'}
            </Typography>
          </Box>
        </Box>
      </CardActionArea>
    </Card>
  )
}

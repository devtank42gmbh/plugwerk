// SPDX-License-Identifier: AGPL-3.0
// Copyright (C) 2026 devtank42 GmbH
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  Button,
  Box,
  Typography,
} from '@mui/material'
import { Download } from 'lucide-react'
import { Badge } from '../common/Badge'
import type { PluginReleaseDto } from '../../api/generated/model'
import { tokens } from '../../theme/tokens'
import type { BadgeVariant } from '../common/Badge'

interface VersionsTabProps {
  releases: PluginReleaseDto[]
  namespace: string
  pluginId: string
  currentVersion?: string
}

const statusToBadge: Record<string, BadgeVariant> = {
  published:  'published',
  draft:      'draft',
  deprecated: 'deprecated',
  yanked:     'yanked',
}

export function VersionsTab({ releases, namespace, pluginId, currentVersion }: VersionsTabProps) {
  return (
    <Box sx={{ overflowX: 'auto' }}>
      <Table aria-label="Release versions" size="small">
        <TableHead>
          <TableRow>
            <TableCell>Version</TableCell>
            <TableCell>Released</TableCell>
            <TableCell>Status</TableCell>
            <TableCell>Changelog</TableCell>
            <TableCell>Download</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {releases.map((rel) => {
            const isCurrent = rel.version === currentVersion
            return (
              <TableRow
                key={rel.id}
                sx={isCurrent ? { background: tokens.color.primaryLight + '33' } : {}}
              >
                <TableCell>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                    <Badge variant="version">v{rel.version}</Badge>
                    {isCurrent && (
                      <Typography variant="caption" sx={{ color: tokens.color.primary, fontWeight: 600 }}>
                        current
                      </Typography>
                    )}
                  </Box>
                </TableCell>
                <TableCell>
                  <Typography variant="caption" color="text.disabled">
                    {rel.publishedAt ? new Date(rel.publishedAt).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' }) : '—'}
                  </Typography>
                </TableCell>
                <TableCell>
                  <Badge variant={statusToBadge[rel.status] ?? 'draft'}>
                    {rel.status.charAt(0).toUpperCase() + rel.status.slice(1)}
                  </Badge>
                </TableCell>
                <TableCell>
                  <Typography variant="caption" color="text.secondary">
                    {rel.changelog ? rel.changelog.slice(0, 60) + (rel.changelog.length > 60 ? '…' : '') : '—'}
                  </Typography>
                </TableCell>
                <TableCell>
                  {rel.status !== 'yanked' ? (
                    <Button
                      variant="text"
                      size="small"
                      startIcon={<Download size={14} />}
                      href={`/api/v1/namespaces/${namespace}/plugins/${pluginId}/releases/${rel.version}/download`}
                      download
                    >
                      .jar
                    </Button>
                  ) : (
                    <Typography variant="caption" color="text.disabled">Unavailable</Typography>
                  )}
                </TableCell>
              </TableRow>
            )
          })}
        </TableBody>
      </Table>
    </Box>
  )
}

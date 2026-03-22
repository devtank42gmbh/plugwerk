// SPDX-License-Identifier: AGPL-3.0
// Copyright (C) 2026 devtank42 GmbH
import { Box, Table, TableBody, TableCell, TableHead, TableRow, Typography, Link } from '@mui/material'
import { Badge } from '../common/Badge'
import type { PluginReleaseDto } from '../../api/generated/model'

interface DependenciesTabProps {
  release: PluginReleaseDto | null
  namespace: string
}

export function DependenciesTab({ release, namespace }: DependenciesTabProps) {
  const deps = release?.pluginDependencies ?? []

  if (deps.length === 0) {
    return (
      <Typography variant="body2" color="text.secondary">This plugin has no dependencies.</Typography>
    )
  }

  return (
    <Box>
      <Typography variant="body2" color="text.disabled" sx={{ mb: 2 }}>
        Required plugins that must be installed alongside this plugin.
      </Typography>
      <Box sx={{ overflowX: 'auto' }}>
        <Table aria-label="Plugin dependencies" size="small">
          <TableHead>
            <TableRow>
              <TableCell>Plugin ID</TableCell>
              <TableCell>Required Version</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {deps.map((dep) => (
              <TableRow key={dep.id}>
                <TableCell>
                  <Link
                    href={`/${namespace}/plugins/${dep.id}`}
                    sx={{ fontFamily: 'monospace', fontSize: '0.8125rem', color: 'primary.main' }}
                  >
                    {dep.id}
                  </Link>
                </TableCell>
                <TableCell>
                  <Badge variant="version">{dep.version}</Badge>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </Box>
    </Box>
  )
}

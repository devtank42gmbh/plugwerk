// SPDX-License-Identifier: AGPL-3.0
// Copyright (C) 2026 devtank42 GmbH
import { Box } from '@mui/material'
import { Check } from 'lucide-react'
import { tokens } from '../../theme/tokens'
import type { ReactNode } from 'react'

export type BadgeVariant =
  | 'version'
  | 'published'
  | 'draft'
  | 'deprecated'
  | 'yanked'
  | 'tag'
  | 'verified'

interface BadgeProps {
  variant: BadgeVariant
  children: ReactNode
}

const styles: Record<BadgeVariant, { bg: string; color: string }> = {
  version:    { bg: tokens.badge.version.bg,    color: tokens.badge.version.text },
  published:  { bg: tokens.badge.published.bg,  color: tokens.badge.published.text },
  draft:      { bg: tokens.badge.draft.bg,       color: tokens.badge.draft.text },
  deprecated: { bg: tokens.badge.deprecated.bg,  color: tokens.badge.deprecated.text },
  yanked:     { bg: tokens.badge.yanked.bg,       color: tokens.badge.yanked.text },
  tag:        { bg: tokens.badge.tag.bg,          color: tokens.badge.tag.text },
  verified:   { bg: tokens.badge.published.bg,    color: tokens.badge.published.text },
}

export function Badge({ variant, children }: BadgeProps) {
  const s = styles[variant]
  return (
    <Box
      component="span"
      sx={{
        display: 'inline-flex',
        alignItems: 'center',
        gap: '3px',
        px: '6px',
        py: '2px',
        borderRadius: tokens.radius.btn,
        background: s.bg,
        color: s.color,
        fontSize: '0.6875rem',
        fontWeight: 600,
        lineHeight: '16px',
        whiteSpace: 'nowrap',
      }}
    >
      {variant === 'verified' && <Check size={10} aria-hidden="true" />}
      {children}
    </Box>
  )
}

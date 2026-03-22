// SPDX-License-Identifier: AGPL-3.0
// Copyright (C) 2026 devtank42 GmbH
import { Box, IconButton, Tooltip } from '@mui/material'
import { Clipboard, Check } from 'lucide-react'
import { useState } from 'react'
import { tokens } from '../../theme/tokens'

interface CodeBlockProps {
  code: string
  lang?: string
}

export function CodeBlock({ code }: CodeBlockProps) {
  const [copied, setCopied] = useState(false)

  async function handleCopy() {
    await navigator.clipboard.writeText(code)
    setCopied(true)
    setTimeout(() => setCopied(false), 1500)
  }

  return (
    <Box
      sx={{
        position: 'relative',
        background: 'background.default',
        border: '1px solid',
        borderColor: 'divider',
        borderRadius: tokens.radius.card,
        p: 2,
        pr: 5,
        overflow: 'auto',
      }}
    >
      <Box
        component="pre"
        sx={{
          m: 0,
          fontFamily: '"JetBrains Mono", "Fira Code", monospace',
          fontSize: '0.8125rem',
          lineHeight: 1.6,
          color: 'text.primary',
          whiteSpace: 'pre-wrap',
          wordBreak: 'break-all',
        }}
      >
        <code>{code}</code>
      </Box>

      <Tooltip title={copied ? 'Copied!' : 'Copy'}>
        <IconButton
          size="small"
          onClick={handleCopy}
          aria-label="Copy code"
          sx={{
            position: 'absolute',
            top: 6,
            right: 6,
            color: 'text.disabled',
            '&:hover': { color: 'text.secondary' },
          }}
        >
          {copied ? <Check size={14} /> : <Clipboard size={14} />}
        </IconButton>
      </Tooltip>
    </Box>
  )
}

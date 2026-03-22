// SPDX-License-Identifier: AGPL-3.0
// Copyright (C) 2026 devtank42 GmbH
import { Box, Typography } from '@mui/material'
import ReactMarkdown from 'react-markdown'
import rehypeSanitize from 'rehype-sanitize'
import type { PluginDto } from '../../api/generated/model'

interface OverviewTabProps {
  plugin: PluginDto
}

export function OverviewTab({ plugin }: OverviewTabProps) {
  const content = plugin.description ?? '*No description available.*'

  return (
    <Box
      sx={{
        '& h2': { fontSize: '1.25rem', fontWeight: 600, mt: 3, mb: 1.5 },
        '& h3': { fontSize: '1.0625rem', fontWeight: 600, mt: 2, mb: 1 },
        '& p':  { fontSize: '0.875rem', color: 'text.secondary', mb: 1.5, lineHeight: 1.7 },
        '& ul, & ol': { pl: 3, mb: 1.5 },
        '& li': { fontSize: '0.875rem', color: 'text.secondary', mb: 0.5, lineHeight: 1.7 },
        '& pre': { my: 2 },
        '& code': { fontFamily: 'monospace', fontSize: '0.8125rem' },
      }}
    >
      <Typography variant="h2" sx={{ mb: 2 }}>About this plugin</Typography>
      <ReactMarkdown rehypePlugins={[rehypeSanitize]}>
        {content}
      </ReactMarkdown>
    </Box>
  )
}

// SPDX-License-Identifier: AGPL-3.0
// Copyright (C) 2026 devtank42 GmbH
import {
  Box,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Button,
  ToggleButton,
  ToggleButtonGroup,
} from '@mui/material'
import { LayoutGrid, List } from 'lucide-react'
import { usePluginStore } from '../../stores/pluginStore'

interface FilterBarProps {
  view: 'card' | 'list'
  onViewChange: (view: 'card' | 'list') => void
  namespace: string
}

const CATEGORIES = ['Reporting', 'Export', 'Integration', 'Security', 'UI Extensions', 'Data Processing']
const SORT_OPTIONS = [
  { value: 'name,asc',          label: 'Name A–Z' },
  { value: 'name,desc',         label: 'Name Z–A' },
  { value: 'downloadCount,desc', label: 'Most Downloads' },
  { value: 'updatedAt,desc',    label: 'Newest' },
]

export function FilterBar({ view, onViewChange, namespace }: FilterBarProps) {
  const { filters, setFilters, fetchPlugins } = usePluginStore()
  const hasActiveFilters = !!(filters.category || filters.tag || filters.status)

  function handleChange(key: string, value: string) {
    setFilters({ [key]: value, page: 0 })
    fetchPlugins(namespace)
  }

  function handleReset() {
    setFilters({ category: '', tag: '', status: '', sort: 'name,asc', page: 0 })
    fetchPlugins(namespace)
  }

  return (
    <Box
      role="group"
      aria-label="Filter and sort options"
      sx={{
        display: 'flex',
        alignItems: 'center',
        flexWrap: 'wrap',
        gap: 1,
        py: 2,
        borderBottom: '1px solid',
        borderColor: 'divider',
        mb: 3,
      }}
    >
      <FormControl size="small" sx={{ minWidth: 160 }}>
        <InputLabel>Category</InputLabel>
        <Select
          value={filters.category}
          label="Category"
          onChange={(e) => handleChange('category', e.target.value)}
        >
          <MenuItem value="">All Categories</MenuItem>
          {CATEGORIES.map((c) => (
            <MenuItem key={c} value={c}>{c}</MenuItem>
          ))}
        </Select>
      </FormControl>

      <FormControl size="small" sx={{ minWidth: 140 }}>
        <InputLabel>Sort</InputLabel>
        <Select
          value={filters.sort}
          label="Sort"
          onChange={(e) => handleChange('sort', e.target.value)}
        >
          {SORT_OPTIONS.map((o) => (
            <MenuItem key={o.value} value={o.value}>{o.label}</MenuItem>
          ))}
        </Select>
      </FormControl>

      {hasActiveFilters && (
        <Button variant="text" size="small" onClick={handleReset} sx={{ color: 'text.secondary' }}>
          Reset filters
        </Button>
      )}

      <Box sx={{ flex: 1 }} />

      <ToggleButtonGroup
        value={view}
        exclusive
        onChange={(_, v) => v && onViewChange(v)}
        aria-label="Switch view"
        size="small"
      >
        <ToggleButton value="card" aria-label="Card view">
          <LayoutGrid size={16} />
        </ToggleButton>
        <ToggleButton value="list" aria-label="List view">
          <List size={16} />
        </ToggleButton>
      </ToggleButtonGroup>
    </Box>
  )
}

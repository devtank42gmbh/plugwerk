// SPDX-License-Identifier: AGPL-3.0
// Copyright (C) 2026 devtank42 GmbH
import { create } from 'zustand'
import type { PluginDto, PluginPagedResponse } from '../api/generated/model'
import type { ListPluginsStatusEnum } from '../api/generated/api/catalog-api'
import { catalogApi, axiosInstance } from '../api/config'

interface PluginFilters {
  search: string
  tag: string
  status: string
  sort: string
  page: number
  size: number
}

interface PluginState {
  plugins: PluginDto[]
  totalElements: number
  totalPages: number
  pendingReviewPluginCount: number | null
  availableTags: string[]
  loading: boolean
  error: string | null
  filters: PluginFilters

  setFilters: (partial: Partial<PluginFilters>) => void
  resetFilters: () => void
  fetchPlugins: (namespace: string) => Promise<void>
  fetchTags: (namespace: string) => Promise<void>
}

const defaultFilters: PluginFilters = {
  search: '',
  tag: '',
  status: '',
  sort: 'name,asc',
  page: 0,
  size: 24,
}

export const usePluginStore = create<PluginState>((set, get) => ({
  plugins: [],
  totalElements: 0,
  totalPages: 0,
  pendingReviewPluginCount: null,
  availableTags: [],
  loading: false,
  error: null,
  filters: { ...defaultFilters },

  setFilters(partial) {
    set((s) => ({
      filters: { ...s.filters, ...partial, page: partial.page ?? 0 },
    }))
  },

  resetFilters() {
    set({ filters: { ...defaultFilters } })
  },

  async fetchPlugins(namespace: string) {
    set({ loading: true, error: null })
    try {
      const { filters } = get()
      const response = await catalogApi.listPlugins({
        ns: namespace,
        page: filters.page,
        size: filters.size,
        sort: filters.sort,
        q: filters.search || undefined,
        tag: filters.tag || undefined,
        status: (filters.status || undefined) as ListPluginsStatusEnum | undefined,
      })
      const data: PluginPagedResponse = response.data
      set({
        plugins: data.content,
        totalElements: data.totalElements,
        totalPages: data.totalPages,
        pendingReviewPluginCount: data.pendingReviewPluginCount ?? null,
        loading: false,
      })
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Failed to load plugins'
      set({ loading: false, error: message })
    }
  },

  async fetchTags(namespace: string) {
    try {
      const response = await axiosInstance.get<string[]>(`/namespaces/${namespace}/tags`)
      set({ availableTags: response.data })
    } catch {
      set({ availableTags: [] })
    }
  },
}))

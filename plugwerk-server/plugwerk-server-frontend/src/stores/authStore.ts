// SPDX-License-Identifier: AGPL-3.0
// Copyright (C) 2026 devtank42 GmbH
import { create } from 'zustand'

interface AuthState {
  apiKey: string | null
  namespace: string

  setApiKey: (key: string) => void
  clearApiKey: () => void
  setNamespace: (ns: string) => void
}

export const useAuthStore = create<AuthState>((set) => ({
  apiKey: localStorage.getItem('pw-api-key'),
  namespace: localStorage.getItem('pw-namespace') ?? 'default',

  setApiKey(key) {
    localStorage.setItem('pw-api-key', key)
    set({ apiKey: key })
  },

  clearApiKey() {
    localStorage.removeItem('pw-api-key')
    set({ apiKey: null })
  },

  setNamespace(ns) {
    localStorage.setItem('pw-namespace', ns)
    set({ namespace: ns })
  },
}))

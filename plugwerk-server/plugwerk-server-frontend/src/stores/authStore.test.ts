// SPDX-License-Identifier: AGPL-3.0
// Copyright (C) 2026 devtank42 GmbH
import { describe, it, expect, beforeEach } from 'vitest'
import { act } from 'react'
import { useAuthStore } from './authStore'

describe('useAuthStore', () => {
  beforeEach(() => {
    localStorage.clear()
    useAuthStore.setState({ apiKey: null, namespace: 'default' })
  })

  describe('initial state', () => {
    it('has null apiKey when nothing in localStorage', () => {
      expect(useAuthStore.getState().apiKey).toBeNull()
    })

    it('has "default" namespace when nothing in localStorage', () => {
      expect(useAuthStore.getState().namespace).toBe('default')
    })
  })

  describe('setApiKey', () => {
    it('sets the apiKey in state', () => {
      act(() => { useAuthStore.getState().setApiKey('my-secret-key') })
      expect(useAuthStore.getState().apiKey).toBe('my-secret-key')
    })

    it('persists apiKey to localStorage', () => {
      act(() => { useAuthStore.getState().setApiKey('my-secret-key') })
      expect(localStorage.getItem('pw-api-key')).toBe('my-secret-key')
    })
  })

  describe('clearApiKey', () => {
    it('sets apiKey to null', () => {
      useAuthStore.setState({ apiKey: 'some-key' })
      act(() => { useAuthStore.getState().clearApiKey() })
      expect(useAuthStore.getState().apiKey).toBeNull()
    })

    it('removes apiKey from localStorage', () => {
      localStorage.setItem('pw-api-key', 'some-key')
      act(() => { useAuthStore.getState().clearApiKey() })
      expect(localStorage.getItem('pw-api-key')).toBeNull()
    })
  })

  describe('setNamespace', () => {
    it('updates namespace in state', () => {
      act(() => { useAuthStore.getState().setNamespace('acme') })
      expect(useAuthStore.getState().namespace).toBe('acme')
    })

    it('persists namespace to localStorage', () => {
      act(() => { useAuthStore.getState().setNamespace('acme') })
      expect(localStorage.getItem('pw-namespace')).toBe('acme')
    })
  })

  describe('login/logout flow', () => {
    it('can log in and log out', () => {
      act(() => {
        useAuthStore.getState().setApiKey('tok_abc')
        useAuthStore.getState().setNamespace('my-org')
      })
      expect(useAuthStore.getState().apiKey).toBe('tok_abc')
      expect(useAuthStore.getState().namespace).toBe('my-org')

      act(() => { useAuthStore.getState().clearApiKey() })
      expect(useAuthStore.getState().apiKey).toBeNull()
      // namespace is preserved after logout
      expect(useAuthStore.getState().namespace).toBe('my-org')
    })
  })
})

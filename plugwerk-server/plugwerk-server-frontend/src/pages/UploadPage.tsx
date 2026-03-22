// SPDX-License-Identifier: AGPL-3.0
// Copyright (C) 2026 devtank42 GmbH
import { useState, useCallback } from 'react'
import {
  Box,
  Container,
  Typography,
  Button,
  TextField,
  Alert,
  LinearProgress,
  Paper,
} from '@mui/material'
import { useDropzone } from 'react-dropzone'
import { UploadCloud, FileBox, CheckCircle } from 'lucide-react'
import axios from 'axios'
import { axiosInstance } from '../api/config'
import { useAuthStore } from '../stores/authStore'
import { useUiStore } from '../stores/uiStore'
import { tokens } from '../theme/tokens'

interface UploadFormState {
  pluginId: string
  version: string
  changelog: string
  requiresSystemVersion: string
}

const EMPTY_FORM: UploadFormState = {
  pluginId: '',
  version: '',
  changelog: '',
  requiresSystemVersion: '',
}

export function UploadPage() {
  const { namespace } = useAuthStore()
  const { addToast } = useUiStore()
  const [form, setForm] = useState<UploadFormState>(EMPTY_FORM)
  const [file, setFile] = useState<File | null>(null)
  const [progress, setProgress] = useState<number | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [success, setSuccess] = useState(false)

  const onDrop = useCallback((accepted: File[]) => {
    if (accepted.length > 0) {
      setFile(accepted[0])
      setError(null)
    }
  }, [])

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: { 'application/java-archive': ['.jar'], 'application/zip': ['.jar'] },
    maxFiles: 1,
    multiple: false,
  })

  function handleFieldChange(key: keyof UploadFormState, value: string) {
    setForm((prev) => ({ ...prev, [key]: value }))
  }

  function validate(): string | null {
    if (!file) return 'Please select a .jar file.'
    if (!form.pluginId.trim()) return 'Plugin ID is required.'
    if (!form.version.trim()) return 'Version is required.'
    if (!/^\d+\.\d+\.\d+/.test(form.version)) return 'Version must be SemVer (e.g. 1.0.0).'
    return null
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    const validationError = validate()
    if (validationError) {
      setError(validationError)
      return
    }

    setError(null)
    setProgress(0)

    const formData = new FormData()
    formData.append('artifact', file!)
    formData.append('version', form.version)
    if (form.changelog) formData.append('changelog', form.changelog)
    if (form.requiresSystemVersion) formData.append('requiresSystemVersion', form.requiresSystemVersion)

    try {
      await axiosInstance.post(
        `/namespaces/${namespace}/plugins/${form.pluginId}/releases`,
        formData,
        {
          headers: { 'Content-Type': 'multipart/form-data' },
          onUploadProgress: (evt) => {
            if (evt.total) {
              setProgress(Math.round((evt.loaded / evt.total) * 100))
            }
          },
        },
      )
      setSuccess(true)
      setProgress(null)
      addToast({ type: 'success', title: 'Release uploaded', message: `v${form.version} is now available for review.` })
    } catch (err: unknown) {
      const message = axios.isAxiosError(err)
        ? (err.response?.data?.message ?? err.message)
        : err instanceof Error ? err.message : 'Upload failed.'
      setError(message)
      setProgress(null)
      addToast({ type: 'error', title: 'Upload failed', message })
    }
  }

  if (success) {
    return (
      <Box component="main" id="main-content" sx={{ flex: 1, py: 8 }}>
        <Container maxWidth="sm">
          <Box sx={{ textAlign: 'center', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 2 }}>
            <CheckCircle size={56} color={tokens.color.success} />
            <Typography variant="h2">Release uploaded!</Typography>
            <Typography variant="body1" color="text.secondary">
              Your release is pending review. You will be notified when it is approved.
            </Typography>
            <Button variant="contained" onClick={() => { setSuccess(false); setForm(EMPTY_FORM); setFile(null) }}>
              Upload another release
            </Button>
          </Box>
        </Container>
      </Box>
    )
  }

  return (
    <Box component="main" id="main-content" sx={{ flex: 1, py: 4 }}>
      <Container maxWidth="sm">
        <Typography variant="h1" gutterBottom>Upload Plugin Release</Typography>
        <Typography variant="body1" color="text.secondary" sx={{ mb: 4 }}>
          Upload a new release for an existing plugin in namespace <strong>{namespace}</strong>.
        </Typography>

        {error && (
          <Alert severity="error" sx={{ mb: 3 }} onClose={() => setError(null)}>
            {error}
          </Alert>
        )}

        <Box component="form" onSubmit={handleSubmit} noValidate sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
          {/* Drop zone */}
          <Box
            {...getRootProps()}
            sx={{
              border: `2px dashed ${isDragActive ? tokens.color.primary : tokens.color.gray20}`,
              borderRadius: tokens.radius.card,
              p: 4,
              textAlign: 'center',
              cursor: 'pointer',
              background: isDragActive ? tokens.color.primaryLight + '22' : 'background.default',
              transition: 'border-color 0.15s, background 0.15s',
              '&:hover': { borderColor: tokens.color.primary },
              '&:focus-visible': { outline: `2px solid ${tokens.color.primary}`, outlineOffset: 2 },
            }}
          >
            <input {...getInputProps()} aria-label="Select plugin JAR file" />
            <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 1.5 }}>
              {file ? (
                <>
                  <FileBox size={36} color={tokens.color.primary} />
                  <Typography variant="body2" fontWeight={600}>{file.name}</Typography>
                  <Typography variant="caption" color="text.disabled">
                    {(file.size / 1024 / 1024).toFixed(2)} MB · Click to replace
                  </Typography>
                </>
              ) : (
                <>
                  <UploadCloud size={36} color={tokens.color.gray40} />
                  <Typography variant="body2" fontWeight={600}>
                    {isDragActive ? 'Drop the .jar here…' : 'Drag & drop a .jar file here'}
                  </Typography>
                  <Typography variant="caption" color="text.disabled">or click to browse</Typography>
                </>
              )}
            </Box>
          </Box>

          <TextField
            label="Plugin ID"
            value={form.pluginId}
            onChange={(e) => handleFieldChange('pluginId', e.target.value)}
            required
            placeholder="e.g. acme-pdf-export"
            helperText="Must match an existing plugin in this namespace"
            size="small"
          />

          <TextField
            label="Version"
            value={form.version}
            onChange={(e) => handleFieldChange('version', e.target.value)}
            required
            placeholder="e.g. 1.0.0"
            helperText="SemVer format (e.g. 1.0.0 or 1.0.0-beta.1)"
            size="small"
          />

          <TextField
            label="Changelog"
            value={form.changelog}
            onChange={(e) => handleFieldChange('changelog', e.target.value)}
            multiline
            rows={4}
            placeholder="Describe what's new in this release (Markdown supported)…"
            size="small"
          />

          <TextField
            label="Requires System Version"
            value={form.requiresSystemVersion}
            onChange={(e) => handleFieldChange('requiresSystemVersion', e.target.value)}
            placeholder="e.g. >=3.0.0 & <5.0.0"
            helperText="SemVer range for compatible host application versions"
            size="small"
          />

          {progress !== null && (
            <Paper variant="outlined" sx={{ p: 2 }}>
              <Typography variant="caption" color="text.secondary" sx={{ mb: 1, display: 'block' }}>
                Uploading… {progress}%
              </Typography>
              <LinearProgress variant="determinate" value={progress} />
            </Paper>
          )}

          <Button
            type="submit"
            variant="contained"
            size="large"
            disabled={progress !== null}
            startIcon={<UploadCloud size={18} />}
          >
            {progress !== null ? 'Uploading…' : 'Upload Release'}
          </Button>
        </Box>
      </Container>
    </Box>
  )
}

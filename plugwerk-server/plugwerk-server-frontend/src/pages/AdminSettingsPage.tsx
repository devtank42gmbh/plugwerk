// SPDX-License-Identifier: AGPL-3.0
// Copyright (C) 2026 devtank42 GmbH
import { useState, useEffect } from 'react'
import {
  Box,
  Container,
  Typography,
  TextField,
  Button,
  Divider,
  Alert,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Snackbar,
  CircularProgress,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
} from '@mui/material'
import { CheckCircle } from 'lucide-react'
import { AdminSidebar } from '../components/admin/AdminSidebar'
import { reviewsApi } from '../api/config'
import { useAuthStore } from '../stores/authStore'
import type { ReviewItemDto } from '../api/generated/model'

function GeneralSection() {
  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
      <Box>
        <Typography variant="h2" gutterBottom>General Settings</Typography>
        <Divider sx={{ mb: 3 }} />
      </Box>
      <TextField label="Instance Name" defaultValue="ACME Corp Plugin Hub" size="small" />
      <TextField label="Default Namespace" defaultValue="default" size="small" />
      <TextField label="Max Upload Size (MB)" type="number" defaultValue={50} size="small" />
      <FormControl size="small" sx={{ minWidth: 200 }}>
        <InputLabel>Default Language</InputLabel>
        <Select defaultValue="en" label="Default Language">
          <MenuItem value="en">English</MenuItem>
          <MenuItem value="de">Deutsch</MenuItem>
        </Select>
      </FormControl>
      <Button variant="contained" sx={{ alignSelf: 'flex-start' }}>Save Changes</Button>
    </Box>
  )
}

function ApiKeysSection() {
  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
      <Box>
        <Typography variant="h2" gutterBottom>API Keys</Typography>
        <Divider sx={{ mb: 3 }} />
      </Box>
      <Alert severity="info">
        API keys are used to authenticate requests from the CLI and CI/CD pipelines.
      </Alert>
      <Button variant="outlined" sx={{ alignSelf: 'flex-start' }}>Generate New API Key</Button>
    </Box>
  )
}

function UsersSection() {
  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
      <Box>
        <Typography variant="h2" gutterBottom>Users</Typography>
        <Divider sx={{ mb: 3 }} />
      </Box>
      <Typography variant="body2" color="text.secondary">
        User management will be available in Phase 2 (RBAC/OIDC).
      </Typography>
    </Box>
  )
}

function ReviewsSection() {
  const namespace = useAuthStore((s) => s.namespace)
  const [items, setItems] = useState<ReviewItemDto[]>([])
  const [loading, setLoading] = useState(true)
  const [approvingId, setApprovingId] = useState<string | null>(null)
  const [toast, setToast] = useState<{ message: string; severity: 'success' | 'error' } | null>(null)

  useEffect(() => {
    async function load() {
      setLoading(true)
      try {
        const res = await reviewsApi.listPendingReviews({ ns: namespace })
        setItems(res.data)
      } catch {
        setItems([])
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [namespace])

  async function handleApprove(item: ReviewItemDto) {
    setApprovingId(item.releaseId)
    try {
      await reviewsApi.approveRelease({ ns: namespace, releaseId: item.releaseId })
      setItems((prev) => prev.filter((i) => i.releaseId !== item.releaseId))
      setToast({ message: `${item.pluginName} v${item.version} approved and published.`, severity: 'success' })
    } catch {
      setToast({ message: `Failed to approve ${item.pluginName} v${item.version}.`, severity: 'error' })
    } finally {
      setApprovingId(null)
    }
  }

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
      <Box>
        <Typography variant="h2" gutterBottom>Pending Reviews</Typography>
        <Divider sx={{ mb: 3 }} />
      </Box>

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
          <CircularProgress size={24} />
        </Box>
      ) : items.length === 0 ? (
        <Typography variant="body2" color="text.secondary">
          No releases awaiting review.
        </Typography>
      ) : (
        <Table size="small" aria-label="Pending reviews">
          <TableHead>
            <TableRow>
              <TableCell>Plugin</TableCell>
              <TableCell>Version</TableCell>
              <TableCell>Submitted</TableCell>
              <TableCell>Action</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {items.map((item) => (
              <TableRow key={item.releaseId}>
                <TableCell>
                  <Typography variant="body2" fontWeight={500}>{item.pluginName}</Typography>
                  <Typography variant="caption" color="text.secondary">{item.pluginId}</Typography>
                </TableCell>
                <TableCell>v{item.version}</TableCell>
                <TableCell>
                  <Typography variant="caption" color="text.disabled">
                    {new Date(item.submittedAt).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })}
                  </Typography>
                </TableCell>
                <TableCell>
                  <Button
                    variant="outlined"
                    size="small"
                    color="success"
                    startIcon={<CheckCircle size={14} />}
                    loading={approvingId === item.releaseId}
                    onClick={() => handleApprove(item)}
                  >
                    Approve
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      )}

      <Snackbar
        open={!!toast}
        autoHideDuration={4000}
        onClose={() => setToast(null)}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert severity={toast?.severity} onClose={() => setToast(null)} sx={{ width: '100%' }}>
          {toast?.message}
        </Alert>
      </Snackbar>
    </Box>
  )
}

function DangerSection() {
  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
      <Box>
        <Typography variant="h2" gutterBottom color="error">Danger Zone</Typography>
        <Divider sx={{ mb: 3 }} />
      </Box>
      <Alert severity="warning">
        Actions in this section are irreversible. Proceed with caution.
      </Alert>
      <Box sx={{ border: '1px solid', borderColor: 'error.main', borderRadius: 1, p: 2 }}>
        <Typography variant="body2" fontWeight={600} gutterBottom>Reset namespace</Typography>
        <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mb: 1.5 }}>
          Delete all plugins and releases in the default namespace. This cannot be undone.
        </Typography>
        <Button variant="outlined" color="error" size="small">Reset namespace</Button>
      </Box>
    </Box>
  )
}

const sectionMap: Record<string, React.ReactNode> = {
  general: <GeneralSection />,
  'api-keys': <ApiKeysSection />,
  users: <UsersSection />,
  reviews: <ReviewsSection />,
  danger: <DangerSection />,
}

export function AdminSettingsPage() {
  const [activeSection, setActiveSection] = useState('general')

  return (
    <Box component="main" id="main-content" sx={{ flex: 1, display: 'flex' }}>
      <AdminSidebar activeSection={activeSection} onSelect={setActiveSection} />
      <Box sx={{ flex: 1, overflow: 'auto' }}>
        <Container maxWidth="md" sx={{ py: 4, maxWidth: 800 }}>
          {sectionMap[activeSection]}
        </Container>
      </Box>
    </Box>
  )
}

// SPDX-License-Identifier: AGPL-3.0
// Copyright (C) 2026 devtank42 GmbH
import axios from 'axios'
import { Configuration } from './generated/configuration'
import { AdminUsersApi } from './generated/api/admin-users-api'
import { AuthApi } from './generated/api/auth-api'
import { CatalogApi } from './generated/api/catalog-api'
import { ManagementApi } from './generated/api/management-api'
import { NamespaceMembersApi } from './generated/api/namespace-members-api'
import { OidcProvidersApi } from './generated/api/oidc-providers-api'
import { ReviewsApi } from './generated/api/reviews-api'
import { UpdatesApi } from './generated/api/updates-api'

const BASE_PATH = '/api/v1'

const axiosInstance = axios.create({
  baseURL: BASE_PATH,
})

// Auth endpoints live under /api/auth (no v1 segment) — a separate instance without
// baseURL is required so that createRequestFunction uses the explicit basePath='/api'
// rather than '' (which would cause Axios to combine with the main baseURL incorrectly).
const authAxiosInstance = axios.create()

function addInterceptors(instance: ReturnType<typeof axios.create>) {
  instance.interceptors.request.use((config) => {
    const token = localStorage.getItem('pw-access-token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  })

  instance.interceptors.response.use(
    (response) => response,
    (error) => {
      if (error.response?.status === 401) {
        localStorage.removeItem('pw-access-token')
        localStorage.removeItem('pw-username')
        window.location.href = '/login'
      }
      return Promise.reject(error)
    },
  )
}

addInterceptors(axiosInstance)
addInterceptors(authAxiosInstance)

const apiConfig = new Configuration({ basePath: BASE_PATH })

export const authApi = new AuthApi(apiConfig, '/api', authAxiosInstance)
export const adminUsersApi = new AdminUsersApi(apiConfig, BASE_PATH, axiosInstance)
export const catalogApi = new CatalogApi(apiConfig, BASE_PATH, axiosInstance)
export const managementApi = new ManagementApi(apiConfig, BASE_PATH, axiosInstance)
export const namespaceMembersApi = new NamespaceMembersApi(apiConfig, BASE_PATH, axiosInstance)
export const oidcProvidersApi = new OidcProvidersApi(apiConfig, BASE_PATH, axiosInstance)
export const reviewsApi = new ReviewsApi(apiConfig, BASE_PATH, axiosInstance)
export const updatesApi = new UpdatesApi(apiConfig, BASE_PATH, axiosInstance)

export { axiosInstance }

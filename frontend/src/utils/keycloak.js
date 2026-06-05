import Keycloak from 'keycloak-js';
import keycloakConfig from '../config/keycloak';

// Initialize Keycloak instance
const keycloak = new Keycloak(keycloakConfig);

// Helper functions for Keycloak operations
export const keycloakService = {
  // Initialize Keycloak
  init: async () => {
    try {
      const authenticated = await keycloak.init({
        onLoad: keycloakConfig.onLoad,
        pkceMethod: keycloakConfig.pkceMethod,
        checkLoginIframe: keycloakConfig.checkLoginIframe,
        silentCheckSsoRedirectUri: keycloakConfig.silentCheckSsoRedirectUri,
        responseMode: keycloakConfig.responseMode,
        responseType: keycloakConfig.responseType,
      });
      return authenticated;
    } catch (error) {
      console.error('Keycloak initialization failed:', error);
      throw error;
    }
  },

  // Login
  login: () => {
    keycloak.login();
  },

  // Logout
  logout: () => {
    keycloak.logout({ redirectUri: window.location.origin });
  },

  // Get access token
  getToken: () => {
    return keycloak.token;
  },

  // Get refresh token
  getRefreshToken: () => {
    return keycloak.refreshToken;
  },

  // Check if authenticated
  isAuthenticated: () => {
    return keycloak.authenticated;
  },

  // Get user info
  getUserInfo: () => {
    return keycloak.tokenParsed;
  },

  // Get user ID
  getUserId: () => {
    return keycloak.subject;
  },

  // Get user email
  getUserEmail: () => {
    return keycloak.tokenParsed?.email;
  },

  // Get user name
  getUserName: () => {
    return keycloak.tokenParsed?.preferred_username || keycloak.tokenParsed?.name;
  },

  // Get user roles
  getUserRoles: () => {
    return keycloak.tokenParsed?.roles || [];
  },

  // Check if user has role
  hasRole: (role) => {
    const roles = keycloak.tokenParsed?.roles || [];
    return roles.includes(role);
  },

  // Check if user has any of the specified roles
  hasAnyRole: (roles) => {
    const userRoles = keycloak.tokenParsed?.roles || [];
    return roles.some(role => userRoles.includes(role));
  },

  // Update token (refresh if needed)
  updateToken: async (minValidity = 30) => {
    try {
      return await keycloak.updateToken(minValidity);
    } catch (error) {
      console.error('Failed to refresh token:', error);
      throw error;
    }
  },

  // Get token parsed
  getTokenParsed: () => {
    return keycloak.tokenParsed;
  },

  // Get realm access
  getRealmAccess: () => {
    return keycloak.tokenParsed?.realm_access;
  },

  // Get resource access
  getResourceAccess: () => {
    return keycloak.tokenParsed?.resource_access;
  },

  // Get client roles
  getClientRoles: (clientId) => {
    return keycloak.tokenParsed?.resource_access?.[clientId]?.roles || [];
  },

  // Check if user has client role
  hasClientRole: (clientId, role) => {
    const roles = keycloak.tokenParsed?.resource_access?.[clientId]?.roles || [];
    return roles.includes(role);
  },
};

// Token refresh interval (refresh token every 4 minutes)
let tokenRefreshInterval = null;

// Setup automatic token refresh
export const setupTokenRefresh = () => {
  if (tokenRefreshInterval) {
    clearInterval(tokenRefreshInterval);
  }

  tokenRefreshInterval = setInterval(async () => {
    try {
      const refreshed = await keycloak.updateToken(60); // Refresh if token expires in less than 60 seconds
      if (refreshed) {
        console.log('Token refreshed successfully');
      }
    } catch (error) {
      console.error('Failed to refresh token:', error);
      // If refresh fails, redirect to login
      keycloakService.logout();
    }
  }, 240000); // Check every 4 minutes
};

// Cleanup token refresh
export const cleanupTokenRefresh = () => {
  if (tokenRefreshInterval) {
    clearInterval(tokenRefreshInterval);
    tokenRefreshInterval = null;
  }
};

export default keycloak;

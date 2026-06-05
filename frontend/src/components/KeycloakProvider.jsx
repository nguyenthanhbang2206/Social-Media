import React, { createContext, useContext, useEffect, useState } from 'react';
import keycloak, { keycloakService, setupTokenRefresh, cleanupTokenRefresh } from '../utils/keycloak';

const KeycloakContext = createContext({
  keycloak: null,
  authenticated: false,
  loading: true,
  login: () => {},
  logout: () => {},
  getToken: () => null,
  getUserInfo: () => null,
  hasRole: () => false,
});

export const KeycloakProvider = ({ children }) => {
  const [authenticated, setAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);
  const [keycloakInstance, setKeycloakInstance] = useState(null);

  useEffect(() => {
    const initKeycloak = async () => {
      try {
        const authenticated = await keycloakService.init();
        setAuthenticated(authenticated);
        setKeycloakInstance(keycloak);
        
        if (authenticated) {
          // Setup automatic token refresh
          setupTokenRefresh();
        }
        
        setLoading(false);
      } catch (error) {
        console.error('Keycloak initialization error:', error);
        setLoading(false);
      }
    };

    initKeycloak();

    // Cleanup on unmount
    return () => {
      cleanupTokenRefresh();
    };
  }, []);

  const login = () => {
    keycloakService.login();
  };

  const logout = () => {
    keycloakService.logout();
  };

  const getToken = () => {
    return keycloakService.getToken();
  };

  const getUserInfo = () => {
    return keycloakService.getUserInfo();
  };

  const hasRole = (role) => {
    return keycloakService.hasRole(role);
  };

  const value = {
    keycloak: keycloakInstance,
    authenticated,
    loading,
    login,
    logout,
    getToken,
    getUserInfo,
    hasRole,
  };

  return (
    <KeycloakContext.Provider value={value}>
      {children}
    </KeycloakContext.Provider>
  );
};

export const useKeycloak = () => {
  const context = useContext(KeycloakContext);
  if (!context) {
    throw new Error('useKeycloak must be used within a KeycloakProvider');
  }
  return context;
};

export default KeycloakProvider;

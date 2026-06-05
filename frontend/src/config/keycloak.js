// Keycloak configuration for Social Media Platform
const keycloakConfig = {
  url: 'http://localhost:8080',
  realm: 'social-media',
  clientId: 'social-media-web',
  // Enable PKCE for better security
  pkceMethod: 'S256',
  // Token configuration
  token: {
    // Access token lifespan (5 minutes)
    'access-token-lifespan': 300,
    // Refresh token lifespan (7 days)
    'refresh-token-lifespan': 604800,
  },
  // Enable silent check-sso
  silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
  // Enable check login iframe
  checkLoginIframe: false,
  // Enable token refresh
  enableLogging: true,
  // On load action
  onLoad: 'login-required',
  // Response mode
  responseMode: 'query',
  // Response type
  responseType: 'code',
  // Flow
  flow: 'standard',
};

export default keycloakConfig;

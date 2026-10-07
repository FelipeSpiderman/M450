// One-time sign-in: opens the Google consent page and stores a refresh token in .env.
// Usage: node get-token.mjs
import http from 'node:http';
import { loadEnv, saveEnvValue, requireValue } from './env.mjs';

const PORT = 8765;
const REDIRECT_URI = `http://localhost:${PORT}/callback`;

const SCOPES = [
  'https://www.googleapis.com/auth/googlehealth.activity_and_fitness.readonly',
  'https://www.googleapis.com/auth/googlehealth.health_metrics_and_measurements.readonly',
  'https://www.googleapis.com/auth/googlehealth.sleep.readonly',
  'https://www.googleapis.com/auth/googlehealth.nutrition.readonly',
  'https://www.googleapis.com/auth/googlehealth.ecg.readonly',
  'https://www.googleapis.com/auth/googlehealth.irn.readonly',
];

const env = loadEnv();
const clientId = requireValue(env, 'GOOGLE_CLIENT_ID');
const clientSecret = requireValue(env, 'GOOGLE_CLIENT_SECRET');

const authUrl = new URL('https://accounts.google.com/o/oauth2/v2/auth');
authUrl.search = new URLSearchParams({
  client_id: clientId,
  redirect_uri: REDIRECT_URI,
  response_type: 'code',
  scope: SCOPES.join(' '),
  access_type: 'offline', // needed to get a refresh token
  prompt: 'consent',      // always ask again, otherwise Google may not return a new refresh token
}).toString();

const server = http.createServer(async (req, res) => {
  const url = new URL(req.url, REDIRECT_URI);
  if (url.pathname !== '/callback') {
    res.writeHead(404).end();
    return;
  }

  const error = url.searchParams.get('error');
  const code = url.searchParams.get('code');
  if (error || !code) {
    res.end(`Sign-in failed: ${error ?? 'no code'}. Check the terminal.`);
    console.error('Sign-in failed:', error ?? 'no code returned');
    server.close();
    return;
  }

  const tokenResponse = await fetch('https://oauth2.googleapis.com/token', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: new URLSearchParams({
      code,
      client_id: clientId,
      client_secret: clientSecret,
      redirect_uri: REDIRECT_URI,
      grant_type: 'authorization_code',
    }),
  });
  const tokens = await tokenResponse.json();

  if (!tokenResponse.ok) {
    res.end('Token exchange failed. Check the terminal.');
    console.error('Token exchange failed:', tokens);
  } else if (!tokens.refresh_token) {
    res.end('No refresh token received. Check the terminal.');
    console.error('Google returned no refresh_token. Remove the app at https://myaccount.google.com/permissions and run again.');
  } else {
    saveEnvValue('GOOGLE_REFRESH_TOKEN', tokens.refresh_token);
    res.end('Done! Refresh token saved to .env. You can close this tab.');
    console.log('\nRefresh token saved to .env');
    console.log('Granted scopes:\n  ' + tokens.scope.split(' ').join('\n  '));
    if (tokens.refresh_token_expires_in) {
      const days = Math.round(tokens.refresh_token_expires_in / 86400);
      console.log(`Refresh token expires in ~${days} days (app is in Testing mode). Run this script again after that.`);
    }
    console.log('\nNext: node explore.mjs');
  }
  server.close();
});

server.listen(PORT, () => {
  console.log('Open this URL in your browser and sign in with the Google account of your Fitbit:\n');
  console.log(authUrl.toString());
  console.log(`\nWaiting for the redirect on ${REDIRECT_URI} ...`);
});

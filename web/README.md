# Great VTU Web

Responsive browser interface for Great VTU, alongside the Android application.

## Run locally

1. Install Node.js 20 or later.
2. Copy `.env.example` to `.env.local`.
3. Add the Supabase project URL and public anon key.
4. Run:

```bash
cd web
npm install
npm run dev
```

## Deploy to Vercel

Import this repository into Vercel. The root-level `vercel.json` configures the build to install and build the `web` application and publish `web/dist`.

Add these Environment Variables in Vercel Project Settings:

- `VITE_SUPABASE_URL`
- `VITE_SUPABASE_ANON_KEY`

Use the same Supabase project as the Android app. Never put a Supabase service-role key in the browser.

## Current integration boundary

The web app includes Supabase email sign-in/sign-up and reads the signed-in user's wallet and recent transactions. Airtime, data and bill checkout are intentionally not enabled yet. The Android repository currently records transactions as successful without verified provider fulfilment or a confirmed wallet debit. Connect a VTU provider and implement server-side payment verification, atomic wallet ledger updates, idempotency and refunds before enabling real purchases.

Confirm the Supabase `wallets` and `transactions` table schemas and Row Level Security policies before production use.

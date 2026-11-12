import { createRoot } from 'react-dom/client'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import App from './App.tsx'

import './bootstrap.custom.scss';
import './index.css'
import { ApiError } from './shared/types/error.types.ts';

const queryClient = new QueryClient({
    defaultOptions: {
        queries: {
          staleTime: 30_000,
            retry: (failureCount, err) =>
                err instanceof ApiError && err.status >= 400 && err.status < 500
                    ? false
                    : failureCount < 3,
        },
    },
});

createRoot(document.getElementById('root')!).render(
  <QueryClientProvider client={queryClient}>
    <App />
  </QueryClientProvider>
)

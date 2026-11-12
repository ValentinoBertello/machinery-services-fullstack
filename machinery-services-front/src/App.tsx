import { lazy, Suspense } from 'react';
import { Routes, Route, Navigate, BrowserRouter } from 'react-router';
import { AuthProvider } from './shared/auth/AuthContext.tsx';
import Layout from './shared/components/Layout.tsx';
import RequireAuth from './shared/auth/RequireAuth.tsx';

const LoginPage = lazy(() => import('./shared/LoginPage.tsx'));
const GeneralMaintenancesPage = lazy(() => import('./features/maintenances/pages/GeneralMaintenancesPage.tsx'));
const CustomersPage = lazy(() => import('./features/customers/pages/CustomersPage.tsx'));
const MaintenancesByCustomerPage = lazy(() => import('./features/maintenances/pages/MaintenancesByCustomerPage.tsx'));
const DashboardPage = lazy(() => import('./features/dashboard/pages/DashboardPage.tsx'))

export default function App() {
  return (

    <BrowserRouter>
      <AuthProvider>
        <Suspense fallback={<div className='text-center mt-4'>Loading...</div>}>
          <Routes>
            <Route path='/login' element={<LoginPage />}></Route>
            <Route element={<RequireAuth />} >

              <Route element={<Layout />}>
                <Route index element={<Navigate to="/customers" replace />} />
                <Route path='/customers' element={<CustomersPage />} />
                <Route path='/customers/:id/maintenances' element={<MaintenancesByCustomerPage />} />
                <Route path='/maintenances' element={<GeneralMaintenancesPage />} />
                <Route path='/dashboard' element={<DashboardPage />} />
              </Route>

            </Route>
          </Routes>
        </Suspense>
      </AuthProvider>
    </BrowserRouter>
  );
}

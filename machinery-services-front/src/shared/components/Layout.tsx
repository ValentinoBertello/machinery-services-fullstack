import { Outlet, NavLink } from 'react-router';
import { useAuth } from '../auth/AuthContext';
import { useState } from 'react';
import { Button, Nav, Offcanvas } from 'react-bootstrap';
import Swal from 'sweetalert2';
import 'sweetalert2/dist/sweetalert2.min.css';

export default function Layout() {
    const [showMenu, setShowMenu] = useState(false);
    const { logout } = useAuth();

    const handleLogout = async () => {
        const { isConfirmed } = await Swal.fire({
            title: '¿Estás seguro de Cerrar Sesión?',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Sí',
            cancelButtonText: 'Cancelar',
        });

        if (isConfirmed) {
            logout();
        }
    };

    return (
        <div>
            <button
                className="menu-trigger"
                onClick={() => setShowMenu(true)}
                aria-label="Abrir menú"
            >
                <span />
                <span />
                <span />
            </button>
            <Offcanvas
                show={showMenu}
                onHide={() => setShowMenu(false)}
                placement="start"
            >
                <Offcanvas.Header closeButton>
                    <Offcanvas.Title>Navegación</Offcanvas.Title>
                </Offcanvas.Header>
                <Offcanvas.Body>
                    <Nav className="flex-column">
                        <NavLink to="/customers" className="side-link" onClick={() => setShowMenu(false)}>
                            Clientes
                        </NavLink>
                        <NavLink to="/maintenances" className="side-link" onClick={() => setShowMenu(false)}>
                            Servicios
                        </NavLink>
                        <NavLink to="/dashboard" className="side-link" onClick={() => setShowMenu(false)}>
                            Panel
                        </NavLink>
                    </Nav>

                    <hr />

                    <Button variant="outline-danger" size="sm" onClick={handleLogout}>
                        Cerrar Sesión
                    </Button>
                </Offcanvas.Body>
            </Offcanvas>

            <main>
                {/* Renderiza componentes de rutas hijas */}
                <Outlet />
            </main>

        </div>
    );
}


import { FiChevronLeft, FiChevronRight } from 'react-icons/fi';

type PaginationProps = {
    page: number;
    totalPages: number;
    totalElements: number;
    first: boolean;
    last: boolean;
    onPageChange: (page: number) => void;

    pageSize: number;
    onPageSizeChange: (size: number) => void;
}

const PAGE_SIZE_OPTIONS = [8, 15, 20, 30];

export default function Pagination({
    page,
    totalPages,
    first,
    last,
    onPageChange,
    pageSize,
    onPageSizeChange
}: PaginationProps) {

    if (totalPages == 0) return null;

    return (
        // Wrapper de paginacion y selector de page size
        <div className='pagination-bar d-flex align-items-center gap-2 mt-3 mb-5'>
            <nav className='d-flex align-items-center justify-content-center gap-2 flex-grow-1'>
                <button
                    className='btn btn-sm btn-secondary pagination-btn'
                    disabled={first}
                    onClick={() => onPageChange(0)}
                >
                    Primera
                </button>
                <button
                    className="btn btn-sm btn-secondary pagination-btn"
                    disabled={first}
                    onClick={() => onPageChange(page - 1)}
                >
                    <FiChevronLeft size={18} />
                </button>

                <span
                    className="range-label"
                    style={{ fontSize: '1rem', fontWeight: 500 }}
                >
                    Página {page + 1} de {totalPages}
                </span>

                <button
                    className="btn btn-sm btn-secondary pagination-btn"
                    disabled={last}
                    onClick={() => onPageChange(page + 1)}
                >
                    <FiChevronRight size={18} />
                </button>
                <button
                    className="btn btn-sm btn-secondary pagination-btn"
                    disabled={last}
                    onClick={() => onPageChange(totalPages - 1)}
                >
                    Última
                </button>
            </nav>

            {/* Selector de Cantidad por página */}
            <div className='d-flex align-items-center gap-2'>
                <label htmlFor="pageSize" className="mb-0 text-muted small">
                    Por página
                </label>
                <select
                    id="pageSize"
                    className='form-select form-select-sm'
                    style={{ width: 'auto' }}
                    value={pageSize}
                    onChange={(e) => onPageSizeChange(Number(e.target.value))}
                >
                    {PAGE_SIZE_OPTIONS.map((n) => (
                        <option value={n} key={n}>{n}</option>
                    ))}
                </select>
            </div>
        </div>
    );
}

import { format } from 'date-fns';

export function toStringLocalDate(d: Date | null): string {
    return d ? format(d, 'yyyy-MM-dd') : '';
}

export function toDate(s: string): Date {
    return new Date(`${s.split('T')[0]}T00:00:00`);
}

/**
 * Combina la Date recibida con la hora actual y devuelve un
 * OffsetDateTime ISO con offset -03:00 (Argentina), formato que espera el backend.
 */
export function toStringOffsetDate(d: Date) {
    const now = new Date();
    const merged = new Date(
        d.getFullYear(), d.getMonth(), d.getDate(),
        now.getHours(), now.getMinutes(), now.getSeconds(),
    );
    return format(merged, "yyyy-MM-dd'T'HH:mm:ss") + '-03:00';
}

/**
 * Convierte un ISO UTC del backend (ej. "2026-10-27T19:12:01Z") a fecha
 * dd/MM/yyyy en horario de Argentina, para mostrar en tablas.
 */
export function formatStringDateToArgentinaZone(value?: string | null) {
    if (!value) return '';
    const dateFormatter = new Intl.DateTimeFormat('es-AR', {
        timeZone: 'America/Argentina/Cordoba',
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
    });
    return dateFormatter.format(new Date(value));
}
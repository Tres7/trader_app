/**
 * Send the correct local date to the backend without UTC shift
 */
export function formatDateForApi(date?: Date): string {
  if (!date) {
    return '';
  }

  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');

  return `${year}-${month}-${day}`;
}

/**
 * Parse an API date string (YYYY-MM-DD) into a local Date
 */
export function parseDateFromApi(date?: string): Date | undefined {
  if (!date) {
    return undefined;
  }

  const [year, month, day] = date.split('-').map(Number);

  if (!year || !month || !day) {
    return undefined;
  }

  return new Date(year, month - 1, day);
}

/**
 * Display the date for the user
 */
export function formatDateForDisplay(date?: Date): string {
  if (!date) {
    return '';
  }

  return date.toLocaleDateString('fr-FR');
}
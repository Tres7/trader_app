/**
 * Send the correct date to the backend
 * @param date 
 * @returns 
 */
export function formatDateForApi(date?: Date): string {
  if (!date) {
    return '';
  }

  return date.toISOString().split('T')[0];
}

/**
 * Display the date for the user
 * @param date 
 * @returns 
 */

export function formatDateForDisplay(date?: Date): string {
  if (!date) {
    return '';
  }

  return date.toLocaleDateString('fr-FR');
}
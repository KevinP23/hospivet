function dosDigitos(valor) {
  return String(valor).padStart(2, '0')
}

// Fecha AAAA-MM-DD en la zona horaria del navegador; toISOString usaría UTC.
export function hoyLocal() {
  const ahora = new Date()
  return `${ahora.getFullYear()}-${dosDigitos(ahora.getMonth() + 1)}-${dosDigitos(ahora.getDate())}`
}

// Verdadero si el día del calendario es anterior a hoy en hora local.
export function esDiaPasado(fecha) {
  const inicioDeHoy = new Date()
  inicioDeHoy.setHours(0, 0, 0, 0)
  return fecha.getTime() < inicioDeHoy.getTime()
}

// Verdadero si la fecha (AAAA-MM-DD) y hora (HH:mm) locales son posteriores al momento actual.
export function esFuturo(fecha, hora) {
  return new Date(`${fecha}T${hora}:00`).getTime() > Date.now()
}

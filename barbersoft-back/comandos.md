Antes de correr springboot, abrir la terminal en la ruta del proyecto del back y ejecutar el siguiente bloque:
$env:DB_PASSWORD = "admin123"
$env:ADMIN_NAME = "Administrador"
$env:ADMIN_EMAIL = "administrador@barbersoft.com"
$env:ADMIN_PASSWORD = "admin123"

$bytes = New-Object byte[] 32
[Security.Cryptography.RandomNumberGenerator]::Fill($bytes)
$env:JWT_SECRET = [Convert]::ToBase64String($bytes)

posteriormente ejeuctar: .\mvnw.cmd spring-boot:run
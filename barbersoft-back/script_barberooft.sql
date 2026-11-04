
-- =========================================
-- BARBERSOFT - CREACION DE TABLAS
-- PostgreSQL
-- =========================================

-- 1. USUARIOS
CREATE TABLE usuarios (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. BARBEROS
CREATE TABLE barberos (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido1 VARCHAR(100) NOT NULL,
	apellido2 VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    email VARCHAR(150) UNIQUE,
    foto VARCHAR(255),
    descripcion TEXT,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. SERVICIOS
CREATE TABLE servicios (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio NUMERIC(10,2) NOT NULL CHECK (precio > 0),
    duracion_minutos INTEGER NOT NULL CHECK (duracion_minutos > 0),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. BARBERO_SERVICIO
CREATE TABLE barbero_servicio (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    barbero_id BIGINT NOT NULL,
    servicio_id BIGINT NOT NULL,

    CONSTRAINT fk_barbero_servicio_barbero
        FOREIGN KEY (barbero_id) REFERENCES barberos(id),

    CONSTRAINT fk_barbero_servicio_servicio
        FOREIGN KEY (servicio_id) REFERENCES servicios(id),

    CONSTRAINT uq_barbero_servicio
        UNIQUE (barbero_id, servicio_id)
);

-- 5. HORARIOS_BARBERO
CREATE TABLE horarios_barbero (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    barbero_id BIGINT NOT NULL,
    dia_semana SMALLINT NOT NULL CHECK (dia_semana BETWEEN 1 AND 7),
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_horario_barbero
        FOREIGN KEY (barbero_id) REFERENCES barberos(id),

    CONSTRAINT chk_horario
        CHECK (hora_fin > hora_inicio)
);

-- 6. RESERVAS
CREATE TABLE reservas (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    barbero_id BIGINT NOT NULL,
    nombre_cliente VARCHAR(150) NOT NULL,
    telefono_cliente VARCHAR(20) NOT NULL,
    email_cliente VARCHAR(150),
    fecha_inicio TIMESTAMP NOT NULL,
    fecha_fin TIMESTAMP NOT NULL,
    total NUMERIC(10,2) NOT NULL CHECK (total > 0),
    estado VARCHAR(20) NOT NULL DEFAULT 'CONFIRMADA',
    observaciones TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_reserva_barbero
        FOREIGN KEY (barbero_id) REFERENCES barberos(id),

    CONSTRAINT chk_fechas_reserva
        CHECK (fecha_fin > fecha_inicio),

    CONSTRAINT chk_estado_reserva
        CHECK (estado IN ('CONFIRMADA', 'CANCELADA', 'COMPLETADA'))
);

-- 7. RESERVA_SERVICIO
CREATE TABLE reserva_servicio (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    reserva_id BIGINT NOT NULL,
    servicio_id BIGINT NOT NULL,
    precio NUMERIC(10,2) NOT NULL CHECK (precio > 0),
    duracion_minutos INTEGER NOT NULL CHECK (duracion_minutos > 0),

    CONSTRAINT fk_reserva_servicio_reserva
        FOREIGN KEY (reserva_id) REFERENCES reservas(id),

    CONSTRAINT fk_reserva_servicio_servicio
        FOREIGN KEY (servicio_id) REFERENCES servicios(id),

    CONSTRAINT uq_reserva_servicio
        UNIQUE (reserva_id, servicio_id)
);

-- =========================================
-- FIN DEL SCRIPT
-- =========================================
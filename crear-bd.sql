-- Ejecutar como superusuario (postgres). Se puede repetir sin errores: crea lo que falta y actualiza lo demás.
--   psql -U postgres -h localhost -p 5432 -f crear-bd.sql

-- Crea el usuario (o le actualiza la clave si ya existe)
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'canchas') THEN
        CREATE USER canchas WITH PASSWORD 'canchas';
    ELSE
        ALTER USER canchas WITH PASSWORD 'canchas';
    END IF;
END
$$;

-- Crea la base solo si no existe (CREATE DATABASE no puede ir dentro de un bloque DO)
SELECT 'CREATE DATABASE canchas_db OWNER canchas'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'canchas_db')\gexec

\connect canchas_db

-- Lo hace dueño de la base: así Hibernate puede crear las tablas al arrancar la aplicación
ALTER DATABASE canchas_db OWNER TO canchas;
GRANT ALL PRIVILEGES ON DATABASE canchas_db TO canchas;
-- Desde PostgreSQL 15 el esquema public ya no es escribible por cualquiera: sin esto Hibernate no puede crear las tablas.
GRANT ALL ON SCHEMA public TO canchas;

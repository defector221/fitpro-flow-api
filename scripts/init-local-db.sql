-- Local PostgreSQL setup for IronCore / fitpro-flow-api
-- Run: psql -h localhost -U postgres -d postgres -f scripts/init-local-db.sql

DO $$
BEGIN
  IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'fitpro') THEN
    CREATE ROLE fitpro WITH LOGIN PASSWORD 'fitpro';
  END IF;
END
$$;

SELECT 'CREATE DATABASE fitpro OWNER fitpro'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'fitpro')\gexec

GRANT ALL PRIVILEGES ON DATABASE fitpro TO fitpro;

\c fitpro

GRANT ALL ON SCHEMA public TO fitpro;

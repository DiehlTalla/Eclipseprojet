
-- Supprime tous les triggers du schema

DO
$code$
    DECLARE
        r RECORD;
    BEGIN
        FOR r IN
            SELECT 'DROP TRIGGER ' || trigger_name || ' ON ' || event_object_table AS sql
			FROM information_schema.triggers t
            WHERE trigger_schema = CURRENT_SCHEMA
            GROUP BY event_object_table, trigger_name
            LOOP
                EXECUTE r.sql;
            END LOOP;
    END;
$code$;


-- Supprime toutes les fonctions du schema

DO $code$
DECLARE 
	r RECORD;
BEGIN
	FOR r IN
		SELECT 'DROP FUNCTION ' || ns.nspname || '.' || proname 
	       || '(' || oidvectortypes(proargtypes) || ')' AS sql
		FROM pg_proc INNER JOIN pg_namespace ns ON (pg_proc.pronamespace = ns.oid)
		WHERE ns.nspname = current_schema  
	LOOP
		EXECUTE r.sql;
	END LOOP;
END;
$code$;


-- Fonctions



-- Triggers







USE diccionario_lengua_materna;

-- =====================================================
-- TRIGGER 1:
-- Normaliza y valida el nombre de una lengua
-- antes de INSERTARLA.
-- =====================================================

DROP TRIGGER IF EXISTS trg_lenguas_before_insert;

DELIMITER //

CREATE TRIGGER trg_lenguas_before_insert
BEFORE INSERT ON lenguas
FOR EACH ROW
BEGIN

    -- Elimina espacios al inicio y al final
    SET NEW.nombre = TRIM(NEW.nombre);

    -- También limpia la familia lingüística si fue enviada
    IF NEW.familia_linguistica IS NOT NULL THEN
        SET NEW.familia_linguistica =
            TRIM(NEW.familia_linguistica);
    END IF;

    -- Impide guardar un nombre vacío o solamente espacios
    IF NEW.nombre IS NULL
       OR CHAR_LENGTH(NEW.nombre) = 0 THEN

        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT =
            'El nombre de la lengua no puede estar vacío';

    END IF;

END //

DELIMITER ;


-- =====================================================
-- TRIGGER 2:
-- Aplica la misma validación cuando se ACTUALIZA
-- una lengua existente.
-- =====================================================

DROP TRIGGER IF EXISTS trg_lenguas_before_update;

DELIMITER //

CREATE TRIGGER trg_lenguas_before_update
BEFORE UPDATE ON lenguas
FOR EACH ROW
BEGIN

    SET NEW.nombre = TRIM(NEW.nombre);

    IF NEW.familia_linguistica IS NOT NULL THEN
        SET NEW.familia_linguistica =
            TRIM(NEW.familia_linguistica);
    END IF;

    IF NEW.nombre IS NULL
       OR CHAR_LENGTH(NEW.nombre) = 0 THEN

        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT =
            'El nombre de la lengua no puede estar vacío';

    END IF;

END //

DELIMITER ;
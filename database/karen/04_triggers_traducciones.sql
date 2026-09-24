USE diccionario_lengua_materna;

-- =====================================================
-- TRIGGER 1:
-- Limpia y valida la traducción antes de INSERT.
-- =====================================================

DROP TRIGGER IF EXISTS trg_traducciones_before_insert;

DELIMITER $$

CREATE TRIGGER trg_traducciones_before_insert
BEFORE INSERT ON traducciones
FOR EACH ROW
BEGIN

    SET NEW.traduccion = TRIM(NEW.traduccion);

    IF NEW.traduccion IS NULL
       OR CHAR_LENGTH(NEW.traduccion) = 0 THEN

        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT =
            'La traduccion no puede estar vacia';

    END IF;

END$$

DELIMITER ;


-- =====================================================
-- TRIGGER 2:
-- Limpia y valida la traducción antes de UPDATE.
-- =====================================================

DROP TRIGGER IF EXISTS trg_traducciones_before_update;

DELIMITER $$

CREATE TRIGGER trg_traducciones_before_update
BEFORE UPDATE ON traducciones
FOR EACH ROW
BEGIN

    SET NEW.traduccion = TRIM(NEW.traduccion);

    IF NEW.traduccion IS NULL
       OR CHAR_LENGTH(NEW.traduccion) = 0 THEN

        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT =
            'La traduccion no puede estar vacia';

    END IF;

END$$

DELIMITER ;

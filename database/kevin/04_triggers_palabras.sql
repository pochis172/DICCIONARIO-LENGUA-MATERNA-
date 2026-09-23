DROP TRIGGER IF EXISTS trg_palabras_before_insert;

DELIMITER $$

CREATE TRIGGER trg_palabras_before_insert
BEFORE INSERT ON palabras
FOR EACH ROW
BEGIN
    SET NEW.palabra_espanol = TRIM(NEW.palabra_espanol);

    IF NEW.palabra_espanol IS NULL
       OR NEW.palabra_espanol = '' THEN

        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'palabra_espanol no puede estar vacia';

    END IF;
END$$

DELIMITER ;

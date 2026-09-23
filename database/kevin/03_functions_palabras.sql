DROP FUNCTION IF EXISTS fn_total_palabras_lengua;

DELIMITER $$

CREATE FUNCTION fn_total_palabras_lengua(
    p_lengua_id BIGINT
)
RETURNS BIGINT
READS SQL DATA
BEGIN
    DECLARE v_total BIGINT;

    SELECT COUNT(*)
    INTO v_total
    FROM palabras
    WHERE id_lengua = p_lengua_id;

    RETURN v_total;
END$$

DELIMITER ;

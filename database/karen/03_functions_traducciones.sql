USE diccionario_lengua_materna;

-- =====================================================
-- FUNCIÓN:
-- Cuenta cuántas traducciones tiene una lengua.
-- Recibe el ID de la lengua y devuelve un número.
-- =====================================================

DROP FUNCTION IF EXISTS fn_total_traducciones_lengua;

DELIMITER $$

CREATE FUNCTION fn_total_traducciones_lengua(
    p_lengua_id BIGINT
)
RETURNS BIGINT
READS SQL DATA
BEGIN

    DECLARE v_total BIGINT;

    SELECT COUNT(*)
    INTO v_total
    FROM traducciones
    WHERE id_lengua = p_lengua_id;

    RETURN v_total;

END$$

DELIMITER ;

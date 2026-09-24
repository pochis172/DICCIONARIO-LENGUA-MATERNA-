USE diccionario_lengua_materna;

-- =====================================================
-- FUNCIÓN: Total de lenguas registradas en una región
-- Recibe el ID de una región y devuelve la cantidad
-- de lenguas asociadas a ella.
-- =====================================================

DROP FUNCTION IF EXISTS fn_total_lenguas_region;

DELIMITER //

CREATE FUNCTION fn_total_lenguas_region(
    p_region_id BIGINT
)
RETURNS BIGINT
READS SQL DATA
BEGIN
    DECLARE v_total BIGINT;

    SELECT COUNT(*)
    INTO v_total
    FROM lenguas
    WHERE id_region = p_region_id;

    RETURN v_total;
END //

DELIMITER ;
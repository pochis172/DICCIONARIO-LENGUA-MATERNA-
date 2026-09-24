USE diccionario_lengua_materna;

-- =====================================================
-- PROCEDIMIENTO:
-- Consulta las traducciones de una lengua específica.
-- Recibe como parámetro el ID de la lengua.
-- =====================================================

DROP PROCEDURE IF EXISTS sp_traducciones_por_lengua;

DELIMITER $$

CREATE PROCEDURE sp_traducciones_por_lengua(
    IN p_lengua_id BIGINT
)
BEGIN

    SELECT
        t.id_traduccion,
        p.palabra_espanol,
        l.nombre AS lengua,
        t.traduccion
    FROM traducciones AS t
    INNER JOIN palabras AS p
        ON t.id_palabra = p.id_palabra
    INNER JOIN lenguas AS l
        ON t.id_lengua = l.id_lengua
    WHERE t.id_lengua = p_lengua_id
    ORDER BY p.palabra_espanol ASC;

END$$

DELIMITER ;

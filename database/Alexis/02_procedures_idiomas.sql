USE diccionario_lengua_materna;

-- =====================================================
-- PROCEDIMIENTO: Consultar lenguas por región
-- Recibe el ID de una región y devuelve las lenguas
-- registradas en ella.
-- =====================================================

DROP PROCEDURE IF EXISTS sp_lenguas_por_region;

CREATE PROCEDURE sp_lenguas_por_region(
    IN p_region_id BIGINT
)
SELECT
    l.id_lengua,
    l.nombre AS lengua,
    l.familia_linguistica,
    r.id_region,
    r.nombre_region AS region
FROM lenguas AS l
INNER JOIN regiones AS r
    ON l.id_region = r.id_region
WHERE r.id_region = p_region_id
ORDER BY l.nombre;
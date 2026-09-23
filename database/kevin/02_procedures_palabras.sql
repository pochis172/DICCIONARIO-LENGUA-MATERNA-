DROP PROCEDURE IF EXISTS sp_palabras_por_categoria;

DELIMITER $$

CREATE PROCEDURE sp_palabras_por_categoria(
    IN p_categoria VARCHAR(80)
)
BEGIN
    SELECT
        p.id_palabra,
        p.palabra_espanol,
        p.categoria,
        p.significado,
        p.id_lengua,
        l.nombre AS lengua
    FROM palabras p
    INNER JOIN lenguas l
        ON l.id_lengua = p.id_lengua
    WHERE p.categoria = TRIM(p_categoria)
    ORDER BY p.palabra_espanol ASC;
END$$

DELIMITER ;

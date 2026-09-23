DROP VIEW IF EXISTS vw_palabras_con_contenido;

CREATE VIEW vw_palabras_con_contenido AS
SELECT
    p.id_palabra,
    p.palabra_espanol,
    p.categoria,
    p.significado,
    p.id_lengua,
    l.nombre AS lengua,
    COUNT(DISTINCT t.id_traduccion) AS total_traducciones,
    COUNT(DISTINCT e.id_ejemplo) AS total_ejemplos,
    COUNT(DISTINCT a.id_audio) AS total_audios
FROM palabras p
INNER JOIN lenguas l
    ON l.id_lengua = p.id_lengua
LEFT JOIN traducciones t
    ON t.id_palabra = p.id_palabra
LEFT JOIN ejemplos e
    ON e.id_palabra = p.id_palabra
LEFT JOIN audios a
    ON a.id_palabra = p.id_palabra
GROUP BY
    p.id_palabra,
    p.palabra_espanol,
    p.categoria,
    p.significado,
    p.id_lengua,
    l.nombre;

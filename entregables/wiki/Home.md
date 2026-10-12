# Taller de Integración Continua con GitHub Actions

**Curso:** Diseño y Arquitectura de Software – Ingeniería Informática, Universidad de La Sabana (2026-2)  
**Repositorio:** fork de [CesarAVegaF312/DAYS-Integracion_continua](https://github.com/CesarAVegaF312/DAYS-Integracion_continua)

## Integrante

| Nombre | Usuario GitHub | Correo institucional |
|---|---|---|
| Ethian White | [EthianWhor](https://github.com/EthianWhor) | ethianwhor@unisabana.edu.co |

## Propósito del taller

Configurar, ejecutar e interpretar un pipeline de **Integración Continua** que, en cada commit, compile el proyecto
`calculadora-notas` (Java 17 + Maven), ejecute las pruebas JUnit, mida la cobertura con JaCoCo y **bloquee el cambio**
si alguna prueba falla o si la cobertura de líneas baja del **80 %**. Además, proteger la rama `main` para que ningún
cambio que rompa el build llegue a ella.

## Resultado final

| Indicador | Valor |
|---|---|
| Pruebas | **26** (11 `CalculadoraNotasTest` + 5 `NotaNecesariaTest` + 10 `ConceptoTest`), 0 fallos |
| Cobertura de líneas | **100 %** (31 de 31) — umbral exigido 80 % |
| Cobertura de ramas | **100 %** (14 de 14) |
| `mvn verify` en `main` | `BUILD SUCCESS` |

## Contenido del Wiki

1. [El pipeline](El-pipeline) – qué hace cada bloque del `ci.yml`.
2. [Evidencias](Evidencias) – ejecuciones en verde y en rojo de los pasos 2, 3, 4 y 5, con el log del error.
3. [Cobertura](Cobertura) – reporte JaCoCo antes y después de escribir las pruebas que faltaban.
4. [Rama protegida](Rama-protegida) – ruleset `proteger-main` y Pull Request bloqueado.
5. [Reflexión final](Reflexion-final).

## Historial del taller (commits)

| Paso | Commit | Mensaje | Resultado esperado del pipeline |
|---|---|---|---|
| 2 | `e96e674` | Primer pipeline: agrega integrantes del equipo | 🟢 verde |
| 3 – Red | `e8f63ca` | TDD (Red): prueba de la nota necesaria… sin implementación | 🔴 rojo por **compilación** |
| 3 – Red (variante) | `15c0464` | TDD (Red, variante): método vacío… | 🔴 rojo por **pruebas fallidas** |
| 3 – Green | `29c1177` | TDD (Green): implementa `notaNecesariaTercerCorte` | 🟢 verde |
| 4 – Red | `4833d32` | Agrega `concepto()` sin pruebas | 🔴 rojo por **cobertura** (0.74 < 0.80) |
| 4 – Green | `32b25d8` | Agrega pruebas de `concepto()` con valores frontera | 🟢 verde (100 %) |
| 5 | `7c16737` (rama `prueba-rama-protegida`) | Rompe una prueba a propósito | 🔴 rojo → PR bloqueado |

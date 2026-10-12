# Entregables – Taller de Integración Continua

**Integrante:** Ethian White (GitHub [EthianWhor](https://github.com/EthianWhor)) – ethianwhor@unisabana.edu.co

**Repositorio publicado:** https://github.com/EthianWhor/DAYS-Integracion_continua · **Wiki:** https://github.com/EthianWhor/DAYS-Integracion_continua/wiki
**Fecha de ejecución:** 2026-10-11 · JDK Temurin 17.0.20.1 · Maven 3.9.9

## Contenido

```text
entregables/
├─ README.md                      # este índice
├─ integrantes.txt                # copia del integrantes.txt de la raíz
├─ wiki/                          # Wiki completo, listo para publicar (documentación oficial del taller)
│   ├─ Home.md                    #   Inicio: integrantes, propósito, historial de commits
│   ├─ El-pipeline.md             #   explicación de cada bloque del ci.yml
│   ├─ Evidencias.md              #   ejecuciones verde/rojo de los pasos 2, 3, 4 y 5 con sus logs
│   ├─ Cobertura.md               #   reporte JaCoCo antes y después
│   ├─ Rama-protegida.md          #   ruleset proteger-main y PR bloqueado
│   ├─ Reflexion-final.md         #   respuestas a las 4 preguntas + aprendizajes
│   ├─ _Sidebar.md
│   └─ images/                    #   20 capturas (logs de cada paso, reportes JaCoCo y evidencias de GitHub)
├─ evidencias/logs/               # log completo de `mvn -B clean verify` de cada commit
├─ reportes/
│   ├─ jacoco-rojo-paso4/         # reporte HTML de cobertura con concepto() sin pruebas (74 %)
│   ├─ jacoco-verde/              # reporte HTML final (100 %)
│   └─ surefire/                  # reportes de las 26 pruebas
└─ github/
    ├─ PUBLICAR-EN-GITHUB.md      # cómo subir todo al fork (Actions, ruleset, PR, Wiki)
    ├─ publicar-en-github.ps1     # script que lo hace automáticamente con GitHub CLI
    └─ ruleset-proteger-main.json # ruleset importable para proteger main
```

## Estado frente a lo que pide el taller

| # | Entregable | Estado | Evidencia |
|---|---|---|---|
| 1 | Repositorio con `integrantes.txt` y `mvn verify` en verde | ✅ Hecho | [`integrantes.txt`](../integrantes.txt), [log final](evidencias/logs/06-paso4-verde-cobertura.log) |
| 2 | Documentación en Wiki | ✅ Publicado en [el Wiki del fork](https://github.com/EthianWhor/DAYS-Integracion_continua/wiki) | [`wiki/`](wiki/Home.md) |
| 3 | Pipeline de CI corriendo en push y PR | ✅ [Ejecuciones en Actions](https://github.com/EthianWhor/DAYS-Integracion_continua/actions) (push y pull_request) | [El pipeline](wiki/El-pipeline.md) |
| 4 | TDD en el pipeline (commit rojo → commit verde) | ✅ `e8f63ca` 🔴 → `15c0464` 🔴 → `29c1177` 🟢 | [Evidencias](wiki/Evidencias.md#paso-3--tdd-en-el-pipeline) |
| 5 | Rojo por cobertura y recuperación | ✅ `4833d32` 🔴 0.74 → `32b25d8` 🟢 1.00 | [Cobertura](wiki/Cobertura.md) |
| 6 | Rama protegida y PR bloqueado | ✅ ruleset `proteger-main` activo · [PR #1](https://github.com/EthianWhor/DAYS-Integracion_continua/pull/1) bloqueado | [Rama protegida](wiki/Rama-protegida.md) |
| 7 | Reflexión final | ✅ Hecho | [Reflexión final](wiki/Reflexion-final.md) |
| – | Reto opcional: badge del pipeline | ✅ en el README raíz (activo en el fork) | [`README.md`](../README.md) |


## Resultados

| Commit | Paso | `mvn verify` | Detalle |
|---|---|---|---|
| `e96e674` | 2 | 🟢 SUCCESS | 11 pruebas, cobertura OK |
| `e8f63ca` | 3 Red | 🔴 FAILURE | `cannot find symbol notaNecesariaTercerCorte(double,double)` |
| `15c0464` | 3 Red (variante) | 🔴 FAILURE | 16 pruebas, 4 fallos (`expected: <3.0> but was: <0.0>`) |
| `29c1177` | 3 Green | 🟢 SUCCESS | 16 pruebas, 0 fallos |
| `4833d32` | 4 Red | 🔴 FAILURE | `lines covered ratio is 0.74, but expected minimum is 0.80` |
| `32b25d8` | 4 Green | 🟢 SUCCESS | 26 pruebas, 100 % líneas y ramas |
| `7c16737` | 5 (rama `prueba-rama-protegida`) | 🔴 FAILURE | `expected: <4.9> but was: <5.0>` |

## Cómo reproducir

```powershell
mvn -B clean verify                       # main: BUILD SUCCESS, 26 pruebas, 100 %
git switch prueba-rama-protegida; mvn -B clean verify; git switch main   # BUILD FAILURE
git checkout 4833d32; mvn -B clean verify; git switch main               # rojo por cobertura
```

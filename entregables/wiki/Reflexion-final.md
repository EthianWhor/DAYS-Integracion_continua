# Reflexión final

## 1. ¿Qué diferencia hay entre un pipeline en rojo por compilación, por pruebas fallidas y por cobertura?

Los tres dejan el pipeline en rojo, pero fallan en **fases distintas** de `mvn verify` y dicen cosas distintas:

| | Compilación (`e8f63ca`) | Pruebas fallidas (`15c0464`) | Cobertura (`4833d32`) |
|---|---|---|---|
| Fase donde falla | `testCompile` | `test` (Surefire) | `verify` (`jacoco:check`) |
| ¿Se ejecutan las pruebas? | **No, ninguna** (ni las que ya pasaban) | Sí, las 16 | Sí, las 16, y **todas pasan** |
| ¿Hay reporte de cobertura? | No | Sí | Sí |
| Mensaje | `cannot find symbol` | `expected: <3.0> but was: <0.0>` | `lines covered ratio is 0.74, but expected minimum is 0.80` |
| Qué significa | El código ni siquiera es coherente: falta un método o hay un error de sintaxis. | El código hace algo, pero **no lo que se especificó**: hay un defecto de comportamiento. | El código hace lo que se probó, pero **hay código que nadie probó**: riesgo, no necesariamente defecto. |
| Tiempo del build | 2.1 s | 4.8 s | 6.1 s |

El orden también importa: un error de compilación **oculta** los demás (no se sabe si las pruebas pasarían ni cuánta
cobertura hay). Por eso en TDD conviene la variante con el método vacío: el log de *pruebas fallidas* ya describe el
comportamiento esperado caso por caso.

## 2. ¿Qué garantiza y qué no garantiza un 80 % de cobertura?

**Garantiza** que al menos el 80 % de las líneas se **ejecutó** durante las pruebas, y que no puede entrar a `main` un
bloque grande de código nuevo sin ninguna prueba (como pasó con `concepto()`, que bajó la cobertura a 74 %).

**No garantiza:**

- **Que las pruebas verifiquen algo.** JaCoCo mide ejecución, no aserciones. `calculadora.concepto(4.0);` sin `assert`
  también cubre líneas. Lo comprobamos: el caso `5.0, 5.0 -> 0.0` pasaba con `return 0;`.
- **Que se prueben las fronteras.** Probar `concepto(4.2)` cubre la misma línea que `concepto(4.0)`, pero solo el
  segundo detecta un `>=` cambiado por `>`. Herramientas como *mutation testing* (PIT) sí miden eso.
- **Que el 20 % restante sea irrelevante.** Ese 20 % puede ser justamente el manejo de errores más crítico.
- **Ausencia de defectos de integración, rendimiento o seguridad**: son pruebas unitarias de una clase.
- **Que el contador elegido sea el adecuado.** En el paso 4 la cobertura de instrucciones era 80,7 % y la de ramas 57 %;
  con la regla sobre líneas (74 %) el build falló, con una regla sobre instrucciones habría pasado.

Conclusión: la cobertura es una **condición necesaria pero no suficiente**; sirve como alarma automática, y la calidad de
las pruebas se asegura en la revisión del Pull Request.

## 3. ¿Por qué el umbral de cobertura se define en el `pom.xml` y no en el YAML?

- **Mismo resultado en todas partes:** `mvn verify` falla igual en el portátil de cada integrante y en el runner. Todas
  las evidencias de este Wiki se generaron localmente con el mismo comando y coinciden con el pipeline. Si el umbral
  estuviera solo en el YAML, se descubriría el problema después del push.
- **Independencia del proveedor de CI:** el mismo `pom.xml` funciona con GitHub Actions y con GitLab CI
  (`gitlab/.gitlab-ci.yml` solo ejecuta `mvn verify`). El pipeline es un "ejecutor", la regla de calidad es del proyecto.
- **Versionado junto al código:** cambiar el umbral queda en el historial y en el PR, visible y revisable.
- **El YAML se mantiene simple:** solo orquesta (checkout, JDK, caché, comando, artefactos).

## 4. ¿Qué tendrían que agregarle a este pipeline para cumplir la entrega 3 del proyecto?

Según el checklist del Proyecto 3 y la evolución DevOps del curso:

1. **Pruebas de integración del Corte 2** en el pipeline (por ejemplo con `maven-failsafe-plugin` en `integration-test`
   y, si hay base de datos, *service containers* o Testcontainers).
2. **Umbral de cobertura del dominio ≥ 70 %**, aplicado al paquete de dominio (regla JaCoCo por `PACKAGE` con
   `includes`), no a todo el proyecto.
3. **Badge** de estado del pipeline en el README.
4. **Ruleset de `main`** con PR obligatorio, **1 aprobación de otro integrante** y checks en verde.
5. **Caché de dependencias** (ya está con `cache: maven`) y control de que el pipeline tarde < 10 minutos.
6. **Construcción de la imagen Docker** en un job que dependa (`needs:`) del job de pruebas.
7. **Escaneos de seguridad (DevSecOps):** dependencias (OWASP Dependency-Check o Dependabot), análisis estático
   (CodeQL/SonarCloud), imagen (Trivy) y secretos (Gitleaks).
8. **Despliegue continuo (CD)** a un entorno, con *environments* y secretos de GitHub, solo desde `main`.
9. Opcional: **matriz** Java 17/21 para detectar incompatibilidades.

## Aprendizajes

- Ver el ciclo **Red → Green** en el historial hace visible TDD para todo el equipo: el commit rojo documenta el
  requisito antes del código.
- Un pipeline útil no solo dice "falló": publica reportes **aun cuando falla** (`if: always()`), porque es cuando más se
  necesitan.
- La rama protegida convierte las buenas prácticas en reglas: ya no depende de la disciplina de cada integrante.

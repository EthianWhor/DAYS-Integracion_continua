# El pipeline (`.github/workflows/ci.yml`)

El pipeline tiene **un solo job** (`build-y-pruebas`, que GitHub muestra como **"Build, pruebas y cobertura"**) con cinco pasos.
Ese nombre es importante: es el *status check* que exige el ruleset de la rama `main`.

## 1. Cuándo corre — `on:`

```yaml
on:
  push:
  pull_request:
    branches: [main]
  workflow_dispatch:
```

- `push` sin filtro de ramas: **cualquier** push a **cualquier** rama dispara el pipeline. Así, incluso el trabajo en
  ramas de característica recibe retroalimentación antes de abrir el PR.
- `pull_request` hacia `main`: cada PR se valida con el código resultante de la integración (el *merge commit* de prueba
  que GitHub arma), que es lo que realmente llegaría a `main`.
- `workflow_dispatch`: botón **Run workflow** en la pestaña Actions para relanzarlo a mano (útil para reintentar sin
  hacer un commit vacío).

## 2. Con qué permisos — `permissions: contents: read`

El `GITHUB_TOKEN` que recibe el job solo puede **leer** el código. Si una dependencia o una action se viera comprometida,
no podría escribir en el repositorio, crear releases ni modificar issues. Es el principio de **mínimo privilegio**.

## 3. Dónde corre — `runs-on: ubuntu-latest`

Cada ejecución arranca en una **máquina virtual nueva y limpia**. Nada de una corrida anterior queda instalado, por eso
el pipeline es reproducible: si pasa, no es porque "en mi máquina" había algo instalado a mano.

## 4. Los pasos

| # | Paso | Qué hace y por qué |
|---|---|---|
| 1 | **Descargar el código** – `actions/checkout@v7` | Clona el commit exacto que disparó el pipeline (`GITHUB_SHA`). Sin esto, el runner está vacío. |
| 2 | **Instalar Java 17** – `actions/setup-java@v6` con `distribution: temurin`, `java-version: '17'`, `cache: maven` | Instala el JDK que pide el `pom.xml` (`maven.compiler.release=17`). `cache: maven` guarda `~/.m2/repository` usando como llave el hash del `pom.xml`: mientras el `pom.xml` no cambie, las dependencias no se vuelven a descargar y el build es más rápido. |
| 3 | **Compilar, probar y verificar cobertura** – `mvn -B verify` | Recorre el ciclo de vida de Maven hasta `verify`: `prepare-agent` de JaCoCo → `compile` → `testCompile` → `test` (Surefire + JUnit) → `package` → `jacoco:report` → **`jacoco:check`**. Si falla la compilación, una prueba o el umbral de cobertura, Maven termina con código ≠ 0, el paso falla y el pipeline queda en **rojo**. `-B` (*batch mode*) evita salidas interactivas y colores ANSI en el log. |
| 4 | **Resumen de cobertura** – `if: always()` | Lee `target/site/jacoco/jacoco.csv`, suma las columnas `LINE_MISSED` (8) y `LINE_COVERED` (9) con `awk` y escribe el porcentaje en `$GITHUB_STEP_SUMMARY` (el resumen de la ejecución). Con `always()` corre aunque el paso anterior haya fallado; si no existe el CSV (falló la compilación) lo dice explícitamente. |
| 5 | **Publicar reportes** – `actions/upload-artifact@v7`, `if: always()` | Sube `target/surefire-reports/` y `target/site/jacoco/` como el artefacto **`reportes`**. Gracias a `always()` los reportes también quedan disponibles cuando el build **falla**, que es justo cuando más se necesitan. `if-no-files-found: ignore` evita un segundo error si no se generó nada. |

Salida del paso 4 con el código final del taller (calculada localmente con el mismo `awk` del workflow):

```text
### Cobertura de líneas: 100.0% (31 de 31)
```

## 5. Dónde vive el umbral

El 80 % **no** está en el YAML sino en el `pom.xml`:

```xml
<jacoco.cobertura.minima>0.80</jacoco.cobertura.minima>
...
<execution>
  <id>verificar-umbral</id>
  <phase>verify</phase>
  <goals><goal>check</goal></goals>
  <configuration>
    <rules><rule>
      <element>BUNDLE</element>
      <limits><limit>
        <counter>LINE</counter>
        <value>COVEREDRATIO</value>
        <minimum>${jacoco.cobertura.minima}</minimum>
      </limit></limits>
    </rule></rules>
  </configuration>
</execution>
```

Por eso `mvn verify` da **exactamente el mismo resultado** en el portátil de cualquier integrante y en el runner de
GitHub. Todas las evidencias de este Wiki se obtuvieron con ese mismo comando (`mvn -B clean verify`, JDK Temurin
17.0.20.1, Maven 3.9.9).

## 6. Flujo completo

```text
git push ──► GitHub Actions ──► runner ubuntu-latest limpio
                                   │
                                   ├─ checkout del commit
                                   ├─ JDK 17 + caché ~/.m2
                                   ├─ mvn -B verify ──┬─ compila ─────────── ✗ → rojo (compilación)
                                   │                  ├─ pruebas JUnit ───── ✗ → rojo (pruebas)
                                   │                  └─ jacoco:check ≥ 80 % ✗ → rojo (cobertura)
                                   ├─ resumen de cobertura   (siempre)
                                   └─ artefacto "reportes"   (siempre)
                                   ▼
                     check "Build, pruebas y cobertura" ✓/✗ ──► ruleset de main permite / bloquea el merge
```

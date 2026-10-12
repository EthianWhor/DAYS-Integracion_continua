# Publicar el taller en GitHub

Todo el trabajo del taller está hecho y verificado en el repositorio local (commits de los pasos 2 a 5, logs, reportes y
Wiki). Lo que falta solo se puede hacer **con tu cuenta de GitHub**: crear el fork, ver las ejecuciones en Actions,
crear el ruleset, abrir el PR bloqueado y publicar el Wiki.

## Opción A — automática (recomendada)

```powershell
winget install GitHub.cli
gh auth login
powershell -ExecutionPolicy Bypass -File entregables\github\publicar-en-github.ps1
```

El script [`publicar-en-github.ps1`](publicar-en-github.ps1):

1. Crea el fork `EthianWhor/DAYS-Integracion_continua` y lo agrega como remoto `fork` (no toca el remoto `origin` del profesor).
2. Habilita Actions y el Wiki en el fork.
3. Sube **un commit a la vez** a `main` y espera cada ejecución, para que Actions muestre la secuencia
   verde → rojo (compilación) → rojo (pruebas) → verde → rojo (cobertura) → verde → verde.
4. Crea el ruleset `proteger-main` desde [`ruleset-proteger-main.json`](ruleset-proteger-main.json).
5. Sube la rama `prueba-rama-protegida` y abre el PR **dentro del fork** (`--repo EthianWhor/...`).
6. Publica `entregables/wiki/` en el Wiki. GitHub solo crea el repositorio del Wiki después de la primera página: si el
   script lo indica, crea una página cualquiera en el navegador y ejecuta de nuevo con `-SoloWiki`.

## Opción B — manual

1. **Fork** en GitHub y, en el fork, **Actions → I understand my workflows, go ahead and enable them**.
2. `git remote add fork https://github.com/EthianWhor/DAYS-Integracion_continua.git`
3. Por cada commit de `git log --reverse --format=%h 08a80c9..main`: `git push fork <hash>:refs/heads/main` y espera
   a que termine la ejecución en Actions.
4. **Settings → Rules → Rulesets → New ruleset → Import a ruleset** → `ruleset-proteger-main.json`
   (o crearlo a mano como se describe en la página *Rama protegida* del Wiki).
5. `git push fork prueba-rama-protegida` y abre el PR hacia `main` **del fork** (cuidado: GitHub propone por defecto el
   repositorio del profesor como base).
6. **Wiki → Create the first page**, luego `git clone https://github.com/EthianWhor/DAYS-Integracion_continua.wiki.git`,
   copia el contenido de `entregables/wiki/` y haz push.

## Capturas que debes tomar en GitHub

| Archivo (en `images/` del Wiki) | Dónde |
|---|---|
| `gh-01-lista-actions.png` | Pestaña Actions con todas las ejecuciones. |
| `gh-02-resumen-cobertura.png` | Una ejecución verde: resumen *Cobertura de líneas: 100.0%* y artefacto `reportes`. |
| `gh-03-log-compilacion.png` | Ejecución de `e8f63ca`, paso *Compilar, probar y verificar cobertura*. |
| `gh-04-log-cobertura.png` | Ejecución de `4833d32`, línea `Rule violated ... 0.74`. |
| `gh-05-ruleset.png` | Settings → Rules → `proteger-main`. |
| `gh-06-pr-bloqueado.png` | El PR con *Merging is blocked*. |

Luego agrégalas con `![](images/gh-0X-....png)` en las secciones marcadas de *Evidencias* y *Rama protegida*.

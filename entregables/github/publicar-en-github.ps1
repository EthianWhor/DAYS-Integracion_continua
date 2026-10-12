# Publica el taller en un fork propio de GitHub: fork, Actions, un push por commit (una ejecución por paso),
# ruleset de main, Pull Request bloqueado y Wiki.
# Requisitos: GitHub CLI (winget install GitHub.cli) y sesión iniciada (gh auth login).
# Uso, desde la raíz del repositorio:
#   powershell -ExecutionPolicy Bypass -File entregables\github\publicar-en-github.ps1
#   powershell -ExecutionPolicy Bypass -File entregables\github\publicar-en-github.ps1 -SoloWiki
param(
    [string]$Usuario = "EthianWhor",
    [switch]$SoloWiki
)
$ErrorActionPreference = "Stop"
$upstream = "CesarAVegaF312/DAYS-Integracion_continua"
$repo = "$Usuario/DAYS-Integracion_continua"
$baseTaller = "08a80c9"   # último commit del enunciado; todo lo posterior es el trabajo del taller

gh auth status
if ($LASTEXITCODE -ne 0) { throw "Inicia sesión primero con: gh auth login" }

function Publicar-Wiki {
    $tmp = Join-Path $env:TEMP "wiki-dyas-ci"
    if (Test-Path $tmp) { Remove-Item -Recurse -Force $tmp }
    git clone "https://github.com/$repo.wiki.git" $tmp
    if ($LASTEXITCODE -ne 0) {
        Write-Warning "El Wiki aún no existe. Abre https://github.com/$repo/wiki, pulsa 'Create the first page', guarda, y vuelve a correr este script con -SoloWiki."
        Start-Process "https://github.com/$repo/wiki"
        return
    }
    Copy-Item -Recurse -Force "entregables\wiki\*" $tmp
    git -C $tmp add -A
    git -C $tmp commit -m "Wiki del taller de Integración Continua"
    git -C $tmp push origin HEAD
    Start-Process "https://github.com/$repo/wiki"
}

if ($SoloWiki) { Publicar-Wiki; exit }

# 1. Fork (sin clonar: este repositorio local ya tiene los commits) y remoto "fork".
gh repo fork $upstream --clone=false --default-branch-only
if (-not (git remote | Select-String -Quiet "^fork$")) { git remote add fork "https://github.com/$repo.git" }

# 2. En los forks, Actions viene deshabilitado; se habilita junto con el Wiki.
gh api -X PUT "repos/$repo/actions/permissions" -F enabled=true -f allowed_actions=all
gh repo edit $repo --enable-wiki
try { gh workflow enable ci.yml --repo $repo } catch { Write-Warning "Habilita el workflow a mano en https://github.com/$repo/actions" }

# 3. Un push por commit, en orden, para que la pestaña Actions muestre una ejecución por paso (verde/rojo/verde...).
foreach ($c in git rev-list --reverse "$baseTaller..main") {
    Write-Host "`n=== $(git log -1 --format='%h %s' $c)" -ForegroundColor Cyan
    git push fork "${c}:refs/heads/main"
    if ($LASTEXITCODE -ne 0) { throw "No se pudo hacer push de $c (¿el main del fork tiene commits que no están en local?)" }
    $run = $null
    for ($i = 0; $i -lt 30 -and -not $run; $i++) {
        Start-Sleep -Seconds 5
        $run = gh run list --repo $repo --commit $c --limit 1 --json databaseId -q ".[0].databaseId"
    }
    if ($run) { gh run watch $run --repo $repo --exit-status; Write-Host "Resultado: código $LASTEXITCODE (rojo esperado en los pasos Red)" }
    else { Write-Warning "No apareció la ejecución de $c; revisa que Actions esté habilitado en el fork." }
}

# 4. Ruleset de main (el check ya existe porque el pipeline corrió).
gh api -X POST "repos/$repo/rulesets" --input "entregables\github\ruleset-proteger-main.json"

# 5. Rama con la prueba rota y Pull Request DENTRO del fork (no hacia el repositorio del profesor).
git push fork prueba-rama-protegida
gh pr create --repo $repo --base main --head prueba-rama-protegida `
    --title "Prueba de la rama protegida: prueba rota a propósito" `
    --body "Este PR rompe una prueba a propósito para comprobar que el ruleset proteger-main bloquea el merge cuando el check 'Build, pruebas y cobertura' está en rojo. No se debe integrar."

# 6. Wiki y páginas para las capturas.
Publicar-Wiki
Start-Process "https://github.com/$repo/actions"
Start-Process "https://github.com/$repo/pulls"
Start-Process "https://github.com/$repo/settings/rules"
Write-Host "`nListo. Toma las capturas gh-01 ... gh-06 indicadas en las páginas Evidencias y Rama-protegida del Wiki." -ForegroundColor Green

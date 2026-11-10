#requires -Version 5.1
<#
.SYNOPSIS
    Executa a coleção Postman da API do Café na Mão com Newman.

.DESCRIPTION
    Gera o JAR (opcionalmente), inicia a API em uma porta isolada, aguarda o
    endpoint /api/bebidas responder, executa a coleção Postman e encerra a API no
    final. O relatório JSON fica em postman/newman-report.json.

.PARAMETER Port
    Porta local da API. Padrão: 18080.

.PARAMETER SkipBuild
    Usa o JAR existente em backend/target sem executar o Maven.

.EXAMPLE
    .\scripts\test-api.ps1

.EXAMPLE
    .\scripts\test-api.ps1 -Port 19090 -SkipBuild
#>
[CmdletBinding()]
param(
    [int]$Port = 18080,
    [switch]$SkipBuild
)

$ErrorActionPreference = "Stop"

$raiz = Split-Path -Parent $PSScriptRoot
$backend = Join-Path $raiz "backend"
$jar = Join-Path $backend "target\cafe-na-mao-api-0.1.0-SNAPSHOT.jar"
$colecao = Join-Path $raiz "postman\CafeNaMao.postman_collection.json"
$ambiente = Join-Path $raiz "postman\CafeNaMao.postman_environment.json"
$relatorio = Join-Path $raiz "postman\newman-report.json"
$logSaida = Join-Path $env:TEMP "cafe-na-mao-api-$Port.out.log"
$logErro = Join-Path $env:TEMP "cafe-na-mao-api-$Port.err.log"
$urlSaude = "http://localhost:$Port/api/bebidas"

if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    throw "Java não encontrado no PATH. Instale o Java 21 ou superior."
}

if (-not (Get-Command npx -ErrorAction SilentlyContinue)) {
    throw "npx não encontrado no PATH. Instale o Node.js 20 ou superior."
}

if (-not (Test-Path $colecao) -or -not (Test-Path $ambiente)) {
    throw "Coleção ou ambiente Postman não encontrado em postman/."
}

if (-not $SkipBuild) {
    Write-Host "Gerando o JAR com Maven (testes são executados no verify separado)..." -ForegroundColor Cyan
    Push-Location $backend
    try {
        & .\mvnw.cmd -B -DskipTests package
        if ($LASTEXITCODE -ne 0) {
            throw "Falha ao gerar o JAR (código $LASTEXITCODE)."
        }
    }
    finally {
        Pop-Location
    }
}

if (-not (Test-Path $jar)) {
    throw "JAR não encontrado em $jar. Execute sem -SkipBuild."
}

# Evita iniciar uma segunda API na mesma porta e depois encerrar o processo errado.
if (Get-Command Get-NetTCPConnection -ErrorAction SilentlyContinue) {
    $emUso = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue
    if ($emUso) {
        $pids = ($emUso | Select-Object -ExpandProperty OwningProcess -Unique) -join ", "
        throw "A porta $Port já está em uso pelos processos: $pids. Encerre-os ou use outra porta."
    }
}

if (Test-Path $logSaida) { Remove-Item $logSaida -Force }
if (Test-Path $logErro) { Remove-Item $logErro -Force }

Write-Host "Iniciando a API na porta $Port..." -ForegroundColor Cyan
# Banco em memória: cada execução do contrato começa limpa, sem afetar o banco
# de desenvolvimento em ./data.
$processo = Start-Process -FilePath "java" `
    -ArgumentList "-jar `"$jar`" --server.port=$Port `"--spring.datasource.url=jdbc:h2:mem:cafe-na-mao-contrato;DB_CLOSE_DELAY=-1`"" `
    -PassThru `
    -WindowStyle Hidden `
    -RedirectStandardOutput $logSaida `
    -RedirectStandardError $logErro

try {
    $pronta = $false
    for ($tentativa = 1; $tentativa -le 90; $tentativa++) {
        Start-Sleep -Seconds 1

        try {
            $resposta = Invoke-WebRequest -Uri $urlSaude -UseBasicParsing -TimeoutSec 2
            if ($resposta.StatusCode -eq 200) {
                $pronta = $true
                break
            }
        }
        catch {
            # A API ainda está subindo; tenta novamente.
        }

        # O comando java pode ser um lançador que cria um processo filho.
        # Só considera falha se ninguém estiver escutando a porta.
        $processo.Refresh()
        if ($processo.HasExited) {
            $ouvinte = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue
            if (-not $ouvinte) {
                $erro = ""
                if (Test-Path $logErro) { $erro = (Get-Content $logErro -Raw) }
                throw "A API encerrou antes de ficar pronta.`n$erro"
            }
        }
    }

    if (-not $pronta) {
        throw "Tempo esgotado aguardando $urlSaude. Veja $logSaida e $logErro."
    }

    Write-Host "API pronta. Executando Newman..." -ForegroundColor Cyan
    & npx --yes newman@6.2.1 run $colecao `
        --environment $ambiente `
        --env-var "base_url=http://localhost:$Port" `
        --reporters cli,json `
        --reporter-json-export $relatorio

    if ($LASTEXITCODE -ne 0) {
        throw "Newman encontrou falhas (código $LASTEXITCODE). Relatório: $relatorio"
    }

    Write-Host "Coleção aprovada. Relatório: $relatorio" -ForegroundColor Green
}
catch {
    Write-Host "Falha na execução: $($_.Exception.Message)" -ForegroundColor Red
    if (Test-Path $logSaida) {
        Write-Host "--- últimas linhas de $logSaida ---" -ForegroundColor Yellow
        Get-Content $logSaida -Tail 30
    }
    if (Test-Path $logErro) {
        Write-Host "--- últimas linhas de $logErro ---" -ForegroundColor Yellow
        Get-Content $logErro -Tail 30
    }
    throw
}
finally {
    # Encerra tanto o processo lançador quanto o processo Java que escuta a porta.
    $pidsParaEncerrar = @()
    $processo.Refresh()
    if (-not $processo.HasExited) {
        $pidsParaEncerrar += $processo.Id
    }

    $ouvintes = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue
    if ($ouvintes) {
        $pidsParaEncerrar += $ouvintes | Select-Object -ExpandProperty OwningProcess
    }

    $pidsParaEncerrar = $pidsParaEncerrar | Sort-Object -Unique
    foreach ($pidApi in $pidsParaEncerrar) {
        Write-Host "Encerrando a API (PID $pidApi)..." -ForegroundColor Cyan
        Stop-Process -Id $pidApi -Force -ErrorAction SilentlyContinue
    }
}

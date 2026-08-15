# SmartSchool AI Build & Run Helper
$MavenVersion = "3.9.6"
$MavenDir = "$PSScriptRoot\.mvn\apache-maven-$MavenVersion"
$MvnCmd = "$MavenDir\bin\mvn.cmd"

if (-not (Test-Path $MvnCmd)) {
    Write-Host "Maven not detected. Downloading Apache Maven $MavenVersion..." -ForegroundColor Cyan
    $Url = "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$MavenVersion/apache-maven-$MavenVersion-bin.zip"
    $ZipPath = "$PSScriptRoot\.mvn\maven.zip"
    
    New-Item -ItemType Directory -Force -Path "$PSScriptRoot\.mvn" | Out-Null
    Invoke-WebRequest -Uri $Url -OutFile $ZipPath
    
    Write-Host "Extracting Maven..." -ForegroundColor Cyan
    Expand-Archive -Path $ZipPath -DestinationPath "$PSScriptRoot\.mvn" -Force
    Remove-Item $ZipPath -Force
}

Write-Host "Running Maven command: $args" -ForegroundColor Green
& $MvnCmd $args

@echo off
set CCanalyzerHome=%ProgramFiles%\Praqma\CCanalyzer

IF EXIST "%CCanalyzerHome%" GOTO update

mkdir "%CCanalyzerHome%"
@echo Installing
goto install

:update

@echo Updating
net stop CCAnalyzerSvc
pushd "%CCanalyzerHome%"
CCAnalyzerSvc.exe uninstall
popd

goto install

:install
pushd "%CCanalyzerHome%"
xcopy "%~dp0CCAnalyzerSvc.exe" . /Y /R
xcopy "%~dp0CCAnalyzerSvc.xml" . /Y /R
xcopy "%~dp0ccanalyzer.jar" . /Y /R
CCAnalyzerSvc.exe install
net start CCAnalyzerSvc
popd

:end

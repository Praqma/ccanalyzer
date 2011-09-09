@echo off
set CCanalyzerHome=%ProgramFiles%\Praqma\CCanalyzer

IF EXIST "%CCanalyzerHome%" GOTO update
mkdir %CCanalyzerHome%
@echo Installing
goto install

:update

@echo Updating
net stop CCAnalyzerSvc
"%CCanalyzerHome%\CCAnalyzerSvc.exe" uninstall

goto install

:install

xcopy "%~dp0CCAnalyzerSvc.exe" "%CCanalyzerHome%" /Y /R
xcopy "%~dp0CCAnalyzerSvc.xml" "%CCanalyzerHome%" /Y /R
xcopy "%~dp0ccanalyzer.jar" "%CCanalyzerHome%" /Y /R
"%CCanalyzerHome%\CCAnalyzerSvc.exe" install
net start CCAnalyzerSvc

:end


pause

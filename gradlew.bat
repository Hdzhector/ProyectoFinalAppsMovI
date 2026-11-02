@ECHO OFF
SETLOCAL
SET APP_HOME=%~dp0
SET JAR=%APP_HOME%gradle\wrapper\gradle-wrapper.jar
IF NOT EXIST "%JAR%" (
  ECHO Descargando Gradle Wrapper oficial para la primera ejecucion...
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -Uri 'https://raw.githubusercontent.com/gradle/gradle/v8.13.0/gradle/wrapper/gradle-wrapper.jar' -OutFile '%JAR%'"
  IF ERRORLEVEL 1 EXIT /B 1
)
java -classpath "%JAR%" org.gradle.wrapper.GradleWrapperMain %*
ENDLOCAL

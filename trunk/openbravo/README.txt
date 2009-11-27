1) Información sobre OpenBravo:

http://wiki.openbravo.com/wiki/ERP/User_Manual

http://wiki.openbravo.com/wiki/ERP/Openbravo_ERP_Installation

http://wiki.openbravo.com/wiki/ERP/2.50/Configuration_Manual

http://wiki.openbravo.com/wiki/ERP/Developers_Guide

http://wiki.openbravo.com/wiki/Functional_Documentation

http://mtopenbravo.blogspot.com/2009/02/openbravo-250-rest-webservices.html


2) Descarga de Openbravo e instalación

a) Ejecutar desde el directorio actual:

> ant unpack-openbravo

b) Instalación de software necesario

> apt-get install postgresql-8.3 postgresql-contrib-8.3 uuid
> su postgres
> createuser -s -d -r -l -E -P sa


c) Construcción de Openbravo (en Debian Linux)

> cd erp
> ant setup
> cd config
> ./setup-properties-XXXXX

d) Configurar con el diálogo anterior las propiedades de OpenBravo
- Formato de fecha y hora
- Propiedades de contexto web y localización de adjuntos (deberá poderse escribir en el directorio)
- Base de datos: POSTGRESQL
- Parámetros de la BBDD
	- Nombre de BBDD
	- Usuario y contraseña del administrador de BBDD
	- Usuario y contraseña del usuario de BBDD de OpenBravo que se creará
	- Otros parámetros

e) Crear BBDD con el nombre del administrador de la BBDD (si no falla el script de setup de OpenBravo)

> createdb -U sa -h localhost sa

f) Editar el fichero <.>/erp/config/Openbravo.properties y añadir las propiedades o modificarlas:

# Parametros build OpenBravo
deploy.mode=war
jakarta.base=<ruta_completa_directorio_catalina_base>

# Parametros para silenus
stomp.host=127.0.0.1
stomp.port=40001
#stomp.user=
#stomp.password=
notify.Project=/queue/erp-project

g) Construcción openbravo desde el directorio actual

> cd erp
> ant install.source

h) Despliegue de war en tomcat

- Bien usar tomcat manager o target apropiado de OpenBravo
- Bien copiar el war o el war explotado al directorio de aplicaciones web de tomcat


> ant compile.complete.war
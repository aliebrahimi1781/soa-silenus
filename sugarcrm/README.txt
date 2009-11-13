
1) Documentación relevante

http://www.sugarcrm.com/wiki/index.php?title=Platform_Requirements_and_Configurations

http://www.sugarcrm.com/crm/products/supported-platforms.html

http://www.sugarcrm.com/crm/support/documentation/SugarCommunityEdition/5.2/-docs-Administration_Guides-CommunityEdition_Install_Admin_Guide_5.2-toc.html

http://www.sugarcrm.com/crm/support/documentation/SugarCommunityEdition/5.2/-docs-User_Guides-CommunityEdition_UserGuide_5.2-toc.html

http://www.sugarcrm.com/crm/support/documentation/SugarCommunityEdition/5.2/-docs-Developer_Guides-Developer_Guide_5.2-toc.html


2) Instalación de Aplicaciones necesarias

- Se requiere Apache 2.X con soporte para PHP4 o PHP5 (mejor el último)
- Se requiere PHP con los módulos: xml, pear, zlib, mysql, ldap, imap, json
- Se requiere MySql 5.X


echo -e "Instalación de PHP"
apt-get install php5 php5-adodb php5-cli php5-common php5-curl php5-gd php5-imagick php5-imap php5-ldap php5-mcrypt php5-mhash php5-mysql php5-sasl php5-xsl

echo -e "Instalación Servidor Apache"
apt-get install apache2	apache2-doc apache2-mpm-prefork apache2-utils apache2.2-common libapache2-mod-apreq2 libapache2-mod-auth-plain libapache2-mod-encoding libapache2-mod-jk libapache2-mod-php5  libapache2-mod-proxy-html

echo -e "Habilitamos los módulos necesarios"
a2enmod ssl
a2enmod jk
a2enmod php5
a2enmod encoding
a2enmod expires
a2enmod headers
a2enmod deflate

echo -e "Reiniciamos Apache"
/etc/init.d/apache2 restart

echo -e "Instalación de MySQL"
apt-get install mysql-client mysql-client-5.0	mysql-server mysql-server-5.0


3) Instalación de SugarCRM

a) Desde el directorio actual (requiere que el usuario tenga permisos de escritura sobre /var/www/sugarcrm)

> ant all

b) Con  el navegador instalar sugarcrm según el manual de instalación

- Es recomendable instalar un juego de datos de prueba para un entorno de pruebas

c) Instalar el módulo <.>/target/soa-crm.zip mediante el Instalador de Módulos en SugarCRM

- Se pueden configurar las direcciones de stomp en el fichero /var/www/sugarcrm/config_override.php

$sugar_config['stomp_url'] = "tcp://localhost:40001";

d) Configuración de SugarCRM

- Se puede integrar con un servidor de LDAP para la autenticación de usuarios, y quedaría integrado con
un backend de usuarios compartido con Alfresco. Pero es opcional.









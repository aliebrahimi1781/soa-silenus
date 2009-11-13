Este documento es un punto de partida para el desarrollo de este proyecto.
Se supone que el/la lector/a posee unas habilidates técnicas suficientes en las tecnologías
que se emplean: Java, Ant, Maven, Apache, PHP, MySQL... y un conocimiento de sistemas operativos
linux


1) Software necesario para el proyecto

- Java (JDK)
- Ant
- Maven

echo "Creación de directorio para binarios Java de terceras partes"
mkdir /opt/java
cd /opt/java

echo "Descarga de binarios"
wget <ruta_binario_jdk_sun_de_mi_plataforma>
wget http://www.apache.org/dist/maven/binaries/apache-maven-2.2.1-bin.tar.gz
wget http://www.apache.org/dist/ant/binaries/apache-ant-1.7.1-bin.tar.gz

echo "Instalación JVM"
chmod +x <binario_jvm>
./<binario_jvm>
ln -s <directorio_jvm> java-6-sun

echo "Instalación Ant"
tar xvzf apache-ant-1.7.1-bin.tar.gz
ln -s apache-ant-1.7.1 ant

echo "Instalación Maven"
tar xvzf apache-maven-2.2.1-bin.tar.gz
ln -s apache-maven-2.2.1 maven

echo "Configuración del perfil de desarrollo”
echo -e "# Entorno de desarrollo Java" >> /etc/profile
echo -e "JAVA_HOME=/opt/java/java-6-sun" >> /etc/profile
echo -e "ANT_HOME=/opt/java/ant" >> /etc/profile
echo -e "MAVEN_HOME=/opt/java/maven" >> /etc/profile
echo -e "PATH=$JAVA_HOME/bin:$ANT_HOME/bin:$MAVEN_HOME/bin:$PATH" >> /etc/profile
echo -e "export JAVA_HOME ANT_HOME MAVEN_HOME PATH" >> /etc/profile

echo "Recargamos el perfil"
source /etc/profile

echo "Comprobaciones"
which java
java -version
which ant
ant -version
which mvn
mvn -version


1) Configuracion del proyecto

- Copiar el fichero default.properties en build.properties y editar convenientemente.

2) Compilación de Alfresco y CAS

(Desde el directorio actual)

> ant all

3) Configuración de Apache

echo -e “Instalación Servidor Apache”
apt-get install apache2	apache2-doc apache2-mpm-prefork apache2-utils apache2.2-common libapache2-mod-apreq2 libapache2-mod-auth-plain libapache2-mod-encoding libapache2-mod-jk libapache2-mod-php5  libapache2-mod-proxy-html
echo -e “Habilitamos los módulos necesarios”
a2enmod ssl
a2enmod jk
a2enmod php5
a2enmod encoding
a2enmod expires
a2enmod headers
a2enmod deflate
echo -e “Reiniciamos Apache”
/etc/init.d/apache2 restart


En la carpeta <.>/target tenemos unos ficheros y directorios para configurar Apache y Tomcat.
- etc/init.d contiene un script para el arranque de tomcat que habrá que instalar en /etc/init.d
y configurar con la ayuda de "update-rc.d"

- etc/apache2 contiene los ficheros de configuración de apache y sus módulos


4) Certificados SSL para Apache

Se incluyen como ejemplo unos certificados y autoridad CA generados con "easy-rsa" de OpenVPN.

Si se desea cambiar estos certificados, se pueden generar con la misma utilidad.

El parámetro "soa.host" del fichero de configuración, se emplea para nombrar los certificados, si se
 cambian de nombre, deberá modificarse el fichero soa-ssl.conf convenientemente.

echo -e "Instalación de openssh"
apt-get install openssl

echo -e "Instalación easy-rsa"
mkdir -p /etc/ssl
cp -r /usr/share/doc/openvpn/examples/easy-rsa/2.0 /etc/ssl
cd /etc/ssl/easy-rsa
source vars
./clean-all

echo -e "Generación de Autoridad Certificadora"
./build-ca

echo -e "Generación de Certificado de servidor"
./build-key-server <nombre-de-host.completo.org>

echo -e "Certificados para apache"
mkdir -p /etc/apache2/ssl
cp keys/ca.crt keys/<nombre-de-host.completo.org>.* /etc/apache2/ssl


Es necesario importar los certificados en la JVM, para ello ponemos un ejemplo:

keytool -import -noprompt -v -trustcacerts -alias SOA_CA -file /etc/apache2/ssl/ca.crt -keystore ${JAVA_HOME}/jre/lib/security/cacerts -storepass changeit
keytool -import -noprompt -v -trustcacerts -alias SOA_HOST -file /etc/apache2/ssl/<nombre-de-host.completo.org>.crt -keystore ${JAVA_HOME}/jre/lib/security/cacerts -storepass changeit


5) LDAP

apt-get install slapd ldap-utils
dpkg-reconfigure slapd

En el directorio <.>/conf/ldap se muestra un ejemplo de la configuración de OpenLDAP,
pero se recomienda el conocimiento de la herramienta y la lectura de algún tutorial.

Para crear el árbol del directorio podemos emplear la herramienta gratuita:

Apache Directory Studio
http://directory.apache.org/studio/


La estructura necesaria es:


- dc=silenus-consultoria,dc=es
	+
	+-ou=people (objectClass = organizationalUnit) --> Usuarios
		+
		+- uid=admin (objectClass = top, inetOrgPerson, posixAccount)
		...
	+ ou=groups (objectClass = organizationalUnit)  --> Grupos / Roles
		+
		+- cn=ALFRESCO_ADMINISTRATORS (objectClass = top, posixGroup)
			 memberUid=uid=admin,ou=people,dc=silenus-consultoria,dc=es




6) Tomcat con Alfresco y CAS

- Después del paso 2) Todo debería estar listo para funcionar correctamente.
- Para comprobarlo se puede arrancar el tomcat.


7) SugarCRM

- Leer <.>/sugarcrm/README.txt


8) Mule

- Leer <.>/mule/README.txt


6) Openbravo

- Leer <.>/openbravo/README.txt



--
--    Maven tips
--

-- Get a list of dependencies

mvn dependency:tree -DoutputFile=./dependencies.txt

-- Fetch sources and javadoc for IDE debugging

mvn eclipse:eclipse  -DdownloadSources=true  -DdownloadJavadocs=true
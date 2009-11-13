#!/bin/sh


# Check arguments
if (( $# < 5 )); then
	echo "Uso: $0 <database> <database_user> <database_password> <path_to_sugar_crm> <path_to_backup_dir>"
	exit 1
fi

# Define variables
DB_NAME=$1
DB_USER=$2
DB_PASSWORD=$3

WORK_DIR=$(dirname $0)/..
WORK_DIR=$( cd $WORK_DIR && pwd )
SUGAR_DIR=$4
WWW_DIR=$(dirname $SUGAR_DIR)
APP_DIR=$(basename $SUGAR_DIR)

BACKUP_DIR=$5

# Test valid sugar crm directory
if ! [ -f "$SUGAR_DIR/config.php" ]; then
	echo "Parece que $SUGAR_DIR no es un directorio de instalacion de SugarCRM valido"
	exit 1
fi

# Test valid backup directory
if ! [ -f "$BACKUP_DIR/sugar.sql" ]; then
	echo "Parece que $BACKUP_DIR no es un directorio de backup valido, falta sugar.sql"
	exit 1
fi
if ! [ -f "$BACKUP_DIR/sugar.tar.gz" ]; then
	echo "Parece que $BACKUP_DIR no es un directorio de backup valido, falta sugar.tar.gz"
	exit 1
fi

# Drop database if exists
echo "Eliminando la base de datos si existe..."
echo 'drop database if exists '$DB_NAME';' | mysql -u $DB_USER --password=$DB_PASSWORD mysql
# Restore database
echo "Restaurando la base de datos desde el backup..."
echo 'create database if not exists '$DB_NAME' default character set utf8;' | mysql -u $DB_USER --password=$DB_PASSWORD mysql
mysql -u $DB_USER --password=$DB_PASSWORD -h localhost $DB_NAME < $BACKUP_DIR/sugar.sql


# Remove sugar directory
echo "Eliminando $SUGAR_DIR ..."
sudo rm -Rf $SUGAR_DIR
# Restore sugar
echo "Restaurando de $BACKUP_DIR/sugar.tar.gz a $WWW_DIR ..."
sudo tar xzf  $BACKUP_DIR/sugar.tar.gz -C $WWW_DIR
# Remove subversion directories
for i in $(find $SUGAR_DIR -name .svn); do sudo rm -Rf $i; done
# Restaurando permisos
sudo chown -R www-data.www-data $SUGAR_DIR
cd $SUGAR_DIR
sudo chmod -R 775 *
sudo chmod -R 664 *.*
sudo chmod 775 $SUGAR_DIR
#!/bin/sh

# Check arguments
if (( $# < 5 )); then
	echo "Uso: $0 <database> <database_user> <database_password> <path_to_sugar_crm> <path_to_backups>"
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
DATE=$(date +%Y%m%d%H%M%S)
BACKUP_DIR=$5/$DATE

# Test valid sugar crm directory
if ! [ -f "$SUGAR_DIR/config.php" ]; then
	echo "Parece que $SUGAR_DIR no es un directorio de instalacion de SugarCRM valido"
	exit 1
fi

# Create backup dir
mkdir -p $BACKUP_DIR
# Backup sugar
echo "Ejecutando backup de sugar..."
sudo tar czf $BACKUP_DIR/sugar.tar.gz -C $WWW_DIR $APP_DIR
# Backup database
echo "Ejecutando backup de la base de datos..."
mysqldump -c  --opt --skip-extended-insert --user=$DB_USER --password=$DB_PASSWORD -h localhost $DB_NAME > $BACKUP_DIR/sugar.sql

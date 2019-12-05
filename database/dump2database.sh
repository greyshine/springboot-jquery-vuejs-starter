#! /bin/bash

# set passwords for different DBs for MacOS in file /Users/<Account>/.pgconf

file=someSqlDump.sql

dbIp=127.0.0.1
dbPort=5432

echo $dbIp:$dbPort

psql -h $dbIp -p $dbPort -U postgres --dbname=postgres -f $file
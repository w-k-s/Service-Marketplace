#!/bin/bash

echo "* Request for authorization"
RESULT=`curl --data "username=pierre&password=pierre&grant_type=password&client_id=admin-cli" http://localhost:8080/auth/realms/master/protocol/openid-connect/token`

echo "\n"
echo "* Recovery of the token"
TOKEN=`echo $RESULT | sed 's/.*access_token":"//g' | sed 's/".*//g'`

echo "\n"
echo "* Display token"
echo $TOKEN

echo "\n"
echo " * user creation\n"
curl   http://localhost:8080/apiv2/users -H "Authorization: bearer $TOKEN"   --data '{"firstName":"xyz","lastName":"xyz", "email":"demo2@gmail.com", "enabled":"true"}'

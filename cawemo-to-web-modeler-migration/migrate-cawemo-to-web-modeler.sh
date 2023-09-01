#!/bin/zsh 

set -eu -o pipefail

# ℹ️ Requires Bash 4 or newer, or zsh
# Recommended to run it on a test environment first

# See these docs to obtain your Cawemo API credentials: https://docs.camunda.org/cawemo/1.9/reference/rest-api/overview/authentication/
CAWEMO_USER_ID=ENTER_HERE
CAWEMO_API_KEY=ENTER_HERE

# See these docs to obtain your Web Modeler client credentials: https://docs.camunda.io/docs/next/apis-tools/web-modeler-api/#authentication
MODELER_CLIENT_ID=ENTER_HERE
MODELER_CLIENT_SECRET=ENTER_HERE

# Color definitions for terminal output formatting
GREEN='\033[0;32m'
NC='\033[0m' # No Color
B=$(tput bold)
N=$(tput sgr0)
BGREEN=$GREEN$B
NNC=$NC$N

echo "------------------------------------------------------------------------"
echo "${BGREEN}MIGRATION FROM CAWEMO TO WEB MODELER${NNC}"
echo "Files are now migrated file-by-file..."
echo "------------------------------------------------------------------------"

echo ""

echo "[$(date +'%Y-%m-%d %H:%M:%S')] Migration started" >> migration.log

HTTP_STATUS=$(curl -s -o .curl_tmp -w "%{http_code}" -u $CAWEMO_USER_ID:$CAWEMO_API_KEY "https://cawemo.com/api/v1/projects")

PROJECTS=$(cat .curl_tmp)

if [ "$HTTP_STATUS" != "200" ]
then
    echo "⚠ GET https://cawemo.com/api/v1/projects failed with status code $HTTP_STATUS"
    echo "[$(date +'%Y-%m-%d %H:%M:%S')] ⚠ GET https://cawemo.com/api/v1/projects failed with status code $HTTP_STATUS" >> migration.log
    
    echo "Aborted"
    exit 1
fi

TOKEN=$(curl -s --header "Content-Type: application/json" --request POST --data "{\"grant_type\":\"client_credentials\", \"audience\":\"api.cloud.camunda.io\", \"client_id\":\"$MODELER_CLIENT_ID\", \"client_secret\":\"$MODELER_CLIENT_SECRET\"}" https://login.cloud.camunda.io/oauth/token | jq -r .access_token)

for row in $(echo $PROJECTS | jq -r '.[] | @base64'); do
    _jq() {
     echo ${row} | base64 --decode | jq -r ${1}
    }

    OLD_PROJECT_ID=$(_jq '.id')
    PROJECT_NAME=$(_jq '.name')

    echo "Migrating project ${GREEN}$PROJECT_NAME${NC} (ID: $OLD_PROJECT_ID)..."

    HTTP_STATUS=$(curl -s -o .curl_tmp -w "%{http_code}" -X POST "https://modeler.cloud.camunda.io/api/beta/projects" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $TOKEN" \
        -d "$(_jq '.')")

    NEW_PROJECT=$(cat .curl_tmp)

    if [ "$HTTP_STATUS" != "200" ]
    then
        echo "⚠ POST https://modeler.cloud.camunda.io/api/beta/projects failed with status code $HTTP_STATUS"
        echo "[$(date +'%Y-%m-%d %H:%M:%S')] ⚠ POST https://modeler.cloud.camunda.io/api/beta/projects failed with status code $HTTP_STATUS" >> migration.log

        echo "Aborted"
        exit 1
    fi

    echo "[$(date +'%Y-%m-%d %H:%M:%S')] Migrated project $PROJECT_NAME (ID: $OLD_PROJECT_ID)" >> migration.log

    NEW_PROJECT_ID=$(jq -r .id <<< "$NEW_PROJECT")

    # maps folder IDs to original IDs to not create them twice + create file in right folder
    declare -A folderIds

    FILES=$(curl -s -u $CAWEMO_USER_ID:$CAWEMO_API_KEY "https://cawemo.com/api/v1/projects/$OLD_PROJECT_ID/files")

    FILE_COUNT=$(echo $FILES | jq '. | length')

    echo "$FILE_COUNT files to be migrated"

    FILE_INDEX=1
    for file in $(echo $FILES | jq -r '.[] | @base64'); do
        _file_jq() {
            echo ${file} | base64 --decode | jq -r ${1}
        }

        if [ $FILE_COUNT -eq $FILE_INDEX ]
        then
            TREE_SYMBOL="└──"
        else
            TREE_SYMBOL="├──"
        fi

        OLD_FILE_ID=$(_file_jq '.id')
        FILE_NAME=$(_file_jq '.name')
        FILE_TYPE=$(_file_jq '.type')
        FILE_PATH=$(_file_jq '.canonicalPath')

        FILE_TREE_SPACES=""

        # Create Folders

        PARENT_ID="null"

        for folder in $(echo $FILE_PATH | jq -r '.[] | @base64'); do
            _folder_jq() {
                echo ${folder} | base64 --decode | jq -r ${1}
            }

            FOLDER_NAME=$(_folder_jq '.name')
            OLD_FOLDER_ID=$(_folder_jq '.id')

            # skip this folder if already created
            if [ -z "${folderIds[$OLD_FOLDER_ID]}" ]
            then
                # New folder
                echo "├── Migrating folder ${GREEN}$FOLDER_NAME${NC} (ID: $OLD_FOLDER_ID)..."

                FOLDER_REQUEST="{
                    \"name\": \"$FOLDER_NAME\",
                    \"projectId\": \"$NEW_PROJECT_ID\",
                    \"parentId\": $PARENT_ID
                }"

                HTTP_STATUS=$(curl -s -o .curl_tmp -w "%{http_code}" -X POST "https://modeler.cloud.camunda.io/api/beta/folders" \
                    -H "Content-Type: application/json" \
                    -H "Authorization: Bearer $TOKEN" \
                    -d "$FOLDER_REQUEST")

                NEW_FOLDER=$(cat .curl_tmp)

                if [ "$HTTP_STATUS" != "200" ]
                then
                    echo "⚠ POST https://modeler.cloud.camunda.io/api/beta/folders failed with status code $HTTP_STATUS"
                    echo "[$(date +'%Y-%m-%d %H:%M:%S')] ⚠ POST https://modeler.cloud.camunda.io/api/beta/folders failed with status code $HTTP_STATUS" >> migration.log
                else
                    NEW_FOLDER_ID=$(jq -r .id <<< "$NEW_FOLDER")

                    folderIds[$OLD_FOLDER_ID]=$NEW_FOLDER_ID

                    PARENT_ID="\"$NEW_FOLDER_ID\""

                    echo "[$(date +'%Y-%m-%d %H:%M:%S')] Migrated folder $FOLDER_NAME (ID: $OLD_FOLDER_ID)" >> migration.log
                    echo "\e[1A\e[K├── ✔ Migrated folder ${GREEN}$FOLDER_NAME${NC} (ID: $OLD_FOLDER_ID)."
                fi
            else
                # Existing folder
                PARENT_ID="\"${folderIds[$OLD_FOLDER_ID]}\""
            fi

            FILE_TREE_SPACES="    $FILE_TREE_SPACES"
        done

        if [ "$FILE_TYPE" = "TEMPLATE_GENERIC" ] || [ "$FILE_TYPE" = "TEMPLATE_SERVICE_TASK" ]
        then
            echo "$FILE_TREE_SPACES$TREE_SYMBOL ⚠ File ${GREEN}$FILE_NAME${NC} can't be migrated: A C7 $FILE_TYPE is not supported in C8. (ID: $OLD_FILE_ID)"
            echo "[$(date +'%Y-%m-%d %H:%M:%S')] File $FILE_NAME can't be migrated: A C7 $FILE_TYPE is not supported in C8. (ID: $OLD_FILE_ID)" >> migration.log
        else
            echo "$FILE_TREE_SPACES$TREE_SYMBOL Migrating file ${GREEN}$FILE_NAME${NC} (Type: $FILE_TYPE, ID: $OLD_FILE_ID)..."

            FILE_WITH_CONTENT=$(curl -s -u $CAWEMO_USER_ID:$CAWEMO_API_KEY "https://cawemo.com/api/v1/files/$OLD_FILE_ID")

            CONTENT=$(jq -r .content <<< "$FILE_WITH_CONTENT")

            CONTENT=$(sed 's/"/\\"/g' <<< $CONTENT)

            CONTENT=$(tr -d '\n' <<< $CONTENT)

            FILE_REQUEST="{
                \"name\": \"$FILE_NAME\",
                \"projectId\": \"$NEW_PROJECT_ID\",
                \"parentId\": $PARENT_ID,
                \"content\": \"$CONTENT\",
                \"fileType\": \"$FILE_TYPE\"
            }"

            HTTP_STATUS=$(curl -s -o .curl_tmp -w "%{http_code}" -X POST "https://modeler.cloud.camunda.io/api/beta/files" \
                -H "Content-Type: application/json" \
                -H "Authorization: Bearer $TOKEN" \
                -d "$FILE_REQUEST")

            NEW_FILE=$(cat .curl_tmp)

            if [ "$HTTP_STATUS" != "200" ]
            then
                echo "⚠ POST https://modeler.cloud.camunda.io/api/beta/files failed with status code $HTTP_STATUS"
                echo "[$(date +'%Y-%m-%d %H:%M:%S')] ⚠ POST https://modeler.cloud.camunda.io/api/beta/files failed with status code $HTTP_STATUS" >> migration.log
            else
                echo "\e[1A\e[K$FILE_TREE_SPACES$TREE_SYMBOL ✔ Migrated file ${GREEN}$FILE_NAME${NC} (Type: $FILE_TYPE, ID: $OLD_FILE_ID)."

                echo "[$(date +'%Y-%m-%d %H:%M:%S')] Migrated file $FILE_NAME (Type: $FILE_TYPE, ID: $OLD_FILE_ID)" >> migration.log
            fi    
        fi

        let FILE_INDEX=${FILE_INDEX}+1
    done
done

echo "[$(date +'%Y-%m-%d %H:%M:%S')] Migration done" >> migration.log

echo $''

echo "------------------------------------------------------------------------"
echo "${BGREEN}MIGRATION SUCCESS${NNC}"
echo "Done! Log in to Web Modeler now and enable the super-user mode (https://docs.camunda.io/docs/next/components/modeler/web-modeler/collaboration/#super-user-mode) to see the projects and assign collaborators."
echo "------------------------------------------------------------------------"

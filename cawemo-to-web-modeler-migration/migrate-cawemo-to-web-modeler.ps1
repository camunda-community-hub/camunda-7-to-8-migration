#Requires -Version 5
# Recommended to run it on a test environment first

# See these docs to obtain your Cawemo API credentials: https://docs.camunda.org/cawemo/1.9/reference/rest-api/overview/authentication/
$CAWEMO_USER_ID="ENTER HERE"
$CAWEMO_API_KEY="ENTER HERE"

# See these docs to obtain your Web Modeler client credentials: https://docs.camunda.io/docs/next/apis-tools/web-modeler-api/#authentication
$MODELER_CLIENT_ID="ENTER HERE"
$MODELER_CLIENT_SECRET="ENTER HERE"

Write-Host "------------------------------------------------------------------------"
Write-Host "MIGRATION FROM CAWEMO TO WEB MODELER" -ForegroundColor Green
Write-Host "Files are now migrated file-by-file..."
Write-Host "------------------------------------------------------------------------"
Write-Host ""

Add-Content "migration.log" (Get-Date -UFormat "%Y-%m-%d %H:%M:%S")

$CREDENTIAL = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes(("{0}:{1}" -f $CAWEMO_USER_ID,$CAWEMO_API_KEY)))
try {
  $PROJECTS=(Invoke-RestMethod -Uri "https://cawemo.com/api/v1/projects" -Headers @{Authorization=("Basic {0}" -f $CREDENTIAL)})
} catch {
  Write-Host "GET https://cawemo.com/api/v1/projects failed with status code  $($_.Exception.Response.StatusCode.value__)"
  Add-Content "migration.log" "$(Get-Date -UFormat "%Y-%m-%d %H:%M:%S") GET https://cawemo.com/api/v1/projects failed with status code $($_.Exception.Response.StatusCode.value__)"

  Write-Host "Aborted"
  exit 1
}

try {
  $TOKEN=(Invoke-RestMethod -Method "Post" -Headers @{"Content-Type"="application/json"} -Body (@{grant_type="client_credentials"; audience="api.cloud.camunda.io"; client_id=$MODELER_CLIENT_ID; client_secret=$MODELER_CLIENT_SECRET}|ConvertTo-Json) -Uri "https://login.cloud.camunda.io/oauth/token").access_token
} catch {
  Write-Host "$([char]0x26a0) POST https://login.cloud.camunda.io/oauth/token failed with status code  $($_.Exception.Response.StatusCode.value__)"
  Add-Content "migration.log" "$(Get-Date -UFormat "%Y-%m-%d %H:%M:%S") $([char]0x26a0) POST https://login.cloud.camunda.io/oauth/token failed with status code  $($_.Exception.Response.StatusCode.value__)"

  Write-Host "Aborted"
  exit 1
}

$REQUEST_HEADER=@{
  "Content-Type"="application/json";
  "Authorization"="Bearer $($TOKEN)"
}

foreach ($PROJECT in $PROJECTS) {
  Write-Host "Migrating project $($Project.name)..."

  try {
    $NEW_PROJECT=(Invoke-RestMethod -Body ($PROJECT|ConvertTo-Json) -Method "Post" -Uri "https://modeler.cloud.camunda.io/api/beta/projects" -Headers ($REQUEST_HEADER))
  } catch {
    Write-Host "$([char]0x26a0) POST https://modeler.cloud.camunda.io/api/beta/projects failed with status code  $($_.Exception.Response.StatusCode.value__)"
    Add-Content "migration.log" "$(Get-Date -UFormat "%Y-%m-%d %H:%M:%S") $([char]0x26a0) POST https://modeler.cloud.camunda.io/api/beta/projects failed with status code  $($_.Exception.Response.StatusCode.value__)"

    Write-Host "Aborted"
    exit 1
  }

  Add-Content "migration.log" "$(Get-Date -UFormat "%Y-%m-%d %H:%M:%S") Migrated project $($PROJECT.name) (ID: $($PROJECT.id))"

  $FILES=(Invoke-RestMethod -Uri "https://cawemo.com/api/v1/projects/$($PROJECT.id)/files" -Headers @{Authorization=("Basic {0}" -f $CREDENTIAL)})

  $folderIds=@{}
  foreach ($FILE in $FILES) {
    if ($FILE -eq ($FILES)[-1]) {
      $TREE_SYMBOL="$([char]0x2514)$([char]0x2500)$([char]0x2500)"
    } else {
      $TREE_SYMBOL="$([char]0x251c)$([char]0x2500)$([char]0x2500)"
    }

    $FILE_TREE_SPACES=""
    # Create Folders
    $PARENT_ID=$null
    foreach ($FOLDER in $FILE.canonicalPath) {
      # skip this folder if already created
      if ($folderIds.ContainsKey($FOLDER.id)) {
        $PARENT_ID=$folderIds[$FOLDER.id]
      } else {
        Write-Progress -Activity "$([char]0x2514)$([char]0x2500)$([char]0x2500) Migrating folder $($FOLDER.name) (ID: $($FOLDER.id))..."

        $FOLDER_REQUEST=@{
          name=$FOLDER.name;
          projectId=$NEW_PROJECT.id;
          parentId=$PARENT_ID
        }

        try {
          $NEW_FOLDER=(Invoke-RestMethod -Method "Post" -Uri "https://modeler.cloud.camunda.io/api/beta/folders" -Headers ($REQUEST_HEADER) -Body ($FOLDER_REQUEST|ConvertTo-Json))
          $folderIds[$FOLDER.id]=$NEW_FOLDER.id
          $PARENT_ID=$NEW_FOLDER.id
          Write-Progress -Completed -Activity "$([char]0x2514)$([char]0x2500)$([char]0x2500) Migrating folder $($FOLDER.name) (ID: $($FOLDER.id))..."
          Write-Host "$([char]0x2514)$([char]0x2500)$([char]0x2500) $([char]0x2714) Migrated folder $($FOLDER.name) (ID: $($FOLDER.id))"
          Add-Content "migration.log" "$(Get-Date -UFormat "%Y-%m-%d %H:%M:%S") Migrated folder $($FOLDER.name) (ID: $($FOLDER.id))"
        } catch {
          $_.Exception
          Write-Host "$([char]0x26a0) POST https://modeler.cloud.camunda.io/api/beta/folders failed with status code  $($_.Exception.Response.StatusCode.value__)"
          Add-Content "migration.log" "$(Get-Date -UFormat "%Y-%m-%d %H:%M:%S") $([char]0x26a0) POST https://modeler.cloud.camunda.io/api/beta/folders failed with status code  $($_.Exception.Response.StatusCode.value__)"
        }
      }
      $FILE_TREE_SPACES="    $($FILE_TREE_SPACES)"
    }

    if (@("TEMPLATE_GENERIC", "TEMPLATE_SERVICE_TASK").contains($FILE.type)) {
      Write-Host "$($FILE_TREE_SPACES)$($TREE_SYMBOL) File $($FILE_NAME) can't be migrated: A C7 $($FILE.type) is not supported in C8. (ID: $($FILE.id))"
      Add-Content "migration.log" "$(Get-Date -UFormat "%Y-%m-%d %H:%M:%S") $($FILE_TREE_SPACES)$($TREE_SQYMBOL) File $($FILE_NAME) can't be migrated: A C7 $($FILE_TYPE) is not supported in C8. (ID: $($FILE.id))"
    } else {
      Write-Progress -Activity "$($FILE_TREE_SPACES)$($TREE_SYMBOL) Migrating file $($FILE_NAME) (Type: $($FILE.type), ID: $($FILE.id))..."
      $FILE_WITH_CONTENT=(Invoke-RestMethod -Uri "https://cawemo.com/api/v1/files/$($FILE.id)" -Headers @{Authorization=("Basic {0}" -f $CREDENTIAL)})
      $content=$FILE_WITH_CONTENT.content
      if ($FILE.type -eq "DMN") {
        $content=($FILE_WITH_CONTENT.content -replace 'camunda:diagramRelationId=".*"','')
      }
      $FILE_REQUEST=@{
        name=$FILE.name;
        projectId=$NEW_PROJECT.id;
        parentId=$PARENT_ID;
        content=$content;
        fileType=$FILE.type
      }
      try {
        $NEW_FILE=Invoke-RestMethod -Body ($FILE_REQUEST|ConvertTo-Json) -Method "Post" -Uri "https://modeler.cloud.camunda.io/api/beta/files" -Headers ($REQUEST_HEADER)
        Write-Progress -Completed -Activity "$($FILE_TREE_SPACES)$($TREE_SYMBOL) Migrating file $($FILE_NAME) (Type: $($FILE.type), ID: $($FILE.id))..."
        Write-Host "$($FILE_TREE_SPACES)$($TREE_SYMBOL) $([char]0x2714) Migrated file $($FILE.name) (Type: $($FILE.type), ID: $($FILE.id))"
        Add-Content "migration.log" "$(Get-Date -UFormat "%Y-%m-%d %H:%M:%S") $($FILE_TREE_SPACES)$($TREE_SYMBOL) Migrated file $($FILE.name) (Type: $($FILE.type), ID: $($FILE.id))"
      } catch {
        Write-Host "$([char]0x26a0) POST https://modeler.cloud.camunda.io/api/beta/files failed with status code $($_.Exception.Response.StatusCode.value__)"
        Add-Content "migration.log" "$(Get-Date -UFormat "%Y-%m-%d %H:%M:%S") $([char]0x26a0) POST https://modeler.cloud.camunda.io/api/beta/files failed with status code $($_.Exception.Response.StatusCode.value__)"
      }
    }
  }
}
Add-Content "migration.log" "$(Get-Date -UFormat "%Y-%m-%d %H:%M:%S") Migration done"

Write-Host ""

Write-Host "------------------------------------------------------------------------"
Write-Host "MIGRATION SUCCESS" -ForegroundColor Green
Write-Host "Done! Log in to Web Modeler now and enable the super-user mode (https://docs.camunda.io/docs/next/components/modeler/web-modeler/collaboration/#super-user-mode) to see the projects and assign collaborators."
Write-Host "------------------------------------------------------------------------"
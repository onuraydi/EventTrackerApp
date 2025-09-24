#!/bin/bash

# Ignore etmek istediğin dosya
FILE_TO_IGNORE="google-services.json"

# Tüm branch isimlerini al
BRANCHES=$(git branch -r | grep -v '\->' | sed 's/origin\///')

for BRANCH in $BRANCHES
do
    echo "Processing branch: $BRANCH"
    
    # Branch'i checkout et
    git checkout $BRANCH
    
    # Dosyayı cache'den çıkar
    git rm --cached $FILE_TO_IGNORE
    
    # Commit oluştur
    git commit -m "Stop tracking $FILE_TO_IGNORE on $BRANCH"
    
    # Push et
    git push origin $BRANCH
done

# Son olarak tekrar ana branch'e dön
git checkout main
echo "All done!"

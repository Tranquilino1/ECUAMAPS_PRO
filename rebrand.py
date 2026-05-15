import os
import re

def replace_in_file(file_path, search_pattern, replacement):
    if not os.path.exists(file_path):
        print(f"Skipping {file_path} (not found)")
        return
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    new_content = re.sub(search_pattern, replacement, content, flags=re.IGNORECASE)
    
    if content != new_content:
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print(f"Updated {file_path}")
    else:
        print(f"No changes for {file_path}")

# 1. Branding Strings
replace_in_file('android/app/src/main/res/values/strings.xml', r'Organic Maps', 'ECUAMAPS')

# 2. Package Name / ID
replace_in_file('android/build.gradle', r"appId = 'app.organicmaps'", "appId = 'app.ecuamaps'")

# 3. NOTICE File
notice_path = 'NOTICE'
derivative_text = "ECUAMAPS is a derivative work of Organic Maps, modified by the ECUAMAPS Team under the Apache License 2.0.\n\n"
if os.path.exists(notice_path):
    with open(notice_path, 'r', encoding='utf-8') as f:
        content = f.read()
    if "ECUAMAPS" not in content:
        with open(notice_path, 'w', encoding='utf-8') as f:
            f.write(derivative_text + content)
        print("Updated NOTICE")
else:
    with open(notice_path, 'w', encoding='utf-8') as f:
        f.write(derivative_text)
    print("Created NOTICE")

print("✅ Rebranding automation complete.")

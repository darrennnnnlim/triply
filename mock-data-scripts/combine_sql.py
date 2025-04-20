import os

# Folder where your .sql files are stored (change if needed)
folder = "."

# Output file
output_file = "combined.sql"

# Get list of .sql files in the folder
sql_files = [f for f in os.listdir(folder) if f.endswith(".sql")]
sql_files.sort(reverse=True)  # optional: alphabetical order

# Read and combine contents
combined_sql = []
for filename in sql_files:
    filepath = os.path.join(folder, filename)
    with open(filepath, "r") as f:
#         combined_sql.append(f"-- START OF {filename} --\n")
        combined_sql.append(f.read())
#         combined_sql.append(f"\n-- END OF {filename} --\n\n")

# Write to output file
with open(output_file, "w") as f:
    f.writelines(combined_sql)

print(f"Combined {len(sql_files)} SQL files into {output_file}")
